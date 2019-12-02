package com.ldw.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Objects;

/**
 * @program: useSwagger
 * @description:
 * @author: zxb
 * @create: 2019-10-21 15:08
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CurrencyConfig {
   /*  `id` int(11) NOT NULL,
  `language` varchar(255) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
            `updated_time` datetime DEFAULT NULL,*/

    private int id;
    private String language;
    private String currency;
    private String key;
    private String value;
    private Date createdTime;
    private Date updatedTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyConfig that = (CurrencyConfig) o;
        return Objects.equals(language, that.language) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, currency, key);
    }
}
