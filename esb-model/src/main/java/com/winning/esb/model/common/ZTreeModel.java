package com.winning.esb.model.common;

/**
 * 树模型（对应于ztree.js）
 *
 * @author xuehao 2017-08-18
 */
public class ZTreeModel extends TreeModel {
    private String name;

    private String id;
    private boolean open = false;
    private String icon;
    private String iconSkin;
    private String font;
    private boolean checked = false;
    private boolean nocheck = false;

    public ZTreeModel() {

    }

    public ZTreeModel(String id, String name, String iconSkin) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
    }

    public ZTreeModel(String id, String name, String iconSkin, boolean open) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
        this.open = open;
    }

    public ZTreeModel(String id, String name, String iconSkin, Object myData) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
        this.setMyData(myData);
    }

    public ZTreeModel(String id, String name, String iconSkin, Object myData, boolean open) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
        this.setMyData(myData);
        this.open = open;
    }

    public ZTreeModel(String id, String name, String iconSkin, Object myData, boolean open, boolean checked) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
        this.setMyData(myData);
        this.open = open;
        this.checked = checked;
    }

    public ZTreeModel(String id, String name, String iconSkin, Object myData, boolean open, boolean checked, boolean nocheck) {
        this.id = id;
        this.name = name;
        this.iconSkin = iconSkin;
        this.setMyData(myData);
        this.open = open;
        this.checked = checked;
        this.nocheck = nocheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconSkin() {
        return iconSkin;
    }

    public void setIconSkin(String iconSkin) {
        this.iconSkin = iconSkin;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isNocheck() {
        return nocheck;
    }

    public void setNocheck(boolean nocheck) {
        this.nocheck = nocheck;
    }
}