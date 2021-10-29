package com.nutzfw.modules.sys.dto;

import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/27
 * 业务触发器
 */
@Data
public class BusinessTableTrigger {

  private String effectiveConditions;
  private String triggerAction;
  private String triggerTiming;
  private String receiveUserField;
  private String messageTemplate;
  private String frequencyLimitField;
}
