package com.example.demo.dto;

import com.example.demo.exception.ContactValidationException;
import com.example.demo.dto.converter.ToLowerCaseConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto extends OperationResultDto {

    public EmailDto(com.example.demo.model.Email email) {
        id = email.getId();
        this.email = email.getEmail();
    }
    public EmailDto(com.example.demo.model.Email email, boolean result) {
        super(result);
        id = email.getId();
        this.email = email.getEmail();
    }

    public EmailDto(ContactValidationException e) {
        super(e);
    }

    @Positive
    private Long id;

    @Email(message = "Email не прошёл валидацию")
    @NotNull
    @JsonDeserialize(converter = ToLowerCaseConverter.class)
    private String email;

}
