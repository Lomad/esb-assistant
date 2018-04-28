package com.winning.monitor.data.storage.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by nicholasyan on 16/9/29.
 */
@Document
public class Person {

    @Field
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
