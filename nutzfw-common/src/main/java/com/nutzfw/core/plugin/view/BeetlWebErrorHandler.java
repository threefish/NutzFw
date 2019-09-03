package com.nutzfw.core.plugin.view;

import com.nutzfw.core.common.util.StringUtil;
import org.beetl.core.ErrorHandler;
import org.beetl.core.Resource;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/2/18
 */
public class BeetlWebErrorHandler implements ErrorHandler {

    private static final Log log = Logs.get();

    private static final String HTML = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><meta name=\"robots\" content=\"noindex,nofollow\"><title>500 - 系统错误</title><style>body{font-size: 14px;font-family: 'helvetica neue',tahoma,arial,'hiragino sans gb','microsoft yahei','Simsun',sans-serif; background-color:#fff; color:#808080;} .wrap{margin:0 auto;width:800px;margin-top: 10%;} td{text-align:left; padding:2px 10px;}td.header{font-size:22px; padding-bottom:10px; color:#000;} td.check-info{padding-top:20px;}  a{color:#328ce5; text-decoration:none;} a:hover{text-decoration:underline;} pre{overflow: auto;width: 800px;max-height: 200px;margin: auto;border: 1px #cdcdcd solid;background: #f5f0f0;padding: 10px;}</style></head><body><div class=\"wrap\"><table><tr><td rowspan=\"5\" style=\"\"><img src=\"data:image/png;base64,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\"></td>" +
            "<td class=\"header\">很抱歉！当前页面出错了！</td></tr>" +
            "[INFO]" +
            "<tr><td>如果刷新页面没能解决问题，你可以联系网站管理员进行反馈。</td></tr>" +
            "<tr><td class=\"check-info\">或者，你也可以<a href=\"/\">跳转到首页</a></td></tr></table></div></body></html>";

    private static final String INFO = "<tr><td>错误信息：<pre>[TITLE]</pre></td></tr><tr><td>详情：<pre>[MSG]</pre></td></tr>";

    @Override
    public void processExcption(BeetlException ex, Writer writer) {

        ErrorInfo error = new ErrorInfo(ex);
        StringBuilder msg = new StringBuilder();
        StringBuilder title = new StringBuilder();
        if (error.getErrorCode().equals(BeetlException.CLIENT_IO_ERROR_ERROR)) {
            //不输出详细提示信息
            if (!ex.gt.getConf().isIgnoreClientIOError()) {
                title.append("客户端IO异常:" + getResourceName(ex.resource.getId()) + ":" + error.getMsg()).append("\n");
                if (ex.getCause() != null) {
                    this.render(writer, title.toString(), ex.getMessage());
                }
                return;
            }
        }
        int line = error.getErrorTokenLine();
        StringBuilder sb = new StringBuilder(">>").append(this.getDateTime()).append(":").append(error.getType())
                .append(":").append(error.getErrorTokenText()).append(" 位于").append(line != 0 ? line + "行" : "").append(" 资源:")
                .append(getResourceName(ex.resource.getId()));
        if (error.getErrorCode().equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
            if (error.getMsg() != null) {
                sb.append(error.getMsg()).append("\n");
            }
            title.append(ex.gt.getResourceLoader().getInfo()).append("\n");
            title.append(sb.toString()).append("\n");
        } else if (ex.getMessage() != null) {
            title.append(ex.getMessage()).append("\n");
        }
        try {
            Resource res = ex.resource;
            //显示前后三行的内容
            int[] range = this.getRange(line);
            String content = res.getContent(range[0], range[1]);
            if (content != null && ex.cr != null) {
                String[] strs = content.split(ex.cr);
                int lineNumber = range[0];
                for (int i = 0; i < strs.length; i++) {
                    msg.append("" + lineNumber);
                    msg.append("|");
                    msg.append(strs[i]).append("\n");
                    lineNumber++;
                }
            }
        } catch (Exception e) {
            //ingore
        }
        if (error.getResourceCallStack() != null && error.hasCallStack()) {
            title.append("  ========================").append("\n");
            title.append("  调用栈:").append("\n");
            for (int i = 0; i < error.getResourceCallStack().size(); i++) {
                title.append("  " + error.getResourceCallStack().get(i) + " 行：" + error.getTokenCallStack().get(i).line).append("\n");
            }
        }
        if(Strings.isBlank(msg)){
            msg.append(StringUtil.throwableToString(ex));
        }
        this.render(writer, title.toString(), msg.toString());
        log.error(ex);
        try {
            writer.flush();
        } catch (IOException e) {

        }
    }

    public void render(Writer writer, String title, String msg) {
        try {
            //在进入这里之前 writer 已经写入一些信息了，需要重置下输出流
            if (!Mvcs.getResp().isCommitted()) {
                Mvcs.getResp().reset();
            }
            if (BeetlViewMaker.isDev) {
                writer.write(HTML.replace("[INFO]", INFO.replace("[TITLE]", Strings.escapeHtml(title)).replace("[MSG]", Strings.escapeHtml(msg))));
            } else {
                writer.write(HTML.replace("[INFO]", ""));
            }
            log.debugf("模版错误：%s \n 错误详情：\n%s", title, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected String getResourceName(String resourceId) {
        return resourceId;
    }


    protected int[] getRange(int line) {
        int startLine = 0;
        int endLine = 0;
        if (line > 3) {
            startLine = line - 3;
        } else {
            startLine = 1;
        }
        endLine = startLine + 6;
        return new int[]{startLine, endLine};
    }

    protected String getDateTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(date);
    }


}
