package hexlet.code.app.repository;

import hexlet.code.app.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findBySlug(String slug);
    Optional<Status> findByName(String name);
}