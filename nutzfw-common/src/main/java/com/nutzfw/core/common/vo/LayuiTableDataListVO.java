package com.nutzfw.core.common.vo;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川
 * @date 2017/9/28  18:20
 * 描述此类： LayuiTable前台传递对象模型
 */
@SuppressWarnings("unchecked")
public class LayuiTableDataListVO<T> {

    /**
     * 防止前台传递大查询量请求，造成数据库效率减慢
     */
    public static final int PAGE_SIZE_MAX = 1000;
    private static final Log log = Logs.get();
    /**
     * 成功的状态码，默认：0
     **/
    private int code = 0;
    /**
     * 错误消息
     **/
    private String msg = "";
    /***
     * 返回结果数
     */
    private int count = 0;
    /**
     * 返回数据
     */
    private List<T> data;
    /**
     * 自定义状态（不是插件必须的）
     **/
    private int status = 0;
    /**
     * 起始页
     **/
    private int pageNumber = 0;
    /**
     * 每页查询数量
     **/
    private int pageSize = 10;


    public LayuiTableDataListVO(Integer pageNumber, int pageSize, int recordCount, List<T> list) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.count = recordCount;
        this.data = list;
    }

    public LayuiTableDataListVO() {

    }

    private LayuiTableDataListVO(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public LayuiTableDataListVO(int code, String msg, int count, List<T> data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public LayuiTableDataListVO(int code, int count, List<T> data) {
        this.code = code;
        this.count = count;
        this.data = data;
    }

    public static <T> LayuiTableDataListVO pageByData(HttpServletRequest request, List<T> list, int count) {
        LayuiTableDataListVO vo = get(request);
        int start = vo.getPageNumber() == 1 ? 0 : (vo.getPageNumber() - 1) * vo.getPageSize();
        int end = start + vo.getPageSize();
        end = end > list.size() ? list.size() : end;
        List<T> datas = new ArrayList<>(vo.getPageSize());
        for (int i = start; i < end; i++) {
            datas.add(list.get(i));
        }
        vo.setData(datas);
        vo.setCount(count);
        return vo;
    }

    public static <T> LayuiTableDataListVO pageByData(List<T> list, int count) {
        LayuiTableDataListVO vo = new LayuiTableDataListVO();
        vo.setData(list);
        vo.setCount(count);
        return vo;
    }

    public static <T> LayuiTableDataListVO pageByData(List<T> list, long count) {
        LayuiTableDataListVO vo = new LayuiTableDataListVO();
        vo.setData(list);
        vo.setCount(count);
        return vo;
    }

    public static LayuiTableDataListVO error(String msg, Object... objects) {
        LayuiTableDataListVO vo = new LayuiTableDataListVO();
        vo.setMsg(MessageFormat.format(msg, objects));
        vo.setCode(500);
        return vo;
    }

    public static LayuiTableDataListVO get(HttpServletRequest req) {
        int pageNumber = 1;
        int pageSize = 10;
        try {
            pageNumber = Integer.parseInt(Strings.safeToString(req.getParameter("page"), "1"));
            pageSize = Integer.parseInt(Strings.safeToString(req.getParameter("limit"), "10"));
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            if (pageSize <= 0) {
                pageSize = 10;
            }
            if (pageSize >= PAGE_SIZE_MAX) {
                //防止前台传递大查询量请求，造成数据库效率减慢
                pageSize = PAGE_SIZE_MAX;
            }
        } catch (Exception e) {
            log.warn(e);
        }
        return new LayuiTableDataListVO(pageSize, pageNumber);
    }

    /**
     * 不分页
     *
     * @param list
     * @return
     */
    public static <T> LayuiTableDataListVO allData(List<T> list) {
        return new LayuiTableDataListVO(0, list.size(), list);
    }

    public static LayuiTableDataListVO noData() {
        return new LayuiTableDataListVO(0, 0, new ArrayList());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCount(long count) {
        this.count = (int) count;
    }

    public List<T> getData() {
        if (data == null) {
            this.data = new ArrayList<>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getFirstResult() {
        return this.getPageSize() * (this.getPageNumber() - 1);
    }

    @Override
    public String toString() {
        return "LayuiTableDataListVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", count=" + count +
                ", data=" + data +
                ", status=" + status +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
