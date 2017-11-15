package com.example.LevelCache;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "simpledata")
public class SimpleStringData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    public SimpleStringData() {
    }

    public SimpleStringData(String description) {
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
        return "SimpleStringData{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
