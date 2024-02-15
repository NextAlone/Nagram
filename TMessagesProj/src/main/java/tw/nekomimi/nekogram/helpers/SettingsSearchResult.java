package tw.nekomimi.nekogram.helpers;

public class SettingsSearchResult {

    public String searchTitle;
    public Runnable openRunnable;
    public String path1;
    public String path2;
    public int iconResId;
    public int guid;

    public SettingsSearchResult(int guid, String searchTitle,String path1, String path2, int iconResId, Runnable open) {
        this.guid = guid;
        this.searchTitle = searchTitle;
        this.path1 = path1;
        this.path2 = path2;
        this.iconResId = iconResId;
        this.openRunnable = open;
    }
}
