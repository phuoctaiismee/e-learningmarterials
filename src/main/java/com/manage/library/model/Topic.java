package com.manage.library.model;

import java.util.List;

public class Topic {

    private int id;
    private String name;
    private int subjectId;
    private List<Resource> resources;

    public Topic() {
    }

    public Topic(String name, int subjectId) {
        this.name = name;
        this.subjectId = subjectId;
    }

    public Topic(int id, String name, List<Resource> resources) {
        this.id = id;
        this.name = name;
        this.resources = resources;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "TopicModel{"
                + "name='" + name + '\''
                + ", resources=" + resources
                + '}';
    }
}
