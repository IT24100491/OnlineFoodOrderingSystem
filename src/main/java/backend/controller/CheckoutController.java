package backend.controller;

import backend.exception.CheckoutNotFoundException;
import backend.model.CheckoutModel;
import backend.repository.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkout")
@CrossOrigin("http://localhost:3000")
public class CheckoutController {

    @Autowired
    private CheckoutRepository checkoutRepository;

    // Create a new checkout order
    @PostMapping
    public ResponseEntity<CheckoutModel> createOrder(@RequestBody CheckoutModel order) {
        try {
            CheckoutModel savedOrder = checkoutRepository.save(order);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<CheckoutModel>> getAllOrders() {
        try {
            List<CheckoutModel> orders = checkoutRepository.findAll();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get order by id
    @GetMapping("/{id}")
    public ResponseEntity<CheckoutModel> getOrderById(@PathVariable Long id) {
        return checkoutRepository.findById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseThrow(() -> new CheckoutNotFoundException(id));
    }

    // Delete order by id
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable Long id) {
        if (!checkoutRepository.existsById(id)) {
            throw new CheckoutNotFoundException(id);
        }

        try {
            checkoutRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
