package com.spring.ragchatservice.mapper;

import com.spring.ragchatservice.dto.ChatMessageDTO;
import com.spring.ragchatservice.dto.CreateMessageRequest;
import com.spring.ragchatservice.model.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper extends BaseMapper<ChatMessageDTO, ChatMessage> {

    ChatMessageMapper INSTANCE = Mappers.getMapper(ChatMessageMapper.class);

    @Override
    ChatMessage toEntity(ChatMessageDTO dto);

    @Override
    @Mapping(source = "chatSession.id", target = "sessionId")
    ChatMessageDTO toDto(ChatMessage entity);

    @Mapping(target = "chatSession", ignore = true) // set manually in service
    @Mapping(target = "id", ignore = true)          // auto-generated
    @Mapping(target = "createdAt", ignore = true)  // auditing
    @Mapping(target = "updatedAt", ignore = true)
    ChatMessage toEntity(CreateMessageRequest request);

}
