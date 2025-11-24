package backend.repository;

import backend.model.CheckoutModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutModel, Long> {
    // You can add custom queries here if needed
}
