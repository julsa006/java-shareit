package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {
    @NotNull
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    Long requestId;
}
