/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.cons;

import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.Options;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/25  18:32
 * 描述此类：带final的全部不允许修改
 */
public class Cons {

    /**
     * 权限认证信息
     */
    public static final String SHIRO_AUTHORIZATION_INFO       = "shiro_authorization_info";
    /**
     * 用户session
     */
    public static final String SESSION_USER_KEY               = "me";
    /**
     * 用户角色session
     */
    public static final String SESSION_ROLES_KEY              = "roles";
    /**
     * 用户有权限管理的人员 userName
     */
    public static final String SESSION_MANAGER_USER_NAMES_KEY = "manager_usernames";
    /**
     * 登录验证码
     */
    public static final String CAPTCHA_KEY                    = "CAPTCHA_KEY";

    /**
     * 默认赋予角色
     */
    public static final String SESSION_USER_ROLE      = "管理员";
    public static final String SESSION_USER_ROLE_CODE = "superadmin";
    /**
     * 菜单
     */
    public static final String SESSION_MENUS          = "sessionMenus";

    /**
     * 禁止修改 error_info
     */
    public static final String ERROR_INFO = "error_info";

    /**
     * 错误页面
     */
    public static final String ERROR_PAGE = "/error/customError.html";

    /**
     * redis key 的统一前缀
     */
    public static final String REDIS_KEY_PREFIX = "NutzFw:";

    /**
     * redis 统一过期时间单位 EX代表秒，PX代表毫秒
     */
    public static final String REDIS_EXPX = "EX";

    /**
     * 系统管理员
     */
    public static final String  ADMIN                   = "admin";
    public static final String  USER_ACCOUNT_TABLE_NAME = UserAccount.class.getAnnotation(Table.class).value();
    /***
     * UEDITOR 富文本编辑工具栏配置
     */
    public final static String  UE_ALL_TOOL             = "['source','undo', 'redo', 'bold', 'indent', 'italic', 'underline', 'strikethrough', 'subscript', 'fontborder', 'superscript', 'formatmatch', 'blockquote', 'pasteplain', 'selectall', 'horizontal', 'removeformat', 'unlink', 'cleardoc', 'fontfamily', 'fontsize', 'paragraph', 'edittable', 'edittd', 'link', 'emotion', 'spechars', 'searchreplace', 'map', 'justifyleft','justifyright', 'justifycenter', 'justifyjustify', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'fullscreen', 'directionalityltr', 'directionalityrtl', 'pagebreak',  'imagecenter', 'lineheight', 'edittip ', 'background', 'inserttable', 'print', 'preview', 'help']";
    /**
     * 系统默认密码
     */
    public static       String  DEFAULT_PASSWORD        = "666666";
    /**
     * 缓存系统配置信息
     */
    public static       Options optionsCach;


}
