package ru.hits.coreservice.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий ошибку в ответе API.
 */
@Data
@Slf4j
public class ApiError {

    /**
     * Список сообщений об ошибке.
     */
    private List<String> messages;

    /**
     * Конструктор класса.
     *
     * @param message сообщение об ошибке.
     */
    public ApiError(String message) {
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    /**
     * Конструктор класса.
     *
     * @param messages список сообщений.
     */
    public ApiError(List<String> messages) {
        this.messages = new ArrayList<>();
        this.messages.addAll(messages);
    }

}
