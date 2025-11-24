package backend.exception;

public class CheckoutNotFoundException extends RuntimeException {
    public CheckoutNotFoundException(Long id) {
        super("Could not find order with id " + id);
    }
}
