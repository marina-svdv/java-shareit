package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;
}
