package ru.seims.database.entitiy;

import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "build")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @Column(name = "id_type")
    private String type;
    @Column
    private String name;
    @Column(name = "id_region")
    private String district;
    private String pageId;

    public Organization() {}

    public Organization(String id) {
        this(id, "", "", "", "");
    }

    public Organization(String id, String type, String name, String district, String pageId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.district = district;
        this.pageId = pageId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.id.equals(((Organization) o).id);
    }
}
