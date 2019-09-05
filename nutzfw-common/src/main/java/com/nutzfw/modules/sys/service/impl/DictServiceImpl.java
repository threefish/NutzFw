package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.service.DictService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 15时09分41秒
 */
@IocBean(args = {"refer:dao"})
public class DictServiceImpl extends BaseServiceImpl<Dict> implements DictService {


    public DictServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public Dict insertOrUpdate(Dict detail) {
        StringBuilder likeCode = new StringBuilder();
        int pid = detail.getPid();
        while (pid != 0) {
            Dict dict = fetch(Cnd.where("id", "=", pid));
            pid = dict.getPid();
            likeCode.append(dict.getLikeCode()).append("_");
        }
        likeCode.append(detail.getSysCode());
        detail.setLikeCode(likeCode.toString());
        Dict dictDetail = super.insertOrUpdate(detail);
        return dictDetail;
    }

    @Override
    public int delete(Dict detail) {
        return super.delete(detail);
    }

    /**
     * 准备更换为j2cache
     * @param sysCode
     * @return
     */
    @Override
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
        List<Dict> depts = query(Cnd.where("id", IN, map.keySet()));
        depts.forEach(d -> d.setShortNo(map.getInt("" + d.getId())));
        this.update(depts);
    }

    @Override
    public List<Dict> listAllDictBylikeCode(String sysCode) {
        return query(Cnd.where("likeCode", "like", sysCode + "%").asc("shortNo"));
    }
}
