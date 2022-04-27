package ru.seims.database.entitiy;

public class Organization {
    private String id;
    private String type;
    private String name;
    private String district;
    private String pageId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Organization(String id, String type, String name, String district, String pageId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.district = district;
        this.pageId = pageId;
    }
}
