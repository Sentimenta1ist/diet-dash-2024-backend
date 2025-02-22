package app.dietdash.repository;

import app.dietdash.model.Profile;
import app.dietdash.model.Ration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RationRepository extends JpaRepository<Ration, Long> {

    Optional<Ration> findByProfileAndDate(Profile profile, LocalDate date);
}
