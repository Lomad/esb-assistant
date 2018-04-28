package com.winning.esb.model.middleware;

import java.util.ArrayList;
import java.util.List;

public class OdinGrantEntity {
    private String id;
    private String name;
    private  String type;
    private List<Items> items = new ArrayList<Items>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }


}
