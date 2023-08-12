/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Grishka, 2013-2016.
 */

package org.telegram.messenger.voip;

import android.content.SharedPreferences;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.SystemClock;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.voip.VoIPHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import tw.nekomimi.nekogram.utils.EnvUtil;

public class VoIPController {

	public static final int NET_TYPE_UNKNOWN = 0;
	public static final int NET_TYPE_GPRS = 1;
	public static final int NET_TYPE_EDGE = 2;
	public static final int NET_TYPE_3G = 3;
	public static final int NET_TYPE_HSPA = 4;
	public static final int NET_TYPE_LTE = 5;
	public static final int NET_TYPE_WIFI = 6;
	public static final int NET_TYPE_ETHERNET = 7;
	public static final int NET_TYPE_OTHER_HIGH_SPEED = 8;
	public static final int NET_TYPE_OTHER_LOW_SPEED = 9;
	public static final int NET_TYPE_DIALUP = 10;
	public static final int NET_TYPE_OTHER_MOBILE = 11;

	public static final int STATE_WAIT_INIT = 1;
	public static final int STATE_WAIT_INIT_ACK = 2;
	public static final int STATE_ESTABLISHED = 3;
	public static final int STATE_FAILED = 4;
	public static final int STATE_RECONNECTING = 5;

	public static final int DATA_SAVING_NEVER = 0;
	public static final int DATA_SAVING_MOBILE = 1;
	public static final int DATA_SAVING_ALWAYS = 2;
	public static final int DATA_SAVING_ROAMING = 3;

	public static final int ERROR_CONNECTION_SERVICE = -5;
	public static final int ERROR_INSECURE_UPGRADE = -4;
	public static final int ERROR_LOCALIZED = -3;
	public static final int ERROR_PRIVACY = -2;
	public static final int ERROR_PEER_OUTDATED = -1;
	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_INCOMPATIBLE = 1;
	public static final int ERROR_TIMEOUT = 2;
	public static final int ERROR_AUDIO_IO = 3;

	protected long nativeInst;
	protected long callStartTime;
	protected ConnectionStateListener listener;

	public VoIPController() {
		nativeInst = nativeInit(new File(ApplicationLoader.applicationContext.getFilesDir(), "voip_persistent_state.json").getAbsolutePath());
	}

	public void start() {
		ensureNativeInstance();
		nativeStart(nativeInst);
	}

	public void connect() {
		ensureNativeInstance();
		nativeConnect(nativeInst);
	}

	public void setEncryptionKey(byte[] key, boolean isOutgoing) {
		if (key.length != 256) {
			throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + key.length);
		}
		ensureNativeInstance();
		nativeSetEncryptionKey(nativeInst, key, isOutgoing);
	}

	public static void setNativeBufferSize(int size) {
		nativeSetNativeBufferSize(size);
	}

	public void release() {
		ensureNativeInstance();
		nativeRelease(nativeInst);
		nativeInst = 0;
	}

	public String getDebugString() {
		ensureNativeInstance();
		return nativeGetDebugString(nativeInst);
	}

	protected void ensureNativeInstance() {
		if (nativeInst == 0) {
			throw new IllegalStateException("Native instance is not valid");
		}
	}

	public void setConnectionStateListener(ConnectionStateListener connectionStateListener) {
		listener = connectionStateListener;
	}

	// called from native code
	private void handleStateChange(int state) {
		if (state == STATE_ESTABLISHED && callStartTime == 0) {
			callStartTime = SystemClock.elapsedRealtime();
		}
		if (listener != null) {
			listener.onConnectionStateChanged(state, false);
		}
	}

	// called from native code
	private void handleSignalBarsChange(int count) {
		if (listener != null) {
			listener.onSignalBarCountChanged(count);
		}
	}

	public void setNetworkType(int type) {
		ensureNativeInstance();
		nativeSetNetworkType(nativeInst, type);
	}

	public long getCallDuration() {
		return SystemClock.elapsedRealtime() - callStartTime;
	}

	public void setMicMute(boolean mute) {
		ensureNativeInstance();
		nativeSetMicMute(nativeInst, mute);
	}

	public void setConfig(double recvTimeout, double initTimeout, int dataSavingOption, long callID) {
		ensureNativeInstance();
		boolean sysAecAvailable = false, sysNsAvailable = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			try {
				sysAecAvailable = AcousticEchoCanceler.isAvailable();
				sysNsAvailable = NoiseSuppressor.isAvailable();
			} catch (Throwable ignore) {

			}
		}
		SharedPreferences preferences = MessagesController.getGlobalMainSettings();
		boolean dump = preferences.getBoolean("dbg_dump_call_stats", false);
		nativeSetConfig(nativeInst, recvTimeout, initTimeout, dataSavingOption,
				!(sysAecAvailable && VoIPServerConfig.getBoolean("use_system_aec", true)),
				!(sysNsAvailable && VoIPServerConfig.getBoolean("use_system_ns", true)),
				true, BuildVars.DEBUG_VERSION ? getLogFilePath("voip" + callID) : getLogFilePath(callID), BuildVars.DEBUG_VERSION && dump ? getLogFilePath("voipStats") : null,
				BuildVars.DEBUG_VERSION);
	}

	public void debugCtl(int request, int param) {
		ensureNativeInstance();
		nativeDebugCtl(nativeInst, request, param);
	}

	public long getPreferredRelayID() {
		ensureNativeInstance();
		return nativeGetPreferredRelayID(nativeInst);
	}

	public int getLastError() {
		ensureNativeInstance();
		return nativeGetLastError(nativeInst);
	}

	public void getStats(Stats stats) {
		ensureNativeInstance();
		if (stats == null) {
			throw new NullPointerException("You're not supposed to pass null here");
		}
		nativeGetStats(nativeInst, stats);
	}

	public static String getVersion() {
		return nativeGetVersion();
	}

	private String getLogFilePath(String name) {
		return new File(ApplicationLoader.applicationContext.getCacheDir(),"logs/" + name + ".log").getPath();
	}

	private String getLogFilePath(long callID){
		return new File(ApplicationLoader.applicationContext.getCacheDir(),"logs/" + callID + ".log").getPath();
	}

	public String getDebugLog() {
		ensureNativeInstance();
		return nativeGetDebugLog(nativeInst);
	}

	public void setProxy(String address, int port, String username, String password) {
		ensureNativeInstance();
		if (address == null) {
			throw new NullPointerException("address can't be null");
		}
		nativeSetProxy(nativeInst, address, port, username, password);
	}

	public void setAudioOutputGainControlEnabled(boolean enabled) {
		ensureNativeInstance();
		nativeSetAudioOutputGainControlEnabled(nativeInst, enabled);
	}

	public int getPeerCapabilities() {
		ensureNativeInstance();
		return nativeGetPeerCapabilities(nativeInst);
	}

	public void requestCallUpgrade() {
		ensureNativeInstance();
		nativeRequestCallUpgrade(nativeInst);
	}

	public void setEchoCancellationStrength(int strength) {
		ensureNativeInstance();
		nativeSetEchoCancellationStrength(nativeInst, strength);
	}

	public boolean needRate() {
		ensureNativeInstance();
		return nativeNeedRate(nativeInst);
	}

	private native long nativeInit(String persistentStateFile);
	private native void nativeStart(long inst);
	private native void nativeConnect(long inst);
	private static native void nativeSetNativeBufferSize(int size);
	private native void nativeRelease(long inst);
	private native void nativeSetNetworkType(long inst, int type);
	private native void nativeSetMicMute(long inst, boolean mute);
	private native void nativeDebugCtl(long inst, int request, int param);
	private native void nativeGetStats(long inst, Stats stats);
	private native void nativeSetConfig(long inst, double recvTimeout, double initTimeout, int dataSavingOption, boolean enableAEC, boolean enableNS, boolean enableAGC, String logFilePath, String statsDumpPath, boolean logPacketStats);
	private native void nativeSetEncryptionKey(long inst, byte[] key, boolean isOutgoing);
	private native void nativeSetProxy(long inst, String address, int port, String username, String password);
	private native long nativeGetPreferredRelayID(long inst);
	private native int nativeGetLastError(long inst);
	private native String nativeGetDebugString(long inst);
	private static native String nativeGetVersion();
	private native void nativeSetAudioOutputGainControlEnabled(long inst, boolean enabled);
	private native void nativeSetEchoCancellationStrength(long inst, int strength);
	private native String nativeGetDebugLog(long inst);
	private native int nativeGetPeerCapabilities(long inst);
	private native void nativeRequestCallUpgrade(long inst);
	private static native boolean nativeNeedRate(long inst);
	public static native int getConnectionMaxLayer();

	public interface ConnectionStateListener {
		void onConnectionStateChanged(int newState, boolean inTransition);
		void onSignalBarCountChanged(int newCount);
	}

	public static class Stats {
		public long bytesSentWifi;
		public long bytesRecvdWifi;
		public long bytesSentMobile;
		public long bytesRecvdMobile;

		@Override
		public String toString() {
			return "Stats{" +
					"bytesRecvdMobile=" + bytesRecvdMobile +
					", bytesSentWifi=" + bytesSentWifi +
					", bytesRecvdWifi=" + bytesRecvdWifi +
					", bytesSentMobile=" + bytesSentMobile +
					'}';
		}
	}
}
