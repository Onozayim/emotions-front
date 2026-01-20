package com.emotions.emotions.entities;

import com.emotions.emotions.anotations.PasswordMatching;
import com.emotions.emotions.anotations.StrongPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@PasswordMatching(
    password = "password",
    confirmPassword = "confirmPassword",
    message = "Las contraseñas deben coincidir"
)
public class RegisterDto {
    @Size(min = 0, max = 50, message = "El correo tiene un límite de 100 caracteres")
    @NotBlank(message = "El correo es necesario")
    @Email(message = "El correo es invalido")
    private String email;

    @Size(min = 0, max = 50, message = "La contraseña tiene un límite de 75 caracteres")
    @NotBlank(message = "La contraseña es necesaria")
    @StrongPassword
    private String password;

    @Size(min = 0, max = 50, message = "La contraseña tiene un límite de 75 caracteres")
    @NotBlank(message = "Favor de confirmar la contraseña")
    private String confirmPassword;

    private String token;
}
