package ru.hits.coreservice.exception;

/**
 * Исключение, которое возникает при попытке получить несуществующий ресурс.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }

}
