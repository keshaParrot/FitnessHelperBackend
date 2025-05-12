package github.keshaparrot.fitnesshelper.domain.mappers;

import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlanConversationMapper {
    PlanConversationMapper INSTANCE = Mappers.getMapper(PlanConversationMapper.class);

    @Mapping(target = "id", ignore = true)
    PlanConversation toEntity(PlanConversationDTO dto);

    @Mapping(source = "user.id", target = "userId")
    PlanConversationDTO toDto(PlanConversation entity);
}
