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
    public static List<? extends BaseTreeEntity> createTree(List<? extends BaseTreeEntity> entities, String parentId) {
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
    public static List<? extends BaseTreeEntity> shortTree(List<? extends BaseTreeEntity> entities) {
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
