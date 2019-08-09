package com.nutzfw.modules.sso.vo;


import org.nutz.json.JsonField;

/**
 * @author 黄川 306955302@qq.com
 * @date: 2018/5/25
 * 描述此类：
 */
public class SsoResultVO {
    /**
     * 状态
     */
    @JsonField("Success")
    private Boolean Success;
    /**
     * 错误消息
     */
    @JsonField("ErrorMessage")
    private String ErrorMessage;
    /**
     * 账号
     */
    @JsonField("UserName")
    private String UserName;
    /**
     * 身份证号码
     */
    @JsonField("UserIdentity")
    private String UserIdentity;

    public SsoResultVO() {
    }

    public SsoResultVO(boolean success, String errorMessage) {
        Success = success;
        ErrorMessage = errorMessage;
    }

    public SsoResultVO(boolean success, String userName, String userIdentity) {
        Success = success;
        UserName = userName;
        UserIdentity = userIdentity;
    }

    public static SsoResultVO sucess(String userName, String userIdentity) {
        return new SsoResultVO(true, userName, userIdentity);
    }

    public static SsoResultVO error(String errorMessage) {
        return new SsoResultVO(false, errorMessage);
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserIdentity() {
        return UserIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        UserIdentity = userIdentity;
    }
}
