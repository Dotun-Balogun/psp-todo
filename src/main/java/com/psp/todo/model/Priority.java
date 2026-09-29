package com.psp.todo.model;

public enum Priority {
    LOW("Low"), MEDIUM("Medium"), HIGH("High");
    private final String label;
    Priority(String label) { this.label = label; }
    public String getLabel() { return label; }
}
