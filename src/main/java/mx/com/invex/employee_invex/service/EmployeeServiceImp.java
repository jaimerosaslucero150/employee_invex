package mx.com.invex.employee_invex.service;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import mx.com.invex.employee_invex.dto.Employee;
import mx.com.invex.employee_invex.dto.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import mx.com.invex.employee_invex.repository.EmployeeRepository;
import mx.com.invex.employee_invex.exceptions.EmployeeNotFoundException;
import mx.com.invex.employee_invex.exceptions.EmployeePersistenceException;

/*
Developer: Enrique Rosas
Date: 05/03/2026
 */
@Service
public class EmployeeServiceImp implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployee(Long idEmployee) {
        Objects.requireNonNull(idEmployee, "Employee id cannot be null");
        return employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new EmployeeNotFoundException(idEmployee));
    }

    @Override
    public List<Employee> findEmployeesByName(String name) {
        String search = name.toLowerCase();

        return employeeRepository.findAll()
                .stream()
                .filter(e -> e.getPrimerNombre().toLowerCase().contains(search) ||
                        (e.getSegundoNombre() != null &&
                                e.getSegundoNombre().toLowerCase().contains(search)))
                .toList();
    }

    @Override
    public Employee saveEmployee(EmployeeRequest request) {

        Employee employee = mapToEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        if (savedEmployee.getId() == null) {
            throw new EmployeePersistenceException("Error saving employee");
        }
        return savedEmployee;
    }

    @Override
    public Employee updateEmployee(Long id, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        updateEmployeeFromRequest(employee, request);
        employeeRepository.save(employee);
        return employee;
    }

    @Override
    public boolean deleteEmployee(Long idEmployee) {
        Objects.requireNonNull(idEmployee, "Employee ID must not be null");

        if (employeeRepository.existsById(idEmployee)) {
            employeeRepository.deleteById(idEmployee);
            return true;
        }

        return false;
    }

    private Employee mapToEntity(EmployeeRequest request) {
        Employee employee = new Employee();

        employee.setPrimerNombre(request.primerNombre());
        employee.setSegundoNombre(request.segundoNombre());
        employee.setApellidoPaterno(request.apellidoPaterno());
        employee.setApellidoMaterno(request.apellidoMaterno());
        employee.setEdad(request.edad());
        employee.setSexo(request.sexo());
        employee.setFechaNacimiento(request.fechaNacimiento());
        employee.setPuesto(request.puesto());

        return employee;
    }

    private void updateEmployeeFromRequest(Employee employee, EmployeeRequest request) {

        employee.setPrimerNombre(request.primerNombre());
        employee.setSegundoNombre(request.segundoNombre());
        employee.setApellidoPaterno(request.apellidoPaterno());
        employee.setApellidoMaterno(request.apellidoMaterno());
        employee.setEdad(request.edad());
        employee.setSexo(request.sexo());
        employee.setFechaNacimiento(request.fechaNacimiento());
        employee.setPuesto(request.puesto());
    }

}
