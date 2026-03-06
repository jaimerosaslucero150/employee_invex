package mx.com.invex.employee_invex.exceptions;

/*
Developer: Enrique Rosas
Date: 05/03/2026
*/

public class EmployeeNotFoundException extends ApiException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id, 404);
    }
}
