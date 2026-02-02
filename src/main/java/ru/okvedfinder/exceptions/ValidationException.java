package ru.okvedfinder.exceptions;

/**
 * Исключение для ошибок валидации телефонных номеров
 * Наследуется от RuntimeException для удобства использования в стримах
 */
public class ValidationException extends RuntimeException {
    
    private final String input;
    private final String reason;
    
    public ValidationException(String message, String input, String reason) {
        super(String.format("%s: input='%s', reason='%s'", message, input, reason));
        this.input = input;
        this.reason = reason;
    }
    
    public ValidationException(String message, String input) {
        this(message, input, "invalid_format");
    }
    
    public ValidationException(String message) {
        this(message, null, "validation_failed");
    }
    
    public String getInput() {
        return input;
    }
    
    public String getReason() {
        return reason;
    }
    
    /**
     * Фабричный метод для создания исключения с конкретным типом ошибки
     */
    public static ValidationException forInput(String input, String reason) {
        return switch (reason) {
            case "empty" -> new ValidationException("Входная строка пуста", input, reason);
            case "too_short" -> new ValidationException(
                "Номер слишком короткий для российского мобильного", input, reason);
            case "too_long" -> new ValidationException(
                "Номер слишком длинный", input, reason);
            case "invalid_prefix" -> new ValidationException(
                "Неверный префикс российского мобильного номера", input, reason);
            case "not_digits" -> new ValidationException(
                "Номер содержит недопустимые символы", input, reason);
            case "no_country_code" -> new ValidationException(
                "Отсутствует код страны (Россия +7)", input, reason);
            default -> new ValidationException("Неверный формат номера", input, reason);
        };
    }
}