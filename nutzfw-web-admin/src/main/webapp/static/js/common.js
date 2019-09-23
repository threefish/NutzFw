/**
 * Created by 30695 on 2016/11/20 0020.
 */
window.NutzFwDictCache = new Object();
var core = {
    noData: "<div class='tree-nodata'>暂无数据</div>",
    cutimg: function (module, success, ratio) {
        HUCuploadFile.open(module, success, "cutimg", ratio);
    },
    uploadFile: function (module, success) {
        HUCuploadFile.open(module, success);
    },
    multiUpload: function (opt) {
        HUCuploadFile.multiUpload(opt);
    },
    singleUpload: function (opt) {
        HUCuploadFile.singleUpload(opt);
    },
    convertDataJSON: function (data) {
        //转换vue传入的请求参数为普通 JSON 配合后台 @Param("::data.") Bean bean 方式接收
        //注意只会转换第一层
        var newData = new Object();
        for (var key in data) {
            newData["data." + key] = data[key];
        }
        return newData;
    },
    handleAddAttach: function (fieldName, isMultiAttach, isImage, attachSuffix, module) {
        var opt = {
            fileType: isImage ? "img" : "file",// 上传 文件还是 图片 (file ---- 文件  img ---- 图片)
            fileExtensions: attachSuffix, //文件过滤类型
            module: module,
            title: "文件上传",
            uploadedIds: vm.fromData[fieldName],
            ok: function (index, response) {
                if (isMultiAttach) {
                    var ids = new Array();
                    for (var i in response) {
                        ids.push(response[i].data);
                    }
                    vm.fromData[fieldName] = ids.join(",");
                    layer.close(index)
                } else {
                    vm.fromData[fieldName] = response.data;
                    layer.close(index)
                }
            }
        }
        if (isMultiAttach) {
            core.multiUpload(opt);
        } else {
            core.singleUpload(opt);
        }
    },
    dictChange: function (fieldId, dictValFieldName, vm) {
        var selectDictId = new Function("vm", "return vm." + dictValFieldName)(vm);
        var fieldName = "";
        if (fieldId <= 0) {
            var temp = ("" + dictValFieldName).split(".");
            fieldName = temp[temp.length - 1];
        }
        //字典变化，需要修改依赖值的内容
        $.post(base + "/sysDynamicFrom/dictDependentChange", {
            fieldId: fieldId,
            fieldName: fieldName,
            selectDictId: selectDictId
        }, function (json) {
            for (var i in json.data) {
                var data = json.data[i];
                try {
                    new Function("vm", "value", "vm." + data.key + "=value;")(vm, data.value)
                } catch (e) {
                    console.log(e);
                    //也许字段没有进行显示，所以过滤下异常
                }
            }
        }, 'JSON');
    },
    handleShowEnumTree: function (vm, fieldName, sysCode, multipleDict, defaualtValueField) {
        if (defaualtValueField == undefined) {
            defaualtValueField == "id";
        }
        core.showMenusSimpleTree({
            title: "请选择",
            url: "/sysDict/tree",
            data: {id: "id", pid: "pid", name: "lable"},
            otherParam: {sysCode: sysCode},
            dataDefaultVal: defaualtValueField,
            chkboxType: {"Y": "s", "N": "ps"},
            isCheckbox: multipleDict,
            onOk: function (data) {
                if (multipleDict) {
                    var ids = new Array();
                    for (var i in data) {
                        var item = data[i];
                        if (!item.grouping) {
                            ids.push(item[defaualtValueField])
                        }
                    }
                    var idsVal = ids.join(",");
                    new Function("vm", "idsVal", "vm." + fieldName + "=idsVal;")(vm, idsVal)
                    return true;
                } else {
                    if (!data.grouping) {
                        new Function("vm", "val", "vm." + fieldName + "=val;")(vm, data[defaualtValueField])
                        return true;
                    }
                    core.error("请勿选择字典分组");
                    return false;
                }
            },
            onSuccess: function (ztree) {
                if (multipleDict) {
                    var idsArr = new Function("vm", "return vm." + fieldName)(vm).split(",");
                    for (var i in idsArr) {
                        var node = ztree.getNodesByParam(defaualtValueField, idsArr[i])[0];
                        ztree.checkNode(node, true, false);
                    }
                } else {
                    var val = new Function("vm", "return vm." + fieldName)(vm);
                    var node = ztree.getNodesByParam(defaualtValueField, val)[0];
                    ztree.selectNode(node);
                }
            }
        });
    },
    /**
     *
     * @param url 请不要带 ${base!} 变量
     * @param data
     * @returns {*}
     */
    postJSON: function (url, data) {
        if (data == undefined) {
            data = {};
        }
        var load = layer.load();
        var jsonData;
        $.ajax({
            url: base + url,
            type: "POST",
            data: data,
            async: false,
            error: function () {
                layer.close(load);
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            dataType: "json",
            success: function (data) {
                jsonData = data;
            }
        });
        layer.close(load);
        return jsonData;
    },
    getJSON: function (url, data) {
        if (data == undefined) {
            data = {};
        }
        var jsonData;
        $.ajax({
            url: base + url,
            type: "GET",
            data: data,
            async: false,
            error: function () {
                layer.close(index);
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            dataType: "json",
            success: function (data) {
                jsonData = data;
            }
        });
        return jsonData;
    },
    postSyncJSON: function (url, data, succuss) {
        if (data == undefined) {
            data = {};
        }
        var load = layer.load(3, {
            shade: [0.1, '#000'] //0.1透明度的白色背景
        });
        $.ajax({
            url: base + url,
            type: "POST",
            data: data,
            async: false,
            error: function () {
                layer.close(load);
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            dataType: "json",
            success: function (data) {
                layer.close(load);
                if (succuss) {
                    succuss(data);
                }
            }
        })
    },
    getHTML: function (url, data) {
        if (data == undefined) {
            data = {};
        }
        var html;
        $.ajax({
            url: base + url,
            type: "GET",
            data: data,
            async: false,
            error: function () {
                layer.close(index);
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            success: function (data) {
                html = data;
            }
        });
        return html;
    },
    postHTML: function (url, data) {
        if (data == undefined) {
            data = {};
        }
        var html;
        $.ajax({
            url: base + url,
            type: "POST",
            data: data,
            async: false,
            error: function () {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            success: function (data) {
                html = data;
            }
        });
        return html;
    },
    postDownload: function (url, params) {
        function setInput(k, p) {
            for (var key in p) {
                var val = p[key];
                if (typeof val == "object") {
                    setInput(key, val);
                } else {
                    var name = key;
                    if (k != "") {
                        name = k + "." + key;
                    }
                    $('<input type="text"/>').attr("name", name).val(p[key]).appendTo($form);
                }
            }
        }
        var $form = $('<form></form>').attr("action", base + url).attr("method", "post");
        setInput("", params);
        $($form).appendTo('body').submit().remove();
    },
    showMsg: function (msg, status) {
        if (status) {
            this.warn(msg);
        } else {
            layer.msg(msg, {icon: 1});
        }
    },
    warn: function (msg) {
        layer.msg(msg, {icon: 7, time: 2000});
    },
    msg: function (msg) {
        if (typeof msg == "string") {
            layer.msg(msg, {icon: 1});
        }
        if (typeof msg == "object") {
            if (msg.ok) {
                layer.msg(msg.msg ? msg.msg : msg.data, {icon: 1});
            } else {
                this.error(msg.msg);
            }
        }
    },
    error: function (msg) {
        layer.confirm(msg, {icon: 7, title: '操作提示', btn: ['关闭']}, function (index) {
            layer.close(index);
        });
    },
    error2: function (msg) {
        layer.msg(msg, {icon: 7});
    },
    tips: function (dom, msg) {
        layer.tips(msg, dom);
    },
    SMValidator: function (selectors, options) {
        if (options) {
            new SMValidator(selectors, options);
        } else {
            new SMValidator(selectors);
        }
    },
    validate: function (selectors) {
        return SMValidator.validate(selectors, undefined);
    },
    Tpl: function (option) {
        new jsTpl(option);
    },
    openTpl: function (option) {
        var opt = {
            template: option.template,
            w: option.w,
            h: option.h,
            title: option.title,
            data: option.data,
        }
        layer.open({
            scrollbar: false,
            type: 1,
            title: opt.title,
            area: [opt.w, opt.h],
            content: "<div id='openTplBox'></div>",
            success: function (index) {
                var tpl = template(opt.template);
                $("#openTplBox").append(tpl({d: opt.data}));
            }
        });
    },
    openUrl: function (url, title, width, height, fun) {
        layer.open({
            type: 2,
            shadeClose: false,
            title: title,
            shade: 0.3,
            area: [width, height], // 宽高
            content: base + url,
            end: function () {
                if (fun) {
                    fun();
                }
            }
        });
    },
    openUrlOk: function (url, title, width, height, onOk) {
        layer.open({
            type: 2,
            shadeClose: false,
            title: title,
            shade: 0.3,
            area: [width, height], // 宽高
            content: base + url,
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                onOk(layer.getChildFrame('body', index), index);
            }
        });
    },
    confirm: function (msg, fun) {
        this.confirmAndTitle(msg, "系统提示", fun);
    },
    prompt: function (title, fun, defaualtValue) {
        layer.prompt({
            formType: 2,
            value: defaualtValue,
            title: title,
            area: ['400px', '350px']
        }, function (value, index, elem) {
            fun(value);
            layer.close(index);
        });
    },
    confirmAndTitle: function (msg, title, fun) {
        layer.confirm(msg, {
            title: title,
            btn: ['确定', '取消']
        }, function (index) {
            if (fun) {
                fun();
            }
            layer.close(index);
        });
    },
    showIcon: function (domid) {
        if (domid) {
            layer.open({
                type: 2,
                title: '选择图标',
                shadeClose: false,
                shade: 0.3,
                area: ['800px', '60%'],
                content: base + '/setting/icon/index?domid=' + domid
            });
        } else {
            core.showMsg("参数不正确");
        }
    },
    closeFrameWindow: function () {
        parent.layer.close(parent.layer.getFrameIndex(window.name));
    },
    //查看附件的统一方法
    showAttachList: function (ids) {
        var win = window.parent.parent || window.parent || window;
        if (ids == "") {
            core.error("没有可查看的附件！")
            return false;
        }
        var json = core.postJSON("/File/fileList", {ids: ids});
        if (typeof json.data == "object" && json.data.length == 0) {
            core.error("附件已经不存在了！")
            return false;
        } else if (typeof json.data == "object" && json.data.length == 1 && "pic,tif,gif,jpg,jpeg,bmp,png".indexOf(json.data[0].ext.toLocaleLowerCase()) > -1) {
            //单个图片
            this.lookImgs({ids: json.data[0].id});
        } else if (typeof json.data == "object" && json.data.length == 1 && "pdf".indexOf(json.data[0].ext.toLocaleLowerCase()) > -1) {
            win.layer.open({
                type: 2,
                shadeClose: false,
                title: "查看" + json.data[0].name,
                shade: 0.3,
                area: ["80%", "80%"],
                maxmin: true,
                content: base + "/File/pdfView?id=" + ids
            });
        } else if (typeof json.data == "object" && json.data.length == 1 && "mp3,mp4,ram,wma,mmf,amr,aac,flac,avi,mpg,mov,webm".indexOf(json.data[0].ext.toLocaleLowerCase()) > -1) {
            win.layer.open({
                type: 2,
                shadeClose: false,
                title: "查看" + json.data[0].name,
                shade: 0.3,
                maxmin: true,
                area: ["80%", "80%"],
                content: base + "/File/mp4View?id=" + ids
            });
        } else {
            var all_img = true;
            for (var i in json.data) {
                var data = json.data[i];
                if ("pic,tif,gif,jpg,jpeg,bmp,png".indexOf(data.ext.toLocaleLowerCase()) == -1) {
                    all_img = false;
                }
            }
            if (all_img) {
                //全是图片
                this.lookImgs({ids: ids});
            } else {
                win.layer.open({
                    type: 2,
                    shadeClose: false,
                    title: false,
                    shade: 0.3,
                    area: ["80%", "80%"], // 宽高
                    content: base + "/File/viewAttachList?ids=" + ids
                });
            }
        }
    },
    /**
     * 查看图片
     *
     * 参数形式 :
     * {
     *   title : "" // 窗口的名称（默认不显示）
     *   exportBtn: true | false // 是否 显示 导出按钮
     *   ids: ""  // string 多个用 逗号分割 (图片的id值)
     *   fun:
     *   isPlay: 是否显示 播放混灯片 按钮
     *   navPage: 是否显示 下面的小导航按钮
     *   downloadUrl:  下载地址 (支持跨域)
     *
     *
     *   注意 : 如果 后台 是下载文件 , 请 用 ids 来接受参数 (是一个 用 逗号分隔的 字符串)
     * }
     */
    lookImgs: function (option) {
        var _e = {};  //
        if (!option) {
            throw new Error("参数错误,对象应该包含 ids 参数");
        }
        if (!option.ids || option.ids == null || option.ids == "" || typeof option.ids == 'undefined') {
            top.layer.msg("没有可查看的附件");
            return false;
        }
        var win = window.parent.parent || window.parent || window;
        var doc = win.document;
        _e.layerIndex;
        _e._imgArrs = [];
        _e.JSON = {
            exportBtn: true,
            ids: "",
            fun: null,
            navPage: true,
            title: false,
            navbar: false,
            // 这个参数不能修改 (默认 显示在 最上面 )
            zIndex: Date.now(),
            elId: "view_box_" + Date.now(),
            // why 修改 是否显示 按钮
            noDown: false,
            initUrl: "/File/fileList",
            lookUrl: "/File/AttachAct",
            isPlay: !0,
            downloadItemUrl: "@base@url".replace(/@base/gi, base).replace(/@url/gi, option["downloadUrl"] || "/File/attachActZip"),
            downloadAllUrl: "@base@url".replace(/@base/gi, base).replace(/@url/gi, option["downloadUrl"] || "/File/attachActZip"),
            closeFun: function () {
                var parentElement = doc;
                var el = parentElement.getElementsByTagName("body")[0];
                if (el != null) {
                    var ulElement = parentElement.getElementById(_e.elId);
                    var contentElement = parentElement.getElementsByClassName("viewer-container")[0];
                    if (ulElement != null) {
                        el.removeChild(ulElement)
                    }
                    if (contentElement != null) {
                        el.removeChild(contentElement)
                    }
                    $(el).removeClass("viewer-open")
                    ulElement = null;
                    contentElement = null;
                    el = null;
                    parentElement = null;
                }
            },
        },
            _e.prototype = {
                _init: function () {
                    // 初始化数据
                    _e.temp = $.extend({}, _e.JSON, option);
                    _e.download = option.exportBtn && !option.noDown;
                    _e.prototype._getImgDate();
                    // 加载 图片 (通过 图片 id 来加载 )
                    _e.prototype._getImghtml();
                    if (_e._imgArrs && _e._imgArrs.length) {
                        win.$("#" + _e.elId).viewer(_e.temp);
                    }
                },
                // 请求数据
                _getImgDate: function () {
                    if (!_e.temp.ids) {
                        console.error("参数不正确或参数为空----------ids")
                    }
                    var json = core.postJSON(_e.temp.initUrl, {ids: _e.temp.ids})
                    if (json && json.ok) {
                        json = json.data;
                        for (var i = 0, len = json.length; i < len; i++) {
                            var obj = json[i];
                            _e._imgArrs.push({"alt": obj.name, 'ext': obj.ext, 'id': obj.id});
                        }
                    } else {
                        core.msg("没有可以查看的图片")
                    }
                },
                // 得到 图片html
                _getImghtml: function () {
                    var _arrs = _e._imgArrs;
                    if (!_arrs || _arrs.length == 0) {
                        core.msg("没有可以查看的附件")
                        return;
                    } else {
                        var $obj = new Object();
                        $obj.now = "ul@id".replace(/@id/gi, Date.now());
                        $obj.div = win.document.getElementsByTagName("body")[0];
                        $obj.ullist = doc.createElement("ul");
                        $obj.ullist.setAttribute("class", "list");
                        $obj.ullist.setAttribute("id", _e.elId);
                        $obj.ullist.style.display = "none";
                        for (var i = 0, len = _arrs.length; i < len; i++) {
                            var reg = (/pic$|tif$|gif$|jpg$|jpeg$|bmp$|png$/gi);
                            if (reg.test(_arrs[i].ext)) {
                                var opt = doc.createElement("li");
                                var img = doc.createElement("img");
                                img.setAttribute("src", "@base@url?id=@id".replace(/@base/g, base).replace(/@url/g, _e.temp.lookUrl).replace(/@id/g, _arrs[i].id));
                                img.setAttribute("alt", _arrs[i].alt);
                                // 这里 的uuid 很重要
                                img.setAttribute("data-uuid", _arrs[i].id);
                                opt.appendChild(img);
                                $obj.ullist.appendChild(opt);
                            }
                        }
                        $obj.div.appendChild($obj.ullist);
                    }
                },
            };
        _e._l = function (option) {
            this.prototype._init();
        };
        _e._l(option);
    },
    showEchartsPie: function (opt) {
        option = {
            title: {
                // text: opt.text,
                subtext: '',
                x: 'left',
                textStyle: {
                    //字体大小
                    fontSize: 15,
                    fontFamily: '微软雅黑',
                    fontWeight: 400,
                }
            },
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c} ({d}%)"
            },
            color: ["#48A9ED", "#98D97F", "#FFD96E", "#F18579", "#8996E5", "#DA70D6", "#32CD32", "#FF9873"],
            legend: {
                orient: 'vertical',
                x: 'right',
                data: opt.names,
                padding: [100, 20, 30, 40],
                textStyle: {color: 'auto'},
                formatter: function (a) {
                    for (var i = 0; i < opt.data.length; i++) {
                        var d = opt.data[i];
                        if (d.name == a) {
                            return a + " " + d.value;
                        }
                    }
                    return a;
                }
            },
            toolbox: {
                show: true,
                feature: {
                    dataView: {show: true, readOnly: true},
                    restore: {show: true},
                    saveAsImage: {show: true},
                    magicType: {show: true, option: ['pie', 'funnel']}
                }
            },
            calculable: true,
            series: [
                {
                    name: opt.text,
                    type: 'pie',
                    radius: '55%',
                    center: ['35%', '60%'],
                    data: opt.data,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b} : {c} ({d}%)'
                            },
                            labelLine: {show: true}
                        }
                    }
                }
            ]
        };
        var myChart = echarts.init(document.getElementById(opt.el));

        //使用制定的配置项和数据显示图表
        myChart.setOption(option);
    },
    showEchartsBar: function (opt) {
        option = {
            title: {
                // text: opt.text,
                subtext: '',
                textStyle: {
                    //字体大小
                    fontSize: 15,
                    fontFamily: '微软雅黑',
                    fontWeight: 400,
                }
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: [opt.text],//['蒸发量','降水量']
            },
            color: ["#48A9ED", "#98D97F", "#FFD96E", "#F18579", "#8996E5", "#DA70D6", "#32CD32", "#FF9873"],
            toolbox: {
                show: true,
                feature: {
                    mark: {show: true},
                    dataView: {show: true, readOnly: true},
                    // magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: true},
                    saveAsImage: {show: true}
                }
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    data: opt.xAxisData,// ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
                    axisLabel: {
                        interval: 0,
                        rotate: 40
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: opt.text,
                    type: 'bar',
                    data: opt.data,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'top',
                                textStyle: {
                                    color: '#48A9ED'
                                }
                            }
                        },
                    },
                    // markPoint: {
                    //     data: [
                    //         {type: 'max', name: '最大值'},
                    //         {type: 'min', name: '最小值'}
                    //     ]
                    // },
                    barWidth: 25,//固定柱子宽度
                    // markLine: {
                    //     data: [
                    //         {type: 'average', name: '平均值'}
                    //     ]
                    // }
                }
            ]
        };
        var myChart = echarts.init(document.getElementById(opt.el));

        //使用制定的配置项和数据显示图表
        myChart.setOption(option);
    },
    showEchartsLine: function (opt) {
        option = {
            title: {
                // text: opt.text,
                subtext: '',
                textStyle: {
                    //字体大小
                    fontSize: 15,
                    fontFamily: '微软雅黑',
                    fontWeight: 400,
                }
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: [opt.text],//['蒸发量','降水量']
            },
            color: ["#48A9ED", "#98D97F", "#FFD96E", "#F18579", "#8996E5", "#DA70D6", "#32CD32", "#FF9873"],
            toolbox: {
                show: true,
                feature: {
                    mark: {show: true},
                    dataView: {show: true, readOnly: true},
                    // magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: true},
                    saveAsImage: {show: true}
                }
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    data: opt.xAxisData,// ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
                    axisLabel: {
                        interval: 0,
                        rotate: 40
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: opt.text,
                    type: 'line',
                    data: opt.data,
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值'},
                            {type: 'min', name: '最小值'}
                        ]
                    },
                    barWidth: 25,//固定柱子宽度
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }
            ]
        };

        var myChart = echarts.init(document.getElementById(opt.el));

        //使用制定的配置项和数据显示图表
        myChart.setOption(option);
    },
    showSelectUsers: function (opt) {
        var option = {
            onOk: opt.onOk,
            option: opt.option
        };
        layer.open({
            type: 2,
            title: '选择用户',
            shadeClose: false,
            shade: 0.3,
            area: ['800px', '600px'],
            content: base + '/selectUser/index',
            btn: ['确定', '取消'],
            success: function (layero, index) {
                var frame = window.frames['layui-layer-iframe' + index];
                for (var key in option.option) {
                    frame.vm.config[key] = option.option[key];
                }
            },
            yes: function (index, layero) {
                if (option.onOk) {
                    var frame = window.frames['layui-layer-iframe' + index];
                    if (option.onOk(frame.vm.selectedUsers)) {
                        layer.close(index);
                    }
                }
            }
        });
    },
    showMenusSimpleTree: ztreeTool.showMenusSimpleTree,
    showZtreeSearch:
    ztreeTool.showZtreeSearch,
    showMenusTree:
    ztreeTool.showMenusTree,
    showUserAccounts:
    ztreeTool.showUserAccounts,
    showChoseTableData: function (params) {
        params.width = params.width || "500px";
        params.height = params.height || "80%";
        params.title = params.title || "请选择";
        var opt = {
            onOk: params.onOk,
            option: params.option
        };
        layer.open({
            type: 2,
            shadeClose: true,
            title: params.title,
            shade: 0.3,
            area: [params.width, params.height],
            content: base + "/showChoseTableData/page",
            btn: ['确定', '取消'],
            success: function (layero, index) {
                var frame = window.frames['layui-layer-iframe' + index];
                for (var key in opt.option) {
                    frame.vm.config[key] = opt.option[key];
                }
                frame.vm.handleInit();
            },
            yes: function (index, layero) {
                if (opt.onOk) {
                    var frame = window.frames['layui-layer-iframe' + index];
                    if (opt.onOk(frame.vm.getChoseData())) {
                        layer.close(index);
                    }
                }
            }
        });
    },
    doGetDictDesc: function (value, vm, fieldName, sysCode, defaualtValueField) {
        return core.postJSON("/sysDict/getDictName", {
            sysCode: sysCode,
            ids: value,
            defaualtValueField: defaualtValueField
        });
    },
    dictDesc: function (value, vm, fieldName, sysCode, defaualtValueField) {
        if (!value) return '';
        //缓存防止重复请求后台
        var cacheName = vm.$options.el + "_" + fieldName;
        var cacheObject = window.NutzFwDictCache[cacheName];
        if (cacheObject == undefined || cacheObject.value != value) {
            cacheObject = {
                value: value,
                desc: this.doGetDictDesc(value, vm, fieldName, sysCode, defaualtValueField)
            }
        }
        window.NutzFwDictCache[cacheName] = cacheObject;
        return cacheObject.desc;
    }
};



