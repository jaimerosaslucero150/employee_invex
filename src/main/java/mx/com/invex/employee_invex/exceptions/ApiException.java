package mx.com.invex.employee_invex.exceptions;

/*
Developer: Enrique Rosas
Date: 05/03/2026
 */
public abstract class ApiException extends RuntimeException {
    private final Integer status;

    protected ApiException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

}
