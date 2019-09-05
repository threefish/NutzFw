var laydate, laytpl, ue = new Object();
var vm = new Vue({
    el: '#container',
    data: {
        fromData: fromData,
        fromDataSubmit: false,

        hasAnyDisplay: hasAnyDisplay,
    },
    methods: {
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
            core.handleAddAttach(fieldName, isMultiAttach, isImage, attachSuffix, module)
        },
        handleViewAttach: function (fieldName) {
            var data = vm.fromData[fieldName];
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
                var olds = vm.fromData[fieldName].split(",");
                var arr = new Array();
                for (var i in olds) {
                    var str = olds[i];
                    if (str != id && str != null && str != undefined) {
                        arr.push(str);
                    }
                }
                vm.fromData[fieldName] = arr.join(",");
            });
        },
        getPostData: function () {
            var postData = new Object();
            for (var key in this.fromData) {
                var val = this.fromData[key];
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
                        vm.fromData[fieldname] = value;
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
                    vm.fromData[fieldname] = ue[fieldname].getContent();
                });
                ue[fieldname].addListener('ready', function () {
                    if (vm.fromData[fieldname] != null) {
                        ue[fieldname].setContent(vm.fromData[fieldname]);
                    }
                });
            });
        });
    }
});