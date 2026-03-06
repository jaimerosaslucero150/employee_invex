package mx.com.invex.employee_invex.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import mx.com.invex.employee_invex.utils.Gender;

public record EmployeeRequest(
        @NotBlank(message = "First name is required") @Size(max = 50, message = "First name must be less than 50 characters") String primerNombre,

        @Size(max = 50) String segundoNombre,

        @NotBlank(message = "Last name is required") String apellidoPaterno,

        String apellidoMaterno,

        @Min(value = 18, message = "Employee must be at least 18 years old") @Max(value = 100, message = "Age must be less than 100") Integer edad,

        @NotNull(message = "Gender is required (MALE OR FEMALE)") Gender sexo,

        @Past(message = "Birth date must be in the past") LocalDate fechaNacimiento,

        @NotBlank(message = "Position is required") String puesto) {
}
