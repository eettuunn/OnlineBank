package ru.hits.coreservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageInfoDto {

    private Integer pageNumber;

    private Integer pageSize;

    private Integer totalSize;


}
