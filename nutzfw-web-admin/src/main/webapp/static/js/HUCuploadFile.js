/**
 * Created with IntelliJ IDEA.
 * User: 黄川
 * Date Time: 2015/12/1210:37
 * To change this template use File | Settings | File Templates.
 */
;
!(function (win, doc) {
    var HUCuploadFile = new Object();
    var cong = {
        url: "",
        title: "文件上传",
        w: "660px",  // 660px
        h: "350px",  // 440px
        fileType: 'file', // 标志 是 文件(file) 还是图片(img)
        targ: 'fileId', // 控件的ID
        uploadedIds: "", // 已上传列表
        maxSize: 20,
        fun: function () {
        },
        del: function (data) {  // 删除图片事件
            document.getElementById(cong.targ).value = data.join(",");
        },
        // 上传文件失败执行方法
        failFun: function (data) {  // 删除图片事件

        }
    };
    HUCuploadFile.layerIndex;
    HUCuploadFile.formData = {};
    HUCuploadFile.prototype = {
        version: '1.0.0',
        _init: function () {
            this.layerIndex = layer.open({
                type: 2,
                shade: [0.2, "#DBDBDB"],
                area: [cong.w, cong.h],
                shadeClose: false,
                title: [cong.title, 'background: #00a5e0;color: #fff;'],
                content: [base + cong.url, 'no']
            });
        },
        ok: function (attachList) {
            var b = cong.fun(this.layerIndex, attachList);
            if (b) {
                layer.close(this.layerIndex);
            }
        },
        del: function (arrs) {
            if (typeof cong.del != 'undefined') {
                cong.del(arrs);
            }
        },
        failFun: function (index, data) {
            if (typeof cong.failFun != 'undefined') {
                cong.failFun(index, data);
            }
        }
    };

    /**
     * 多文件上传至数据库
     * 兼容老版本方法
     * @param url
     * @param fileExtensions
     * @param fun
     */
    HUCuploadFile.open = function (module, fun, type, x, y) {
        if (type && type === "cutimg") {
            x = x == undefined ? 720 : x;
            y = y == undefined ? 250 : y;
            cong.url = '/File/cutimg?module=' + module + "&x=" + x + "&y=" + y;
            cong.title = "图片裁剪上传";
            cong.w = "1024px";
            cong.h = "560px";
        } else {
            cong.url = '/File/page?module=' + module;
        }
        cong.fun = fun;
        this.prototype._init();
    }


    /**
     * 多文件上传至数据库
     * 兼容老版本方法
     * @param url
     * @param fileExtensions
     * @param fun
     */
    HUCuploadFile.cutImg = function (config) {
        var x = config.x == undefined ? 720 : config.x;
        var y = config.y == undefined ? 250 : config.y;
        var ratio = config.ratio == undefined ? 0 : config.ratio;
        cong.url = '/File/cutimg?module=' + config.module + "&x=" + x + "&y=" + y + "&ratio=" + ratio;
        cong.title = config.title == undefined ? "图片裁剪上传" : config.title;
        cong.w = config.w == undefined ? "1024px" : config.w;
        cong.h = config.h == undefined ? "569px" : config.h;
        cong.fun = config.fun;
        this.prototype._init();
    }


    /**
     * 多文件上传至指定url
     * 注意：formData 中不能使用id,name,file
     * @param config
        {
             module:"",
             url:"",//文件接收地址
             fileType:'',// 上传 文件还是 图片 (file ---- 文件  img ---- 图片)
             fileExtensions:"", //文件过滤类型 如 exe,png,xls
             title:"",  //弹窗标题
             formData:{}, //文件上传时一起发送的参数  注意：formData 中不能使用id,name,file
             ok:function(index,response){}  //上传完成后点击确定执行的事件 index表示layer窗口的标识，response是服务器返回的数据
             uploadedIds: 回显的 图片id
             targ : 图片的隐藏域id
             delete : 删除图片的回调函数
         }
     *
     */
    HUCuploadFile.multiUpload = function (config) {
        if (config.url == undefined) {
            config.url = "/File/FileUploadact";
        }
        config.fileType == config.fileType || cong.fileType;
        config.uploadedIds == config.uploadedIds || cong.uploadedIds;
        config.maxSize = config.maxSize == undefined ? cong.maxSize : config.maxSize;
        cong.url = '/File/multiUpload?url=' + config.url + '&module=' + config.module + '&fileExtensions='
            + config.fileExtensions + '&fileType=' + config.fileType
            + '&uploadedIds=' + config.uploadedIds
            + '&maxSize=' + config.maxSize;
        cong.w = config.w == undefined ? "660px" : config.w;
        cong.h = config.h == undefined ? "350px" : config.h;
        cong.fun = config.ok;
        // 用于删除图片的回调函数
        cong.del = config.delete || cong.del;
        cong.title = config.title;
        HUCuploadFile.formData = config == undefined ? {} : config.formData;
        this.prototype._init();
    }

    /**
     * 单文件上传至指定url
     * 注意：formData 中不能使用id,name,file
     * @param config
        {
             url:"",//文件接收地址
             fileExtensions:"", //文件过滤类型 如 exe,png,xls
             title:"",  //弹窗标题
             formData:{}, //文件上传时一起发送的参数  注意：formData 中不能使用id,name,file
             ok:function(index,response){}  //上传完成后点击确定执行的事件 index表示layer窗口的标识，response是服务器返回的数据
         }
     *
     */
    HUCuploadFile.singleUpload = function (config) {
        config.url = config.url == undefined ? "/File/FileUploadact" : config.url;
        if (config.auto == undefined) {
            config.auto = false;
        }
        if (config.module == undefined) {
            core.error("附件字典类型不存在");
            return;
        }
        config.fileType == config.fileType || cong.fileType;
        config.uploadedIds == config.uploadedIds == undefined ? "" : cong.uploadedIds;
        cong.url = '/File/singleUpload?url=' + config.url
            + '&module=' + config.module
            + '&auto=' + config.auto
            + '&fileExtensions=' + config.fileExtensions
            + '&fileType=' + config.fileType
            + '&uploadedIds=' + config.uploadedIds;
        // 上传文件失败执行方法
        cong.failFun = config.failFun || cong.failFun;
        cong.fun = config.ok;
        // 用于删除图片的回调函数
        cong.del = config.delete || cong.del;
        cong.title = config.title || cong.title;
        cong.w = "300px"
        cong.h = "300px"
        HUCuploadFile.formData = config == undefined ? {} : config.formData;
        this.prototype._init();
    }

    HUCuploadFile.ok = function (response) {
        HUCuploadFile.prototype.ok(response);
    }
    HUCuploadFile.del = function (response) {
        HUCuploadFile.prototype.del(response);
    }
    HUCuploadFile.failFun = function (response) {
        HUCuploadFile.prototype.failFun(HUCuploadFile.prototype.layerIndex, response);
    }
    win.HUCuploadFile = HUCuploadFile;
}(window, document));

