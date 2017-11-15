package com.example.LevelCache;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "simpledata")
public class SimpleData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    public SimpleData() {
    }

    public SimpleData(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SimpleData{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
