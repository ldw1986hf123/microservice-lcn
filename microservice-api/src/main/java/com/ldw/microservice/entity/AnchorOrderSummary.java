package com.ldw.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @program: useSwagger
 * @description:
 * @author: zxb
 * @create: 2019-10-21 15:08
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AnchorOrderSummary {
    private Integer id;
    private String currency;
    private String anchorCurrency;
    private BigDecimal actualAmount;
    private BigDecimal anchorAmount;

}
