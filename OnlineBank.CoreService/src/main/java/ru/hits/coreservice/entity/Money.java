package ru.hits.coreservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Money {

    @Column(precision = 100, scale = 50)
    private BigDecimal amount;
    private Currency currency;

}
