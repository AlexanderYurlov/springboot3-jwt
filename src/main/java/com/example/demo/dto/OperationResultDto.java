package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResultDto {

    public OperationResultDto(Boolean result) {
        isSuccess = result;
    }

    public OperationResultDto(Exception e) {
        isSuccess = false;
        message = e.getMessage();
    }

    private Boolean isSuccess;;
    private String message;

}
