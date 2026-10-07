package org.example.socialnetwork.csv;

import java.util.List;

/**
 * Результат чтения: корректные объекты и ошибки битых строк.
 */
public record CsvParseResult<T>(List<T> items, List<String> errors) {
}
