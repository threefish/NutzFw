/**
 * Created with IntelliJ IDEA.
 * @author huchuc@vip.qq.com
 * @date 2018/1/4  14:43
 */
$(function () {
    $("#cellDataFrom [data-value]").each(function () {
        var el = $(this);
        el.val(el.attr("data-value"));
    });
    $("#celltype").change(function () {
        changeCellType($(this).val());
    });
    $("#globalCnd").change(function () {
        $("#globalCndLable").html($(this).val());
    });
    $("#sqlViewTemplate").change(function () {
        $(this).val(($(this).val() + "").replace(/，/g, ",").replace(/”/g, "\"").replace(/“/g, "\"").replace(/：/g, ":"));
    });
    ace.require("ace/ext/language_tools");
    var editor = ace.edit("editor");
    editor.setOptions({
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true
    });
    editor.setTheme("ace/theme/chrome");
    editor.session.setMode("ace/mode/javascript");
    editor.session.setUseWrapMode(true);
    editor.session.on('change', function (e) {
        $("#sqlText").val(editor.getValue());
    });
});

function changeCellType(val){
    var variableInfo=$("#variableInfo");
    switch (Number(val)) {
        case 4:
            variableInfo.show();
            break;
        default :
            variableInfo.hide();
            break
    }
}

function sqlTextMore() {
    layer.open({
        type: 1,
        shade: false,
        area: ['600px', '500px'],
        title: "SQL条件说明",
        content: $("#sqlTextMore").html()
    });
}

function sqlTextViewMore() {
    layer.open({
        type: 1,
        shade: false,
        area: ['600px', '500px'],
        title: "SQL显示模板说明",
        content: $("#sqlTextViewMore").html()
    });
}