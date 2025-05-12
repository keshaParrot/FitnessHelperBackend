package github.keshaparrot.fitnesshelper.domain.mappers;

import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlanMessageMapper {

    PlanMessageMapper INSTANCE = Mappers.getMapper(PlanMessageMapper.class);

    @Mapping(target = "id", ignore = true)
    PlanMessage toEntity(PlanMessageDTO dto);

    PlanMessageDTO toDto(PlanMessage entity);
}
