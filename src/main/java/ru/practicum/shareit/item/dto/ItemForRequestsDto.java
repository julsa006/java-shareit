package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForRequestsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long requestId;
}
