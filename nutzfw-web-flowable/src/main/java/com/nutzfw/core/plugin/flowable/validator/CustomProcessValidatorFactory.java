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
        ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
        ValidatorSet validatorSet = new ValidatorSetFactory().createFlowableExecutableProcessValidatorSet();
        validatorSet.addValidator(new CustomUserTaskValidator());
        processValidator.addValidatorSet(validatorSet);
        return processValidator;
    }

}
