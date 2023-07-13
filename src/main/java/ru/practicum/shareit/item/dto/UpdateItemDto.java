package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.Size;

@Value
public class UpdateItemDto {
    @Size(min = 1, message = "Name cannot be blank")
    String name;
    String description;
    Boolean available;
}
