package com.manage.library.model;

import java.util.List;

public class Subject {

    int id;
    String name;
    private List<Topic> topics;

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }
    

    public Subject(int id, String name, List<Topic> topics) {
        this.id = id;
        this.name = name;
        this.topics = topics;
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

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "SubjectModel{"
                + "name='" + name + '\''
                + ", topics=" + topics
                + '}';
    }
}
