package github.keshaparrot.fitnesshelper.domain.mappers;

import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlanEntryMapper {
    PlanEntryMapper INSTANCE = Mappers.getMapper(PlanEntryMapper.class);

    PlanEntry toEntity(PlanEntryDTO dto);

    PlanEntryDTO toDto(PlanEntry entity);
}
