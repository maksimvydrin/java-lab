package org.example.socialnetwork.model;

import java.util.List;

/**
 * Контракт для сущностей, которые можно создавать и изменять в GUI.
 */
public interface Editable {
    /**
     * @return список ошибок; пустой список означает корректные данные.
     */
    List<String> validate();
}
