package in.snotes.snotes.model;

import android.support.annotation.DrawableRes;

public class AboutModel {

    public String title;
    @DrawableRes
    public int icon;

    public AboutModel(String title, @DrawableRes int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
