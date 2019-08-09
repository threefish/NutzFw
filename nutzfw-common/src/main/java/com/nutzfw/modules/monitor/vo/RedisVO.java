package com.nutzfw.modules.monitor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisVO {

    String key;

    String value;

    Long ttl;
}
