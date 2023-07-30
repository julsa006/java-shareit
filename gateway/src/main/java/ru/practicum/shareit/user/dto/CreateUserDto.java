package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class CreateUserDto {

    @NotNull
    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotNull
    @NotBlank
    @Email
    String email;
}
