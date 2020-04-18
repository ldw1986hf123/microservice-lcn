package com.ldw.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Coin {
    private Integer id;
    private String name;
    private String url;

}
