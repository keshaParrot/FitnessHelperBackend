package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanEntryMapper;
import github.keshaparrot.fitnesshelper.repository.PlanEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PlanEntryServiceImplTest {
    @Mock
    private PlanEntryRepository planEntryRepository;
    @Mock
    private PlanEntryMapper planEntryMapper;
    @InjectMocks
    private PlanEntryServiceImpl service;

    private PlanConversation conversation;
    private PlanEntry entry1;
    private PlanEntry entry2;
    private PlanEntryDTO dto1;
    private PlanEntryDTO dto2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        conversation = new PlanConversation();
        conversation.setId(10L);

        entry1 = new PlanEntry();
        entry1.setWeekNumber(1);
        entry1.setDayOfWeek(DayOfWeek.MONDAY);
        entry1.setAction("action1");

        entry2 = new PlanEntry();
        entry2.setWeekNumber(2);
        entry2.setDayOfWeek(DayOfWeek.FRIDAY);
        entry2.setAction("action2");

        dto1 = new PlanEntryDTO();
        dto1.setWeekNumber(1);
        dto1.setDayOfWeek(DayOfWeek.MONDAY);
        dto1.setAction("action1");

        dto2 = new PlanEntryDTO();
        dto2.setWeekNumber(2);
        dto2.setDayOfWeek(DayOfWeek.FRIDAY);
        dto2.setAction("action2");

        pageable = Pageable.unpaged();
    }

    @Test
    @DisplayName("saveEntries sets conversation and calls saveAll")
    void saveEntriesSetsConversationAndSaves() {
        List<PlanEntry> entries = Arrays.asList(entry1, entry2);
        when(planEntryRepository.saveAll(entries)).thenReturn(entries);

        List<PlanEntry> result = service.saveEntries(conversation, entries);

        assertEquals(entries, result);
        assertTrue(result.stream().allMatch(e -> e.getConversation() == conversation));
        verify(planEntryRepository).saveAll(entries);
    }

    @Test
    @DisplayName("getAllByConversationId returns page of DTOs")
    void getAllByConversationIdReturnsDtoPage() {
        Page<PlanEntry> pageIn = new PageImpl<>(Arrays.asList(entry1, entry2));
        when(planEntryRepository.findByConversation_Id(10L, pageable)).thenReturn(pageIn);
        when(planEntryMapper.toDto(entry1)).thenReturn(dto1);
        when(planEntryMapper.toDto(entry2)).thenReturn(dto2);

        Page<PlanEntryDTO> result = service.getAllByConversationId(10L, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(planEntryRepository).findByConversation_Id(10L, pageable);
    }
}
