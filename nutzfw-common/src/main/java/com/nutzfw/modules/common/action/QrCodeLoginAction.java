/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.vo.QrLoginVO;
import io.swagger.annotations.*;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.qrcode.QRCode;
import org.nutz.qrcode.QRCodeFormat;

import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/25  19:30
 * 描述此类：
 */
@IocBean
@At("/qrcode")
@Api("/qrcode")
public class QrCodeLoginAction extends BaseAction {

    public static final String QRCODE_LOGIN_PREFIX = RedisHelpper.buildRediskey("QRCODE_LOGIN_PREFIX.");

    @Inject
    RedisHelpper redisHelpper;

    /**
     * 扫描登录的二维码
     *
     * @param size
     * @return
     */
    @At("/QrCodeLoginImg")
    @Ok("raw:png")
    public BufferedImage qrCodeLoginImg(@Param("size") int size) {
        QRCodeFormat format = QRCodeFormat.NEW();
        format.setSize(size);
        format.setEncode(Encoding.UTF8);
        format.setErrorCorrectionLevel('H');
        format.setForeGroundColor("#2F4F4F");
        format.setBackGroundColor("#FFFFFF");
        format.setImageFormat("jpg");
        format.setMargin(2);
        String key = Mvcs.getHttpSession().getId();
        String redisKey = QRCODE_LOGIN_PREFIX + key;
        QrLoginVO qrLoginVO = QrLoginVO.create();
        redisHelpper.setJsonString(redisKey, qrLoginVO, 90);
        StringBuffer urlSb = Mvcs.getReq().getRequestURL();
        String url = urlSb.toString().replace(Mvcs.getReq().getServletPath(), "") + "/qrcode/downloadApp?";
        return QRCode.toQRCode(url + key + "." + R.UU16(), format);
    }

    /**
     * 取得下载APP的二维码
     *
     * @return
     */
    @At("/app")
    @Ok("raw:png")
    public BufferedImage app() {
        QRCodeFormat format = QRCodeFormat.NEW();
        format.setSize(150);
        format.setEncode(Encoding.UTF8);
        format.setErrorCorrectionLevel('H');
        format.setForeGroundColor("#2F4F4F");
        format.setBackGroundColor("#FFFFFF");
        format.setImageFormat("jpg");
        format.setMargin(2);
        StringBuffer urlSb = Mvcs.getReq().getRequestURL();
        String url = urlSb.toString().replace(Mvcs.getReq().getServletPath(), "") + "/qrcode/downloadApp";
        return QRCode.toQRCode(url, format);
    }

    /**
     * 进入下载APP的页面
     *
     * @return
     */
    @At("/downloadApp")
    @Ok("btl:WEB-INF/view/tool/downloadApp.html")
    public NutMap downloadApp() {
        return NutMap.NEW();
    }


    /**
     * 发送扫描数据至服务器
     *
     * @return
     */
    @At("/scanningSucess")
    @Ok("json")
    @POST
    @ApiOperation(value = "发送扫描数据至服务器", nickname = "/scanningSucess", tags = "扫描登录", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", paramType = "query", value = "扫描数据", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{ok:true,msg:'扫描成功'}"),
    })
    public AjaxResult scanningSucess(@Param("token") String token) {
        try {
            String key = token.split("\\?")[1];
            key = key.split("\\.")[0];
            String redisKey = QRCODE_LOGIN_PREFIX + key;
            UserAccount userAccount = getSessionUserAccount();
            if (redisHelpper.exists(redisKey) && userAccount != null) {
                QrLoginVO qrLoginVO = redisHelpper.getByJson(redisKey, QrLoginVO.class);
                qrLoginVO.setScanning(true);
                qrLoginVO.setUserName(userAccount.getUserName());
                redisHelpper.setJsonString(redisKey, qrLoginVO, 90);
                return AjaxResult.sucess("扫描成功");
            } else {
                return AjaxResult.error("二维码已过期或登录已超时！");
            }
        } catch (Exception e) {
            return AjaxResult.error("参数错误！");
        }
    }

    /**
     * 确认登录
     *
     * @return
     */
    @At("/confirmLogin")
    @Ok("json")
    @POST
    @ApiOperation(value = "确认登录", nickname = "/confirmLogin", tags = "扫描登录", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", paramType = "query", value = "扫描数据", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{ok:true,msg:'确认成功'}"),
    })
    public AjaxResult qrCodeLoginImg(@Param("token") String token) {
        try {
            String key = token.split("\\?")[1];
            key = key.split("\\.")[0];
            String redisKey = QRCODE_LOGIN_PREFIX + key;
            if (redisHelpper.exists(redisKey)) {
                QrLoginVO qrLoginVO = redisHelpper.getByJson(redisKey, QrLoginVO.class);
                if (qrLoginVO.getScanning()) {
                    qrLoginVO.setConfirmLogin(true);
                    redisHelpper.setJsonString(redisKey, qrLoginVO, 90);
                    return AjaxResult.sucess("确认成功！");
                } else {
                    return AjaxResult.error("请先扫码！");
                }
            } else {
                return AjaxResult.error("二维码已过期！");
            }
        } catch (Exception e) {
            return AjaxResult.error("参数错误！");
        }
    }
}
