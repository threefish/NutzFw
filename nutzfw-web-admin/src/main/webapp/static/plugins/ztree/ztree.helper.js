var ztreeTool = {
    noData: "<div style='text-align: center'>暂无相关数据！</div>",
    /*
     * 针对数据为ID和PID的简单结构
     * 使用方法
     *  core.showMenusSimpleTree({
     *       title: "修改上级菜单",
     *       url: "/sys/sysMenu/tree",
     *       isCheckbox: false,
     *       target: ['menuPid', 'menuPidDesc'],
     *       data: {id: "id", pid: "pid", name: "menuName"},
     *       onSelect: function (data) {
     *          console.log(data)
     *       }, onSuccess: function (data) {
     *          console.log(data)
     *       },
     *       onOk: function (data) {
     *          console.log(data)
     *       }
     *   });
     * **/
    showMenusSimpleTree: function (option) {
        var treeManager;
        var opt = {
            el: "yh_class_ztree_simple",
            rightClickMenuId: "yh_class_ztree_simple_RightClick",
            title: option.title ? option.title : "选择器",
            url: option.url ? option.url : "",
            w: option.w ? option.w : "600",
            h: option.h ? option.h : '420',
            isSearch: option.isSearch == undefined ? true : option.isSearch,
            isCheckbox: option.isCheckbox ? option.isCheckbox : false,
            target: option.target ? ([option.target[0], option.target[1]]) : (["", ""]),
            chkboxType: option.chkboxType ? option.chkboxType : {"Y": "", "N": ""},
            otherParam: option.otherParam == undefined ? {} : option.otherParam,
            autoParam: option.autoParam == undefined ? ["id", "name"] : option.autoParam,
            data: option.data,
            dataDefaultVal: option.dataDefaultVal == undefined ? option.data.id : option.dataDefaultVal,
            selectId: option.selectId,
            selectedIds: option.selectedIds,
            onOk: option.onOk,
            onChange: option.onChange,
            onSelect: option.onSelect,
            onSuccess: option.onSuccess,
            radioType: option.radioType ? option.radioType : "all",
            isRadio: option.isRadio ? option.isRadio : false,
            rightClickMenu: option.rightClickMenu || []
        };
        this.opt = opt;
        var treeSetting = {
            async: {
                enable: true,
                type: "post",
                url: base + opt.url,
                autoParam: opt.autoParam,
                otherParam: opt.otherParam
            },
            check: {
                enable: opt.isCheckbox || opt.isRadio,
                chkboxType: opt.chkboxType,
                chkStyle: opt.isRadio ? "radio" : "checkbox",
                radioType: opt.radioType,
            },
            data: {
                simpleData: {
                    enable: true,
                    idKey: opt.data.id,
                    pIdKey: opt.data.pid,
                    rootPId: 0
                },
                key: {
                    name: opt.data.name
                }
            }, view: {
                showIcon: true,
                fontCss: getFontCss
            },
            callback: {
                beforeClick: zTreeBeforeClick,
                onAsyncSuccess: zTreeOnAsyncSuccess,
                onRightClick: onRightClick,
            }
        };
        var idEl = (opt.target[0] && opt.target[0] != '') ? $("#" + opt.target[0]) : undefined;
        var iddescEl = (opt.target[1] && opt.target[1] != '') ? $("#" + opt.target[1]) : undefined;

        function onRightClick(event, treeId, treeNode) {
            if (opt.rightClickMenu.length > 0) {
                var elid = opt.rightClickMenuId + treeNode[opt.data.id];
                var rightClickMenuBox = $("#" + elid);
                if (rightClickMenuBox.length == 0) {
                    var el = $("<ul class='onRightClick' id='" + elid + "'></ul>")
                    for (var i in opt.rightClickMenu) {
                        var item = opt.rightClickMenu[i];
                        var li = $("<li>" + item.title + "</li>");
                        if (typeof item.click === "string") {
                            if (item.click == "selectAll") {
                                li.click(function (e) {
                                    checkOrUncheckNode(treeNode, true);
                                    checkOrUncheckAllNode(treeNode, true)
                                    rightClickMenuRemove(el);
                                });
                            } else if (item.click == "unSelectAll") {
                                li.click(function (e) {
                                    checkOrUncheckNode(treeNode, false);
                                    checkOrUncheckAllNode(treeNode, false)
                                    rightClickMenuRemove(el);
                                });
                            } else if (item.click == "inverseSelection") {
                                li.click(function (e) {
                                    checkOrUncheckNode(treeNode, !treeNode.checked);
                                    checkOrUncheckAllNode(treeNode, undefined)
                                    rightClickMenuRemove(el);
                                });
                            } else {
                                console.error("[" + item.title + "]未绑定事件");
                            }
                        } else {
                            li.click(function (e) {
                                item.click(treeNode, treeManager, e);
                                rightClickMenuRemove(el);
                            });
                        }
                        el.append(li)
                    }
                    el.css({"top": event.clientY + "px", "left": event.clientX + "px", "visibility": "visible"});
                    $("body").append(el);
                }
                $("body").bind("mousedown", function (event) {
                    if (!(event.target.id == elid || $(event.target).parents("#" + elid).length > 0)) {
                        rightClickMenuRemove($("#" + elid));
                    }
                });
            }
        }

        function rightClickMenuRemove(rightClickMenuBox) {
            rightClickMenuBox.remove();
        }

        function checkOrUncheckAllNode(node, checked) {
            if (node.children != null) {
                for (var i in node.children) {
                    var n = node.children[i];
                    checkOrUncheckNode(n, checked)
                    if (n.children != null && n.children.length > 0) {
                        checkOrUncheckAllNode(n, checked)
                    }
                }
            }
        }

        function checkOrUncheckNode(node, checked) {
            if (checked == undefined) {
                treeManager.checkNode(node, !node.checked, false, true);
            } else {
                treeManager.checkNode(node, checked, false, true);
            }
        }

        function zTreeBeforeClick(treeId, data, clickFlag) {
            if (opt.onSelect) {
                return opt.onSelect(data);
            }
        }

        function getFontCss(treeId, treeNode) {
            if (treeNode.highlight && treeNode.highlight == true) {
                return {color: "#00a5e0", "font-weight": "bold"};
            } else {
                return {color: "#333", "font-weight": "normal"};
            }
        }

        function zTreeOnAsyncSuccess(treeId, data, clickFlag) {
            if (opt.selectId) {
                var node = treeManager.getNodesByParam(opt.data.id, opt.selectId)[0];
                treeManager.selectNode(node);
                zTreeBeforeClick("", node, "");
            }
            var selectedIds = opt.selectedIds.split(",");
            if (selectedIds.length > 0) {
                for (var i = 0; i < selectedIds.length; i++) {
                    var node = treeManager.getNodesByParam(opt.data.id, selectedIds[i])[0];
                    treeManager.checkNode(node, true, true);
                }
            }
            if (treeManager.getNodes().length == 0) {
                $("#" + opt.el).html(core.noData);
                $("#ztree_searchbox").html("");
            }
            var rootNode = treeManager.getNodesByParam(opt.dataDefaultVal, 0)[0];
            treeManager.expandNode(rootNode, true, false, true);
            if (idEl && idEl.val() != "") {
                if (!opt.isCheckbox) {
                    var node = treeManager.getNodesByParam(opt.dataDefaultVal, idEl.val())[0];
                    treeManager.selectNode(node);
                } else {
                    var idsArr = (idEl.val() + "").split(",");
                    for (var i in idsArr) {
                        var node = treeManager.getNodesByParam(opt.dataDefaultVal, idsArr[i])[0];
                        treeManager.checkNode(node, true, false);
                    }
                }
            }
            if (opt.onSuccess) {
                option.onSuccess(treeManager);
            }
        }

        var searchBox = opt.isSearch == true ?
            "<div id='ztree_searchbox' style='position: relative;display: block;height: 50px;'>" +
            "<div class='ztree_searchbox' style='padding: 10px 0px;background: #fff;display: block;overflow: hidden;position: absolute;width: 100%;top: 0px;z-index: 999;text-align: center;'>" +
            "<input type=text placeholder='模糊查询快速定位' class=form-control id=ztree_search_key_1 style='display: inline-block;width: 70%;'>" +
            "<input type='button' value='快速定位' id='ztree_search_btn_1' style='background-color: #00A5E0;border: 0px;display: inline-block;height: 34px;border-radius: 0px;margin-top: -3px;' class='btn btn-primary btn-sm'>" +
            "</div></div>"
            : "";
        layer.open({
            scrollbar: false,
            type: 1,
            title: [opt.title, 'font-weight: bold'],
            area: [opt.w + "px", opt.h + "px"],
            content: searchBox + "<div id='" + opt.el + "' class='ztree' style='width: 86%;margin: 0px auto;overflow-y: auto;max-height:270px;position: relative'></div>",
            btn: ['确定', "清除", '取消'],
            yes: function (index, layero) {
                var data;
                if (opt.isCheckbox) {
                    data = treeManager.getCheckedNodes();
                    var idArr = new Array();
                    var descArr = new Array();
                    for (var i in data) {
                        idArr.push(data[i][opt.dataDefaultVal]);
                        descArr.push(data[i][opt.data.name]);
                    }
                    if (idEl) {
                        idEl.val(idArr.join(","));
                    }
                    if (iddescEl) {
                        iddescEl.val(descArr.join(","));
                    }
                } else {
                    data = treeManager.getSelectedNodes()[0];
                    if (data) {
                        if (idEl) {
                            idEl.val(data[opt.dataDefaultVal]);
                        }
                        if (iddescEl) {
                            iddescEl.val(data[opt.data.name]);
                        }
                    }
                }
                if (opt.onOk) {
                    if (opt.onOk(data)) {
                        layer.close(index);
                        return true;
                    }
                } else {
                    layer.close(index);
                }
            }, btn2: function (index, layero) {
                if (idEl) {
                    idEl.val("");
                }
                if (iddescEl) {
                    iddescEl.val("");
                }
                if (opt.onChange) {
                    opt.onChange(index);
                }
                //取消所有选中 by ysy
                treeManager.checkAllNodes(false);
                treeManager.cancelSelectedNode()
                return false;
                //
            }, cancel: function (index) {
            }, success: function (layero, index) {
                treeManager = $.fn.zTree.init($("#" + opt.el), treeSetting);
                $("#ztree_search_btn_1").click(function () {
                    var id = "ztree_search_key" + (Math.random() + "").substr(2, 9);
                    if ($(this).attr("sid")) {
                        id = $(this).attr("sid");
                    } else {
                        $(this).attr("sid", id);
                    }
                    if (!window[id]) {
                        window[id] = {nodes: [], index: 0, key: ""};
                    }
                    var search = window[id];
                    var treeManager = $.fn.zTree.getZTreeObj(opt.el);

                    function updateNodes(highlight) {
                        for (var i = 0, l = search.nodes.length; i < l; i++) {
                            search.nodes[i].highlight = highlight;
                            treeManager.updateNode(search.nodes[i]);
                        }
                    }

                    var key = $("#ztree_search_key_1").val();
                    if (key.trim().length == "") {
                        core.error("请输入关键字");
                        return false;
                    }
                    if (search.key != key) {
                        search.key = key;
                        search.index = 0;
                        updateNodes(false);
                        search.nodes = treeManager.getNodesByParamFuzzy(opt.data.name, key, null);
                        if (search.nodes.length == 0) {
                            search.key = "";
                            core.error("没有查询到于【" + key + "】相关的信息");
                            return false;
                        }
                        updateNodes(true);
                    }
                    if (search.index == search.nodes.length) {
                        search.index = 0;
                    }
                    var node = search.nodes[search.index];
                    treeManager.selectNode(node);
                    opt.selectId = node[opt.dataDefaultVal];
                    zTreeBeforeClick("", node, "");
                    search.index++;
                });
            }
        });
    },
    showMenusTree: function (option) {
        var treeManager;
        var opt = {
            el: "yh_class_menus_trees",
            title: option.title ? option.title : "选择器",
            url: option.url ? option.url : "",
            w: option.w ? option.w : 420,
            h: option.h ? option.h : 420,
            isSearch: option.isSearch == undefined ? true : option.isSearch,
            isCheckbox: option.isCheckbox ? option.isCheckbox : false,
            target: option.target ? ([option.target[0], option.target[1]]) : (["", ""]),
            chkboxType: option.chkboxType ? option.chkboxType : {"Y": "", "N": ""},
            data: option.data,
            dataDefaultVal: option.dataDefaultVal == undefined ? option.data.id : option.dataDefaultVal,
            otherParam: option.otherParam == undefined ? {} : option.otherParam,
            autoParam: option.autoParam == undefined ? ["id", "name"] : option.autoParam,
            onOk: option.onOk,
            onChange: option.onChange,
            onSelect: option.onSelect,
            onSuccess: option.onSuccess,
        };
        var treeSetting = {
            async: {
                enable: true,
                type: "post",
                url: base + opt.url,
                autoParam: opt.autoParam,
                otherParam: opt.otherParam,
            },
            check: {
                enable: opt.isCheckbox,
                chkStyle: "checkbox",
                chkDisabledInherit: false,
                chkboxType: opt.chkboxType
            },
            data: {
                simpleData: {
                    enable: false,
                    idKey: opt.data.id,
                    pIdKey: opt.data.pid,
                    rootPId: 0
                },
                key: {
                    name: opt.data.name
                }
            }, view: {
                showIcon: true,
                fontCss: getFontCss
            },
            callback: {
                beforeClick: zTreeBeforeClick,
                onAsyncSuccess: zTreeOnAsyncSuccess
            }
        };
        var idEl;
        if (opt.target[0] && opt.target[0] != '') {
            idEl = $("#" + opt.target[0]);
        }

        var iddescEl;
        if (opt.target[1] && opt.target[1] != '') {
            iddescEl = $("#" + opt.target[1]);
        }

        function zTreeBeforeClick(treeId, data, clickFlag) {
            if (opt.onSelect) {
                return opt.onSelect(data);
            }
        }

        function zTreeOnAsyncSuccess(treeId, data, clickFlag) {
            if (treeManager.getNodes().length == 0) {
                $("#" + opt.el).html(core.noData);
                $("#ztree_searchbox").html("");
            }
            var rootNode = treeManager.getNodesByParam(opt.dataDefaultVal, 0)[0];
            treeManager.expandNode(rootNode, true, false, true);
            if (idEl && idEl.val() != "") {
                if (!opt.isCheckbox) {
                    var node = treeManager.getNodesByParam(opt.dataDefaultVal, idEl.val())[0];
                    treeManager.selectNode(node);
                } else {
                    var idsArr = (idEl.val() + "").split(",");
                    for (var i in idsArr) {
                        var node = treeManager.getNodesByParam(opt.dataDefaultVal, idsArr[i])[0];
                        treeManager.checkNode(node, true, false);
                    }
                }
            }
            if (opt.onSuccess) {
                option.onSuccess(treeManager);
            }
        }

        function getFontCss(treeId, treeNode) {
            if (treeNode.highlight && treeNode.highlight == true) {
                return {color: "#00a5e0", "font-weight": "bold"};
            } else {
                return {color: "#333", "font-weight": "normal"};
            }
        }

        var searchBox = opt.isSearch == true ?
            "<div id='ztree_searchbox' style='position: relative;display: block;height: 35px'><div class='ztree_searchbox' style='padding: 2px;background:#fff;display: block;height: 35px;overflow: hidden;position: fixed;width: 100%'>" +
            "<input type=text class=form-control placeholder='模糊查询快速定位' id=ztree_search_key_1 style='display: inline-block;width: 85%;height: 33px;'>" +
            "<input type='button' value='快速定位' id='ztree_search_btn_1' style='display: inline-block;height: 33px;border-radius: 0px;width: 15%;margin-top: -4px;' class='btn btn-primary btn-sm'>" +
            "</div></div>"
            : "";

        layer.open({
            scrollbar: false,
            type: 1,
            title: opt.title,
            area: [opt.w + 'px', opt.h + 'px'],
            content: searchBox + "<div id='" + opt.el + "' class='ztree' style='width: 100%'></div>",
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                var data;
                if (opt.isCheckbox) {
                    data = treeManager.getCheckedNodes();
                    var idArr = new Array();
                    var descArr = new Array();
                    for (var i in data) {
                        idArr.push(data[i][opt.dataDefaultVal]);
                        descArr.push(data[i][opt.data.name]);
                    }
                    if (idEl) {
                        idEl.val(idArr.join(","));
                    }
                    if (iddescEl) {
                        iddescEl.val(descArr.join(","));
                    }
                } else {
                    data = treeManager.getSelectedNodes()[0];
                    if (data) {
                        if (idEl) {
                            idEl.val(data[opt.dataDefaultVal]);
                        }
                        if (iddescEl) {
                            iddescEl.val(data[opt.data.name]);
                        }
                    }
                }
                if (opt.onOk) {
                    if (opt.onOk(data, index)) {
                        layer.close(index);
                        return true;
                    }
                } else {
                    layer.close(index);
                }
            }, cancel: function (index) {
                if (opt.onChange) {
                    opt.onChange();
                }
            }, success: function (layero, index) {
                treeManager = $.fn.zTree.init($("#" + opt.el), treeSetting);
                $("#ztree_search_btn_1").click(function () {
                    var id = "ztree_search_key" + (Math.random() + "").substr(2, 9);
                    if ($(this).attr("sid")) {
                        id = $(this).attr("sid");
                    } else {
                        $(this).attr("sid", id);
                    }
                    if (!window[id]) {
                        window[id] = {nodes: [], index: 0, key: ""};
                    }
                    var search = window[id];
                    var treeManager = $.fn.zTree.getZTreeObj(opt.el);

                    function updateNodes(highlight) {
                        for (var i = 0, l = search.nodes.length; i < l; i++) {
                            search.nodes[i].highlight = highlight;
                            treeManager.updateNode(search.nodes[i]);
                        }
                    }

                    var key = $("#ztree_search_key_1").val();
                    if (key.trim().length == "") {
                        core.error("请输入关键字");
                        return false;
                    }
                    if (search.key != key) {
                        search.key = key;
                        search.index = 0;
                        updateNodes(false);
                        search.nodes = treeManager.getNodesByParamFuzzy(opt.data.name, key, null);
                        if (search.nodes.length == 0) {
                            search.key = "";
                            core.error("没有查询到于【" + key + "】相关的信息");
                            return false;
                        }
                        updateNodes(true);
                    }
                    if (search.index == search.nodes.length) {
                        search.index = 0;
                    }
                    var node = search.nodes[search.index];
                    treeManager.selectNode(node);
                    opt.selectId = node[opt.dataDefaultVal];
                    // zTreeBeforeClick("", node, "");
                    search.index++;
                });

            }
        });
    },
    showUserAccounts: function (option) {
        var opt = {
            title: option.title ? option.title : "人员选择器",
            url: option.url ? option.url : "",
            w: option.w ? option.w : 420,
            h: option.h ? option.h : 420,
            target: option.target ? ([option.target[0], option.target[1]]) : (["", ""]),
            onOk: option.onOk,
            onSuccess: option.onSuccess,
        };
        layer.open({
            scrollbar: false,
            type: 2,
            title: opt.title,
            area: [opt.w + 'px', opt.h + 'px'],
            content: opt.url,
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                var body = layer.getChildFrame('body', index);
                var val = body.find("#yxz").val();
                if (opt.onOk) {
                    if (opt.onOk(val, index)) {
                        layer.close(index);
                    }
                }
            }, success: function (layero, index) {
                if (opt.onSuccess) {
                    option.onSuccess(index);
                }
            }
        });
    },
    /**
     *
     * @param showZtree 当前页面的showZtree对象
     * @param value 检索值
     * @param searchNodeName 搜索的具体属性key
     * @returns {boolean}
     */
    showZtreeSearch: function (showZtree, value, searchNodeName) {
        var id = "searchbox" + (Math.random() + "").substr(2, 9);
        if ($(showZtree.opt.el).attr("sid")) {
            id = $(showZtree.opt.el).attr("sid");
        } else {
            $(showZtree.opt.el).attr("sid", id);
        }
        if (window[id] == undefined) {
            window[id] = {nodes: [], index: 0, key: ""};
        }
        var search = window[id];
        var treeManager = showZtree.getZtree();

        function updateNodes(highlight) {
            for (var i = 0, l = search.nodes.length; i < l; i++) {
                search.nodes[i].highlight = highlight;
                treeManager.updateNode(search.nodes[i]);
            }
        }

        var key = value;
        if (key.trim().length == "") {
            core.error("请输入关键字");
            return false;
        }
        if (search.key != key) {
            search.key = key;
            search.index = 0;
            updateNodes(false);
            search.nodes = treeManager.getNodesByParamFuzzy(searchNodeName, key, null);
            if (search.nodes.length == 0) {
                search.key = "";
                core.error("没有查询到于【" + key + "】相关的信息");
                return false;
            }
            updateNodes(true);
        }
        if (search.index == search.nodes.length) {
            search.index = 0;
        }
        var node = search.nodes[search.index];
        showZtree.selectNode(node);
        search.index++;
    }
};
var showZtree = function (option) {
    var opt = {
        el: option.el,
        rightClickMenuId: option.el + "RightClick",
        type: option.type ? option.type : "post",
        url: option.url ? option.url : "",
        edit: option.edit ? option.edit : {},
        chkStyle: option.chkStyle || "checkbox",
        isCheckbox: option.isCheckbox ? option.isCheckbox : false,
        target: option.target ? [option.target[0], option.target[1]] : ['', ''],
        chkboxType: option.chkboxType ? option.chkboxType : {"Y": "p", "N": "s"},
        otherParam: option.otherParam == undefined ? {} : option.otherParam,
        autoParam: option.autoParam,
        showIcon: option.showIcon != undefined ? option.showIcon : true,
        data: option.data,
        onOk: option.onOk,
        selectId: option.selectId,
        selectedIds: option.selectedIds,
        onSelect: option.onSelect,
        onSuccess: option.onSuccess,
        onError: option.onError,   // 加载失败 的处理方法
        onCheck: option.onCheck,
        onUnCheck: option.onUnCheck,
        onClick: option.onClick,
        onBefore: option.onBefore,
        onExpand: option.onExpand,
        onCollapse: option.onCollapse,
        onDragMove: option.onDragMove,
        onDrop: option.onDrop,
        beforeDrop: option.beforeDrop,
        beforeDrag: option.beforeDrag,
        rightClickMenu: option.rightClickMenu || []
    };
    this.opt = opt;
    var treeManager;
    var search = {nodes: [], index: 0, key: ""};
    var idEl;
    if (opt.target[0] && opt.target[0] != '') {
        idEl = $("#" + opt.target[0]);
    }

    var iddescEl;
    if (opt.target[1] && opt.target[1] != '') {
        iddescEl = $("#" + opt.target[1]);
    }
    var treeSetting = {
        async: {
            enable: true,
            type: opt.type,
            url: base + opt.url,
            autoParam: opt.autoParam == undefined ? ["id", "name"] : opt.autoParam,
            otherParam: opt.otherParam,
        },
        check: {
            enable: opt.isCheckbox,
            chkStyle: opt.chkStyle,
            chkDisabledInherit: false,
            chkboxType: option.chkboxType,
        },
        edit: opt.edit,
        data: {
            simpleData: {
                enable: true,
                idKey: opt.data.id,
                pIdKey: opt.data.pid,
                rootPId: 0
            },
            key: {
                name: opt.data.name
            }
        }, view: {
            showIcon: opt.showIcon,
            fontCss: getFontCss
        },
        callback: {
            beforeClick: zTreeBeforeClick,
            onAsyncSuccess: zTreeOnAsyncSuccess,
            onCheck: zTreeOnCheck,
            onClick: zTreeOnClick,
            beforeAsync: beforeAsync,
            onAsyncError: opt.onError || zTreeOnAsyncError,
            onExpand: onExpand,
            onCollapse: onCollapse,
            onDragMove: opt.onDragMove,
            onDrop: opt.onDrop,
            beforeDrop: opt.beforeDrop,
            beforeDrag: opt.beforeDrag,
            onRightClick: onRightClick,
        }
    };

    function onRightClick(event, treeId, treeNode) {
        if (opt.rightClickMenu.length > 0) {
            var rightClickMenuBox = $(opt.rightClickMenuId)
            if (rightClickMenuBox.length == 0) {
                var el = $("<ul class='onRightClick' id='" + (opt.rightClickMenuId).replace("#", "") + "'></ul>")
                for (var i in opt.rightClickMenu) {
                    var item = opt.rightClickMenu[i];
                    var li = $("<li>" + item.title + "</li>");
                    if (typeof item.click === "string") {
                        if (item.click == "selectAll") {
                            li.click(function (e) {
                                checkOrUncheckNode(treeNode, true);
                                checkOrUncheckAllNode(treeNode, true)
                                rightClickMenuRemove();
                            });
                        } else if (item.click == "unSelectAll") {
                            li.click(function (e) {
                                checkOrUncheckNode(treeNode, false);
                                checkOrUncheckAllNode(treeNode, false)
                                rightClickMenuRemove();
                            });
                        } else if (item.click == "inverseSelection") {
                            li.click(function (e) {
                                checkOrUncheckNode(treeNode, !treeNode.checked);
                                checkOrUncheckAllNode(treeNode, undefined)
                                rightClickMenuRemove();
                            });
                        } else {
                            console.error("[" + item.title + "]未绑定事件");
                        }
                    } else {
                        li.click(function (e) {
                            item.click(treeNode, treeManager, e);
                            rightClickMenuRemove();
                        });
                    }
                    el.append(li)
                }
                $("body").append(el);
            }
            showRightMenu(opt.rightClickMenuId, event);
        }
    }

    function rightClickMenuRemove() {
        $(opt.rightClickMenuId).remove();
    }

    function checkOrUncheckAllNode(node, checked) {
        if (node.children != null) {
            for (var i in node.children) {
                var n = node.children[i];
                checkOrUncheckNode(n, checked)
                if (n.children != null && n.children.length > 0) {
                    checkOrUncheckAllNode(n, checked)
                }
            }
        }
    }

    function checkOrUncheckNode(node, checked) {
        if (checked == undefined) {
            treeManager.checkNode(node, !node.checked, false, true);
        } else {
            treeManager.checkNode(node, checked, false, true);
        }
    }

    function showRightMenu(id, event) {
        var el = $(id);
        var y = event.clientY
        var x = event.clientX;
        el.css({"top": y + "px", "z-index": "99999999", "left": x + "px", "visibility": "visible"});
        $("body").bind("mousedown", onBodyMouseDown);
    }

    function onBodyMouseDown(event) {
        if (!(event.target.id == opt.rightClickMenuId || $(event.target).parents(opt.rightClickMenuId).length > 0)) {
            rightClickMenuRemove();
        }
    }

    function onExpand(event, treeId, treeNode) {
        if (opt.onExpand) {
            return opt.onExpand(event, treeId, treeNode);
        }
    }

    function onCollapse(event, treeId, treeNode) {
        if (opt.onCollapse) {
            return opt.onCollapse(event, treeId, treeNode);
        }
    }

    function beforeAsync(treeId, treeNode) {
        if (opt.onBefore) {
            return opt.onBefore(treeNode);
        }
    }

    function zTreeOnAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
        core.error("ztree请求数据加载错误");
        throw new Error("ztree请求数据加载错误");
    }


    function getFontCss(treeId, treeNode) {
        if (treeNode.highlight && treeNode.highlight == true) {
            return {color: "#00a5e0", "font-weight": "bold"};
        } else {
            return {color: "#333", "font-weight": "normal"};
        }
    }

    function zTreeBeforeClick(treeId, data, clickFlag) {
        if (opt.onSelect) {
            return opt.onSelect(data);
        }
    }

    function zTreeOnCheck(event, treeId, treeNode) {
        if (treeNode.checked && opt.onCheck) {
            return opt.onCheck(treeNode);
        }
        if ((!treeNode.checked) && opt.onUnCheck) {
            return opt.onUnCheck(treeNode);
        }
    }

    function zTreeOnClick(event, treeId, treeNode) {
        if (opt.onClick) {
            return opt.onClick(treeNode);
        }
    }

    function zTreeOnAsyncSuccess(event, treeId, data, clickFlag) {
        if (opt.selectId) {
            var node = treeManager.getNodesByParam(opt.data.id, opt.selectId)[0];
            treeManager.selectNode(node);
            zTreeBeforeClick("", node, "");
        }
        if (opt.selectedIds) {
            for (var i = 0; i < opt.selectedIds.length; i++) {
                var node = treeManager.getNodesByParam(opt.data.id, opt.selectedIds[i])[0];
                treeManager.checkNode(node, true, true);
            }
        }
        if (treeManager.getNodes().length == 0) {
            $(opt.el).html(core.noData);
        }
        var rootNode = treeManager.getNodesByParam(opt.data.id, 0)[0];
        treeManager.expandNode(rootNode, true, false, true);
        if (idEl && idEl.val() != "") {
            if (!opt.isCheckbox) {
                var node = treeManager.getNodesByParam(opt.data.id, idEl.val())[0];
                treeManager.selectNode(node);
            } else {
                var idsArr = (idEl.val() + "").split(",");
                for (var i in idsArr) {
                    var node = treeManager.getNodesByParam(opt.data.id, idsArr[i])[0];
                    treeManager.checkNode(node, true, false);
                }
            }
        }
        if (opt.onSuccess) {
            option.onSuccess(treeManager, data);
        }
    }

    function updateNodes(highlight) {
        for (var i = 0, l = search.nodes.length; i < l; i++) {
            search.nodes[i].highlight = highlight;
            treeManager.updateNode(search.nodes[i]);
        }
    }

    this.ok = function () {
        var data;
        if (opt.isCheckbox) {
            data = treeManager.getCheckedNodes();
            var idArr = new Array();
            var descArr = new Array();
            for (var i in data) {
                idArr.push(data[i][opt.data.id]);
                descArr.push(data[i][opt.data.name]);
            }
            if (idEl) {
                idEl.val(idArr.join(","));
            }
            if (iddescEl) {
                iddescEl.val(descArr.join(","));
            }
        } else {
            data = treeManager.getSelectedNodes()[0];
            if (data) {
                if (idEl) {
                    idEl.val(data[opt.dataDefaultVal]);
                }
                if (iddescEl) {
                    iddescEl.val(data[opt.data.name]);
                }
            }
        }
        if (opt.onOk) {
            opt.onOk(data);
            return true;
        }
    }
    /**
     * 移除节点
     * @param node
     */
    this.removeNode = function (node) {
        treeManager.removeNode(node);
    }
    /**
     * 移除节点
     * @param node
     */
    this.remove = function (node) {
        treeManager.removeNode(node);
    }
    /**
     * 添加节点
     * @param target
     * @param newNode
     */
    this.addNodes = function (target, newNode) {
        treeManager.addNodes(target, newNode);
    }
    /**
     * 更新节点
     * @param target
     * @param newNode
     */
    this.updateNode = function (node) {
        treeManager.updateNode(node);
    }
    /**
     * 添加节点-覆盖liguerTree方法
     * @param target
     * @param newNode
     */
    this.append = function (target, newNode) {
        this.addNodes(target, newNode);
    }
    /**
     * 取得复选的节点-覆盖liguerTree方法
     * @param target
     * @param newNode
     */
    this.getCheckedData = function () {
        return treeManager.getCheckedNodes();
    }
    /**
     * 取得选中节点-覆盖liguerTree方法
     * @param target
     * @param newNode
     */
    this.getSelected = function () {
        return treeManager.getSelectedNodes()[0];
    }
    /**
     * 刷新某一节点
     * @param target
     * @param newNode
     */
    this.reAsyncChildNodes = function (selectId, parentNode, reloadType, isSilent) {
        opt.selectId = selectId;
        treeManager.reAsyncChildNodes(parentNode, reloadType, isSilent)
    }
    /**
     * 刷新树
     * @param target
     * @param newNode
     */
    this.reload = function (selectId) {
        opt.selectId = selectId;
        treeManager.reAsyncChildNodes(null, "refresh");
    }
    /**
     * 刷新树
     * @param target
     * @param newNode
     */
    this.update = function (node) {
        treeManager.updateNode(node);
    }

    /**
     * 展开全部/收起全部
     * @param target
     * @param newNode
     */
    this.expandAll = function (flag) {
        treeManager.expandAll(flag);
    }

    /**
     * 展开节点
     * @param target
     * @param newNode
     */
    this.expandNode = function (node) {
        treeManager.expandNode(node, true, true, true, true);
    }
    /**
     * 展开节点
     * @param target
     * @param newNode
     */
    this.unExpandNode = function (node) {
        treeManager.expandNode(node, false, true, true, true);
    }

    /**
     * 展开节点
     * @param target
     * @param newNode
     */
    this.expandNodeOrUnexpandNode = function (node) {
        if (node.open) {
            this.unExpandNode(node);
        } else {
            this.expandNode(node);
        }
    }
    /**
     * 快速选中至某一节点
     * @param nodeOrId
     */
    this.selectNode = function (nodeOrId) {
        var node;
        if (typeof nodeOrId === "object") {
            node = nodeOrId;
        }
        if (typeof nodeOrId === "string" || typeof nodeOrId === "number") {
            node = treeManager.getNodesByParam(opt.data.id, nodeOrId)[0];
        }
        if (node.length > 0) {
            node = node[0];
        }
        treeManager.selectNode(node);
        zTreeBeforeClick("", node, "");
    }

    /**
     * 快速勾选或取消勾选某一节点
     * @param nodeOrId
     */
    this.checkOrUnCheckNode = function (nodeOrId) {
        var node;
        if (typeof nodeOrId === "object") {
            node = nodeOrId;
        }
        if (typeof nodeOrId === "string" || typeof nodeOrId === "number") {
            node = treeManager.getNodesByParam(opt.data.id, nodeOrId)[0];
        }
        if (node) {
            if (node.length > 0) {
                node = node[0];
            }
            if (!node.checked) {
                treeManager.checkNode(node, true, true);
            } else {
                treeManager.checkNode(node, false, true);
            }
        } else {
            console.error(nodeOrId + "未查询到该节点")
        }
    }

    /**
     * 快速勾选某一节点
     * @param nodeOrId
     */
    this.checkNode = function (nodeOrId) {
        var node;
        if (typeof nodeOrId === "object") {
            node = nodeOrId;
        }
        if (typeof nodeOrId === "string" || typeof nodeOrId === "number") {
            node = treeManager.getNodesByParam(opt.data.id, nodeOrId)[0];
        }
        if (node) {
            if (node.length > 0) {
                node = node[0];
            }
            treeManager.checkNode(node, true, true);
        } else {
            console.error(nodeOrId + "未查询到该节点")
        }
    }
    /**
     * 快速勾选某一节点
     * @param nodeOrId
     */
    this.unCheckNode = function (nodeOrId) {
        var node;
        if (typeof nodeOrId === "object") {
            node = nodeOrId;
        }
        if (typeof nodeOrId === "string" || typeof nodeOrId === "number") {
            node = treeManager.getNodesByParam(opt.data.id, nodeOrId)[0];
        }
        if (node) {
            if (node.length > 0) {
                node = node[0];
            }
            treeManager.checkNode(node, false, true);
        } else {
            console.error(nodeOrId + "未查询到该节点")
        }
    }

    /**
     * 查找节点
     * @param nodeOrId
     */
    this.getNodesByParam = function (key, val, parentNode) {
        return treeManager.getNodesByParam(key, val, parentNode);
    }
    /**
     * 全部勾选-取消勾选  默认勾选
     * @param target
     * @param newNode
     */
    this.checkAllNodes = function (expandFlag) {
        if (expandFlag == undefined) {
            expandFlag = true;
        }
        treeManager.checkAllNodes(expandFlag);
    }

    /**
     * 展开-收起全部
     * @param target
     * @param newNode
     */
    this.expandAll = function (expandFlag) {
        if (expandFlag == undefined) {
            expandFlag = true;
        }
        treeManager.expandAll(expandFlag);
    }
    /**
     * 返回ztree原生对象
     * @returns {*}
     */
    this.getZtree = function () {
        return treeManager;
    }

    /**
     * 修改selectId
     * @param selectId
     */
    this.setSelectId = function (selectId) {
        opt.selectId = selectId;
    }
    /**
     * 查询功能
     * @param target
     * @param newNode
     */
    this.search = function (type, key) {
        if (key.trim().length == "") {
            core.error("请输入关键字");
            return false;
        }
        if (search.key != key) {
            search.key = key;
            search.index = 0;
            updateNodes(false);
            search.nodes = treeManager.getNodesByParamFuzzy(type, key, null);
            if (search.nodes.length == 0) {
                search.key = "";
                core.error("没有查询到于【" + key + "】相关的信息");
                return false;
            }
            updateNodes(true);
        }
        if (search.index == search.nodes.length) {
            search.index = 0;
        }
        var node = search.nodes[search.index];
        treeManager.selectNode(node);
        opt.selectId = node[opt.data.id];
        zTreeBeforeClick("", node, "");
        search.index++;
    }
    treeManager = $.fn.zTree.init($(opt.el), treeSetting);
    this.ztree = treeManager;
};

