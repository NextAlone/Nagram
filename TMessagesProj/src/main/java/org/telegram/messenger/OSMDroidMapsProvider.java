package org.telegram.messenger;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.util.Consumer;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class OSMDroidMapsProvider implements IMapsProvider {

    @Override
    public void initializeMaps(Context context) {

    }

    @Override
    public IMapView onCreateMapView(Context context) {
        return new OSMDroidMapView(context);
    }

    @Override
    public IMarkerOptions onCreateMarkerOptions(IMapView imapView) {
        return new OSMDroidMarkerOptions((MapView) imapView.getView());
    }

    @Override
    public ICircleOptions onCreateCircleOptions() {
        return new OSMDroidCircleOptions();
    }

    @Override
    public ILatLngBoundsBuilder onCreateLatLngBoundsBuilder() {
        return new OSMDroidLatLngBoundsBuilder();
    }

    @Override
    public ICameraUpdate newCameraUpdateLatLng(LatLng latLng) {
        OSMDroidCameraUpdate update = new OSMDroidCameraUpdate();
        update.target = latLng;
        return update;
    }

    @Override
    public ICameraUpdate newCameraUpdateLatLngZoom(LatLng latLng, float zoom) {
        OSMDroidCameraUpdate update = new OSMDroidCameraUpdate();
        update.target = latLng;
        update.zoom = zoom;
        return update;
    }

    @Override
    public ICameraUpdate newCameraUpdateLatLngBounds(ILatLngBounds bounds, int padding) {
        OSMDroidCameraUpdateBounds update = new OSMDroidCameraUpdateBounds();
        update.targetBounds = (OSMDroidLatLngBounds) bounds;
        update.padding = padding;
        return update;
    }

    public final static class OSMDroidCameraUpdate implements ICameraUpdate {
        private LatLng target;
        private Float zoom = null;
    }

    public final static class OSMDroidCameraUpdateBounds implements ICameraUpdate {
        private OSMDroidLatLngBounds targetBounds;
        private Integer padding;
    }
    
    public static GeoPoint getGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }


    @Override
    public IMapStyleOptions loadRawResourceStyle(Context context, int resId) {
        return null;
    }

    @Override
    public String getMapsAppPackageName() {
        return null;
    }

    @Override
    public int getInstallMapsString() {
        return 0;
    }

    public final static class OSMDroidMapView implements IMapView {
        private final MapView mapView;

        private ITouchInterceptor dispatchInterceptor;
        private ITouchInterceptor interceptInterceptor;
        private Runnable onLayoutListener;

        private OSMDroidMapView(Context context) {
            mapView = new MapView(context) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    if (dispatchInterceptor != null) {
                        return dispatchInterceptor.onInterceptTouchEvent(ev, super::dispatchTouchEvent);
                    }
                    return super.dispatchTouchEvent(ev);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (interceptInterceptor != null) {
                        return interceptInterceptor.onInterceptTouchEvent(ev, super::onInterceptTouchEvent);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                @Override
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    if (onLayoutListener != null) {
                        onLayoutListener.run();
                    }
                }
            };
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            GeoPoint initLocation = new GeoPoint(48.85825, 2.29448);
            final IMapController controller = mapView.getController();
            mapView.setMaxZoomLevel(20.0);
            mapView.setMultiTouchControls(true);
            mapView.setBuiltInZoomControls(false);
            controller.setCenter(initLocation);
            controller.setZoom(7.);
        }

        @Override
        public void setOnDispatchTouchEventInterceptor(ITouchInterceptor touchInterceptor) {
            dispatchInterceptor = touchInterceptor;
        }

        @Override
        public void setOnInterceptTouchEventInterceptor(ITouchInterceptor touchInterceptor) {
            interceptInterceptor = touchInterceptor;
        }

        @Override
        public void setOnLayoutListener(Runnable callback) {
            onLayoutListener = callback;
        }

        @Override
        public View getView() {
            return mapView;
        }

        @Override
        public void getMapAsync(Consumer<IMap> callback) {
            callback.accept(new OSMDroidMapImpl(this.mapView));
        }

        @Override
        public void onPause() {
            mapView.onPause();
        }

        @Override
        public void onResume() {
            mapView.onResume();
        }

        @Override
        public void onCreate(Bundle savedInstance) {

        }

        @Override
        public void onDestroy() {
        }

        @Override
        public void onLowMemory() {
        }
    }

    public final static class OSMDroidMapImpl implements IMap {
        private final MapView mapView;
        private TextView attributionOverlay;
        private MyLocationNewOverlay myLocationOverlay;
        private Consumer<Location> onMyLocationChangeListener;
        private OnMarkerClickListener onMarkerClickListener;
        private OSMDroidUISettings uiSettings;
        private OSMDroidMapImpl(MapView mapView) {
            this.mapView = mapView;
            attributionOverlay = new TextView(mapView.getContext());
            attributionOverlay.setText(Html.fromHtml("© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"));
            attributionOverlay.setShadowLayer(1, -1, -1, Color.WHITE);
            attributionOverlay.setLinksClickable(true);
            attributionOverlay.setMovementMethod(LinkMovementMethod.getInstance());
        }
        @Override
        public void setMapType(int mapType) {
            switch (mapType) {
                case MAP_TYPE_NORMAL: {
                    attributionOverlay.setText(Html.fromHtml("© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"));
                    mapView.setTileSource(TileSourceFactory.MAPNIK);
                    break;
                }
                case MAP_TYPE_SATELLITE: {
                    // Create a custom tile source
                    ITileSource tileSource = new XYTileSource(
                            "Wikimedia", 0, 19,
                            256, ".png",
                            new String[]{"https://maps.wikimedia.org/osm-intl/"},
                            "© OpenStreetMap contributors");
                    attributionOverlay.setText(Html.fromHtml("© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"));
                    mapView.setTileSource(tileSource);
                    break;
                }
                case MAP_TYPE_HYBRID: {
                    ITileSource tileSource = new XYTileSource(
                            "Carto Dark", 0, 20,
                            256, ".png",
                            new String[]{
                                    "https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/",
                                    "https://cartodb-basemaps-b.global.ssl.fastly.net/dark_all/",
                                    "https://cartodb-basemaps-c.global.ssl.fastly.net/dark_all/",
                                    "https://cartodb-basemaps-d.global.ssl.fastly.net/dark_all/"},
                            "© OpenStreetMap contributors, © CARTO");
                    attributionOverlay.setText(Html.fromHtml("© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, © <a href=\"https://carto.com/attributions\">CARTO</a>"));
                    mapView.setTileSource(tileSource);
                }
            }
        }

        @Override
        public void animateCamera(ICameraUpdate update) {
            if (update instanceof OSMDroidCameraUpdate) {
                OSMDroidCameraUpdate osmUpdate = (OSMDroidCameraUpdate) update;
                if (osmUpdate.zoom == null)
                    this.mapView.getController().animateTo(getGeoPoint(osmUpdate.target));
                else
                    this.mapView.getController().animateTo(getGeoPoint(osmUpdate.target), (double) osmUpdate.zoom, null);
            } else {
                OSMDroidCameraUpdateBounds osmUpdate = (OSMDroidCameraUpdateBounds) update;
                if (osmUpdate.padding == null)
                    this.mapView.zoomToBoundingBox(osmUpdate.targetBounds.box, true, AndroidUtilities.dp(60), mapView.getMaxZoomLevel(), 500L);
                else
                    this.mapView.zoomToBoundingBox(osmUpdate.targetBounds.box, true, AndroidUtilities.dp(60), mapView.getMaxZoomLevel(), Long.valueOf(osmUpdate.padding));
            }
        }

        @Override
        public void animateCamera(ICameraUpdate update, ICancelableCallback callback) {
            assert callback == null;
            this.animateCamera(update);
        }

        @Override
        public void animateCamera(ICameraUpdate update, int duration, ICancelableCallback callback) {
            assert callback == null;
            OSMDroidCameraUpdate osmUpdate = (OSMDroidCameraUpdate) update;
            if (osmUpdate.zoom == null)
                this.mapView.getController().animateTo(getGeoPoint(osmUpdate.target), this.mapView.getZoomLevelDouble(), (long) duration);
            else
                this.mapView.getController().animateTo(getGeoPoint(osmUpdate.target), (double) osmUpdate.zoom, (long) duration);
        }

        @Override
        public void moveCamera(ICameraUpdate update) {
            if (update instanceof OSMDroidCameraUpdate) {
                OSMDroidCameraUpdate osmUpdate = (OSMDroidCameraUpdate) update;
                this.mapView.getController().setCenter(getGeoPoint(osmUpdate.target));
                if (osmUpdate.zoom != null)
                    this.mapView.getController().setZoom(osmUpdate.zoom);
            } else {
                OSMDroidCameraUpdateBounds osmUpdate = (OSMDroidCameraUpdateBounds) update;
                this.mapView.zoomToBoundingBox(osmUpdate.targetBounds.box, false, AndroidUtilities.dp(60));
            }
        }

        @Override
        public float getMaxZoomLevel() {
            return (float) this.mapView.getMaxZoomLevel();
        }

        @Override
        public void setMyLocationEnabled(boolean enabled) {
            if (myLocationOverlay != null) {
                if (enabled) this.myLocationOverlay.enableMyLocation();
                else this.myLocationOverlay.disableMyLocation();
            } else {
                GpsMyLocationProvider imlp = new GpsMyLocationProvider(this.mapView.getContext());
                imlp.setLocationUpdateMinDistance(10);
                imlp.setLocationUpdateMinTime(10000);
                imlp.addLocationSource(LocationManager.NETWORK_PROVIDER);
                myLocationOverlay = new MyLocationNewOverlay(imlp, mapView) {
                    @Override
                    public void onLocationChanged(final Location location, IMyLocationProvider source) {
                        super.onLocationChanged(location, source);
                        if (location != null && onMyLocationChangeListener != null) {
                            onMyLocationChangeListener.accept(location);
                        }
                    }
                };
                this.mapView.getOverlayManager().add(myLocationOverlay);
                myLocationOverlay.enableMyLocation();
                myLocationOverlay.setDrawAccuracyEnabled(true);
            }
        }

        @Override
        public IUISettings getUiSettings() {
            if (this.uiSettings == null)
                this.uiSettings = new OSMDroidUISettings();
            return this.uiSettings;
        }

        @Override
        public void setOnCameraMoveStartedListener(OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.mapView.addMapListener(new MapListener() {
                @Override
                public boolean onScroll(ScrollEvent event) {
                    onCameraMoveStartedListener.onCameraMoveStarted(OnCameraMoveStartedListener.REASON_GESTURE);
                    return false;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    return false;
                }
            });
        }

        @Override
        public CameraPosition getCameraPosition() {
            return new CameraPosition(new LatLng(this.mapView.getMapCenter().getLatitude(), this.mapView.getMapCenter().getLongitude()), (float) this.mapView.getZoomLevelDouble());
        }

        @Override
        public void setOnMapLoadedCallback(Runnable callback) {
            // ignore
        }

        @Override
        public IProjection getProjection() {
            return new OSMDroidProjection(this.mapView.getProjection());
        }

        @Override
        public void setPadding(int left, int top, int right, int bottom) {
            this.mapView.setPadding(left, top, right, bottom);
        }

        @Override
        public void setMapStyle(IMapStyleOptions style) {
            // ignore
        }

        @Override
        public IMarker addMarker(IMarkerOptions markerOptions) {
            OSMDroidMarker marker = new OSMDroidMarker((OSMDroidMarkerOptions) markerOptions, mapView);
            this.mapView.getOverlayManager().add(marker.marker);
            return marker;
        }

        @Override
        public void setOnMyLocationChangeListener(Consumer<Location> callback) {
            this.onMyLocationChangeListener = callback;
            this.myLocationOverlay.runOnFirstFix(() -> {
                AndroidUtilities.runOnUIThread(() -> {
                    callback.accept(this.myLocationOverlay.getLastFix());
                });
            });
        }

        @Override
        public void setOnMarkerClickListener(OnMarkerClickListener markerClickListener) {
            this.onMarkerClickListener = markerClickListener;
        }

        @Override
        public void setOnCameraMoveListener(Runnable callback) {
            this.mapView.addMapListener(new MapListener() {
                @Override
                public boolean onScroll(ScrollEvent event) {
                    callback.run();
                    return false;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    return false;
                }
            });
        }

        @Override
        public ICircle addCircle(ICircleOptions circleOptions) {
            OSMDroidCircleOptions options = (OSMDroidCircleOptions) circleOptions;
            options.resetCircle();
            this.mapView.getOverlayManager().add(options.proximityCircle);
            return new OSMDroidCircle(this.mapView, options);
        }
    }

    public final static class OSMDroidMarkerOptions implements IMarkerOptions {
        private final Marker marker;

        private OSMDroidMarkerOptions(MapView mapView) {
            this.marker = new Marker(mapView);
        }
        @Override
        public IMarkerOptions position(LatLng latLng) {
            marker.setPosition(new GeoPoint(latLng.latitude, latLng.longitude));
            return this;
        }

        @Override
        public IMarkerOptions icon(Resources resources, Bitmap bitmap) {
            marker.setIcon(new BitmapDrawable(resources, bitmap));
            return this;
        }

        @Override
        public IMarkerOptions icon(Resources resources, int resId) {
            marker.setIcon(resources.getDrawable(resId));
            return this;
        }

        @Override
        public IMarkerOptions anchor(float lat, float lng) {
            marker.setAnchor(lat, lng);
            return this;
        }

        @Override
        public IMarkerOptions title(String title) {
            marker.setTitle(title);
            return this;
        }

        @Override
        public IMarkerOptions snippet(String snippet) {
            marker.setSnippet(snippet);
            return this;
        }

        @Override
        public IMarkerOptions flat(boolean flat) {
            marker.setFlat(flat);
            return this;
        }
    }

    public final static class OSMDroidMarker implements IMarker {
        private final Marker marker;
        private final MapView mapView;
        private Object tag;

        public OSMDroidMarker(OSMDroidMarkerOptions options, MapView mapView) {
            this.marker = options.marker;
            this.mapView = mapView;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public void setTag(Object tag) {
            this.tag = tag;
        }

        @Override
        public LatLng getPosition() {
            GeoPoint pos = this.marker.getPosition();
            return new LatLng(pos.getLatitude(), pos.getLatitude());
        }

        @Override
        public void setPosition(LatLng latLng) {
            this.marker.setPosition(getGeoPoint(latLng));
        }

        @Override
        public void setRotation(int rotation) {
            this.marker.setRotation(rotation);
        }

        @Override
        public void setIcon(Resources resources, Bitmap bitmap) {
            marker.setIcon(new BitmapDrawable(resources, bitmap));
        }

        @Override
        public void setIcon(Resources resources, int resId) {
            marker.setIcon(resources.getDrawable(resId));
        }

        @Override
        public void remove() {
            this.marker.remove(mapView);
        }
    }

    public final static class OSMDroidCircle implements ICircle {
        private final MapView mapView;
        private final OSMDroidCircleOptions options;

        public OSMDroidCircle(MapView mapView, OSMDroidCircleOptions options) {
            this.mapView = mapView;
            this.options = options;
        }

        @Override
        public void setStrokeColor(int color) {
            this.options.strokeColor(color);
        }

        @Override
        public void setFillColor(int color) {
            this.options.fillColor(color);
        }

        @Override
        public void setRadius(double radius) {
            this.options.proximityCircleRadius = radius;
            options.resetCircle();
        }

        @Override
        public double getRadius() {
            return this.options.proximityCircleRadius;
        }

        @Override
        public void setCenter(LatLng latLng) {
            this.options.center(latLng);
            options.resetCircle();
        }

        @Override
        public void remove() {
            this.mapView.getOverlayManager().remove(this.options.proximityCircle);
        }
    }

    public final static class OSMDroidCircleOptions implements ICircleOptions {
        private final Polygon proximityCircle = new Polygon();
        private GeoPoint proximityCircleCenter;
        private double proximityCircleRadius = 500;
        @Override
        public ICircleOptions center(LatLng latLng) {
            proximityCircleCenter = new GeoPoint(latLng.latitude, latLng.longitude);
            return this;
        }

        @Override
        public ICircleOptions radius(double radius) {
            this.proximityCircleRadius = radius;
            return this;
        }

        @Override
        public ICircleOptions strokeColor(int color) {
            proximityCircle.getOutlinePaint().setColor(color);
            return this;
        }

        @Override
        public ICircleOptions fillColor(int color) {
            proximityCircle.getFillPaint().setColor(color);
            return this;
        }

        @Override
        public ICircleOptions strokePattern(List<PatternItem> patternItems) {
            return this;
        }

        @Override
        public ICircleOptions strokeWidth(int width) {
            proximityCircle.getOutlinePaint().setStrokeWidth(width);
            return this;
        }

        private void resetCircle() {
            proximityCircle.setPoints(Polygon.pointsAsCircle(proximityCircleCenter, proximityCircleRadius));
        }
    }

    public final static class OSMDroidLatLngBoundsBuilder implements ILatLngBoundsBuilder {
        private final List<GeoPoint> geoPoints = new ArrayList<>();

        @Override
        public ILatLngBoundsBuilder include(LatLng latLng) {
            GeoPoint point = new GeoPoint(latLng.latitude, latLng.longitude);
            geoPoints.add(point);
            return this;
        }

        @Override
        public ILatLngBounds build() {
            OSMDroidLatLngBounds bounds = new OSMDroidLatLngBounds();
            bounds.box = BoundingBox.fromGeoPoints(this.geoPoints);
            return null;
        }
    }

    public final static class OSMDroidLatLngBounds implements ILatLngBounds {
        private BoundingBox box = null;
        private OSMDroidLatLngBounds() {
        }

        @Override
        public LatLng getCenter() {
            GeoPoint center = box.getCenterWithDateLine();
            return new LatLng(center.getLatitude(), center.getLongitude());
        }
    }

    public final static class OSMDroidProjection implements IProjection {
        private final Projection projection;

        public OSMDroidProjection(Projection projection) {
            this.projection = projection;
        }

        @Override
        public Point toScreenLocation(LatLng latLng) {
            return projection.toPixels(getGeoPoint(latLng), null);
        }
    }

    public final static class OSMDroidUISettings implements IUISettings {
        public OSMDroidUISettings() {
        }

        @Override
        public void setZoomControlsEnabled(boolean enabled) {

        }

        @Override
        public void setMyLocationButtonEnabled(boolean enabled) {

        }

        @Override
        public void setCompassEnabled(boolean enabled) {

        }
    }
}
