package backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CheckoutNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(CheckoutNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(CheckoutNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());
        return response;
    }
}
