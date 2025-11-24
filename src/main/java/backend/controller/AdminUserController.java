package backend.controller;

import backend.exception.AdminUserNotFoundException;
import backend.exception.UserNotFoundException;
import backend.model.AdminUserModel;
import backend.repository.AdminUserRepository;

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
public class AdminUserController {

    @Autowired
    private AdminUserRepository adminUserRepository;

    // Regex for simple email validation
    private final Pattern emailPattern = Pattern.compile("^\\S+@\\S+\\.\\S+$");

    @PostMapping("/admin")
    public ResponseEntity<?> newAdminUserModel(@RequestBody AdminUserModel newAdminUserModel) {
        // Backend validation
        Map<String, String> errors = validateAdmin(newAdminUserModel);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        AdminUserModel savedUser = adminUserRepository.save(newAdminUserModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Admin Login
    @PostMapping("/Alogin")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AdminUserModel loginDetails) {
        AdminUserModel user = adminUserRepository.findByEmail(loginDetails.getEmail())
                .orElseThrow(() -> new AdminUserNotFoundException("Email not found: " + loginDetails.getEmail()));

        if (user.getPassword().equals(loginDetails.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("id", user.getId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials!"));
        }
    }

    // Display all admins
    @GetMapping("/admin")
    public List<AdminUserModel> getAllUsers() {
        return adminUserRepository.findAll();
    }

    @GetMapping("/admin/{id}")
    public AdminUserModel getUserId(@PathVariable Long id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminUserModel updatedUser, @PathVariable Long id) {
        // Backend validation
        Map<String, String> errors = validateAdmin(updatedUser);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        AdminUserModel updated = adminUserRepository.findById(id).map(user -> {
            user.setFullname(updatedUser.getFullname());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            return adminUserRepository.save(user);
        }).orElseThrow(() -> new AdminUserNotFoundException(id));

        return ResponseEntity.ok(updated);
    }

    // Delete admin
    @DeleteMapping("/admin/{id}")
    public String deleteProfile(@PathVariable Long id) {
        if (!adminUserRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        adminUserRepository.deleteById(id);
        return "Admin account " + id + " deleted";
    }

    // Validation method
    private Map<String, String> validateAdmin(AdminUserModel admin) {
        Map<String, String> errors = new HashMap<>();

        if (admin.getFullname() == null || admin.getFullname().trim().length() < 3) {
            errors.put("fullname", "Full name must be at least 3 characters");
        }

        if (admin.getEmail() == null || !emailPattern.matcher(admin.getEmail()).matches()) {
            errors.put("email", "Invalid email format");
        }

        if (admin.getPassword() == null || admin.getPassword().length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        return errors;
    }
}
