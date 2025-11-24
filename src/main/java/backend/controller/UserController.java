package backend.controller;

import backend.exception.UserNotFoundException;
import backend.model.UserModel;
import backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Email regex pattern for basic validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\S+@\\S+\\.\\S+$");

    private ResponseEntity<Map<String, String>> validateUserModel(UserModel user) {
        Map<String, String> errors = new HashMap<>();

        if (user.getFullname() == null || user.getFullname().trim().length() < 3) {
            errors.put("fullname", "Full name is required and must be at least 3 characters");
        }

        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.put("email", "Valid email is required");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            errors.put("password", "Password is required and must be at least 6 characters");
        }

        if (user.getPhone() == null || !user.getPhone().matches("\\d{10}")) {
            errors.put("phone", "Phone number is required and must be 10 digits");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return null; // no errors
    }

    @PostMapping("/user")
    public ResponseEntity<?> newUserModel(@RequestBody UserModel newUserModel) {
        ResponseEntity<Map<String, String>> validationError = validateUserModel(newUserModel);
        if (validationError != null) {
            return validationError;
        }
        UserModel savedUser = userRepository.save(newUserModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // user Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserModel loginDetails) {
        UserModel user = userRepository.findByEmail(loginDetails.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email not found : " + loginDetails.getEmail()));

        if (user.getPassword().equals(loginDetails.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("id", user.getId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials!"));
        }
    }

    // Display all users
    @GetMapping("/user")
    List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by id
    @GetMapping("/user/{id}")
    UserModel getUserId(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    // Update user profile
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateProfile(@RequestBody UserModel newUserModel, @PathVariable Long id) {
        ResponseEntity<Map<String, String>> validationError = validateUserModel(newUserModel);
        if (validationError != null) {
            return validationError;
        }

        UserModel updatedUser = userRepository.findById(id).map(userModel -> {
            userModel.setFullname(newUserModel.getFullname());
            userModel.setEmail(newUserModel.getEmail());
            userModel.setPassword(newUserModel.getPassword());
            userModel.setPhone(newUserModel.getPhone());
            return userRepository.save(userModel);
        }).orElseThrow(() -> new UserNotFoundException(id));

        return ResponseEntity.ok(updatedUser);
    }

    // Delete user profile
    @DeleteMapping("/user/{id}")
    String deleteProfile(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        return "User account " + id + " deleted";
    }
}
