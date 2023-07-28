package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {
    @Size(min = 1, message = "Name cannot be blank")
    String name;
    String description;
    Boolean available;
}
