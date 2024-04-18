package ru.hits.coreservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.hits.coreservice.dto.ApiError;
import ru.hits.coreservice.exception.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработчик исключений, который предоставляет обработку исключений для различных типов ошибок,
 * возникающих в приложении. Наследуется от класса {@link ResponseEntityExceptionHandler},
 * который предоставляет базовую обработку исключений, связанных с {@link ResponseEntity}.
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    /**
     * Метод обрабатывает исключение типа {@link MethodArgumentNotValidException},
     * которое возникает при некорректном вводе данных в запросе.
     *
     * @param exception исключение типа {@link MethodArgumentNotValidException}.
     * @param headers заголовки ответа.
     * @param status статус ответа.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        logError(request, exception);

        Map<String, List<String>> errors = new HashMap<>();

        exception
                .getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();

                    if (message != null) {
                        if (errors.containsKey(fieldName)) {
                            errors.get(fieldName).add(message);
                        } else {
                            List<String> newErrorList = new ArrayList<>();
                            newErrorList.add(message);

                            errors.put(fieldName, newErrorList);
                        }
                    }
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Метод обрабатывает исключение типа {@link NotFoundException}, которое возникает,
     * когда запрошенный ресурс не найден.
     *
     * @param exception исключение типа {@link NotFoundException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException exception,
                                                            WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternalServerException(NotFoundException exception,
                                                            WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Метод обрабатывает исключение типа {@link BadRequestException}, которое возникает,
     * когда запрос содержит некорректные данные.
     *
     * @param exception исключение типа {@link BadRequestException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException exception,
                                                              WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Метод обрабатывает исключение типа {@link ConflictException}, которое возникает,
     * когда происходит конфликт в запросе.
     *
     * @param exception исключение типа {@link ConflictException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException exception,
                                                            WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Метод обрабатывает исключение типа {@link UnauthorizedException}, которое возникает,
     * когда пользователь не авторизован.
     *
     * @param exception исключение типа {@link UnauthorizedException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException exception,
                                                            WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Метод обрабатывает непредвиденные внутренние ошибки сервера,
     * которые не относятся к другим типам исключений.
     *
     * @param exception исключение типа {@link Exception}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedInternalException(Exception exception,
                                                                      WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(
                new ApiError("Непредвиденная внутренняя ошибка сервера"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Метод обрабатывает исключение типа {@link MultiForbiddenException}, которое возникает,
     * когда сервер понял запрос, но отказывается его авторизовать.
     *
     * @param exception исключение типа {@link MultiForbiddenException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(MultiForbiddenException.class)
    public ResponseEntity<ApiError> handleMultiForbiddenException(MultiForbiddenException exception,
                                                                 WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessages()), HttpStatus.FORBIDDEN);
    }

    /**
     * Метод обрабатывает исключение типа {@link ForbiddenException}, которое возникает,
     * когда сервер понял запрос, но отказывается его авторизовать.
     *
     * @param exception исключение типа {@link ForbiddenException}.
     * @param request объект запроса.
     * @return объект ответа с ошибкой в формате JSON.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException exception,
                                                                WebRequest request
    ) {
        logError(request, exception);
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Метод для логирования всех исключений, которые доходят до контроллера.
     *
     * @param request   запрос, в ходе выполнения которого возникло исключение.
     * @param exception исключение.
     */
    private void logError(WebRequest request, Exception exception) {
        log.error("Возникла ошибка при запросе на URL: {}", request.getDescription(true));
        log.error(exception.getMessage(), exception);
    }

}