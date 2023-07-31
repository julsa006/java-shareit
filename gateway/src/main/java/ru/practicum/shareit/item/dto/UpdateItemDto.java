package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {
    @Size(min = 1, message = "Name cannot be blank")
    private String name;
    private String description;
    private Boolean available;
}
