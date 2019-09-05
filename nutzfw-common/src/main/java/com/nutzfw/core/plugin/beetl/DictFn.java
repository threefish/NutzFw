package com.nutzfw.core.plugin.beetl;

import com.nutzfw.core.plugin.beetl.dto.DictDTO;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.enums.DictType;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/17  17:30
 * <p>
 * 第一位参数必填,之后的参数按照顺序总共4位，自定义属性前必须添加END字符参数
 * <p>
 * ${dict("holiday_types",false,"form-control","leave.leaveType")}
 * ${dict("holiday_types")}
 * <p>
 * 如果还需要设置自定义HTML属性需要设置END字符参数隔开，例子参考
 * <p>
 * ${dict("holiday_types",false,"form-control","leave.leaveType","END","v-model='test.xxx'")}
 * ${dict("holiday_types","END","v-model='test.xxx'")}
 * <p>
 * 不能为单例模式，否则会出现问题
 */
@IocBean(singleton = false)
public class DictFn implements Function {

    final static int MIN_PARAM = 1;

    private static final StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();

    @Inject
    DictBiz dictBiz;


    @Override
    public Object call(Object[] obj, Context context) {
        if (obj.length < MIN_PARAM) {
            return "数据字典参数格式不正确！！！";
        }
        DictDTO dto = getDictDTO(obj);
        return this.render(dto, context);
    }


    private String render(DictDTO dto, Context context) {
        String templeText = "";
        //0下拉框  1单选框  2复选框  3弹窗树  通过字段是否多选的控制动态控制字典单选复选展示
        DictType dictType = DictType.valueOf(dto.getDictType().getShowType());
        if (dictType == DictType.select) {
            templeText = getTempleText("select.html");
            if (dto.getMultiple()) {
                //下拉框多选强制修改为弹窗树
                templeText = getTempleText("tree.html");
            }
            dto.getHtmlClass().add("form-control");
        } else if (dictType == DictType.radio || dictType == DictType.checkbox) {
            //单复选动态控制
            if (dto.getMultiple()) {
                templeText = getTempleText("checkbox.html");
                dto.getHtmlClass().add("magic-checkbox");
            } else {
                templeText = getTempleText("radio.html");
                dto.getHtmlClass().add("magic-radio");
            }
        } else if (dictType == DictType.tree) {
            //弹窗树
            templeText = getTempleText("tree.html");
            dto.getHtmlClass().add("form-control");
        }
        Template template = context.gt.getTemplate(templeText, resourceLoader);
        Map data = new HashMap(9);
        data.put("dict", dto.getDictType());
        data.put("dto", dto);
        data.put("list", dto.getDictVals());
        data.put("attrs", Strings.join(" ", dto.getHtmlAttrs()));
        data.put("class", Strings.join(" ", dto.getHtmlClass()));
        data.put("onlyName", R.UU16());
        data.put("fieldName", dto.getVueModelFieldName());
        data.put("isMultiple", dto.getMultiple());
        data.put("disabled", dto.getDisabled());
        template.binding(data);
        return template.render();
    }


    /**
     * @param obj 0 sysCode
     *            1 multiple
     *            2 htmlClass
     *            3 vueModelFieldName （为树节点时必须指定）
     *            N ....
     *            如果N为是 "END" 那么表示参数结束，之后的全部为HTML attr
     * @return
     */
    private DictDTO getDictDTO(Object[] obj) {
        DictDTO dto = new DictDTO();
        dto.setSysCode(Strings.sNull(obj[0]).trim());
        dto.setMultiple(false);
        dto.setDisabled(false);
        dto.setDictType(dictBiz.getCacheDict(dto.getSysCode()));
        dto.setDefaualtValueField("id");
        if (dto.getDictType() == null) {
            throw new RuntimeException("[" + dto.getSysCode() + "]字典类型不存在，请检查！");
        }
        int endStartIndex = getEndStartIndex(obj);
        if (endStartIndex > 1) {
            dto.setMultiple(Boolean.parseBoolean(Strings.sNull(obj[1]).trim()));
        }
        if (endStartIndex > 2) {
            dto.getHtmlClass().addAll(Arrays.asList(Strings.splitIgnoreBlank(Strings.sNull(obj[2]))));
        }
        if (endStartIndex > 3) {
            dto.setVueModelFieldName(Strings.sNull(obj[3]).trim());
        }
        if (endStartIndex > 4) {
            dto.setDefaualtValueField(Strings.sNull(obj[4]).trim());
        }
        if (Strings.isBlank(dto.getVueModelFieldName())) {
            dto.setVueModelFieldName(getVueModelFieldName(obj, endStartIndex));
        }
        dto.setDictVals(dictBiz.listCache(dto.getSysCode()));
        AtomicBoolean end = new AtomicBoolean(false);
        Arrays.stream(obj).forEach(val -> {
            if ("END".equals(val)) {
                end.set(true);
            } else if (end.get()) {
                String value = Strings.sNull(val);
                if ("disabled".equals(value) || "readonly".equals(value)) {
                    dto.setDisabled(true);
                }
                //树形节点需要过滤掉 v-model 属性
                if (!((value).indexOf("v-model") > -1 && dto.getDictType().getShowType() == DictType.tree.getValue())) {
                    dto.getHtmlAttrs().add(value);
                }
            }
        });
        return dto;
    }

    private String getTempleText(String fileName) {
        return Files.read("../view/functions/dict/".concat(fileName));
    }

    private int getEndStartIndex(Object[] obj) {
        int endStartIndex = 0;
        sw:
        for (int i = 0; i < obj.length; i++) {
            if ("END".equals(Strings.sNull(obj[i]))) {
                endStartIndex = i;
                break sw;
            }
        }
        return endStartIndex;
    }

    /**
     * 自动识别VUE双向绑定字段
     *
     * @param obj
     * @return
     */
    private String getVueModelFieldName(Object[] obj, int endStartIndex) {
        String fieldName = "";
        for (int i = endStartIndex; i < obj.length; i++) {
            String val = Strings.sNull(obj[i]).trim();
            if (val.startsWith("v-model=")) {
                fieldName = val.replace("v-model=", "");
            } else if (val.startsWith("v-model.number=")) {
                fieldName = val.replace("v-model.number=", "");
            } else if (val.startsWith("v-model.lazy=")) {
                fieldName = val.replace("v-model.lazy=", "");
            } else if (val.startsWith("v-model.trim=")) {
                fieldName = val.replace("v-model.trim=", "");
            }
        }
        return fieldName.replaceAll("\'", "").replaceAll("\"", "");
    }
}
