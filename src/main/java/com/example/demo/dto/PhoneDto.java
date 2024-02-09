package com.example.demo.dto;

import com.example.demo.exception.ContactValidationException;
import com.example.demo.model.Phone;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto extends OperationResultDto {

    public PhoneDto(Phone phone) {
        this.id = phone.getId();
        this.phone = phone.getPhone();
    }

    public PhoneDto(Phone phone, boolean result) {
        super(result);
        this.id = phone.getId();
        this.phone = phone.getPhone();
    }

    public PhoneDto(ContactValidationException e) {
        super(e);
    }

    @Positive
    private Long id;

    @NotNull
    @Pattern(regexp = "^\\d{10,13}$", message = "Телефон не прошёл валидацию")
    private String phone;

}
