package github.keshaparrot.fitnesshelper.repository;

import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanConversationRepository extends JpaRepository<PlanConversation, Long> {

    Page<PlanConversation> findByUser_Id(Long userId, Pageable pageable);
}
