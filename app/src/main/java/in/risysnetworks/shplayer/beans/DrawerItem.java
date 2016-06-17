package in.risysnetworks.shplayer.beans;


public class DrawerItem {
    String title;
    //    Drawable icon;
    int icon;

//    public DrawerItem(String title, Drawable icon) {
//        this.title = title;
//        this.icon = icon;
//    }

//    public DrawerItem(String drawerTitle, int drawerIcon) {
//        this.title = drawerTitle;
//        this.icon = drawerIcon;
//    }

    public DrawerItem(String drawerTitle) {
        this.title = drawerTitle;
    }

    public String getTitle() {
        return title;
    }

//    public Drawable getIcon() {
//        return icon;
//    }

    public int getIcon() {
        return icon;
    }
}
