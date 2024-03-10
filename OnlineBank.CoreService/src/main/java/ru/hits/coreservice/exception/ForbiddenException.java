package ru.hits.coreservice.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к запрещенному ресурсу.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Конструктор, который создает объект {@link ForbiddenException} с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке, которое будет связано с выбрасываемым исключением.
     */
    public ForbiddenException(String message) {
        super(message);
    }

}
