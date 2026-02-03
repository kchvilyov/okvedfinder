package ru.okvedfinder.exceptions;

/**
 * Исключение для ошибок валидации телефонных номеров.
 * Наследуется от RuntimeException для удобства использования в стримах
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message, String input, String reason) {
        super(String.format("%s: input='%s', reason='%s'", message, input, reason));
    }
    
    public ValidationException(String message, String input) {
        this(message, input, "invalid_format");
    }
    
    public ValidationException(String message) {
        this(message, null, "validation_failed");
    }

}