package mx.com.invex.employee_invex.service;

import java.util.List;
import mx.com.invex.employee_invex.dto.Employee;
import mx.com.invex.employee_invex.dto.EmployeeRequest;

public interface EmployeeService {
    public List<Employee> getAllEmployees();

    public Employee getEmployee(Long idEmployee);

    public List<Employee> findEmployeesByName(String name);

    public Employee saveEmployee(EmployeeRequest request);

    public Employee updateEmployee(Long id,EmployeeRequest employee);

    public boolean deleteEmployee(Long idEmployee);

}
