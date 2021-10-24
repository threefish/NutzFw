var laydate, laytpl, ue = new Object();
console.log("formData:", formData)
var form = new Vue({
    el: '#form',
    data: {
        formData: formData,
        formDataSubmit: false
    },
    methods: {
        /**
         * 页面加载完成之后执行初始化
         * **/
        handleInitAfter: function () {
            console.log("handleInitAfter")
        },
        /**
         * 提交审核之前执行
         *  @return Boolean 返回true提交审核 返回false提示校验失败
         * **/
        handleSaveAuditBefor: function () {
            return true;
        },
        /**
         * 提交回退之前执行
         *  @return Boolean 返回true提交审核 返回false提示校验失败
         * **/
        handleBackToStepBefor: function () {
            console.log("handleBackToStepBefor")
            return true;
        }, /**
         * 提交加签前行
         *  @return Boolean 返回true提交审核 返回false提示校验失败
         * **/
        handleAddMultiInstanceBefor: function () {
            console.log("handleAddMultiInstanceBefor")
            return true;
        },
        renderDate: function () {
            $("[d-format]").each(function () {
                var fieldName = $(this).attr("d-name");
                var format = $(this).attr("d-format");
                var type = $(this).attr("d-type");
                var el = $(this)[0];
                laydate.render({
                    elem: el,
                    type: type,
                    format: format,
                    done: function (value) {
                        new Function("vm,value", "form." + fieldName + "=value;")(form, value);
                    }
                });
            });
        },
        //END
        reverEnumDesc: function (fieldName, sysCode, vm) {
            var ids = new Function("vm", "return vm." + fieldName)(vm);
            this.dictChange(0, fieldName, vm);
            return core.postJSON("/sysDict/getDictName", {sysCode: sysCode, ids: ids});
        },
        dictChange: function (fieldId, dictValFieldName) {
            //字典变化，需要修改依赖值的内容
            core.dictChange(fieldId, dictValFieldName, this)
        },
        handleShowEnumTree: function (fieldName, sysCode, multipleDict, defaualtValueField) {
            core.handleShowEnumTree(this, fieldName, sysCode, multipleDict, defaualtValueField)
        },
        handleAddAttach: function (fieldName, controlType, fieldType, attachSuffix, module) {
            //多附件
            var isMultiAttach = fieldType == 5;
            //是否纯图片
            var isImage = controlType == 8;
            this.doHandleAddAttach(fieldName, isMultiAttach, isImage, attachSuffix, module)
        },
        doHandleAddAttach: function (fieldName, isMultiAttach, isImage, attachSuffix, module) {
            var opt = {
                fileType: isImage ? "img" : "file",// 上传 文件还是 图片 (file ---- 文件  img ---- 图片)
                fileExtensions: attachSuffix, //文件过滤类型
                module: module,
                title: "文件上传",
                uploadedIds: form.formData[fieldName],
                ok: function (index, response) {
                    if (isMultiAttach) {
                        var ids = new Array();
                        for (var i in response) {
                            ids.push(response[i].data);
                        }
                        form.formData[fieldName] = ids.join(",");
                        layer.close(index)
                    } else {
                        form.formData[fieldName] = response.data;
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
        handleViewAttach: function (fieldName) {
            console.log(fieldName)
            var data = form.formData[fieldName];
            var json = core.postJSON("/File/fileList", {ids: data});
            var obj = new Array();
            obj.list = json.data;
            obj.fieldName = fieldName;
            layer.open({
                scrollbar: false,
                type: 1,
                title: "查看已上传文件",
                area: ['500px', '320px'],
                content: "<div id='fileBoxView'></div>",
                success: function () {
                    var getTpl = document.getElementById("fileBoxTpl").innerHTML
                        , view = document.getElementById('fileBoxView');
                    laytpl(getTpl).render(obj, function (html) {
                        view.innerHTML = html;
                    });
                }
            });
        },
        handleShowFile: function (fieldName, id, ext) {
            core.showAttachList(id);
        },
        handleRemoveFile: function (fieldName, id, ext) {
            core.confirm("确定删除？", function () {
                $("#td_" + id).remove();
                var olds = form.formData[fieldName].split(",");
                var arr = new Array();
                for (var i in olds) {
                    var str = olds[i];
                    if (str != id && str != null && str != undefined) {
                        arr.push(str);
                    }
                }
                form.formData[fieldName] = arr.join(",");
            });
        },
        getPostData: function () {
            var postData = new Object();
            for (var key in this.formData) {
                var val = this.formData[key];
                if (typeof val == "object" && val != null) {
                    postData[key] = val.join(",");
                } else {
                    postData[key] = val;
                }
            }
            return postData;
        },
        handleSave: function () {
            var postData = this.getPostData();
            var json = new Object();
            if (needReview) {
                json = core.postJSON("/sysDynamicFrom/saveReviewData", {
                    tableId: tableId,
                    data: JSON.stringify(postData)
                });
            } else {
                json = core.postJSON("/sysDynamicFrom/saveData", {
                    tableId: tableId,
                    data: JSON.stringify(postData)
                });
            }
            return json;
        },
        handleChoseDetp: function (fieldName) {
            core.showMenusSimpleTree({
                title: "选择新部门",
                url: "/sysOrganize/department/tree",
                data: {id: "id", pid: "pid", name: "name"},
                onOk: function (data) {
                    if (!data) {
                        core.error("请选择部门!")
                        return false;
                    } else {
                        console.log(data)
                        form.formData[fieldName ] = data.name;
                        form.formData[fieldName + '_id'] = data.id;
                    }
                    return true;
                },
                onSuccess: function (data) {
                }
            })
        },
        handleChoseUser: function (fieldName) {
            core.showSelectUsers({
                option: {
                    multipleSelection: false,
                },
                onOk: function (users) {
                    console.log(users[0])
                    form.formData[fieldName] = users[0].realName;
                    form.formData[fieldName + '_user_name'] = users[0].userName;
                    return true;
                }
            })
        }
    },
    created: function () {

    },
    updated: function () {
    },
    mounted: function () {
        /*时间选择*/
        layui.use(['laytpl', 'laydate'], function () {
            laydate = layui.laydate;
            laytpl = layui.laytpl;
            $("[d-format]").each(function () {
                var fieldname = $(this).attr("d-name");
                var format = $(this).attr("d-format");
                var type = $(this).attr("d-type");
                var el = $(this)[0];
                laydate.render({
                    elem: el,
                    type: type,
                    format: format,
                    done: function (value) {
                        form.formData[fieldname] = value;
                    }
                });
            });
            $("[ueditor]").each(function () {
                var fieldname = $(this).attr("d-name");
                var toolbars = [['undo', 'redo', 'bold', 'indent', 'italic', 'underline', 'strikethrough', 'blockquote', 'pasteplain', 'selectall', 'horizontal', 'removeformat', 'unlink', 'cleardoc', 'fontfamily', 'fontsize', 'paragraph', 'edittable', 'edittd', 'link', 'justifyleft', 'justifyright', 'justifycenter', 'justifyjustify', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'fullscreen', 'directionalityltr', 'directionalityrtl', 'pagebreak', 'imagecenter', 'lineheight', 'inserttable', 'preview']];
                if (readonly) {
                    toolbars = [['preview']];
                }
                ue[fieldname] = new UE.ui.Editor({
                    toolbars: toolbars,
                    allHtmlEnabled: false,
                    autoHeightEnabled: false,
                    autoFloatEnabled: false,
                    elementPathEnabled: false,
                    initialFrameHeight: 200,
                    enableAutoSave: false,
                    initialFrameWidth: '100%',
                    readonly: readonly
                });
                ue[fieldname].render(fieldname);
                ue[fieldname].addListener('contentChange', function () {
                    form.formData[fieldname] = ue[fieldname].getContent();
                });
                ue[fieldname].addListener('ready', function () {
                    if (form.formData[fieldname] != null) {
                        ue[fieldname].setContent(form.formData[fieldname]);
                    }
                });
            });
        });
    }
});
