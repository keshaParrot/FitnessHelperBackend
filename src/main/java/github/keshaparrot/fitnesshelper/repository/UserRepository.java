package github.keshaparrot.fitnesshelper.repository;

import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByEmail(String email);
    Optional<UserProfile> findByEmail(String email);
    Optional<UserProfile> getUserProfileById(Long id);
}
