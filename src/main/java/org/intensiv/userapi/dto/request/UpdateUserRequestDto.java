package org.intensiv.userapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(@Size(message = "Имя должно содержать как минимум 1 символ", min = 1) String name,
                                   @Email(message = "Email должет быть корректным") String email,
                                   @Min(0) @Max(150) Integer age)
{}
