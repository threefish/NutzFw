/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:29:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

import com.nutzfw.core.common.util.excel.ExcelUtils;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ExcelExport {

    private static final String PATH = "C:\\Users\\HC\\Documents\\temp\\";

    @Test
    public void exportTemplate() {
        Context ctx = Lang.context();
        /*组装数据*/
        List<NutMap> nutMaps = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            NutMap temp = new NutMap();
            temp.put("测试A", "测试A=" + i);
            temp.put("测试D", "测试D=123" + i);
            temp.put("测试E", "测试E=123" + i);
            if (i == 3 || i == 5) {
                temp.put("测试B", R.UU16());
            } else {
                temp.put("测试B", new java.util.Date());
                temp.put("测试D", "测试E=1234");
            }
            temp.put("测试C", new Timestamp(System.currentTimeMillis()));

            nutMaps.add(temp);
        }
        NutMap mapdata = new NutMap();
        mapdata.put("A", "aaaa");
        mapdata.put("B", "bbb");
        mapdata.put("C", "ccc");
        //arr对象
        ctx.set("datalist", nutMaps);
        //这是个map对象
        ctx.set("m", mapdata);
        //普通数据
        ctx.set("A", "我是A");
        ctx.set("B", "我是B");
        ctx.set("C", "我是C");
        //模版文件
        String filePath = ExcelExport.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File templateFile = new File(filePath + File.separator + "template1.xlsx");
        //导出文件
        File templateOutFile = new File(PATH + "template1_out.xlsx");
        try {
            ExcelUtils.renderSheetMacro(ctx, "Sheet1", templateFile, templateOutFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("生成文件地址：" + templateOutFile.getAbsolutePath());
        }
    }

    @Test
    public void exportTemplate2() {
        Context ctx = Lang.context();
        /*组装数据*/
        List<Object> datalist = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Department department = Department.builder().name("部门哦").build();
            datalist.add(UserAccount.builder().mail(i + "@qq.com").realName("张三" + i).dept(department).build());
        }
        ctx.set("datalist", datalist);

        List<NutMap> datalist2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datalist2.add(NutMap.NEW().setv("张三", "呵呵" + i).setv("XX", i));
        }
        ctx.set("datalist2", datalist2);
        ctx.set("A", "XXXA");
        ctx.set("B", "XXXB");
        File templateFile = Paths.get(PATH, "template2.xlsx").toFile();
        File templateOutFile = Paths.get(PATH, "template2_out.xlsx").toFile();
        try {
            ExcelUtils.renderSheetMacro(ctx, "Sheet2", templateFile, templateOutFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("生成文件地址：" + templateOutFile.getAbsolutePath());
        }
    }

}