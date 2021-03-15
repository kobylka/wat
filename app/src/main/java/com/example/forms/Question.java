package com.example.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question
{
    private Map<String, String> val;
    private class Value
    {
        private String label;
        private String value;
    }
    private String key;
    private List<Value> values;
    private String type;

    public Map<String, String> processQuestions()
    {
        if(val == null)
            val = new HashMap<String, String>();

        if(values != null)
            for(Value v : values)
                val.put(v.label, v.value);

        return val;
    }

    public String getType() { return type; }
    public String getKey() { return key; }
    public Map<String, String> getValues() { return val; }
}
