package com.nutzfw.core.plugin.beetl.dto;

import com.nutzfw.modules.sys.entity.Dict;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/9
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictDTO {
    /**
     * 字典唯一code
     */
    String sysCode;
    /**
     * 是否多选
     */
    Boolean multiple;
    /**
     * HTML class 样式属性
     */
    @Builder.Default
    HashSet<String> htmlClass = new HashSet<>();
    /**
     * 树枚举需要该字段对数据进行反填和显示
     * <p>
     * v-model="formdata.id"
     */
    String vueModelFieldName;
    /**
     * 字典类型
     */
    Dict dictType;
    /**
     * 字段值
     */
    List<Dict> dictVals;
    /**
     * 扩展属性
     */
    @Builder.Default
    HashSet<String> htmlAttrs = new HashSet<>();

    /**
     * 主要用于标识树节点对象不可用
     * disabled 或者 readonly
     */
    Boolean disabled;

}
