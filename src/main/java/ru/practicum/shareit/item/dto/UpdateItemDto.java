package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateItemDto {
    @Size(min = 1, message = "Name cannot be blank")
    String name;
    String description;
    Boolean available;
}
