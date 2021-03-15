package com.example.forms;

import java.io.Serializable;
import java.util.List;

public class FormClass implements Serializable
{
    private String _id;
    private String type;
    private String title;
    private List<Question> components;

    public String getType() { return type; }
    public String getName() { return title; }
    public String getId() { return _id; }

    public List<Question> getQuestions() { return components; }
}
