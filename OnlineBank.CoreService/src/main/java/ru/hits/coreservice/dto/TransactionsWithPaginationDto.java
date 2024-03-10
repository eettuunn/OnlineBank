package ru.hits.coreservice.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionsWithPaginationDto {

    private PageInfoDto pageInfo;

    private List<TransactionDto> data;

}
