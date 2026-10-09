package org.example.social_network.gui;

public record FieldSpec(String label, String initValue, boolean disabled) {
    public FieldSpec(String label, String initlValue) {
        this(label, initlValue, false);
    }
}