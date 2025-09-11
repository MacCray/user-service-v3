package org.intensiv.userapi.dto.request;

import jakarta.validation.constraints.*;

public record CreateUserRequestDto(@NotNull @Size(min = 1, message = "Имя не может быть меньше 1 символа") String name,
                                   @NotNull(message = "Email не может быть null") @Email(message = "Некорректный email адрес") String email,
                                   @NotNull @Min(0) @Max(150) Integer age) {
}