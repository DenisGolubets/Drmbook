package com.denisgolubets.dreambook.util;

import java.lang.String;public class HomeItem {
    private int HomeItemID;
    private String HomeItemName;
    private String HomeItemDescription;


    public int getHomeItemID() {
        return HomeItemID;
    }
    public void setHomeItemID(int ID) {
        this.HomeItemID = ID;
    }

    public String getHomeItemName() {
        return HomeItemName;
    }
    public void setHomeItemName(String Name) {
        this.HomeItemName = Name;
    }


    public String getHomeItemDescription() {
        return HomeItemDescription;
    }
    public void setHomeItemDescription(String Description) {
        this.HomeItemDescription = Description;
    }

}