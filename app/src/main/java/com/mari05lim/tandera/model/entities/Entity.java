package com.mari05lim.tandera.model.entities;

import java.io.Serializable;

/**
 * DEV Mari05liM
 */
public class Entity implements Serializable {

    private String id;
    private String parentId;

    public Entity(String parentId) {
        this.id = parentId;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

}