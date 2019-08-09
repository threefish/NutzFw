/**
 * Created by zhengxingquan on 2017/9/6.
 */
(function ($) {
    function calcDisableClasses(oSettings) {
        console.log(oSettings.oClasses)
        var start = oSettings._iDisplayStart;
        var length = oSettings._iDisplayLength;
        var visibleRecords = oSettings.fnRecordsDisplay();
        var all = length === -1;

        // Gordey Doronin: Re-used this code from main jQuery.dataTables source code. To be consistent.
        var page = all ? 0 : Math.ceil(start / length);
        var pages = all ? 1 : Math.ceil(visibleRecords / length);

        var disableFirstPrevClass = (page > 0 ? '' : oSettings.oClasses.sPageButtonDisabled);
        var disableNextLastClass = (page < pages - 1 ? '' : oSettings.oClasses.sPageButtonDisabled);

        return {
            'first': disableFirstPrevClass,
            'previous': disableFirstPrevClass,
            'next': disableNextLastClass,
            'last': disableNextLastClass
        };
    }

    function calcCurrentPage(oSettings) {
        return Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) + 1;
    }

    function calcPages(oSettings) {
        return Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength);
    }

    var firstClassName = 'first';
    var previousClassName = 'previous';
    var nextClassName = 'next';
    var lastClassName = 'last';

    var paginateClassName = 'paginate';
    var paginateOfClassName = 'paginate_of';
    var paginatePageClassName = 'paginate_page';
    var paginateInputClassName = 'paginate_input';
    var styleClass = {
        "position": "relative",
        // "float": "left",
        "padding": "6px 12px",
        "margin-left": "-1px",
        "line-height": "1.42857143",
        "color": "#337ab7",
        "text-decoration": "none",
        "background-color": " #fff",
        "border": "1px solid #ddd",
        "cursor": "pointer",
    }
    var first = {
        "margin-left": "0",
        "border-top-left-radius": "4px",
        "border-bottom-left-radius": "4px"
    }
    var last ={
        "border-top-right-radius": "4px",
        "border-bottom-right-radius": "4px"
    }

    $.fn.dataTableExt.oPagination.input = {
        'fnInit': function (oSettings, nPaging, fnCallbackDraw) {
            var nFirst = document.createElement('span');  // 首页
            var nPrevious = document.createElement('span'); // 上一页
            var nNext = document.createElement('span');     // 下一页
            var nLast = document.createElement('span');     // 末页
            var nLast2 = document.createElement('span');     // 末页
            // var nInput = document.createElement('input');   // 跳转
            var nInput = document.createElement('select');   // 跳转
            var nPage = document.createElement('span');     //
            var nOf = document.createElement('span');       //

            var language = oSettings.oLanguage.oPaginate;
            var classes = oSettings.oClasses;

            $(nFirst).css(styleClass).css(first);
            $(nPrevious).css(styleClass);
            $(nNext).css(styleClass);
            $(nLast).css(styleClass ).css(last);

            nFirst.innerHTML = language.sFirst;
            nPrevious.innerHTML = language.sPrevious;
            nNext.innerHTML = language.sNext;
            nLast.innerHTML = language.sLast;
            nLast2.innerHTML = "&nbsp;";

            $(nPaging).addClass("Page navigation");


            nFirst.className = firstClassName + ' ' + classes.sPageButton;
            nPrevious.className = previousClassName + ' ' + classes.sPageButton;
            nNext.className = nextClassName + ' ' + classes.sPageButton;
            nLast.className = lastClassName + ' ' + classes.sPageButton;

            nOf.className = paginateOfClassName;
            nPage.className = paginatePageClassName;
            nInput.className = paginateInputClassName;

            if (oSettings.sTableId !== '') {
                nPaging.setAttribute('id', oSettings.sTableId + '_' + paginateClassName);
                nFirst.setAttribute('id', oSettings.sTableId + '_' + firstClassName);
                nPrevious.setAttribute('id', oSettings.sTableId + '_' + previousClassName);
                nNext.setAttribute('id', oSettings.sTableId + '_' + nextClassName);
                nLast.setAttribute('id', oSettings.sTableId + '_' + lastClassName);
            }

            nInput.type = 'text';
            nPage.innerHTML = "第&nbsp;&nbsp;" || 'Page ';

            // nPage.appendChild(nInput);
            // nPage.appendChild(nOf);

            nPaging.appendChild(nPage);
            nPaging.appendChild(nInput);
            nPaging.appendChild(nOf);
            nPaging.appendChild(nLast2);
            nPaging.appendChild(nFirst);
            nPaging.appendChild(nPrevious);
            nPaging.appendChild(nNext);
            nPaging.appendChild(nLast);


            $(nFirst).click(function () {
                var iCurrentPage = calcCurrentPage(oSettings);
                if (iCurrentPage !== 1) {
                    oSettings.oApi._fnPageChange(oSettings, 'first');
                    fnCallbackDraw(oSettings);
                }
            });

            $(nPrevious).click(function () {
                var iCurrentPage = calcCurrentPage(oSettings);
                if (iCurrentPage !== 1) {
                    oSettings.oApi._fnPageChange(oSettings, 'previous');
                    fnCallbackDraw(oSettings);
                }
            });

            $(nNext).click(function () {
                var iCurrentPage = calcCurrentPage(oSettings);
                if (iCurrentPage !== calcPages(oSettings)) {
                    oSettings.oApi._fnPageChange(oSettings, 'next');
                    fnCallbackDraw(oSettings);
                }
            });

            $(nLast).click(function () {
                var iCurrentPage = calcCurrentPage(oSettings);
                if (iCurrentPage !== calcPages(oSettings)) {
                    oSettings.oApi._fnPageChange(oSettings, 'last');
                    fnCallbackDraw(oSettings);
                }
            });

            // $(nInput).keyup(function (e) {
            //     // 38 = up arrow, 39 = right arrow
            //     if (e.which === 38 || e.which === 39) {
            //         this.value++;
            //     }
            //     // 37 = left arrow, 40 = down arrow
            //     else if ((e.which === 37 || e.which === 40) && this.value > 1) {
            //         this.value--;
            //     }
            //
            //     if (this.value === '' || this.value.match(/[^0-9]/)) {
            //         /* Nothing entered or non-numeric character */
            //         this.value = this.value.replace(/[^\d]/g, ''); // don't even allow anything but digits
            //         return;
            //     }
            //
            //     var iNewStart = oSettings._iDisplayLength * (this.value - 1);
            //     if (iNewStart < 0) {
            //         iNewStart = 0;
            //     }
            //     if (iNewStart >= oSettings.fnRecordsDisplay()) {
            //         iNewStart = (Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength) - 1) * oSettings._iDisplayLength;
            //     }
            //
            //     oSettings._iDisplayStart = iNewStart;
            //     fnCallbackDraw(oSettings);
            // });
            $(nInput).change(function (e) { // Set DataTables page property and redraw the grid on listbox change event.
                window.scroll(0,0); //scroll to top of page
                if (this.value === "" || this.value.match(/[^0-9]/)) { /* Nothing entered or non-numeric character */
                    return;
                }
                var iNewStart = oSettings._iDisplayLength * (this.value - 1);
                if (iNewStart > oSettings.fnRecordsDisplay()) { /* Display overrun */
                    oSettings._iDisplayStart = (Math.ceil((oSettings.fnRecordsDisplay() - 1) / oSettings._iDisplayLength) - 1) * oSettings._iDisplayLength;
                    fnCallbackDraw(oSettings);
                    return;
                }
                oSettings._iDisplayStart = iNewStart;
                fnCallbackDraw(oSettings);
            }); /* Take the brutal approach to cancelling text selection */

            // Take the brutal approach to cancelling text selection.
            $('span', nPaging).bind('mousedown', function () {
                return false;
            });
            $('span', nPaging).bind('selectstart', function () {
                return false;
            });

            // If we can't page anyway, might as well not show it.
            var iPages = calcPages(oSettings);
            if (iPages <= 1) {
                $(nPaging).hide();
            }
        },

        'fnUpdate': function (oSettings) {
            if (!oSettings.aanFeatures.p) {
                return;
            }

            var iPages = calcPages(oSettings);
            var iCurrentPage = calcCurrentPage(oSettings);

            var an = oSettings.aanFeatures.p;
            if (iPages <= 1) // hide paging when we can't page
            {
                $(an).hide();
                return;
            }

            // var disableClasses = calcDisableClasses(oSettings);

            $(an).show();

            // // Enable/Disable `first` button.
            // $(an).children('.' + firstClassName)
            //     .removeClass(oSettings.oClasses.sPageButtonDisabled)
            //     .addClass(disableClasses[firstClassName]);
            //
            // // Enable/Disable `prev` button.
            // $(an).children('.' + previousClassName)
            //     .removeClass(oSettings.oClasses.sPageButtonDisabled)
            //     .addClass(disableClasses[previousClassName]);
            //
            // // Enable/Disable `next` button.
            // $(an).children('.' + nextClassName)
            //     .removeClass(oSettings.oClasses.sPageButtonDisabled)
            //     .addClass(disableClasses[nextClassName]);
            //
            // // Enable/Disable `last` button.
            // $(an).children('.' + lastClassName)
            //     .removeClass(oSettings.oClasses.sPageButtonDisabled)
            //     .addClass(disableClasses[lastClassName]);

            // Paginate of N pages text
            // $(an).children('.' + paginateOfClassName).html(' of ' + iPages);
            for (var i = 0, iLen = an.length; i < iLen; i++) {
                var spans = an[0].getElementsByClassName(paginateOfClassName);
                var inputs = an[0].getElementsByTagName('select');
                var elSel = inputs[0];
                if(elSel.options.length != iPages) {
                    elSel.options.length = 0; //clear the listbox contents
                    for (var j = 0; j < 1000; j++) { //add the pages  // iPages
                        var oOption = document.createElement('option');
                        oOption.text = j + 1;
                        oOption.value = j + 1;
                        try {
                            elSel.add(oOption, null); // standards compliant; doesn't work in IE
                        } catch (ex) {
                            elSel.add(oOption); // IE only
                        }
                    }
                    spans[0].innerHTML = "&nbsp;&nbsp;页&nbsp;&nbsp;,共&nbsp;&nbsp;" + iPages +"&nbsp;&nbsp;页";
                }
                elSel.value = iCurrentPage;
            }

            // Current page numer input value 设置 下拉列表中的值
            $(an).children('.' + paginateInputClassName).val(iCurrentPage);

            setFirstOrPrevDisabled(an,(iCurrentPage == 1 ? "no-drop" : "pointer"));
            setNextOrLastDisabled(an,(iCurrentPage == iPages ? "no-drop" : "pointer"));
        }
    };

    // first / prev
    function setFirstOrPrevDisabled(an ,cursor) {
        $(an).children('.' + firstClassName).css("cursor",cursor);

        // Enable/Disable `prev` button.
        $(an).children('.' + previousClassName).css("cursor",cursor)
    }

    // next / last
    function setNextOrLastDisabled(an,cursor) {
        $(an).children('.' + nextClassName).css("cursor",cursor);
        // Enable/Disable `prev` button.
        $(an).children('.' + lastClassName).css("cursor",cursor)
    }


})(jQuery);