package com.github.mcpgateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParameterConfig {
    private String type;
    private String description;
    @JsonProperty("default")
    private Object defaultValue;
    private boolean required;

    // Constructors
    public ParameterConfig() {}

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
