/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.monitor.vo.RedisVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/1
 */
@IocBean
@At("/sysRedis")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class RedisAction extends BaseAction {

    @Inject
    RedisService redisService;

    @Inject
    JedisPool pool;

    @Ok("btl:WEB-INF/view/sys/monitor/redis/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysRedis.index")
    @AutoCreateMenuAuth(name = "redis缓存管理", icon = "fa-eye", parentPermission = "sys.monitor")
    public void index() {
    }

    @Ok("json")
    @POST
    @At("/list")
    @RequiresPermissions("sysRedis.index")
    public LayuiTableDataListVO list(HttpServletRequest request, @Param("key") String key) {
        try (Jedis jedis = pool.getResource()) {
            key = Strings.sNull(key);
            if (Strings.isBlank(key)) {
                key = "*";
            }
            List<RedisVO> redisVos = new ArrayList<>();
            List<String> binaryDataList = Arrays.asList("dict-Cache");
            for (String redisKey : jedis.keys(Cons.REDIS_KEY_PREFIX.concat(key))) {
                if (binaryDataList.stream().anyMatch(s -> redisKey.indexOf(s) > -1)) {
                    redisVos.add(new RedisVO(redisKey, "二进制数据，不提供查看", jedis.ttl(redisKey)));
                } else {
                    redisVos.add(new RedisVO(redisKey, jedis.get(redisKey), jedis.ttl(redisKey)));
                }
            }
            return LayuiTableDataListVO.pageByData(request, redisVos, redisVos.size());
        }
    }

    /**
     * 修改
     */
    @GET
    @POST
    @At("/edit")
    @Ok("json")
    @RequiresPermissions("sysRedis.edit")
    @AutoCreateMenuAuth(name = "redis缓存编辑", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-eye", parentPermission = "sysRedis.index")
    public AjaxResult edit(@Param("::") RedisVO redisVO) {
        if (redisService.exists(redisVO.getKey())) {
            redisService.set(redisVO.getKey(), redisVO.getValue(), "XX", Cons.REDIS_EXPX, redisVO.getTtl());
        } else {
            redisService.set(redisVO.getKey(), redisVO.getValue(), "NX", Cons.REDIS_EXPX, redisVO.getTtl());
        }
        return AjaxResult.sucessMsg("修改成功");
    }

    /**
     * 删除
     */
    @GET
    @POST
    @At("/del")
    @Ok("json")
    @RequiresPermissions("sysRedis.del")
    @AutoCreateMenuAuth(name = "redis缓存删除", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-eye", parentPermission = "sysRedis.index")
    public AjaxResult del(@Param("keys") String[] keys) {
        redisService.del(keys);
        return AjaxResult.sucessMsg("删除成功");
    }
}
