package mx.com.invex.employee_invex.dto;

public record ApiResult<T>(Integer status, String message, T data) {

}
