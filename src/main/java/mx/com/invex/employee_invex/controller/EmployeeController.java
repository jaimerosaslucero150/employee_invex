package mx.com.invex.employee_invex.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import mx.com.invex.employee_invex.dto.ApiResult;
import mx.com.invex.employee_invex.dto.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import mx.com.invex.employee_invex.dto.EmployeeRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import mx.com.invex.employee_invex.service.EmployeeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mx.com.invex.employee_invex.exceptions.GlobalExceptionHandler;

/*
Developer: Enrique Rosas
Date: 05/03/2026
 */

@Tag(name = "Employees", description = "Employee management APIs")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

        private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

        @Autowired
        private EmployeeService employeeService;

        @Operation(summary = "Get all employees", description = "Returns a list of all employees registered in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list (even if empty)", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.class)))
        })
        @GetMapping("/")
        public ResponseEntity<ApiResult<List<Employee>>> getEmployees() {
                long startTime = System.currentTimeMillis();
                logger.info("REST request to get all employees");

                List<Employee> employees = employeeService.getAllEmployees();
                long duration = System.currentTimeMillis() - startTime;

                if (employees.isEmpty()) {
                        logger.warn("Search completed: No employees found in database ({}ms)", duration);
                } else {
                        logger.info("Successfully retrieved {} employees in {}ms", employees.size(), duration);
                }

                ApiResult<List<Employee>> response = new ApiResult<>(
                                HttpStatus.OK.value(),
                                employees.isEmpty() ? "No employees found" : "Success",
                                employees);

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get employee by ID", description = "Provides detailed information about a specific employee based on their unique database identifier.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Employee found and data returned successfully", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "404", description = "Employee not found with the provided ID", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid ID format supplied", content = @Content)
        })
        @GetMapping("/{idEmployee}")
        public ResponseEntity<ApiResult<Employee>> getEmployeeById(@PathVariable Long idEmployee) {
                logger.info("REST request to get Employee ID: {}", idEmployee);

                Employee employee = employeeService.getEmployee(idEmployee);

                logger.info("Successfully found employee: {} {} (ID: {})",
                                employee.getPrimerNombre(), employee.getApellidoPaterno(), idEmployee);

                ApiResult<Employee> response = new ApiResult<>(
                                HttpStatus.OK.value(),
                                "Employee retrieved successfully",
                                employee);

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Search employees by name", description = "Returns a list of employees whose first name contains the specified search string. Case-insensitive.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully. If no matches are found, data will be an empty list.", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "400", description = "Missing required search parameter", content = @Content)
        })
        @GetMapping("/search")
        public ResponseEntity<ApiResult<List<Employee>>> getEmployeesByName(@RequestParam String name) {
                long startTime = System.currentTimeMillis();
                logger.info("REST request to search employees by name containing: '{}'", name);

                List<Employee> results = employeeService.findEmployeesByName(name);
                long duration = System.currentTimeMillis() - startTime;

                if (results.isEmpty()) {
                        logger.warn("No employees found matching name: '{}' ({}ms)", name, duration);
                } else {
                        logger.info("Found {} employees matching '{}' in {}ms", results.size(), name, duration);
                }

                ApiResult<List<Employee>> response = new ApiResult<>(
                                HttpStatus.OK.value(),
                                results.isEmpty() ? "No matches found for: " + name : "Search successful",
                                results);

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Create a new employee", description = "Registers a new employee in the system and returns the persisted entity with its generated ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Employee successfully created", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error - Failed to persist the employee", content = @Content(schema = @Schema(implementation = ApiResult.class)))
        })
        @PostMapping("/")
        public ResponseEntity<ApiResult<Employee>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
                logger.info("REST request to save Employee: {} {} [Position: {}]",
                                request.primerNombre(),
                                request.apellidoPaterno(),
                                request.puesto());

                Employee savedEmployee = employeeService.saveEmployee(request);
                logger.info("Employee successfully persisted with ID: {}", savedEmployee.getId());

                ApiResult<Employee> response = new ApiResult<>(
                                HttpStatus.CREATED.value(),
                                "Employee created successfully",
                                savedEmployee);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Update an existing employee", description = "Updates the information of an employee identified by the ID in the URL. If the ID does not exist, a 404 error is returned.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Employee updated successfully", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "404", description = "Update failed - Employee ID not found", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input or ID format", content = @Content)
        })
        @PutMapping("/{id}")
        public ResponseEntity<ApiResult<Employee>> updateEmployee(@PathVariable Long id,
                        @RequestBody EmployeeRequest request) {
                logger.info("REST request to update Employee ID: {} - Target: {} {}",
                                id, request.primerNombre(), request.apellidoPaterno());
                Employee updatedEmployee = employeeService.updateEmployee(id, request);
                
                ApiResult<Employee> response = new ApiResult<>(
                                HttpStatus.OK.value(),
                                "Employee updated successfully",
                                updatedEmployee);

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Delete an employee by ID", description = "Permanently removes an employee from the database. Returns a 404 error if the ID does not exist.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Employee successfully deleted", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "404", description = "Deletion failed - Employee ID not found", content = @Content(schema = @Schema(implementation = ApiResult.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error during deletion", content = @Content)
        })

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResult<Void>> deleteEmployee(@PathVariable Long id) {
                logger.info("REST request to delete Employee with ID: {}", id);

                boolean isDeleted = employeeService.deleteEmployee(id);

                if (isDeleted) {
                        logger.info("Employee with ID: {} successfully deleted", id);

                        ApiResult<Void> response = new ApiResult<>(
                                        HttpStatus.OK.value(),
                                        "Employee with ID " + id + " has been successfully deleted",
                                        null);

                        return ResponseEntity.ok(response);
                } else {
                        logger.warn("Deletion failed: Employee with ID: {} not found", id);

                        ApiResult<Void> errorResponse = new ApiResult<>(
                                        HttpStatus.NOT_FOUND.value(),
                                        "Deletion failed: Employee not found with ID " + id,
                                        null);

                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
        }

}
