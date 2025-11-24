package backend.repository;

import backend.model.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {
    
    // Find feedback by email
    List<FeedbackModel> findByEmail(String email);
    
    // Find feedback by rating
    List<FeedbackModel> findByRating(Integer rating);
    
    // Find feedback by subject containing keyword
    List<FeedbackModel> findBySubjectContainingIgnoreCase(String keyword);
    
    // Custom query to get feedback ordered by creation date
    @Query("SELECT f FROM FeedbackModel f ORDER BY f.createdAt DESC")
    List<FeedbackModel> findAllOrderByCreatedAtDesc();
    
    // Custom query to get average rating
    @Query("SELECT AVG(f.rating) FROM FeedbackModel f")
    Double getAverageRating();
}
