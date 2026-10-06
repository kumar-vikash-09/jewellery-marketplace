package com.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    private Long parentId;
}
