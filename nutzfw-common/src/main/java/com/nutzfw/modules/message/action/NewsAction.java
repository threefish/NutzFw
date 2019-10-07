/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.message.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.message.entity.News;
import com.nutzfw.modules.message.service.NewsReceiveDeptService;
import com.nutzfw.modules.message.service.NewsReceiveRoleService;
import com.nutzfw.modules.message.service.NewsService;
import com.nutzfw.modules.sys.biz.DictBiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年08月01日
 * 新闻
 */
@IocBean
@At("/News")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
@Slf4j
public class NewsAction extends BaseAction {

    @Inject
    NewsService newsService;

    @Inject
    DictBiz                dictBiz;
    @Inject
    NewsReceiveDeptService newsReceiveDeptService;
    @Inject
    NewsReceiveRoleService newsReceiveRoleService;

    /**
     * 管理页面
     *
     * @return
     */
    @At("/index")
    @Ok("btl:WEB-INF/view/message/news/index.html")
    @RequiresPermissions("News.index")
    @AutoCreateMenuAuth(name = "新闻管理", icon = "fa-cogs")
    public NutMap index() {
        NutMap data = NutMap.NEW();
        data.put("sys_news_category", dictBiz.getDictEnumsJson("sys_news_category"));
        data.put("sys_news_level", dictBiz.getDictEnumsJson("sys_news_level"));
        return data;
    }

    /**
     * 列表管理
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    @At("/list")
    @Ok("json:{dateFormat:'yyyy年MM月dd日',locked:'content',nullAsEmtry:true}")
    @RequiresPermissions("News.index")
    public LayuiTableDataListVO list(@Param("pageNum") int pageNum,
                                     @Param("pageSize") int pageSize,
                                     @Param("key") String key
    ) {
        Cnd cnd = Cnd.where("opBy","=",getSessionUserAccount().getUserid());
        if (Strings.isNotBlank(key)) {
            cnd.and("name", "like", "%" + key + "%");
        }
        cnd.desc("opAt");
        return newsService.listPage(pageNum, pageSize, cnd);
    }


    /**
     * 新增、编辑保存页面
     *
     * @param uuid
     * @return
     */
    @At("/edit")
    @Ok("btl:WEB-INF/view/message/news/edit.html")
    @RequiresPermissions("News.index.edit")
    @AutoCreateMenuAuth(name = "新增/编辑新闻", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "News.index")
    public NutMap edit(@Param("uuid") String uuid) {
        return NutMap.NEW().setv("uuid", uuid).setv("fromDataEdit", true);
    }

    /**
     * 新增、编辑保存页面
     *
     * @param uuid
     * @return
     */
    @At("/view")
    @Ok("btl:WEB-INF/view/message/news/edit.html")
    @RequiresPermissions("News.index.edit")
    @AutoCreateMenuAuth(name = "查看详情", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "News.index")
    public NutMap view(@Param("uuid") String uuid) {
        return NutMap.NEW().setv("uuid", uuid).setv("fromDataEdit", false);
    }

    /**
     * 加载详情
     *
     * @param uuid
     * @return
     */
    @At("/details")
    @Ok("json:{ignoreNull:false,dateFormat:'yyyy-MM-dd',nullListAsEmpty:true}")
    @RequiresPermissions("News.index.edit")
    public AjaxResult details(@Param("uuid") String uuid) {
        News news = newsService.fetch(uuid);
        newsService.fetchLinks(news, null);
        return AjaxResult.sucess(news);
    }

    /**
     * 加载详情
     *
     * @param uuid
     * @return
     */
    @At("/lookNews/?")
    @Ok("btl:WEB-INF/view/message/news/lookNews.html")
    public NutMap lookNews(String uuid) {
        News news = newsService.fetch(uuid);
        newsService.fetchLinks(news, null);
        boolean canLook = false;
        if (CollectionUtils.isNotEmpty(news.getToRoles())) {
            Set<String> sessionRoleIds = getSessionRoleIds();
            canLook = news.getToRoles().stream().filter(s -> sessionRoleIds.contains(s.getRoleId())).findAny().isPresent();
        }
        if (canLook == false && CollectionUtils.isNotEmpty(news.getToDepts())) {
            canLook = news.getToDepts().stream().filter(s -> Objects.equals(s.getDeptId(), getSessionUserAccount().getDeptId())).findAny().isPresent();
        }
        return NutMap.NEW().setv("canLook", canLook).setv("news", news);
    }


    /**
     * 查看更多页面
     *
     * @return
     */
    @At("/lookMore")
    @Ok("btl:WEB-INF/view/message/news/lookMore.html")
    @RequiresPermissions("News.index")
    @AutoCreateMenuAuth(name = "新闻管理", icon = "fa-cogs")
    public NutMap lookMore() {
        NutMap data = NutMap.NEW();
        data.put("sys_news_category", dictBiz.getDictEnumsJson("sys_news_category"));
        data.put("sys_news_level", dictBiz.getDictEnumsJson("sys_news_level"));
        return data;
    }

    /**
     * 列表管理
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    @At("/lookMoreList")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日'}")
    public LayuiTableDataListVO lookMoreList(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("key") String key) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(key)) {
            cnd.and("name", "like", "%" + key + "%");
        }
        cnd.desc("opAt");
        return newsService.queryLookMoreList(getSessionUserAccount(), getSessionRoleIds(), pageNum, pageSize, key);
    }

    /**
     * 批量删除
     *
     * @param uuids
     * @return
     */
    @At("/del")
    @Ok("json")
    @RequiresPermissions("News.index.del")
    @AutoCreateMenuAuth(name = "批量删除", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "News.index")
    public AjaxResult del(@Param("::uuids") String[] uuids) {
        newsService.deleteByUUIDs(uuids);
        return AjaxResult.sucess("删除成功");
    }

    /**
     * 保存
     *
     * @param data
     * @return
     */
    @At("/save")
    @Ok("json")
    @POST
    @RequiresPermissions("News.index.edit")
    @Aop(TransAop.READ_UNCOMMITTED)
    public AjaxResult save(@Param("::fromData") News data, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        newsService.insertOrUpdate(data);
        newsReceiveDeptService.delete(Cnd.where("newsId", "=", data.getUuid()));
        newsReceiveRoleService.delete(Cnd.where("newsId", "=", data.getUuid()));
        if (CollectionUtils.isNotEmpty(data.getToDepts())) {
            data.getToDepts().forEach(item -> item.setNewsId(data.getUuid()));
            newsReceiveDeptService.insert(data.getToDepts());
        }
        if (CollectionUtils.isNotEmpty(data.getToRoles())) {
            data.getToRoles().forEach(item -> item.setNewsId(data.getUuid()));
            newsReceiveDeptService.insert(data.getToRoles());
        }
        return AjaxResult.sucessMsg("保存成功");
    }


    /**
     * 首页新闻查询
     *
     * @return
     */
    @At("/indexNewsList")
    @Ok("json")
    @POST
    public List<News> indexNewsList() {
        return newsService.queryIndexNewsList(getSessionUserAccount(), getSessionRoleIds());
    }

    /**
     * 首页推荐新闻查询
     *
     * @return
     */
    @At("/indexImgNewsList")
    @Ok("json")
    @POST
    public List<News> indexImgNewsList() {
        return newsService.queryIndexImgNewsList(getSessionUserAccount(), getSessionRoleIds());
    }
}
