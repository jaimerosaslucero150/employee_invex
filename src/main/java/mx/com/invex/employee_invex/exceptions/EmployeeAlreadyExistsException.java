package mx.com.invex.employee_invex.exceptions;

/*
Developer: Enrique Rosas
Date: 05/03/2026
 */

public class EmployeeAlreadyExistsException extends ApiException {

    public EmployeeAlreadyExistsException(String name) {
        super("Employee already exists with name: " + name, 409);
    }
}
