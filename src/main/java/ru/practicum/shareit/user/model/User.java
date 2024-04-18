package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Column(nullable = false, length = 255)
    private String name;

    @Email
    @NotBlank(message = "Email must not be blank")
    @Column(nullable = false, length = 255, unique = true)
    private String email;
}
