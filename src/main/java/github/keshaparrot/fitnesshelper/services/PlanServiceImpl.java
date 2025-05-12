package github.keshaparrot.fitnesshelper.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.domain.dto.ChatMessage;
import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanConversationMapper;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanMessageMapper;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanConversationService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanEntryService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanMessageService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanService;
import github.keshaparrot.fitnesshelper.utils.CleanMarkdown;
import github.keshaparrot.fitnesshelper.utils.llm.LLMClient;
import github.keshaparrot.fitnesshelper.utils.llm.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanConversationService conversationService;
    private final PlanMessageService messageService;
    private final PlanEntryService planEntryService;
    private final PromptBuilder promptBuilder;
    private final LLMClient llmClient;

    private final PlanConversationMapper conversationMapper;
    private final PlanMessageMapper messageMapper;

    private final ObjectMapper objectMapper;
    @Value("${app.ollama.model}")
    private String defaultModel;

    private static final int MAX_RETRIES = 2;

    @Override
    @Transactional
    public PlanConversationDTO createPlan(UserProfile user, CreatePlanRequest req) {
        PlanConversation conv = conversationService.create(user, req);
        String prompt = promptBuilder.buildPrompt(req, user);
        PlanMessage userMsg = messageService.add(conv, MessageRole.USER, prompt);
        conv.getMessages().add(userMsg);

        List<PlanEntry> entries = fetchPlanEntries(prompt);

        String cleanedJson;
        try {
            cleanedJson = objectMapper.writeValueAsString(entries);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize plan entries to JSON", e);
        }
        PlanMessage assistantMsg = messageService.add(conv, MessageRole.ASSISTANT, cleanedJson);
        conv.getMessages().add(assistantMsg);
        conv.setStatus("COMPLETED");
        conversationService.complete(conv);

        planEntryService.saveEntries(conv, entries);
        return toDto(conv);
    }

    private List<PlanEntry> fetchPlanEntries(String prompt) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            String raw = llmClient.generate(defaultModel, prompt);
            String cleaned = CleanMarkdown.clean(raw);
            log.info("🔍 Cleaned assistant response (attempt {}): {}", attempt, cleaned);

            try {
                return objectMapper.readValue(
                        cleaned,
                        new TypeReference<List<PlanEntry>>() {}
                );
            } catch (Exception ex) {
                lastFailure = new RuntimeException(
                        "Parsing failed on attempt " + attempt + ": " + cleaned, ex
                );
                log.warn("Parsing JSON failed (attempt {}): {}", attempt, ex.getMessage());
            }
        }

        throw lastFailure != null
                ? lastFailure
                : new RuntimeException("Failed to parse plan JSON after " + MAX_RETRIES + " attempts");
    }

    @Transactional
    @Override
    public PlanMessageDTO sendMessage(Long convId, String content, UserProfile user) {
        PlanConversation conv = conversationService.getEntityById(convId);
        PlanMessage userMsg = messageService.add(conv, MessageRole.USER, content);

        List<ChatMessage> history = conv.getMessages().stream()
                .map(pm -> new ChatMessage(pm.getRole().toLowerCase(), pm.getContent()))
                .toList();

        String ai = llmClient.chat(this.defaultModel, history);
        PlanMessage aiMsg = messageService.add(conv, MessageRole.ASSISTANT, ai);
        return toDto(aiMsg);
    }

    private PlanConversationDTO toDto(PlanConversation planConversation) {
        return conversationMapper.toDto(planConversation);
    }
    private PlanMessageDTO toDto(PlanMessage planMessage) {
        return messageMapper.toDto(planMessage);
    }
}
