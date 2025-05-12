package github.keshaparrot.fitnesshelper.controllers;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanConversationMapper;
import github.keshaparrot.fitnesshelper.services.PlanEntryServiceImpl;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanConversationService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanMessageService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanConversationService conversationService;
    private final PlanMessageService messageService;
    private final PlanEntryServiceImpl entryService;
    private final PlanService planService;

    @PostMapping()
    public ResponseEntity<PlanConversationDTO> createPlan(
            @RequestBody CreatePlanRequest request,
            @AuthenticationPrincipal UserProfile user) {
        PlanConversationDTO conv = planService.createPlan(user, request);
        return ResponseEntity.ok(conv);
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<PlanMessageDTO> sendMessage(
            @PathVariable Long id,
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal UserProfile user
    ) {
        String content = body.get("content");
        var dto = planService.sendMessage(id, content, user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<PlanMessage> addMessage(
            @PathVariable Long id,
            @RequestParam("role") MessageRole role,
            @RequestBody String content) {
        PlanConversation conv = conversationService.getEntityById(id);
        PlanMessage msg = messageService.add(conv, role, content);
        return ResponseEntity.ok(msg);
    }

    /**
     *
     * GET methods
     *
     */

    @GetMapping("/user/get-all")
    public ResponseEntity<Page<PlanConversationDTO>> getUserPlans(
            @AuthenticationPrincipal UserProfile user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanConversationDTO> result = conversationService.findForUser(user.getId(), pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanConversationDTO> getPlan(@PathVariable Long id) {
        PlanConversationDTO conv = conversationService.getById(id);
        return ResponseEntity.ok(conv);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<PlanMessageDTO>> getMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanMessageDTO> msgs = messageService.findByConversation(id,pageable);
        return ResponseEntity.ok(msgs);
    }

    @GetMapping("/{id}/entries")
    public ResponseEntity<Page<PlanEntryDTO>> getEntriesMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanEntryDTO> entries = entryService.getAllByConversationId(id,pageable);
        return ResponseEntity.ok(entries);
    }
}