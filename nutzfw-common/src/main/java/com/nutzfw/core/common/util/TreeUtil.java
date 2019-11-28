/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;


import com.nutzfw.core.common.entity.BaseTreeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 */
public class TreeUtil {

    /**
     * 迭代ID,PID树
     *
     * @param entities
     * @param parentId
     * @return
     */
    public static List<BaseTreeEntity> createTree(List<? extends BaseTreeEntity> entities, String parentId) {
        List<BaseTreeEntity> childList = new ArrayList<>();
        for (BaseTreeEntity c : entities) {
            String id = c.getId();
            String pid = c.getPid();
            if (parentId.equals(pid)) {
                List<? extends BaseTreeEntity> childs = createTree(entities, id);
                c.setChildren(childs);
                childList.add(c);
            }
        }
        Collections.sort(childList, (o1, o2) -> o1.getShortNo() > o2.getShortNo() ? 0 : -1);
        return childList;
    }

    /**
     * 排序ID,PID树
     *
     * @param entities
     * @return
     */
    public static List<BaseTreeEntity> shortTree(List<? extends BaseTreeEntity> entities) {
        List<BaseTreeEntity> shortMenu = new ArrayList<>();
        for (BaseTreeEntity c : entities) {
            if (c.getChildren() != null && c.getChildren().size() > 0) {
                List<? extends BaseTreeEntity> nenuChilds = c.getChildren();
                Collections.sort(nenuChilds, (o1, o2) -> o1.getShortNo() > o2.getShortNo() ? 0 : -1);
                nenuChilds = shortTree(nenuChilds);
                c.setChildren(nenuChilds);
            }
            shortMenu.add(c);
        }
        return shortMenu;
    }
}
