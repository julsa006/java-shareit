package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Value
public class UpdateUserDto {

    @Size(min = 1, message = "Name cannot be blank")
    String name;

    @Email
    String email;
}
