/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 */
@Slf4j
public class SerializeUtil {
    /**
     * 序列化
     *
     * @param object
     * @return
     */
    public static byte[] serizlize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("无法序列化数据", e);
            throw new RuntimeException("无法序列化数据");
        }
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    public static Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            return ois.readObject();
        } catch (Exception e) {
            log.error("无法反序列化数据", e);
            throw new RuntimeException("无法反序列化数据");
        }
    }

    /**
     * 反序列化
     *
     * @param path
     * @return
     */
    public static Object deserialize(String path) {
        return deserialize(new File(path));
    }

    /**
     * 反序列化
     *
     * @param file
     * @return
     */
    public static Object deserialize(File file) {
        try (FileInputStream fis = FileUtils.openInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return ois.readObject();
        } catch (Exception e) {
            log.error("无法反序列化数据", e);
            throw new RuntimeException("无法反序列化数据");
        }
    }
}