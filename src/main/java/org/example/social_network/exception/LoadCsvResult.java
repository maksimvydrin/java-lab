package org.example.social_network.exception;

import java.util.List;

public class LoadCsvResult<T> {
    private final List<T> items;
    private final List<LoadCsvException> errors;

    public LoadCsvResult(List<T> items, List<LoadCsvException> errors) {
        this.items = items;
        this.errors = errors;
    }

    public List<T> getItems() {
        return items;
    }

    public List<LoadCsvException> getErrors() {
        return errors;
    }
}
