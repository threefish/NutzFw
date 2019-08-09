/**
 * Created by zhengxingquan on 2017/9/5.
 */
$.fn.dataTableExt.oPagination.listbox = {
    /*
     * Function: oPagination.listbox.fnInit
     * Purpose:  Initalise dom elements required for pagination with listbox input
     * Returns:  -
     * Inputs:   object:oSettings - dataTables settings object
     *             node:nPaging - the DIV which contains this pagination control
     *             function:fnCallbackDraw - draw function which must be called on update
     */
    "fnInit": function (oSettings, nPaging, fnCallbackDraw) {
        console.log(oSettings, nPaging, fnCallbackDraw)
        var nPaging =  oSettings.aanFeatures.i;
        if(nPaging){
            nPaging = nPaging[0].parentElement;
        }
        var nInput = document.createElement('select');
        var nPage = document.createElement('span');
        var nOf = document.createElement('span');
        var inputOf = document.createElement('input');
        nOf.className = "paginate_of";
        nPage.className = "paginate_page";
        if (oSettings.sTableId !== '') {
            // nPaging.setAttribute('id', oSettings.sTableId + '_paginate');
        }
        // fnInput()
        nInput.style.display = "inline";
        // nPage.innerHTML = "Page ";
        nPage.innerHTML = "跳到 ";
        nPaging.appendChild(inputOf);
        nPaging.appendChild(nPage);
        nPaging.appendChild(nInput);
        nPaging.appendChild(nOf);

        $(nInput).change(function (e) { // Set DataTables page property and redraw the grid on listbox change event.
            window.scroll(0, 0); //scroll to top of page
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
        });
        /* Take the brutal approach to cancelling text selection */
        $('span', nPaging).bind('mousedown', function () {
            return false;
        });
        $('span', nPaging).bind('selectstart', function () {
            return false;
        });
    },

    /*
     * Function: oPagination.listbox.fnUpdate
     * Purpose:  Update the listbox element
     * Returns:  -
     * Inputs:   object:oSettings - dataTables settings object
     *             function:fnCallbackDraw - draw function which must be called on update
     */
    "fnUpdate": function (oSettings, fnCallbackDraw) {
        if (!oSettings.aanFeatures.p) {
            return;
        }
        var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
        var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) + 1;
        /* Loop over each instance of the pager */
        var an = oSettings.aanFeatures.p;
        for (var i = 0, iLen = an.length; i < iLen; i++) {
            var spans = an[i].getElementsByTagName('span');
            var inputs = an[i].getElementsByTagName('select');
            var elSel = inputs[0];
            if (elSel.options.length != iPages) {
                elSel.options.length = 0; //clear the listbox contents
                for (var j = 0; j < iPages; j++) { //add the pages
                    var oOption = document.createElement('option');
                    oOption.text = j + 1;
                    oOption.value = j + 1;
                    try {
                        elSel.add(oOption, null); // standards compliant; doesn't work in IE
                    } catch (ex) {
                        elSel.add(oOption); // IE only
                    }
                }
                // spans[1].innerHTML = "&nbsp;of&nbsp;" + iPages;
                spans[1].innerHTML = "&nbsp;页&nbsp;,共&nbsp" + iPages +"&nbsp页";
            }
            elSel.value = iCurrentPage;
        }
        var $fun = function (page, pages) {
            // buttons = plugin(page, pages)

            var numbers = [];
            var buttons = $.fn.DataTable.ext.pager.numbers_length;
            var half = Math.floor(buttons / 2);

            var _range = function (len, start) {
                var end;

                if (typeof start === "undefined") {
                    start = 0;
                    end = len;

                } else {
                    end = start;
                    start = len;
                }

                var out = [];
                for (var i = start; i < end; i++) {
                    out.push(i);
                }

                return out;
            };


            if (pages <= buttons) {
                numbers = _range(0, pages);

            } else if (page <= half) {
                numbers = _range(0, buttons);

            } else if (page >= pages - 1 - half) {
                numbers = _range(pages - buttons, pages);

            } else {
                numbers = _range(page - half, page + half + 1);
            }

            numbers.DT_el = 'span';

            // var spans = an[i].getElementsByTagName('input');
            // spans[1].innerHTML = numbers;
            return ['first', 'previous', numbers, 'next', 'last'];
        };
        var
            start      = oSettings._iDisplayStart,
            len        = oSettings._iDisplayLength,
            visRecords = oSettings.fnRecordsDisplay(),
            all        = len === -1,
            page = all ? 0 : Math.ceil( start / len ),
            pages = all ? 1 : Math.ceil( visRecords / len );

        return $fun(page,pages);
    },
}

$.fn.DataTable.ext.pager.full_numbers_no_ellipses = function(page, pages){
    var numbers = [];
    var buttons = $.fn.DataTable.ext.pager.numbers_length;
    var half = Math.floor( buttons / 2 );
    var _range = function ( len, start ){
        var end;

        if ( typeof start === "undefined" ){
            start = 0;
            end = len;

        } else {
            end = start;
            start = len;
        }

        var out = [];
        for ( var i = start ; i < end; i++ ){ out.push(i); }

        return out;
    };


    if ( pages <= buttons ) {
        numbers = _range( 0, pages );

    } else if ( page <= half ) {
        numbers = _range( 0, buttons);

    } else if ( page >= pages - 1 - half ) {
        numbers = _range( pages - buttons, pages );

    } else {
        numbers = _range( page - half, page + half + 1);
    }

    numbers.DT_el = 'span';

    return [ 'first', 'previous', numbers, 'next', 'last' ];
};
