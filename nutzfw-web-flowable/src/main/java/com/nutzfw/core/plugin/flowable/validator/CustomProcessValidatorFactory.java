/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.validator;

import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ProcessValidatorImpl;
import org.flowable.validation.validator.ValidatorSet;
import org.flowable.validation.validator.ValidatorSetFactory;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/2
 */
public class CustomProcessValidatorFactory extends ProcessValidatorFactory {

    @Override
    public ProcessValidator createDefaultProcessValidator() {
        CustomProcessValidator processValidator = new CustomProcessValidator();
        ValidatorSet validatorSet = new ValidatorSetFactory().createFlowableExecutableProcessValidatorSet();
        validatorSet.addValidator(new CustomUserTaskValidator());
        processValidator.addValidatorSet(validatorSet);
        return processValidator;
    }

}
