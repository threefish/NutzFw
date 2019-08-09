package com.nutzfw.modules.tabledata.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import org.nutz.lang.util.NutMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/3
 * 描述此类：
 */
public interface UserDataReviewBiz {


    NutMap showReviewData(String id);

    AjaxResult agreeReview(String id, String reviewOpinion);
}
