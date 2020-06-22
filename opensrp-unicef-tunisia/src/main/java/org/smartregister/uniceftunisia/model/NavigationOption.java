package org.smartregister.uniceftunisia.model;

public class NavigationOption {

    private int ResourceID;
    private int ResourceActiveID;
    private int TitleID;
    private String MenuTitle;
    private long RegisterCount;
    private boolean isEnabled;

    public NavigationOption(int resourceID, int resourceActiveID, int titleID, String menuTitle, long registerCount, boolean isEnabled) {
        ResourceID = resourceID;
        ResourceActiveID = resourceActiveID;
        TitleID = titleID;
        MenuTitle = menuTitle;
        RegisterCount = registerCount;
        this.isEnabled = isEnabled;
    }

    public int getResourceID() {
        return ResourceID;
    }

    public void setResourceID(int resourceID) {
        ResourceID = resourceID;
    }

    public int getResourceActiveID() {
        return ResourceActiveID;
    }

    public void setResourceActiveID(int resourceActiveID) {
        ResourceActiveID = resourceActiveID;
    }

    public int getTitleID() {
        return TitleID;
    }

    public void setTitleID(int titleID) {
        TitleID = titleID;
    }

    public String getMenuTitle() {
        return MenuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        MenuTitle = menuTitle;
    }

    public long getRegisterCount() {
        return RegisterCount;
    }

    public void setRegisterCount(long registerCount) {
        RegisterCount = registerCount;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}

