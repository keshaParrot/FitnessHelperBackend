package github.keshaparrot.fitnesshelper.repository;

import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanEntryRepository extends JpaRepository<PlanEntry, Long> {
    List<PlanEntry> findByConversationIdOrderByWeekNumberAscDayOfWeekAsc(Long conversationId);
    Page<PlanEntry> findByConversation_Id(Long conversationId, Pageable pageable);
}
