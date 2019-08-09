/**
 * Created with IntelliJ IDEA.
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/2  18:34
 * 描述此JS功能：
 */
;
!(function (win, doc) {
    var DesignBind = new Object();

    /**
     * 插入图片
     */
    DesignBind.addImg = function () {
        spread.addSheet();
        var sheet = spread.getActiveSheet();
        var base64Image =
            "data:image/png;base64,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";
        sheet.addPicture("f2", base64Image, 2, 2, 6, 6);
        console.log(spread.toJSON());
    }

    /**
     * 锁定某几个单元格、或行、或列
     */
    DesignBind.test = function () {
        var sheet = spread.getActiveSheet();
        sheet.getCell(1, 1).locked(false);
        sheet.setValue(1, 1, "unLocked");
        sheet.getColumn(3).locked(false);
        sheet.getRow(5).locked(false);
        sheet.setIsProtected(true);
    }

    /**
     * 插入行或列
     */
    DesignBind.insertColOrRow = function (sheetArea, sheet, addline) {
        var isCol = false, line = -1;
        if (sheetArea === GcSpread.Sheets.SheetArea.colHeader) {
            isCol = true;
            line = sheet.getActiveColumnIndex();
        } else if (sheetArea === GcSpread.Sheets.SheetArea.rowHeader) {
            isCol = false
            line = sheet.getActiveRowIndex();
        }
        DesignBind.showLoading();
        $.ajax({
            url: base + '/report/design/insertColOrRow',
            type: "POST",
            async: false,
            data: {
                excelid: $("#excelid").val(),
                isCol: isCol,
                line: line,
                sheetname: sheet.getName(),
                addline: addline
            },
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                DesignBind.hideLoading();
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    if (isCol) {
                        sheet.addColumns(line, addline);
                    } else {
                        sheet.addRows(line, addline);
                    }
                    DesignBind.saveExcle(false);
                    DesignBind.hideMsg();
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
                DesignBind.hideLoading();
            }
        });


    };
    /**
     * 删除行或列
     */
    DesignBind.delColOrRow = function (sheetArea, sheet, delline) {
        var isCol = false, line = -1;
        if (sheetArea === GcSpread.Sheets.SheetArea.colHeader) {
            isCol = true;
            line = sheet.getActiveColumnIndex();
        } else if (sheetArea === GcSpread.Sheets.SheetArea.rowHeader) {
            isCol = false
            line = sheet.getActiveRowIndex();
        }
        DesignBind.showLoading();
        $.ajax({
            url: base + '/report/design/delColOrRow',
            type: "POST",
            async: false,
            data: {
                excelid: $("#excelid").val(),
                isCol: isCol,
                line: line,
                sheetname: sheet.getName(),
                delline: delline
            },
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                DesignBind.hideLoading();
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    if (isCol) {
                        sheet.deleteColumns(line, delline);
                    } else {
                        sheet.deleteRows(line, delline);
                    }
                    DesignBind.saveExcle(false);
                    DesignBind.hideMsg();
                } else {
                    layer.msg(data.msg, {icon: 7, time: 5000});
                }
                DesignBind.hideLoading();
            }
        });


    };


    /**
     * 显示信息
     */
    DesignBind.showMsg = function () {
        var width = $(window).width(), height = $("#controlPanel").height() + 150;
        $('<div class="hasNotSheet"><div>请先添加工作表后再进行操作</div><div class="notsheet-btn-group">' +
            '<div class="btn btn-danger" style="margin-left: 8px;" onclick="DesignBind.addSheet()">添加工作表</div>' +
            '<div class="btn btn-danger" style="margin-left: 8px;" onclick="DesignBind.importExcel(1)">导入文件</div>' +
            '<div class="btn btn-info" style="margin-left: 8px;" onclick="history.back();">退出设计</div></div></div>')
            .css("left", width / 2 - (335 / 2))
            .css("top", "30%")
            .css("width", "335")
            .css("position", "fixed")
            .css("color", "#4f4f4f")
            .css("background", "#ffffff")
            .css("border", "1px solid #a8a8a8")
            .css("border-radius", "3px")
            .css("-webkit-border-radius", "3px")
            .css("box-shadow", "0 0 10px rgba(0, 0, 0, 0.25")
            .css("font-family", "Arial, sans-serif")
            .css("font-size", "20px")
            .css("padding", "0.8em")
            .css("z-index", "99")
            .css("text-align", "center")
            .insertAfter("#loading");
        $("<div id='delayDiv'></div>")
            .css("background", "#2D5972")
            .css("opacity", 0.5)
            .css("position", "fixed")
            .css("top", 0)
            .css("left", 0)
            .css("width", "100%")
            .css("height", 9999)
            .insertAfter("#loading");
    };
    /**
     * 关闭显示信息
     */
    DesignBind.hideMsg = function () {
        $(".hasNotSheet").remove();
        $("#delayDiv").remove();
    };

    /**
     * 关闭显示信息
     */
    DesignBind.hideLoading = function () {
        $("#delayDiv").remove();
        $("#delaySpan").remove();
    };


    /**
     * 导入文件
     */
    DesignBind.importExcel = function (isConfirm) {
        var config = {
            url: "/report/design/importReportExcel",
            fileExtensions: "xlsx,xls",
            title: "导入Excel文件",
            module: "temp",
            formData: {"excelid": $("#excelid").val()},
            ok: function (layerIndex, data) {
                if (data && data.ok) {
                    var spreadJson = data.data;
                    spreadJson = spreadJson.replace(/&amp;/g, "&").replace(/&gt;/g, ">").replace(/&lt;/g, ">").replace(/&quot;/g, "\"");
                    var spreadJsonObj = JSON.parse(spreadJson);
                    spread.fromJSON(spreadJsonObj);
                    app.reset(true);
                    onCellSelected();
                    syncSpreadPropertyValues();
                    syncSheetPropertyValues();
                    spread.sheets.forEach(function (sheet) {
                        sheet.allowCellOverflow(true);
                        var rowCount = sheet.getRowCount();
                        var ColCount = sheet.getColumnCount();
                        if (rowCount < 500) {
                            sheet.setRowCount(500);
                        }
                        if (ColCount < 256) {
                            sheet.setColumnCount(256);
                        }
                        sheet.bind(GcSpread.Sheets.Events.TopRowChanged, function (sender, args) {
                            var rowCount = args.sheet.getRowCount();
                            var bottomRow = args.sheet.getViewportBottomRow(1);
                            if (bottomRow == rowCount - 1) {
                                if (rowCount < 10000) {
                                    args.sheet.setRowCount(rowCount + 10);
                                }
                            }
                        });
                    });
                    DesignBind.saveExcle(false);
                    window.location.reload();
                    DesignBind.hideMsg();
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
                layer.close(layerIndex);
            }
        };
        if (isConfirm !== 1) {
            layer.confirm('您确定导入excle吗？该操作将清除当前工作表所有内容包括绑定数据，且无法恢复！请谨慎操作！', {
                icon: 3,
                title: '提示'
            }, function (index) {
                HUCuploadFile.singleUpload(config);
                layer.close(index);
            });
        } else {
            HUCuploadFile.singleUpload(config);
        }
    };


    /**
     * 导出文件
     */
    DesignBind.exportExcel = function () {
        layer.open({
            type: 1,
            area: ['300px', '200px'],
            title: '导出设置',
            shade: 0.1,
            skin: 'layui-layer-rim',
            content: '<div style="padding:10px;">' +
            '<div style="height: 40px"><span>文件名：</span><input style="padding: 5px;" type="text" id="exportFileNameLast" value="' + $("#exportFileName").val() + '"></div>' +
            '<div style="height: 40px"><span>文件类型：</span>' +
            '<label for="fileType1"><input type="radio" value="excel" checked id="fileType1" name="fileType">excle</label>&nbsp;&nbsp;&nbsp;&nbsp;' +
            '<label for="fileType2"><input type="radio" value="pdf" id="fileType2" name="fileType">pdf</label>' +
            '</div>' +
            '</div>',
            btn: ["确定", "取消"],
            yes: function (index) {
                var spread = getSpread("ss");
                var name = $("#exportFileNameLast").val();
                var settings = getSavePDFSettings();
                var sheetIndexes = getSheetList(spread);
                var saveFlags = getSaveFlags();
                var dataObj;
                var fileType = $("input[name='fileType']:checked").val()

                var spreadJson = spread.toJSON();
                if (spreadJson.version.indexOf("9.40") >= 0) {
                    for (var pro in spreadJson.sheets) {
                        var sheet = spreadJson.sheets[pro];
                        if (sheet.floatingObjects) {
                            for (var i = 0; i < sheet.floatingObjects.length; i++) {
                                sheet.floatingObjects[i].typeName = sheet.floatingObjects[i].floatingObjectType;
                            }
                        }
                    }
                }
                if (fileType == "excel") {
                    dataObj = {
                        "spread": spreadJson,
                        "exportFileType": "xlsx",
                        "exportFileName": name,
                        "excel": {
                            "saveFlags": saveFlags,
                            "password": ""
                        }
                    };
                } else {
                    dataObj = {
                        "spread": spreadJson,
                        "exportFileType": "pdf",
                        "exportFileName": name,
                        "pdf": {
                            "sheetIndexes": sheetIndexes,
                            "setting": settings
                        }
                    };
                }
                var content = JSON.stringify(dataObj);
                dataObj = null;
                layer.close(index);
                exportFile(content);
            }
        });
    };


    /**
     * 保存excle
     */
    DesignBind.saveExcle = function (showMsg) {
        $.ajax({
            url: base + '/report/design/saveExcel',
            type: "POST",
            async: false,
            data: {
                excelid: $("#excelid").val(),
                excelJson: JSON.stringify(spread.toJSON()),
            },
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                DesignBind.hideLoading();
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    if (showMsg == true || showMsg == undefined) {
                        layer.msg(data.msg, {icon: 1});
                    }
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
            }
        });
    };

    /**
     * 设置宏表达式
     */
    DesignBind.macroControl = function () {
        var excelid = $("#excelid").val();
        var optionHtml = core.getHTML("/report/design/macros", {excelid: excelid});
        var html = "<div id='macro' style='padding: 10px'><select class='form-control input-sm'>" + optionHtml + "</select></div>";
        layer.open({
            type: 1,
            title: "宏表达式",
            area: ['300px', '150px'], //宽高
            content: html,
            btn: ['添加', '插入', '取消'],
            yes: function (index, layero) {
                var sheet = spread.getActiveSheet();
                var rowIndex = sheet.getActiveRowIndex();
                var columnIndex = sheet.getActiveColumnIndex();
                var cellVal = sheet.getCell(rowIndex, columnIndex).value();
                cellVal = (cellVal + "") == "null" ? "" : cellVal;
                sheet.setValue(rowIndex, columnIndex, cellVal + $("#macro select").val());
                layer.close(index);
            },
            btn2: function (index, layero) {
                var sheet = spread.getActiveSheet();
                var rowIndex = sheet.getActiveRowIndex();
                var columnIndex = sheet.getActiveColumnIndex();
                sheet.setValue(rowIndex, columnIndex, $("#macro select").val());
                return true;
            }
        });
    };

    /**
     * 设置全局变量
     */
    DesignBind.settingGlobalCnd = function () {
        layer.open({
            scrollbar: false,
            type: 2,
            shade: 0.4,
            area: ['80%', '80%'],
            shadeClose: false,
            title: '设置全局变量',
            content: [base + "/report/GlobalSetting/index?exceluuid=" + $("#excelid").val(), 'no'],
            btn: ['确定', "关闭"],
            yes: function (index) {
                layer.close(index);
            }
        });
    };

    /**
     * 无限行及默认256列
     */
    DesignBind.autoSheetRow = function (spread) {
        if (spread == undefined) {
            spread = window.spread;
        }
        spread.sheets.forEach(function (sheet) {
            sheet.allowCellOverflow(true);
            var rowCount = sheet.getRowCount();
            var colCount = sheet.getColumnCount();
            if (rowCount < 500) {
                sheet.setRowCount(500);
            }
            if (colCount < 256) {
                sheet.setColumnCount(256);
            }
            sheet.bind(GcSpread.Sheets.Events.TopRowChanged, function (sender, args) {
                var rowCount = args.sheet.getRowCount();
                var bottomRow = args.sheet.getViewportBottomRow(1);
                if (bottomRow == rowCount - 1) {
                    if (rowCount < 10000) {
                        args.sheet.setRowCount(rowCount + 10);
                    }
                }
            });
        });
    }
    /**
     * 加载excle
     */
    DesignBind.loadExcle = function () {
        $.ajax({
            url: base + '/report/design/loadExcle',
            type: "POST",
            async: false,
            dataType: "json",
            data: {
                excelid: $("#excelid").val()
            },
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            success: function (data) {
                if (data && data.ok) {
                    if (data.data && data.data != "") {
                        var spreadJson = JSON.parse(data.data);
                        if (spreadJson.version && spreadJson.sheets) {
                            importSpreadFromJSON(null, data.data);
                        } else {
                            DesignBind.showMsg();
                        }
                    } else {
                        DesignBind.showMsg();
                    }
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
            }, beforeSend: function () {
                DesignBind.showLoading();
                console.log("show")
            }, complete: function () {
                DesignBind.hideLoading();
                console.log("hidden")
            }
        });
    };
    DesignBind.reSizeLoading = function () {
        var width = $(window).width();
        var hasNotSheet = $(".hasNotSheet");
        if (hasNotSheet.length > 0) {
            hasNotSheet.css("left", ( width / 2) - (hasNotSheet.width() / 2));
        }
    };
    DesignBind.showLoading = function () {
        var width = $(window).width(), height = $("#controlPanel").height() + 150;
        $("<span id='delaySpan'><span id='icon' style='display:inline-block'></span>加载中...</span>")
            .css("left", width / 2 - 70)
            .css("top", "20%")
            .css("position", "fixed")
            .css("color", "#4f4f4f")
            .css("background", "#ffffff")
            .css("border", "1px solid #a8a8a8")
            .css("border-radius", "3px")
            .css("-webkit-border-radius", "3px")
            .css("box-shadow", "0 0 10px rgba(0, 0, 0, 0.25")
            .css("font-family", "Arial, sans-serif")
            .css("font-size", "20px")
            .css("padding", "0.8em")
            .insertAfter("#loading");
        $("<div id='delayDiv'></div>")
            .css("background", "#2D5972")
            .css("opacity", 0.5)
            .css("position", "fixed")
            .css("top", 0)
            .css("left", 0)
            .css("width", "100%")
            .css("height", 9999)
            .insertAfter("#loading");
    };

    /**
     * 检查sheetName是否存在
     * @param SheetName
     * @return true 存在 false不存在
     */
    DesignBind.checkSheetName = function (SheetName) {
        var b = true;
        var Count = spread.getSheetCount();
        for (var i = 0; i < Count; i++) {
            var name = spread.getSheet(i).getName();
            if (name == SheetName) {
                b = true;
            }
        }
        if (b) {
            $.ajax({
                url: base + '/report/design/checkSheetName',
                type: "POST",
                async: false,
                data: {
                    excelid: $("#excelid").val(),
                    sheetName: SheetName,
                },
                error: function (request) {
                    layer.msg("网络连接出错", {icon: 5, time: 1000});
                    DesignBind.hideLoading();
                },
                dataType: "json",
                success: function (data) {
                    if (data && data.ok) {
                        b = false;
                    } else {
                        layer.msg(data.msg, {icon: 7, time: 2000});
                    }
                }
            });
        }
        return b;
    };

    /**
     * 添加工作表
     * @param sheet
     */
    DesignBind.addSheet = function () {
        layer.prompt({
            title: '请输入新工作表名称，并确认'
        }, function (name) {
            if (DesignBind.checkSheetName(name)) {
                layer.msg("工作表名称不能重复!", {icon: 7, time: 2000});
                return false;
            } else {
                if (name) {
                    spread.addSheet();
                    var Count = spread.getSheetCount() - 1;
                    spread.setActiveSheetIndex(Count);
                    var sheet = spread.getActiveSheet();
                    sheet.setName(name);
                    var SheetName = sheet.getName();
                    DesignBind.showLoading();
                    $.ajax({
                        url: base + '/report/design/addSheet',
                        type: "POST",
                        async: true,
                        data: {
                            excelid: $("#excelid").val(),
                            sheetName: SheetName
                        },
                        error: function (request) {
                            layer.msg("网络连接出错", {icon: 5, time: 1000});
                            DesignBind.hideLoading();
                        },
                        dataType: "json",
                        success: function (data) {
                            if (data && data.ok) {
                                layer.msg(data.msg, {icon: 1});
                                DesignBind.autoSheetRow();
                                DesignBind.saveExcle(false);
                                DesignBind.hideMsg();
                            } else {
                                spread.removeSheet(Count);
                                layer.msg(data.msg, {icon: 7, time: 2000});
                            }
                            DesignBind.hideLoading();
                        }
                    });
                } else {
                    layer.msg("请输入工作表名称", {icon: 7, time: 2000});
                }

            }
        });
    }

    /**
     * 修改工作表
     * @param sheet
     */
    DesignBind.editSheet = function (sheet) {
        layer.prompt({
            title: '请输入新工作表名称，并确认'
        }, function (name) {
            if (DesignBind.checkSheetName(name)) {
                layer.msg("工作表名称不能重复!", {icon: 7, time: 2000});
                return false;
            } else {
                if (name) {
                    DesignBind.showLoading();
                    $.ajax({
                        url: base + '/report/design/editSheet',
                        type: "POST",
                        async: false,
                        data: {
                            excelid: $("#excelid").val(),
                            newSheetName: name,
                            oldSheetName: sheet.getName()
                        },
                        error: function (request) {
                            layer.msg("修改失败", {icon: 5, time: 1000});
                            DesignBind.hideLoading();
                        },
                        dataType: "json",
                        success: function (data) {
                            if (data && data.ok) {
                                sheet.setName(name);
                                DesignBind.saveExcle(false);
                                layer.msg(data.msg, {icon: 1});
                            } else {
                                layer.msg(data.msg, {icon: 7, time: 2000});
                            }
                            DesignBind.hideLoading();

                        }
                    });
                } else {
                    layer.msg("请输入工作表名称", {icon: 7, time: 2000});
                }
            }
        });
    }
    /**
     * 删除工作表
     * @param sheet
     */
    DesignBind.delSheet = function (sheet) {
        var SheetName = sheet.getName();
        layer.confirm('您确定删除当前【<span style="color: red;">' + SheetName + '</span>】工作表？<br>该操作将无法恢复！！！', {
            btn: ['确定', '我再想想']
        }, function (index) {
            DesignBind.showLoading();
            $.ajax({
                url: base + '/report/design/delSheet',
                type: "POST",
                async: false,
                data: {
                    excelid: $("#excelid").val(),
                    sheetName: SheetName
                },
                error: function (request) {
                    layer.msg("删除失败", {icon: 5, time: 1000});
                    DesignBind.hideLoading();
                },
                dataType: "json",
                success: function (data) {
                    if (data && data.ok) {
                        layer.msg(data.msg, {icon: 1});
                        spread.removeSheet(spread.getActiveSheetIndex());
                        DesignBind.saveExcle(false);
                        if (spread.getSheetCount() == 0) {
                            DesignBind.showMsg();
                        }
                        DesignBind.hideLoading();

                        layer.close(index);
                    } else {
                        layer.msg(data.msg, {icon: 7, time: 2000});
                    }
                }
            })

        });
    }


    /**
     * 拷贝数据单元格
     * @param sheet
     */
    DesignBind.copyExcleData = function (sheet) {
        var sheetName = sheet.getName();
        var sel = sheet.getSelections()[0];
        var excelid = $("#excelid").val();
        $.ajax({
            url: base + '/report/design/copyExcleData',
            type: "POST",
            async: true,
            data: {sheetName: sheetName, excelid: excelid, row: sel.row, col: sel.col},
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    layer.msg(data.msg, {icon: 1});
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
            }
        });
    }
    /**
     * 拷贝数据单元格
     * @param sheet
     */
    DesignBind.pasteExcleData = function (sheet) {
        var SheetName = sheet.getName();
        var sel = sheet.getSelections()[0];
        DesignBind.showLoading();
        var excelid = $("#excelid").val();
        var arr = new Array();
        for (var row = sel.row, rowCount = sel.row + sel.rowCount; row < rowCount; row++) {
            for (var col = sel.col, colCount = sel.col + sel.colCount; col < colCount; col++) {
                arr.push({row: row, col: col, sheetName: SheetName});
            }
        }
        $.ajax({
            url: base + '/report/design/pasteExcleData',
            type: "POST",
            async: true,
            data: JSON.stringify({list: arr, excelid: excelid}),
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                DesignBind.hideLoading();
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    for (var i = 0; i < arr.length; i++) {
                        var cellDto = arr[i];
                        sheet.setValue(cellDto.row, cellDto.col, "${数据单元格}");
                    }
                    layer.msg(data.msg, {icon: 1});
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
                DesignBind.saveExcle(false);
                DesignBind.hideLoading();
            }
        });
    }


    /**
     * 添加数据单元格
     * @param sheet
     */
    DesignBind.addExcleData = function (sheet) {
        var SheetName = sheet.getName();
        var sel = sheet.getSelections()[0];
        DesignBind.showLoading();
        var excelid = $("#excelid").val();
        var arr = new Array();
        for (var row = sel.row, rowCount = sel.row + sel.rowCount; row < rowCount; row++) {
            for (var col = sel.col, colCount = sel.col + sel.colCount; col < colCount; col++) {
                arr.push({row: row, col: col, sheetName: SheetName});
            }
        }
        $.ajax({
            url: base + '/report/design/addExcleData',
            type: "POST",
            async: true,
            data: JSON.stringify({list: arr, excelid: excelid}),
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                DesignBind.hideLoading();
            },
            dataType: "json",
            success: function (data) {
                if (data && data.ok) {
                    for (var i = 0; i < arr.length; i++) {
                        var cellDto = arr[i];
                        sheet.setValue(cellDto.row, cellDto.col, "${数据单元格}");
                    }
                    layer.msg(data.msg, {icon: 1});
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
                DesignBind.saveExcle(false);
                DesignBind.hideLoading();
            }
        });
    }


    /**
     * 隐藏行
     * @param sheet
     */
    DesignBind.hideRow = function (sheet) {
        layer.open({
            type: 1,
            shade: [0.2, "#DBDBDB"],
            area: [200, 250],
            shadeClose: false,
            title: "请输入需要隐藏的行 , 隔开",
            btn: ['确定', '取消'],
            content: "<div><input style='width: 100%;border: #cdcdcd solid 1px;height: 35px;padding: 5px;'" +
            " type='text' id='hideRow'></div>",
            yes: function (index) {
                var val = $("#hideRow").val() + "";
                val = val.replace(/，/g, ",")
                var vals = val.split(",");
                for (var i in vals) {
                    var row = vals[i];
                    sheet.setRowVisible(Number(row) - 1, false);
                }
                layer.close(index);
            }
        });
    }

    /**
     * 显示行
     * @param sheet
     */
    DesignBind.showRow = function (sheet) {
        layer.open({
            type: 1,
            shade: [0.2, "#DBDBDB"],
            area: [200, 250],
            shadeClose: false,
            title: "请输入需要显示的行 , 隔开",
            btn: ['确定', '取消'],
            content: "<div><input style='width: 100%;border: #cdcdcd solid 1px;height: 35px;padding: 5px;'" +
            " type='text' id='hideRow'></div>",
            yes: function (index) {
                var val = $("#hideRow").val() + "";
                val = val.replace(/，/g, ",")
                var vals = val.split(",");
                for (var i in vals) {
                    var row = vals[i];
                    sheet.setRowVisible(Number(row) - 1, true);
                }
                layer.close(index);
            }
        });
    }

    /**
     * showColumn
     * @param sheet
     */
    DesignBind.showColumn = function (sheet) {
        layer.open({
            type: 1,
            shade: [0.2, "#DBDBDB"],
            area: [200, 250],
            shadeClose: false,
            title: "请输入需要显示的列 , 隔开",
            btn: ['确定', '取消'],
            content: "<div><input style='width: 100%;border: #cdcdcd solid 1px;height: 35px;padding: 5px;'" +
            " type='text' id='hideRow'></div>",
            yes: function (index) {
                var val = $("#hideRow").val() + "";
                val = val.replace(/，/g, ",")
                var vals = val.split(",");
                for (var i in vals) {
                    var row = vals[i];
                    sheet.setColumnVisible(Number(row) - 1, true);
                }
                layer.close(index);
            }
        });
    }
    /**
     * hideColumn
     * @param sheet
     */
    DesignBind.hideColumn = function (sheet) {
        layer.open({
            type: 1,
            shade: [0.2, "#DBDBDB"],
            area: [200, 250],
            shadeClose: false,
            title: "请输入需要隐藏的列 , 隔开",
            btn: ['确定', '取消'],
            content: "<div><input style='width: 100%;border: #cdcdcd solid 1px;height: 35px;padding: 5px;'" +
            " type='text' id='hideRow'></div>",
            yes: function (index) {
                var val = $("#hideRow").val() + "";
                val = val.replace(/，/g, ",")
                var vals = val.split(",");
                for (var i in vals) {
                    var row = vals[i];
                    sheet.setColumnVisible(Number(row) - 1, false);
                }
                layer.close(index);
            }
        });
    }
    /**
     * 删除数据单元格
     * @param sheet
     */
    DesignBind.delExcleData = function (sheet) {
        var SheetName = sheet.getName();
        var sel = sheet.getSelections()[0];
        var excelid = $("#excelid").val();
        var arr = new Array();
        for (var row = sel.row, rowCount = sel.row + sel.rowCount; row < rowCount; row++) {
            for (var col = sel.col, colCount = sel.col + sel.colCount; col < colCount; col++) {
                arr.push({row: row, col: col, sheetName: SheetName});
            }
        }
        layer.confirm('您确定删除数据单元格吗？该操作无法恢复！请谨慎操作！', {
            icon: 3,
            title: '提示'
        }, function (index) {
            DesignBind.showLoading();
            $.ajax({
                url: base + '/report/design/delExcleData',
                type: "POST",
                async: true,
                data: JSON.stringify({list: arr, excelid: excelid}),
                error: function (request) {
                    layer.msg("网络连接出错", {icon: 5, time: 1000});
                    DesignBind.hideLoading();
                },
                dataType: "json",
                success: function (data) {
                    if (data && data.ok) {
                        layer.msg(data.msg, {icon: 1});
                        for (var i = 0; i < arr.length; i++) {
                            var cellDto = arr[i];
                            sheet.setValue(cellDto.row, cellDto.col, "");
                        }
                    } else {
                        layer.msg(data.msg, {icon: 7, time: 2000});
                    }
                    DesignBind.saveExcle(false);
                    DesignBind.hideLoading();
                }
            });
            layer.close(index);
        });
    };

    /**
     * 绑定数据源
     * @param sheet
     */
    DesignBind.bindExcleData = function (sheet) {
        var colIndex = sheet.getActiveColumnIndex();
        var RowIndex = sheet.getActiveRowIndex();
        var sheetName = sheet.getName();
        var excelid = $("#excelid").val();
        var loadIndex = layer.load();
        var RAN = "";
        $.ajax({
            url: base + '/report/design/bindData',
            type: "POST",
            async: false,
            data: {
                excelid: excelid,
                colIndex: colIndex,
                rowIndex: RowIndex,
                sheetName: sheetName
            },
            error: function (request) {
                layer.msg("网络连接出错", {icon: 5, time: 1000});
                layer.close(loadIndex);
            },
            dataType: "json",
            success: function (data) {
                layer.close(loadIndex);
                if (data && data.ok) {
                    RAN = data.data;
                } else {
                    layer.msg(data.msg, {icon: 7, time: 2000});
                }
            }
        });

        if (RAN != "") {
            this.layerIndex;
            var cong = {
                url: base + "/report/design/bindData?id=" + RAN,
                title: "单元格数据源设置",
                w: "80%",
                h: "85%"
            };
            this.fun = {
                _init: function () {
                    this.layerIndex = layer.open({
                        scrollbar: false,
                        type: 2,
                        shade: 0.4,
                        area: [cong.w, cong.h],
                        shadeClose: false,
                        title: cong.title,
                        content: [cong.url, 'no'],
                        btn: ['保存', '验证', '清空', "关闭"],
                        yes: function (index, layero) {
                            var frame = layer.getChildFrame('body', index);
                            var body = frame.contents();
                            if (window.frames['layui-layer-iframe' + index].validateFrom()) {
                                var data = body.find("#cellDataFrom").serialize();
                                var data = core.postJSON("/report/design/saveCellBind", data);
                                core.msg(data);
                                if (data.ok) {
                                    layer.close(index);
                                }
                            }
                        },
                        btn2: function (index, layero) {
                            return false;
                        },
                        btn3: function (index, layero) {
                            layer.confirm('确定清除条件吗？该操作无法恢复！请谨慎操作！', {
                                icon: 3,
                                title: '提示'
                            }, function (confirmIndex) {
                                var body = layer.getChildFrame('body', index).contents();
                                var uuid = body.find("input[name='obj.uuid']").val();
                                var data = core.postJSON("/report/design/cleanCellBind", {uuid: uuid});
                                core.msg(data);
                                if (data.ok) {
                                    layer.iframeSrc(index, cong.url)
                                }
                            })
                            return false;
                        }
                    });
                }
            };
            this.fun._init();
        }
    }


    function exportFile(content) {
        var formInnerHtml = '<input type="hidden" name="data.type" value="application/json" />';
        formInnerHtml += '<input type="hidden" name="data.data" value="' + htmlSpecialCharsEntityEncode(content) + '" />';
        content = null;
        var $iframe = $("<iframe style='display: none' src='about:blank'></iframe>").appendTo("body");
        $iframe.ready(function () {
            var formDoc = getiframeDocument($iframe);
            formDoc.write("<html><head></head><body><form method='Post' action='" + base + "/report/design/export'>" + formInnerHtml + "</form>dummy windows for postback</body></html>");
            formInnerHtml = null;
            var $form = $(formDoc).find('form');
            $form.submit();
            $form[0].reset();
        });
    }


    function getSavePDFSettings() {
        var author_prop = $("#author").val(),
            title_prop = $("#title").val(),
            subject_prop = $("#subject").val(),
            creator_prop = $("#application").val(),
            keywords_prop = $("#keyWords").val(),
            centerWindow_prop = $("#centerWindow").prop("checked"),
            displayDocTitle_prop = $("#showTitle").prop("checked"),
            hideMenubar_prop = !($("#showMenuBar").prop("checked")),
            fitWindow_prop = $("#fitWindow").prop("checked"),
            hideToolbar_prop = !($("#showToolbar").prop("checked")),
            hideWindowUI_prop = $("#showWindowUI").prop("checked");
        var settings = {
            author: author_prop,
            title: title_prop,
            subject: subject_prop,
            creator: creator_prop,
            keywords: keywords_prop,
            centerWindow: centerWindow_prop,
            displayDocTitle: displayDocTitle_prop,
            hideMenubar: hideMenubar_prop,
            fitWindow: fitWindow_prop,
            hideToolbar: hideToolbar_prop,
            hideWindowUI: hideWindowUI_prop
        };
        return settings;
    }


    function getSheetList(spread) {
        var sheetCount = spread.getSheetCount();
        var sheetIndexes = [];
        for (var index = 0; index < sheetCount; index++) {
            sheetIndexes.push(index);
        }
        return sheetIndexes;
    }

    function getiframeDocument($iframe) {
        var iframeDoc = $iframe[0].contentWindow || $iframe[0].contentDocument;
        if (iframeDoc.document) {
            iframeDoc = iframeDoc.document;
        }
        return iframeDoc;
    }

    function htmlSpecialCharsEntityEncode(str) {
        var htmlSpecialCharsRegEx = /[<>&\r\n"']/gm;
        var htmlSpecialCharsPlaceHolders = {
            '<': 'lt;',
            '>': 'gt;',
            '&': 'amp;',
            '\r': "#13;",
            '\n': "#10;",
            '"': 'quot;',
            "'": 'apos;' /*single quotes just to be safe*/
        };
        return str.replace(htmlSpecialCharsRegEx, function (match) {
            return '&' + htmlSpecialCharsPlaceHolders[match];
        });
    }

    function getSaveFlags() {
        var ExcelSaveFlags = {
            NoFlagsSet: 0,
            NoFormulas: 1,
            SaveCustomRowHeaders: 2,
            SaveCustomColumnHeaders: 4,
            SaveAsFiltered: 8,
            SaveBothCustomRowAndColumnHeaders: 6,
            SaveAsViewed: 136,
            DataOnly: 32,
            AutoRowHeight: 4096
        };

        var flags = ExcelSaveFlags.NoFlagsSet;
        var collection = $("#saveFlags input");
        $.each(collection, function (i, v) {
            if (collection[i].checked) {
                flags |= ExcelSaveFlags[collection[i].id];
            }
        });
        return flags;
    }

    function getSpread(domid) {
        var Spread;
        try {
            Spread = GC.Spread.Sheets.findControl($("#" + domid)[0]);
        } catch (e) {
            Spread = $("#" + domid).data("spread");
        }
        return Spread;
    }

    window.DesignBind = DesignBind;
}(window, document));

