package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanEntryService {
    List<PlanEntry> saveEntries(PlanConversation conv, List<PlanEntry> entries);
    Page<PlanEntryDTO> getAllByConversationId(Long convId, Pageable pageable);
}
