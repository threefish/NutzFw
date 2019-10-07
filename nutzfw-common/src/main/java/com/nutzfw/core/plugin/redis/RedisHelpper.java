/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.redis;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.SerializeUtil;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/29
 */
@IocBean
public class RedisHelpper {
    /**
     * 默认缓存有效期1小时
     */
    public static final int DEFAULT_SECOND = 60 * 60 * 1;

    @Inject
    RedisService redisService;

    /**
     * 生成带前缀的 rediskey
     *
     * @param rediskeys
     * @return
     */
    public static String buildRediskey(String... rediskeys) {
        String key = Cons.REDIS_KEY_PREFIX.concat(rediskeys[0]);
        for (int i = 1; i < rediskeys.length; i++) {
            key = key.concat(rediskeys[i]);
        }
        return key;
    }

    /**
     * 存入对象采用json序列化
     *
     * @param rediskey
     * @param data
     * @param second
     * @param <T>
     */
    public <T> void setJsonString(String rediskey, T data, int second) {
        if (redisService.exists(rediskey)) {
            setXXString(rediskey, Json.toJson(data, JsonFormat.compact()), second);
        } else {
            setNXString(rediskey, Json.toJson(data, JsonFormat.compact()), second);
        }
    }

    /**
     * 将字符串放入redis，不管是否存在
     *
     * @param rediskey
     * @param data
     * @param second
     */
    public void setString(String rediskey, String data, int second) {
        if (redisService.exists(rediskey)) {
            setXXString(rediskey, data, second);
        } else {
            setNXString(rediskey, data, second);
        }
    }

    /**
     * 将字符串放入redis，key存在时才能放入
     *
     * @param rediskey
     * @param data
     * @param second
     */
    public void setXXString(String rediskey, String data, int second) {
        redisService.set(rediskey, data, "XX", Cons.REDIS_EXPX, second);
    }

    /**
     * 将字符串放入redis，key不存在时才能放入
     *
     * @param rediskey
     * @param data
     * @param second
     */
    public void setNXString(String rediskey, String data, int second) {
        redisService.set(rediskey, data, "NX", Cons.REDIS_EXPX, second);
    }

    /**
     * 将可序列化对象放入redis，key不存在时才能放入
     *
     * @param rediskey
     * @param data
     * @param second
     */
    public void setNXSerializable(String rediskey, Object data, int second) {
        redisService.set(rediskey.getBytes(), SerializeUtil.serizlize(data), "NX".getBytes(), Cons.REDIS_EXPX.getBytes(), second);
    }

    /**
     * 将可序列化对象放入redis，key存在时才能放入
     *
     * @param rediskey
     * @param data
     * @param second
     */
    public void setXXSerializable(String rediskey, Object data, int second) {
        redisService.set(rediskey.getBytes(), SerializeUtil.serizlize(data), "XX".getBytes(), Cons.REDIS_EXPX.getBytes(), second);
    }

    /**
     * rediskey 是否存在
     *
     * @param rediskey
     * @return
     */
    public boolean exists(String rediskey) {
        return redisService.exists(rediskey);
    }

    /**
     * 取得json序列化对象
     *
     * @param rediskey
     * @param <T>
     * @return
     */
    public <T> T getByJson(String rediskey, Class<T> klass) {
        return Json.fromJson(klass, redisService.get(rediskey));
    }

    /**
     * 取得序列化对象
     *
     * @param rediskey
     * @param <T>
     * @return
     */
    public <T> T getBySerializable(String rediskey) {
        return (T) SerializeUtil.deserialize(redisService.get(rediskey.getBytes()));
    }

    /**
     * 取得字符串
     *
     * @param rediskey
     * @return
     */
    public String get(String rediskey) {
        return redisService.get(rediskey);
    }

    /**
     * 删除指定key value
     *
     * @param key
     */
    public void del(String key) {
        redisService.del(key);
    }

    public void del(String... keys) {
        redisService.del(keys);
    }

    /**
     * 正则取得keys
     * @param key
     * @return
     */
    public Set<String> keys(String key) {
        return redisService.keys(key);
    }
}
