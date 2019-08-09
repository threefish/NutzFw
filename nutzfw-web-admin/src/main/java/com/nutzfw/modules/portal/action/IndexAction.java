package com.nutzfw.modules.portal.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.portal.biz.IndexBiz;
import com.nutzfw.modules.portal.entity.MsgNotice;
import com.nutzfw.modules.sys.entity.Dict;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

/**
 * @author 叶世游
 * @date 2018/6/20 14:03
 * @description 首页
 */
@IocBean
@At("/index")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class IndexAction extends BaseAction {
    @Inject
    IndexBiz indexBiz;


    @POST
    @At("/userinfo")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    public AjaxResult userinfo() {
        try {
            UserAccount account = getSessionUserAccount();
            Dict dict = indexBiz.getDict(account.getGender(), "sys_user_sex");
            NutMap nutMap = new NutMap();
            if (dict != null) {
                nutMap.setv("sex", dict.getLable());
            } else {
                nutMap.setv("sex", "无数据");
            }
            NutMap userInfo = new NutMap();
            userInfo.setv("realName", account.getRealName());
            userInfo.setv("gender", nutMap.get("sex"));
            userInfo.setv("deptName", account.getDept() != null ? account.getDept().getName() : "");
            userInfo.setv("phone", account.getPhone());
            userInfo.setv("mail", account.getMail());
            userInfo.setv("userName", account.getUserName());
            nutMap.setv("userinfo", userInfo);
            return AjaxResult.sucess(nutMap, "操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("操作失败");
        }
    }

    @At("/msgDetail")
    @Ok("btl:WEB-INF/view/portal/msgDetail.html")
    public MsgNotice detail(@Param("mid") String mid) {
        return indexBiz.getMsgNotice(mid);
    }

    /**
     * 获取当前用户可以展示的提醒消息
     *
     * @author 叶世游 yeshiyou@nutzfw.com
     * @date 2018年06月20日 14时51分15秒
     */
    @POST
    @At("/msgNotices")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    public AjaxResult msgNotices() {
        try {
            UserAccount account = getSessionUserAccount();
            return indexBiz.msgNotices(account);
        } catch (Exception e) {
            return AjaxResult.error("操作失败");
        }
    }

    /**
     * 获取当前用户可以展示的统计信息
     *
     * @author 叶世游 yeshiyou@nutzfw.com
     * @date 2018年06月20日 14时51分15秒
     */
    @POST
    @At("/statisticsConfigures")
    @Ok("json:{locked:'delFlag|sqlStr|opAt|opBy|sort|params',nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    public AjaxResult statisticsConfigures() {
        try {
            UserAccount account = getSessionUserAccount();
            return indexBiz.statisticsConfigures(account);
        } catch (Exception e) {
            return AjaxResult.error("操作失败");
        }
    }

    /**
     * 保存用户首页排序
     *
     * @param sort
     * @return
     */
    @POST
    @At("/savePageSort")
    @Ok("json")
    public AjaxResult savePageSort(@Param("sort") String sort) {
        try {
            getSessionUserAccount().setIndexSort(sort);
            UserAccount account = indexBiz.getUserInfo(getSessionUserAccount().getId());
            account.setIndexSort(sort);
            indexBiz.updateUser(account);
            return AjaxResult.sucess("保存排序成功!");
        } catch (Exception e) {
            return AjaxResult.error("操作失败");
        }
    }

    /**
     * 获取当前用户可以展示的快捷操作
     *
     * @author 叶世游 yeshiyou@nutzfw.com
     * @date 2018年06月20日 14时51分15秒
     */
    @POST
    @At("/quickLinks")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    public AjaxResult quickLinks() {
        try {
            UserAccount account = getSessionUserAccount();
            return indexBiz.quickLinks(account);
        } catch (Exception e) {
            return AjaxResult.error("操作失败");
        }
    }

    /**
     * 消息详情
     *
     * @param mid
     * @return
     */
    @POST
    @At("/msgDetail")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    public LayuiTableDataListVO quickLinks(@Param("mid") String mid,
                                           @Param("pageNum") int pageNum,
                                           @Param("pageSize") int pageSize) {
        return indexBiz.msgDetail(mid, pageNum, pageSize);

    }

}
