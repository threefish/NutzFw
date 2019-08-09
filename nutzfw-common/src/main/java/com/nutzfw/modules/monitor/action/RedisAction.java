package com.nutzfw.modules.monitor.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.monitor.vo.RedisVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        key = Strings.sNull(key);
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            List<RedisVO> redisVos = new ArrayList<>();
            if (!"*".equals(key)) {
                key = "*" + key + "*";
            }
            for (String s : jedis.keys(key)) {
                if (!s.startsWith(CachingSessionDAO.ACTIVE_SESSION_CACHE_NAME)) {
                    redisVos.add(new RedisVO(s, jedis.get(s), jedis.ttl(s)));
                }
            }
            return LayuiTableDataListVO.pageByData(request, redisVos, redisVos.size());
        } finally {
            if (null != jedis) {
                // 释放资源还给连接池
                jedis.close();
            }
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
    public AjaxResult del(@Param("::") RedisVO redisVO) {
        redisService.del(redisVO.getKey());
        return AjaxResult.sucessMsg("删除成功");
    }
}
