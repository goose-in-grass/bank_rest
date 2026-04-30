package com.example.bankcards.mapper;

import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "status", expression = "java(card.getStatus().name())")
    CardResponse toResponse(Card card);
}
