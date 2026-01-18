package org.example.qnrassignment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Status is required.")
    private String status;
}
