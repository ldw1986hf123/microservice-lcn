package com.ldw.microservice.service.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: useSwagger
 * @description:
 * @author: zxb
 * @create: 2019-11-02 17:03
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BannerClickVo {
    private String bannerName;
    private int noted;
    private int clickCnt;
}
