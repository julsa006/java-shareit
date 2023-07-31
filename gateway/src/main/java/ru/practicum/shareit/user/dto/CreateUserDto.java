package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    @NotNull
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull
    @NotBlank
    @Email
    private String email;
}
