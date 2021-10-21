package com.nutzfw.core.plugin.flowable.util;

import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
import com.nutzfw.core.plugin.flowable.extmodel.OnlineFieldAuth;
import com.nutzfw.modules.sys.enums.FieldAuth;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * ${onlineForm.fieldIsHidden()}
 */
public class OnlineFormUtils {

    /**
     * 字段是否不可见
     *
     * @return
     */
    public static final boolean fieldIsHidden(Integer field, FormElementModel formElementModel) {
        final Optional<OnlineFieldAuth> first = formElementModel.getFieldAuths().stream()
                .filter(onlineFieldAuth -> Objects.nonNull(onlineFieldAuth.getField()))
                .filter(onlineFieldAuth -> String.valueOf(field).equals(onlineFieldAuth.getField()))
                .findFirst();
        if (first.isPresent()) {
            return first.get().getAuth() == FieldAuth.hide;
        }
        return false;
    }

    /**
     * 字段是否只读
     *
     * @return
     */
    public static final String fieldIsReadonly(Integer field, FormElementModel formElementModel) {
        final Optional<OnlineFieldAuth> first = formElementModel.getFieldAuths().stream()
                .filter(onlineFieldAuth -> Objects.nonNull(onlineFieldAuth.getField()))
                .filter(onlineFieldAuth -> String.valueOf(field).equals(onlineFieldAuth.getField()))
                .findFirst();
        if (first.isPresent()) {
            return first.get().getAuth() == FieldAuth.r ? "disabled" :"";
        }
        return "";
    }

    /**
     * 字段是否只读
     *
     * @return
     */
    public static final boolean fieldIsReadonlyBoolean(Integer field, FormElementModel formElementModel) {
        final Optional<OnlineFieldAuth> first = formElementModel.getFieldAuths().stream()
                .filter(onlineFieldAuth -> Objects.nonNull(onlineFieldAuth.getField()))
                .filter(onlineFieldAuth -> String.valueOf(field).equals(onlineFieldAuth.getField()))
                .findFirst();
        if (first.isPresent()) {
            return first.get().getAuth() == FieldAuth.r ;
        }
        return false;
    }
}
