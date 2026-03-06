package mx.com.invex.employee_invex.exceptions;

public class EmployeePersistenceException extends RuntimeException {

    public EmployeePersistenceException(String message) {
        super(message);
    }

    public EmployeePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}