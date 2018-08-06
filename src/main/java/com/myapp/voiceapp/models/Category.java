package com.myapp.voiceapp.models;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
public class Category extends AbstractEntity {

    private int id;

    @Size(min = 3, message = "Category names must be at least 3 characters long")
    private String name;


    @ManyToMany(mappedBy = "categories")
    private List<Query> queries;

    public Category() {}

    public Category(@Size(min = 3, message = "Category names must be at least 3 characters long") String firstName,
                    @Size(min = 3, message = "Category names must be at least 3 characters long") String lastName) {

        if (name.length() < 3)
            throw new IllegalArgumentException("Category names must be at least 3 characters long");


        this.name = firstName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
