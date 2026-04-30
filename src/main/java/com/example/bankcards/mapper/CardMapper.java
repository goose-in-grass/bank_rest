package com.example.bankcards.mapper;


import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    CardResponse toResponse(Card card);

    // Если понадобится в будущем
    // Card toEntity(CardResponse dto);
}
