/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.websocket.handler;

import com.nutzfw.core.common.vo.SocketMsgVO;
import com.nutzfw.modules.monitor.websocket.LogsWsSessionsEndpoint;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/1
 */
public class TailLogsWsHandler extends SimpleWsHandler {

    private static final String  UTF8    = Encoding.UTF8;
    private static final Log     log     = Logs.get();
    private              Process process = null;
    private              String  command;
    private              String  level;
    private              String  charSet = UTF8;

    public TailLogsWsHandler(String command, String level) {
        this.command = command;
        this.level = level;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void depose() {
        super.depose();
        stop(NutMap.NEW());
    }

    /**
     * 关闭
     */
    public void stop(NutMap req) {
        if (process != null) {
            process.destroy();
        }
    }

    /**
     * 开始监控
     */
    public void start(NutMap req) {
        String reqCharSet = req.getString("charSet", UTF8);
        if (Strings.isNotBlank(reqCharSet)) {
            charSet = reqCharSet;
        }
        stop(req);
        if (process == null || !process.isAlive()) {
            try {
                process = Runtime.getRuntime().exec(command);
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charSet));
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String finalLine = line;
                        ((LogsWsSessionsEndpoint) endpoint).getSessions().forEach((sessionId, se) ->
                                endpoint.sendJson(sessionId,
                                        SocketMsgVO.builder()
                                                .action(level)
                                                .ok(true)
                                                .charSet(charSet)
                                                .data(finalLine))
                        );
                    } catch (Exception e) {
                        //忽略错误
                    }
                }
            } catch (IOException e) {
                log.error(e);
                stop(req);
            }
        }
    }


}
