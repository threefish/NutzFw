/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.Base64Tool;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.dto.FileZipDTO;
import com.nutzfw.modules.sys.entity.FileAttach;
import com.nutzfw.modules.sys.service.FileAttachService;
import io.swagger.annotations.*;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.DaoException;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2017/1/5 0005
 * 描述此类：附件相关
 */
@IocBean
@At("/File")
@Api("/File/")
public class FileAction extends BaseAction {

    public static final String            SYS_ATTACH_TYPE = "sys_attach_type";
    @Inject
    protected           FileAttachService fileAttachService;
    @Inject
    DictBiz            dictBiz;
    @Inject
    UserAccountService userAccountService;
    @Inject("java:$conf.get('attach.upload.maxFileSize')")
    private int    maxFileSize;
    @Inject("java:$conf.get('attach.extensions')")
    private String extensions;
    @Inject("java:$conf.get('attach.savePath')")
    private String parentPath;

    /**
     * 全局上传文件页面-通过open方式打开
     *
     * @param module
     */
    @At("/page")
    @GET
    @Ok("btl:WEB-INF/view/tool/upload.html")
    public void attachPage(@Param("module") String module) {
        setRequestAttribute("maxFileSize", maxFileSize);
        setRequestAttribute("maxFileSizeMsg", Strings.formatSizeForReadBy1024(maxFileSize));
        setRequestAttribute("module", module);
        setRequestAttribute("url", "/File/FileUploadact");
        setRequestAttribute("FILE_EXTENSIONS", extensions);
    }

    /**
     * 单文件上传到指定URL-通过open方式打开
     */
    @At("/singleUpload")
    @GET
    @Ok("btl:WEB-INF/view/tool/singleUploadVue.html")
    public NutMap singleUpload(boolean isMultiUpload, @Param("url") String url,
                               @Param("fileExtensions") String fileExtensions,
                               @Param("module") String module,
                               //已上传文件
                               @Param("uploadedIds") String[] uploadedIds,
                               @Param("fileType") String fileType,
                               //上传完成后自动执行ok事件
                               @Param("auto") boolean auto
    ) {
        NutMap data = new NutMap();
        final String defaultType = "file";
        data.put("maxFileSize", maxFileSize);
        data.put("maxFileSizeMsg", Strings.formatSizeForReadBy1024(maxFileSize));
        data.put("url", url);
        data.put("module", module);
        data.put("auto", auto);
        data.put("FILE_EXTENSIONS", "未设置具体格式无法上传文件");
        data.put("FILE_TYPE", "file");
        data.put("MIME_TYPES", "*/*");
        if (!isMultiUpload) {
            data.put("uploadedFile", NutMap.NEW());
            if (uploadedIds != null && uploadedIds.length > 0 && Strings.isNotBlank(uploadedIds[0])) {
                FileAttach attach = fileAttachService.fetch(Cnd.where("id", "=", uploadedIds[0]));
                if (attach != null) {
                    data.put("uploadedFile", NutMap.NEW().setv("name", attach.getFileName()).setv("id", attach.getId()).setv("ext", Files.getSuffixName(attach.getFileName())));
                }
            }
        } else {
            List<FileAttach> attaches = new ArrayList<>();
            if (uploadedIds != null && uploadedIds.length > 0) {
                attaches = fileAttachService.query(Cnd.where("id", "in", uploadedIds));
            }
            List<NutMap> uploadedList = new ArrayList<>();
            attaches.forEach(attach -> uploadedList.add(NutMap.NEW().setv("name", attach.getFileName()).setv("id", attach.getId()).setv("ext", Files.getSuffixName(attach.getFileName()))));
            if (uploadedList.size() == 0) {
                data.put("uploadedList", "[]");
            } else {
                data.put("uploadedList", Json.toJson(uploadedList, JsonFormat.compact()));
            }
        }
        if (!Strings.isEmpty(fileExtensions)) {
            data.put("FILE_EXTENSIONS", fileExtensions.replace("|", ","));
        } else if (defaultType.equals(fileType)) {
            data.put("FILE_EXTENSIONS", extensions);
        }

        if (!defaultType.equals(fileType)) {
            data.put("FILE_TYPE", "Images");
            data.put("MIME_TYPES", "image/*");
        }
        return data;
    }

    /**
     * 多 文件上传- 通过open方式打开
     *
     * @param url            上传地址
     * @param fileExtensions 上传类型
     * @param fileType       上传类型 （是文件还是 图片）
     * @return
     */
    @At("/multiUpload")
    @GET
    @Ok("btl:WEB-INF/view/tool/multiUpload.html")
    public NutMap multiUpload(@Param("url") String url,
                              @Param("fileExtensions") String fileExtensions,
                              @Param("module") String module,
                              @Param("uploadedIds") String[] uploadedIds,
                              @Param("fileType") String fileType,
                              @Param("maxSize") int maxSize) {
        return singleUpload(true, url, fileExtensions, module, uploadedIds, fileType, false).setv("maxSize", maxSize);
    }

    /**
     * 全局上传封面图片文件页面-通过open方式打开
     *
     * @param module
     */
    @At("/cutimg")
    @GET
    @Ok("btl:WEB-INF/view/tool/cutimg.html")
    public void cutimgindex(@Param("module") String module, @Param("ratio") double ratio) {
        setRequestAttribute("maxFileSize", 1024 * 1024);
        setRequestAttribute("maxFileSizeMsg", Strings.formatSizeForReadBy1024(1024 * 1024));
        setRequestAttribute("module", module);
        setRequestAttribute("ratio", ratio);
    }

    @At("/md5check")
    @POST
    @Ok("json")
    public AjaxResult md5check(@Param("md5") String md5) {
        FileAttach attach = fileAttachService.fetchByMd5(md5);
        if (attach != null) {
            return AjaxResult.sucess(attach.getId());
        } else {
            return AjaxResult.error("");
        }
    }

    @POST
    @Ok("json")
    @At("/FileUploadact")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:upload"})
    @ApiOperation(value = "附件上传", nickname = "FileUploadact", tags = "附件", httpMethod = "POST", consumes = "multipart/form-data", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", paramType = "form", value = "文件", dataType = "file", required = true),
            @ApiImplicitParam(name = "module", paramType = "form", value = "模块名", dataType = "string", defaultValue = "temp", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"附件UUID\"}"),
    })
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult fileUploadact(@Param("file") TempFile tf, @Param("module") String module) {
        if (dictBiz.getCacheDict(SYS_ATTACH_TYPE, module) == null) {
            return AjaxResult.error("附件类型不存在！");
        }
        String rootPath = getRootPath(Mvcs.getHttpSession());
        String md5 = FileUtil.getMD5(tf.getFile());
        FileAttach referenceFileAttach = fileAttachService.fetchByMd5(md5);
        if (referenceFileAttach != null) {
            return this.md5FileUploadact(module, tf.getSubmittedFileName(), md5);
        } else {
            try {
                Path newFile = Paths.get(parentPath, module, String.valueOf(rootPath), DateUtil.date2string(new Date(), "yyyyMMdd"), R.UU16());
                String filePath = newFile.toAbsolutePath().toString().replace(Paths.get(parentPath).toString(), "");
                Files.createNewFile(newFile.toFile());
                Files.write(newFile.toFile(), tf.getInputStream());
                //存数据库
                FileAttach fileAttach = FileAttach.builder()
                        .fileName(tf.getSubmittedFileName().replaceAll("-", "").replaceAll("_", ""))
                        .adduser(getUserName(Mvcs.getHttpSession())).md5(md5).attachtype(module).filesize(tf.getSize()).savedPath(filePath).build();
                fileAttachService.insert(fileAttach);
                return AjaxResult.sucess(fileAttach.getId());
            } catch (Exception e) {
                return AjaxResult.error(e.getMessage());
            } finally {
                try {
                    tf.delete();
                } catch (Exception e) {
                }
            }
        }
    }

    @POST
    @Ok("json")
    @At("/Md5FileUploadact")
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult md5FileUploadact(@Param("module") String module, @Param("name") String name, @Param("md5") String md5) {
        if (dictBiz.getCacheDict(SYS_ATTACH_TYPE, module) == null) {
            return AjaxResult.error("附件类型不存在！");
        }
        FileAttach referenceFileAttach = fileAttachService.fetchByMd5(md5);
        if (referenceFileAttach == null) {
            return AjaxResult.error("MD5文件不存在不能进行秒传！");
        }
        int referenceFileAttachUpdate = fileAttachService.updateAndIncrIfMatch(referenceFileAttach, null, "referenceCount");
        if (referenceFileAttachUpdate > 0) {
            //存数据库
            FileAttach fileAttach = FileAttach.builder()
                    .adduser(getUserName(Mvcs.getHttpSession())).md5(md5).savedPath(referenceFileAttach.getSavedPath())
                    .filesize(referenceFileAttach.getFilesize())
                    .referenceId(referenceFileAttach.getId())
                    .fileName(name.replaceAll("-", "").replaceAll("_", ""))
                    .attachtype(module)
                    .build();
            fileAttachService.insert(fileAttach);
            return AjaxResult.sucess(fileAttach.getId());
        } else {
            return AjaxResult.error("操作太快，请重试");
        }
    }

    private String getUserName(HttpSession session) {
        UserAccount account = (UserAccount) session.getAttribute(Cons.SESSION_USER_KEY);
        String userName = "guest";
        if (account != null) {
            userName = String.valueOf(account.getUserName());
        }
        return userName;
    }

    private String getRootPath(HttpSession session) {
        UserAccount account = (UserAccount) session.getAttribute(Cons.SESSION_USER_KEY);
        String rootPath = "guest";
        if (account != null) {
            rootPath = "loginuser";
        }
        return rootPath;
    }

    /**
     * 上传文件
     *
     * @param module
     * @param filedate
     */
    @At("/uploadBase64Act")
    @Ok("json")
    @Fail("json")
    @POST
    @AdaptBy(type = JsonAdaptor.class)
    @ApiOperation(value = "图片附件以BASE64编码进行上传", nickname = "uploadBase64Act", tags = "附件", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filedate", paramType = "form", value = "图片文件BASE64编码内容", dataType = "string", required = true),
            @ApiImplicitParam(name = "module", paramType = "form", value = "模块名", dataType = "string", defaultValue = "temp", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"2ble6kgdnshcko02d17mn4gg13\"}"),
    })
    public AjaxResult uploadBase64Act(@Param("module") String module, @Param("filedate") String filedate) {
        if (Strings.isBlank(filedate)) {
            return AjaxResult.error("空文件不允许上传");
        } else {
            try {
                if (dictBiz.getCacheDict(SYS_ATTACH_TYPE, module) == null) {
                    return AjaxResult.error("附件类型不存在！");
                }
                String rootPath = getRootPath(Mvcs.getHttpSession());
                String name = R.UU16();
                Path newFile = Paths.get(parentPath, module, rootPath, DateUtil.date2string(new Date(), "yyyyMMdd"), name);
                Files.createNewFile(newFile.toFile());
                String relPath = newFile.toAbsolutePath().toString();
                String filePath = newFile.toAbsolutePath().toString().replace(Paths.get(parentPath).toString(), "");
                if (Base64Tool.generateImage(filedate, relPath)) {
                    String md5 = FileUtil.getMD5(newFile.toFile());
                    //存数据库
                    FileAttach fileAttach = new FileAttach();
                    fileAttach.setAdduser(getUserName(Mvcs.getHttpSession()));
                    fileAttach.setFileName(name + ".jpg");
                    fileAttach.setAttachtype(module);
                    fileAttach.setMd5(md5);
                    fileAttach.setFilesize(newFile.toFile().length());
                    fileAttach.setSavedPath(filePath);
                    fileAttach = fileAttachService.insert(fileAttach);
                    return AjaxResult.sucess(fileAttach.getId());
                } else {
                    return AjaxResult.error("图片转换错误！");
                }
            } catch (DaoException e) {
                log.error("系统错误", e);
                return AjaxResult.error("系统错误");
            } catch (Throwable e) {
                return AjaxResult.error("文件格式错误");
            }
        }
    }

    /**
     * 取得文件信息
     *
     * @param ids
     */
    @ApiOperation(value = "批量取得文件信息", nickname = "fileList", tags = "附件", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", paramType = "query", value = "文件ID列表多个文件使用,号隔开", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": {{\"name\": \"专业资格.xlsx\",\"id\": \"04fp270s60g12qu7fi4a1esdnp\",\"ext\": \"xlsx\",\"sortName\": \"专业资格.xlsx\",\"size\": \"106.19 KB\"},....}}"),
    })
    @At("/fileList")
    @POST
    @Ok("json")
    public AjaxResult fileList(@Param("ids") String[] ids) {
        List<FileAttach> list = fileAttachService.query(Cnd.where("id", "in", ids));
        List<HashMap> dataList = new ArrayList<>();
        list.forEach(fileAttach -> {
            NutMap data = new NutMap();
            data.setv("name", fileAttach.getFileName());
            data.setv("id", fileAttach.getId());
            data.setv("ext", Files.getSuffixName(fileAttach.getFileName()));
            if (fileAttach.getFileName().length() < 18) {
                data.setv("sortName", fileAttach.getFileName());
            } else {
                data.setv("sortName", fileAttach.getFileName().substring(0, 18) + "...");
            }
            data.setv("size", Strings.formatSizeForReadBy1024(fileAttach.getFilesize()));
            dataList.add(data);
        });
        return AjaxResult.sucess(dataList);
    }

    /**
     * 查看PDF文件
     *
     * @return
     */
    @At("/pdfView")
    @GET
    @Ok("btl:WEB-INF/view/tool/views/pdf.html")
    public HashMap pdfView(@Param("id") String attachId) {
        return NutMap.NEW().addv("attachId", attachId);
    }

    /**
     * 查看MP4文件
     *
     * @return
     */
    @At("/mp4View")
    @GET
    @Ok("btl:WEB-INF/view/tool/views/mp4.html")
    public HashMap mp4View(@Param("id") String attachId) {
        return NutMap.NEW().addv("attachId", attachId);
    }

    /**
     * 查看文件
     *
     * @param ids
     * @return
     */
    @At("/viewAttachList")
    @GET
    @Ok("btl:WEB-INF/view/tool/viewAttachList.html")
    public List<HashMap> viewAttachList(@Param("ids") String[] ids) {
        return (List<HashMap>) fileList(ids).getData();
    }

    /**
     * 取得文件
     *
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @ApiOperation(value = "取得附件-文件下载", nickname = "attachAct", tags = "附件", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", value = "文件ID", dataType = "string", required = true),
    })
    @ApiResponse(code = 200, message = "", response = File.class)
    @At({"/attachAct", "/attachAct/?"})
    @Ok("raw")
    @POST
    @GET
    public Object attachAct(@Param("id") String id) throws UnsupportedEncodingException {
        FileAttach attach = fileAttachService.fetch(id);
        if (attach != null) {
            Path path = Paths.get(parentPath, attach.getSavedPath());
            if (path.toFile().exists()) {
                Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(attach.getFileName(), Encoding.UTF8));
                return path.toFile();
            }
        }
        return "文件不存在";
    }

    /**
     * 取得文件并压缩
     *
     * @param ids
     * @return
     * @throws UnsupportedEncodingException
     */
    @ApiOperation(value = "取得文件并压缩", nickname = "attachActZip", tags = "附件", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", paramType = "query", value = "文件ID列表多个文件,号隔开", dataType = "string", required = true),
    })
    @ApiResponse(code = 200, message = "", response = File.class)
    @At({"/attachActZip", "/attachActZip/?"})
    @Ok("raw")
    @POST
    @GET
    public Object attachActZip(@Param("ids") String[] ids) throws IOException {
        List<FileAttach> attachs = fileAttachService.query(Cnd.where("id", "in", ids));
        if (attachs.size() == 1) {
            return attachAct(attachs.get(0).getId());
        }
        List<FileZipDTO> fileList = new ArrayList<>();
        HashSet<String> names = new HashSet<>();
        attachs.forEach(attach -> {
            Path path = Paths.get(parentPath, attach.getSavedPath());
            if (path.toFile().exists()) {
                String fileName = attach.getFileName();
                if (names.contains(fileName)) {
                    do {
                        fileName = Files.getMajorName(attach.getFileName()) + "-重命名" + R.UU16().substring(0, 6) + Files.getSuffix(attach.getFileName());
                    } while (names.contains(fileName));
                }
                names.add(fileName);
                fileList.add(new FileZipDTO(fileName, path));
            }
        });
        Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("打包下载" + System.currentTimeMillis() + ".zip", Encoding.UTF8));
        return FileUtil.zipFiles(fileList, Mvcs.getResp().getOutputStream());

    }

    /**
     * 取得头像
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "取得用户头像", nickname = "avatar", tags = "附件", httpMethod = "GET", response = String.class)
    @ApiResponse(code = 200, message = "", response = File.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", value = "头像ID,不传ID或不传userName取得当前登录用户头像，没有登录则取得默认头像", dataType = "string", required = false),
            @ApiImplicitParam(name = "userName", paramType = "query", value = "头像ID,不传ID或不传userName取得当前登录用户头像，没有登录则取得默认头像", dataType = "string", required = true),
    })
    @At("/avatar")
    @Ok("raw")
    @GET
    public Object avatar(@Param("id") String avatarId, @Param("userName") String userName, @Attr(Cons.SESSION_USER_KEY) UserAccount account) {
        if (Strings.isNotBlank(userName)) {
            avatarId = userAccountService.fetchByUserName(userName).getAvatar();
        } else if (Strings.isBlank(avatarId) && account != null) {
            avatarId = account.getAvatar();
        }
        if (Strings.isNotBlank(avatarId)) {
            return this.getAvatar(fileAttachService.fetch(avatarId));
        } else {
            return this.getAvatar(null);
        }
    }

    public File getAvatarByUserId(String userId) {
        UserAccount userAccount = userAccountService.fetch(userId);
        FileAttach attach = null;
        if (Strings.isNotBlank(userAccount.getAvatar())) {
            attach = fileAttachService.fetch(userAccount.getAvatar());
        }
        return this.getAvatar(attach);
    }

    private File getAvatar(FileAttach attach) {
        //默认头像
        String avatar = "avatar.jpg";
        Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + avatar);
        if (attach != null) {
            Path path = Paths.get(parentPath, attach.getSavedPath());
            if (path.toFile().exists()) {
                return path.toFile();
            }
        }
        return Paths.get(Mvcs.getServletContext().getRealPath("/"), "static", "img", avatar).toFile();
    }

    /**
     * 取得系统logo
     *
     * @return
     */
    @ApiOperation(value = "取得系统logo", nickname = "productLogo", tags = "附件", httpMethod = "GET", response = String.class)
    @ApiResponse(code = 200, message = "", response = File.class)
    @At("/productLogo")
    @Ok("raw")
    @GET
    public Object productLogo() {
        //默认LOGO
        String logo = "logo.png";
        Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + logo);
        if (Strings.isNotBlank(Cons.optionsCach.getProductLogo())) {
            FileAttach attach = fileAttachService.fetch(Cons.optionsCach.getProductLogo());
            if (attach != null) {
                Path path = Paths.get(parentPath, attach.getSavedPath());
                if (path.toFile().exists()) {
                    return path.toFile();
                }
            }
        }
        return Paths.get(Mvcs.getServletContext().getRealPath("/"), "static", "img", logo).toFile();
    }
}
