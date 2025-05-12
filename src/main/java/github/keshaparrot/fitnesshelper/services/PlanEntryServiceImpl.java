package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanEntryMapper;
import github.keshaparrot.fitnesshelper.repository.PlanEntryRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanEntryServiceImpl implements PlanEntryService {
    private final PlanEntryRepository planEntryRepository;
    private final PlanEntryMapper planEntryMapper;

    @Override
    public List<PlanEntry> saveEntries(PlanConversation conv, List<PlanEntry> entries) {
        for (PlanEntry e : entries) {
            e.setConversation(conv);
        }
        return planEntryRepository.saveAll(entries);
    }

    @Override
    public Page<PlanEntryDTO> getAllByConversationId(Long convId, Pageable pageable) {
        return planEntryRepository.findByConversation_Id(convId, pageable).map(this::toDto);
    }

    private PlanEntryDTO toDto(PlanEntry planEntry) {
        return planEntryMapper.toDto(planEntry);
    }
}
