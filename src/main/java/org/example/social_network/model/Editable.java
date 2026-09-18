package org.example.social_network.model;
import java.util.List;

public interface Editable {
    /** @return список ошибок; пустой список = данные корректны */
    List<String> validate();
}
