package backend.controller;

import backend.model.FeedbackModel;
import backend.repository.FeedbackRepository;
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
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Email regex pattern for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\S+@\\S+\\.\\S+$");

    private ResponseEntity<Map<String, String>> validateFeedback(FeedbackModel feedback) {
        Map<String, String> errors = new HashMap<>();

        if (feedback.getName() == null || feedback.getName().trim().length() < 2) {
            errors.put("name", "Name is required and must be at least 2 characters");
        }

        if (feedback.getEmail() == null || !EMAIL_PATTERN.matcher(feedback.getEmail()).matches()) {
            errors.put("email", "Valid email is required");
        }

        if (feedback.getSubject() == null || feedback.getSubject().trim().length() < 5) {
            errors.put("subject", "Subject is required and must be at least 5 characters");
        }

        if (feedback.getMessage() == null || feedback.getMessage().trim().length() < 10) {
            errors.put("message", "Message is required and must be at least 10 characters");
        }

        if (feedback.getRating() == null || feedback.getRating() < 1 || feedback.getRating() > 5) {
            errors.put("rating", "Rating must be between 1 and 5");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return null; // no errors
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> createFeedback(@RequestBody FeedbackModel feedback) {
        ResponseEntity<Map<String, String>> validationError = validateFeedback(feedback);
        if (validationError != null) {
            return validationError;
        }

        FeedbackModel savedFeedback = feedbackRepository.save(feedback);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Feedback submitted successfully");
        response.put("id", savedFeedback.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/feedback")
    public List<FeedbackModel> getAllFeedback() {
        return feedbackRepository.findAllOrderByCreatedAtDesc();
    }

    @GetMapping("/feedback/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        return feedbackRepository.findById(id)
                .map(feedback -> ResponseEntity.ok().body(feedback))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/feedback/email/{email}")
    public List<FeedbackModel> getFeedbackByEmail(@PathVariable String email) {
        return feedbackRepository.findByEmail(email);
    }

    @GetMapping("/feedback/rating/{rating}")
    public List<FeedbackModel> getFeedbackByRating(@PathVariable Integer rating) {
        return feedbackRepository.findByRating(rating);
    }

    @GetMapping("/feedback/search/{keyword}")
    public List<FeedbackModel> searchFeedbackBySubject(@PathVariable String keyword) {
        return feedbackRepository.findBySubjectContainingIgnoreCase(keyword);
    }

    @GetMapping("/feedback/stats/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRating() {
        Double averageRating = feedbackRepository.getAverageRating();
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating != null ? averageRating : 0.0);
        response.put("totalFeedback", feedbackRepository.count());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/feedback/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        if (!feedbackRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feedbackRepository.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Feedback deleted successfully"));
    }
}
