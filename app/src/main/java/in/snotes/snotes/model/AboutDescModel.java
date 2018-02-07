package in.snotes.snotes.model;

import android.support.annotation.DrawableRes;

// model used where the about element has a description
public class AboutDescModel {

    public String title;
    public String desc;
    @DrawableRes
    int icon;

    public AboutDescModel(String title, String desc, @DrawableRes int icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
