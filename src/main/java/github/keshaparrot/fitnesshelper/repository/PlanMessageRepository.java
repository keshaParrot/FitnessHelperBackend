package github.keshaparrot.fitnesshelper.repository;

import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMessageRepository extends JpaRepository<PlanMessage, Long> {
    Page<PlanMessage> findByConversationIdOrderByTimestamp(Long conversationId, Pageable pageable);
}
