/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.validator;

import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.Problems;
import org.flowable.validation.validator.Validator;
import org.flowable.validation.validator.ValidatorImpl;
import org.flowable.validation.validator.ValidatorSet;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

import static org.flowable.bpmn.constants.BpmnXMLConstants.ATTRIBUTE_LISTENER_EXPRESSION;


/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/2
 */
public class CustomProcessValidator extends ValidatorImpl implements ProcessValidator {


    protected List<ValidatorSet> validatorSets;

    @Override
    public List<ValidationError> validate(BpmnModel bpmnModel) {

        List<ValidationError> allErrors = new ArrayList<>();
        Process mainProcess = bpmnModel.getMainProcess();
        for (ValidatorSet validatorSet : validatorSets) {
            for (Validator validator : validatorSet.getValidators()) {
                List<ValidationError> validatorErrors = new ArrayList<>();
                validator.validate(bpmnModel, validatorErrors);
                if (!validatorErrors.isEmpty()) {
                    for (ValidationError error : validatorErrors) {
                        error.setValidatorSetName(validatorSet.getName());
                    }
                    allErrors.addAll(validatorErrors);
                }
            }
        }

        mainProcess.getDataObjects().stream()
                .filter(va -> FlowConstant.PROCESS_TITLE.equals(va.getId()))
                .findFirst()
                .ifPresent(valuedDataObject -> {
                    List<ExtensionElement> expression = valuedDataObject.getExtensionElements().get(ATTRIBUTE_LISTENER_EXPRESSION);
                    if (Lang.isEmpty(expression) && Strings.isBlank(expression.get(0).getElementText())) {
                        addError(allErrors, Problems.DATA_OBJECT_MISSING_NAME, mainProcess, String.format(" [%s] 表达式值不能为空", FlowConstant.PROCESS_TITLE));
                    }
                });


        return allErrors;
    }

    @Override
    public List<ValidatorSet> getValidatorSets() {
        return validatorSets;
    }

    public void setValidatorSets(List<ValidatorSet> validatorSets) {
        this.validatorSets = validatorSets;
    }

    public void addValidatorSet(ValidatorSet validatorSet) {
        if (validatorSets == null) {
            validatorSets = new ArrayList<>();
        }
        validatorSets.add(validatorSet);
    }


    @Override
    public void validate(BpmnModel bpmnModel, List<ValidationError> errors) {

    }
}
