package ru.hits.coreservice.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к недоступному сервису.
 */
public class ServiceUnavailableException extends RuntimeException {

    /**
     * Конструктор, который создает объект {@link ServiceUnavailableException} с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке, которое будет связано с выбрасываемым исключением.
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

}
