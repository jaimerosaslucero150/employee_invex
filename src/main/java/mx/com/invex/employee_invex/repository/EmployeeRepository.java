package mx.com.invex.employee_invex.repository;

import mx.com.invex.employee_invex.dto.Employee;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
