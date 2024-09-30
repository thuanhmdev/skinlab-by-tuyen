package com.domain.blog.exception;

import com.domain.blog.domain.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalException {

    // handle all exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> handleAllException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value={DataNotFoundException.class})
    public ResponseEntity<ResponseDTO<Object>> handleDataNotFoundException (Exception e) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<Object>();
        responseDTO.setData(null);
        responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        responseDTO.setError("BAD REQUEST");
        responseDTO.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(value={NoResourceFoundException.class})
    public ResponseEntity<ResponseDTO<Object>> handleNotFoundException(Exception ex) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<Object>();
        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        responseDTO.setError(ex.getMessage());
        responseDTO.setMessage("404 Not Found. URL may not exist...");
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMethodArgumentNotValidException (final MethodArgumentNotValidException e) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<Object>();
        responseDTO.setData(null);
        responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        responseDTO.setMessage(e.getMessage());

        // Extract validation errors
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        List<String> errorMessages = fieldErrors
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        responseDTO.setMessage(errorMessages);
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<Object>> handleBadCredentialsException(Exception ex) {
        ResponseDTO<Object> responseDTO = new ResponseDTO();
        responseDTO.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        responseDTO.setMessage("Token is invalid (expired, not in the correct format, or does not transmit JWT in the header)...");
        responseDTO.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

}
