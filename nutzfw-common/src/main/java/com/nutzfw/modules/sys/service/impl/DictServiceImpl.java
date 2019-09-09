package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.annotation.CachePut;
import com.nutzfw.core.common.annotation.CacheRemove;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.service.DictService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 15时09分41秒
 */
@IocBean
public class DictServiceImpl implements DictService {

    @Inject
    Dao dao;

    @Override
    @CacheRemove("dict-Cache:${arg[0].sysCode}")
    public Dict insertOrUpdate(Dict detail) {
        StringBuilder likeCode = new StringBuilder();
        int pid = detail.getPid();
        while (pid != 0) {
            Dict dict = fetch(pid);
            pid = dict.getPid();
            likeCode.append(dict.getLikeCode()).append("_");
        }
        likeCode.append(detail.getSysCode());
        detail.setLikeCode(likeCode.toString());
        return dao.insertOrUpdate(detail);
    }


    /**
     * @param sysCode
     * @return
     */
    @Override
    @CachePut("dict-Cache:${arg[0]}")
    public List<Dict> getCache(String sysCode) {
        return listAllDictBylikeCode(sysCode);
    }

    /**
     * 拖动排序
     *
     * @param map
     */
    @Override
    public void sort(NutMap map) {
        List<Dict> depts = dao.query(Dict.class, Cnd.where("id", "in", map.keySet()));
        depts.forEach(d -> d.setShortNo(map.getInt("" + d.getId())));
        dao.update(depts);
    }

    @Override
    public List<Dict> listAllDictBylikeCode(String sysCode) {
        return dao.query(Dict.class, Cnd.where("likeCode", "like", sysCode + "%").asc("shortNo"));
    }

    @Override
    @CacheRemove("dict-Cache:${arg[0].sysCode}")
    public int delete(Dict detail) {
        return dao.delete(detail);
    }

    @Override
    public Dict fetch(Cnd cnd) {
        return dao.fetch(Dict.class, cnd);
    }

    @Override
    public int count(Cnd cnd) {
        return dao.count(Dict.class, cnd);
    }

    @Override
    public List<Dict> query() {
        return query(null);
    }

    @Override
    public List<Dict> query(Condition shortNo) {
        return dao.query(Dict.class, shortNo);
    }

    @Override
    public void delete(int id) {
        delete(fetch(id));
    }

    @Override
    public Dict fetch(int id) {
        return dao.fetch(Dict.class, id);
    }
}
