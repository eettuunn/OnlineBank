package ru.hits.coreservice.exception;

/**
 * Исключение, выбрасываемое при некорректном запросе со стороны клиента.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public BadRequestException(String message) {
        super(message);
    }

}
