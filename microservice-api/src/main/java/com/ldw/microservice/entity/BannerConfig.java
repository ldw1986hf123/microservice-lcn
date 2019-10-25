package com.ldw.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: useSwagger
 * @description:
 * @author: zxb
 * @create: 2019-10-21 15:08
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BannerConfig {
    private String id;
    private String bannerName;
    private String imageURL;
    private String clientId;

}
