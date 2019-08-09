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

    public static final String PREFIX = RedisHelpper.buildRediskey("dict.");
    @Inject
    RedisHelpper redisHelpper;

    public DictServiceImpl(Dao dao) {
        super(dao);
    }


    @Override
    public Dict insertOrUpdate(Dict detail) {
        Dict dictDetail = super.insertOrUpdate(detail);
        updateCache(detail.getSysCode());
        return dictDetail;
    }

    @Override
    public int delete(Dict detail) {
        int count = super.delete(detail);
        updateCache(detail.getSysCode());
        return count;
    }

    @Override
    public List<Dict> updateCache(String sysCode) {
        List<Dict> details = this.query(Cnd.where("sysCode", "=", sysCode).and("grouping", "=", false).asc("shortNo"));
        redisHelpper.setJsonString(PREFIX + sysCode, details, 60 * 60);
        return details;
    }

    @Override
    public List<Dict> getCache(String sysCode) {
        List<Dict> details;
        if (redisHelpper.exists(PREFIX + sysCode)) {
            details = Json.fromJsonAsList(Dict.class, redisHelpper.get(PREFIX + sysCode));
        } else {
            details = updateCache(sysCode);
        }
        return details;
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
}
