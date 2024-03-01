package ru.hits.coreservice.exception;

/**
 * Исключение, которое выбрасывается, когда возникает конфликт между данными или запросами.
 */
public class ConflictException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ConflictException(String message) {
        super(message);
    }

}
