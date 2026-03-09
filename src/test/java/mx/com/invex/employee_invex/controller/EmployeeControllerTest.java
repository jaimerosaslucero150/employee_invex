package mx.com.invex.employee_invex.controller;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import org.springframework.http.MediaType;
import mx.com.invex.employee_invex.dto.Employee;
import mx.com.invex.employee_invex.dto.EmployeeRequest;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.invex.employee_invex.service.EmployeeService;
import mx.com.invex.employee_invex.utils.Gender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void shouldReturnAllEmployees() throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPrimerNombre("Juan");
        employee.setApellidoPaterno("Perez");

        List<Employee> employees = List.of(employee);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].primerNombre").value("Juan"))
                .andExpect(jsonPath("$.data[0].apellidoPaterno").value("Perez"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void shouldReturnEmptyEmployeeList() throws Exception {

        when(employeeService.getAllEmployees()).thenReturn(List.of());

        mockMvc.perform(get("/employees/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("No employees found"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(employeeService).getAllEmployees();
    }

    @Test
    void shouldReturnEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPrimerNombre("Juan");
        employee.setApellidoPaterno("Perez");

        when(employeeService.getEmployee(1L)).thenReturn(employee);

        mockMvc.perform(get("/employees/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee retrieved successfully"))
                .andExpect(jsonPath("$.data.primerNombre").value("Juan"))
                .andExpect(jsonPath("$.data.apellidoPaterno").value("Perez"));

        verify(employeeService).getEmployee(1L);
    }

    @Test
    void shouldReturnEmployeesByName() throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPrimerNombre("Juan");
        employee.setApellidoPaterno("Perez");

        List<Employee> employees = List.of(employee);

        when(employeeService.findEmployeesByName("Juan")).thenReturn(employees);

        mockMvc.perform(get("/employees/search")
                .param("name", "Juan")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Search successful"))
                .andExpect(jsonPath("$.data[0].primerNombre").value("Juan"));

        verify(employeeService).findEmployeesByName("Juan");
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesFound() throws Exception {

        when(employeeService.findEmployeesByName("Carlos")).thenReturn(List.of());

        mockMvc.perform(get("/employees/search")
                .param("name", "Carlos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("No matches found for: Carlos"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(employeeService).findEmployeesByName("Carlos");
    }

    @Test
    void shouldDeleteEmployeeSuccessfully() throws Exception {

        when(employeeService.deleteEmployee(1L)).thenReturn(true);

        mockMvc.perform(delete("/employees/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Employee with ID 1 has been successfully deleted"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingEmployee() throws Exception {

        when(employeeService.deleteEmployee(1L)).thenReturn(false);

        mockMvc.perform(delete("/employees/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Deletion failed: Employee not found with ID 1"));

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void shouldCreateEmployee() throws Exception {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        Employee saved = new Employee();
        saved.setId(1L);
        saved.setPrimerNombre("Juan");
        saved.setApellidoPaterno("Perez");

        when(employeeService.saveEmployee(any(EmployeeRequest.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.primerNombre").value("Juan"));

        verify(employeeService).saveEmployee(any(EmployeeRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenFirstNameIsBlank() throws Exception {

        EmployeeRequest request = new EmployeeRequest(
                "",
                null,
                "Perez",
                null,
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Developer");

        mockMvc.perform(post("/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAgeIsInvalid() throws Exception {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                null,
                "Perez",
                null,
                15,
                Gender.MALE,
                LocalDate.of(2010, 5, 10),
                "Developer");

        mockMvc.perform(post("/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenGenderIsNull() throws Exception {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                null,
                "Perez",
                null,
                30,
                null,
                LocalDate.of(1994, 5, 10),
                "Developer");

        mockMvc.perform(post("/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateEmployee() throws Exception {

        EmployeeRequest request = new EmployeeRequest(
                "Juan",
                "Carlos",
                "Perez",
                "Lopez",
                30,
                Gender.MALE,
                LocalDate.of(1994, 5, 10),
                "Senior Developer");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setPrimerNombre("Juan");
        updatedEmployee.setApellidoPaterno("Perez");

        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"))
                .andExpect(jsonPath("$.data.primerNombre").value("Juan"))
                .andExpect(jsonPath("$.data.apellidoPaterno").value("Perez"));

        verify(employeeService).updateEmployee(eq(1L), any(EmployeeRequest.class));
    }

}
