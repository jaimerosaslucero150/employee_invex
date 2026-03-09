package mx.com.invex.employee_invex.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import mx.com.invex.employee_invex.dto.Employee;
import mx.com.invex.employee_invex.dto.EmployeeRequest;
import mx.com.invex.employee_invex.exceptions.EmployeeNotFoundException;
import mx.com.invex.employee_invex.exceptions.EmployeePersistenceException;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mx.com.invex.employee_invex.repository.EmployeeRepository;
import mx.com.invex.employee_invex.utils.Gender;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImp employeeService;

    private List<Employee> employees;

    @BeforeEach
    void setUp() {

        Employee e1 = new Employee();
        e1.setPrimerNombre("Juan");
        e1.setSegundoNombre("Carlos");

        Employee e2 = new Employee();
        e2.setPrimerNombre("Pedro");
        e2.setSegundoNombre("Luis");

        Employee e3 = new Employee();
        e3.setPrimerNombre("Maria");
        e3.setSegundoNombre(null);

        employees = List.of(e1, e2, e3);
    }

    @Test
    void shouldReturnListOfEmployees() {// valid list of employees
        Employee e1 = new Employee();
        e1.setPrimerNombre("Juan");

        Employee e2 = new Employee();
        e2.setPrimerNombre("Pedro");

        List<Employee> employees = List.of(e1, e2);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        verify(employeeRepository).findAll();
    }

    @Test
    void shouldReturnEmployeeWhenFirstNameMatches() {// findEmployeesByName

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findEmployeesByName("juan");

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getPrimerNombre());
    }

    @Test
    void shouldReturnEmployeeWhenSecondNameMatches() {

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findEmployeesByName("luis");

        assertEquals(1, result.size());
        assertEquals("Pedro", result.get(0).getPrimerNombre());
    }

    @Test
    void shouldIgnoreEmployeesWithNullSecondName() {

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findEmployeesByName("maria");

        assertEquals(1, result.size());
        assertEquals("Maria", result.get(0).getPrimerNombre());
    }

    @Test
    void shouldReturnEmployeeWhenExists() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPrimerNombre("Juan");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployee(1L);

        assertNotNull(result);
        assertEquals("Juan", result.getPrimerNombre());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployee(1L);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {

        assertThrows(NullPointerException.class, () -> {
            employeeService.getEmployee(null);
        });
    }

    @Test
    void shouldSaveEmployeeSuccessfully() {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setPrimerNombre("Juan");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        Employee result = employeeService.saveEmployee(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIsNotSaved() {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(null);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        assertThrows(EmployeePersistenceException.class, () -> {
            employeeService.saveEmployee(request);
        });
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPrimerNombre("Pedro");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.updateEmployee(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(employeeRepository).save(employee);
    }

    @Test
    void updateShouldThrowExceptionWhenEmployeeNotFound() {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(1L, request));
    }

    @Test
    void shouldDeleteEmployeeWhenExists() {

        when(employeeRepository.existsById(1L)).thenReturn(true);

        boolean result = employeeService.deleteEmployee(1L);

        assertTrue(result);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenEmployeeDoesNotExist() {

        when(employeeRepository.existsById(1L)).thenReturn(false);

        boolean result = employeeService.deleteEmployee(1L);

        assertFalse(result);

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void deleteShouldThrowExceptionWhenIdIsNull() {

        assertThrows(NullPointerException.class, () -> {
            employeeService.deleteEmployee(null);
        });
    }
}