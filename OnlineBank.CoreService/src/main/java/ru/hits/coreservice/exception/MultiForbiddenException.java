package ru.hits.coreservice.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Исключение, выбрасываемое при попытке доступа к нескольким запрещенным ресурсам.
 */
public class MultiForbiddenException extends RuntimeException {

    /**
     * Список сообщений.
     */
    private final List<String> messages;

    /**
     * Конструктор, который создает объект {@link MultiForbiddenException}
     * с указанным списком сообщений об ошибках.
     *
     * @param messages список сообщений об ошибках, которые будут связаны с выбрасываемым исключением.
     */
    public MultiForbiddenException(List<String> messages) {
        this.messages = new ArrayList<>();
        this.messages.addAll(messages);
    }

    /**
     * Метод, который возвращает список сообщений об ошибках, связанных с выбрасываемым исключением.
     *
     * @return список сообщений об ошибках.
     */
    public List<String> getMessages() {
        return this.messages;
    }

}
