package mx.com.invex.employee_invex.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import mx.com.invex.employee_invex.utils.Gender;
import org.hibernate.annotations.CreationTimestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    private Integer edad;

    @Enumerated(EnumType.STRING)
    private Gender sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String puesto;

    @CreationTimestamp
    @Column(name = "fecha_alta", updatable = false, nullable = false)
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
    private Boolean activo = true;

}
