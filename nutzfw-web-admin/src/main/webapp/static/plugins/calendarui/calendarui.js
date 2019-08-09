function getPath() {
    var js = document.scripts, jsPath = js[js.length - 1].src;
    return jsPath.substring(0, jsPath.lastIndexOf("/") + 1);
}
function useStyle() {
    var link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    link.href = getPath() + 'skins/default.css';
    document.getElementsByTagName('head')[0].appendChild(link);
}
useStyle();
var Calendar = (function () {
    var Calendar = function (div, date) {
        this.div = document.getElementById(div);
        var w = this.div.offsetWidth;
        var h = this.div.offsetHeight;
        this.width = parseInt(w);
        this.height = parseInt(h) >= 270 ? h : 270;
        this.date = date;
        this.div.style.width = this.width + 'px'; //按默认值设置回去
        this.div.style.height = this.height + 'px';//按默认值设置回去
    };

    Calendar.week = ['一', '二', '三', '四', '五', '六', '日'];

    Calendar.prototype['showUI'] = function (callback) {
        var exist = document.getElementById('celldom_0');
        if (!!exist) {
            for (var e = 0; e < 42; e++) {
                var node = document.getElementById('celldom_' + e);
                node.onclick = null; //移除事件处理程序
                this.div.removeChild(node);
            }
        }
        var width = this.width,
            height = this.height,
            cell = {width: parseInt(width) / 7, height: (parseInt(height) - 30 - 20) / 6},
            monthArr = this._monthPanel(this.date);
        this.div.setAttribute("class", "date-clendar");
        this._addHeader();
        this._addWeekday();

        for (var i = 0; i < 42; i++) {
            var cellDOM = document.createElement('div');
            cellDOM.style.width = cell.width + 'px';
            cellDOM.id = 'celldom_' + i;
            cellDOM.setAttribute('date', monthArr.date[i]); //设置日期对象到DOM属性date上
            cellDOM.innerHTML = monthArr.date[i].getDate();
            var thisDate = monthArr.date[i];
            var tempdate = thisDate.format("yyyy-MM-dd");
            var nowDate = new Date();
            var nowdate = nowDate.format("yyyy-MM-dd");
            if(i==5||i==6||i==12||i==13||i==19||i==20||i==26||i==27||i==33||i==34){
                cellDOM.style.color='red';
            }
            if (tempdate === nowdate) {
                cellDOM.setAttribute("class", "day active");

            } else {
                cellDOM.setAttribute("class", "day");
            }
            if (i < monthArr.preLen || i >= monthArr.currentLen + monthArr.preLen) {
                cellDOM.setAttribute("class", "day notvis");
            }
            this.div.appendChild(cellDOM);
        }

        //使用父元素事件委托
        this.div.addEventListener('click', function (e) {
            var node = e.target;
            if (node.id.indexOf('celldom_') > -1) {
                var date = new Date(node.getAttribute('date'));
                callback(date);
            }
        });
    };

    Calendar.prototype._addHeader = function () {
        var exist = document.getElementById('dateBox');
        if (!!exist) {
            exist.onclick = null;
            this.div.removeChild(exist);
        }
        var dateBox = document.createElement('div');
        dateBox.id = 'dateBox';
        dateBox.setAttribute("class", "date-top");

        var leftDiv = document.createElement('div');
        leftDiv.innerHTML = '&gt';
        leftDiv.setAttribute("class", "date-top--next");
        var _that = this; //获取到this对象
        leftDiv.addEventListener('click', function (event) {
            var year = parseInt(_that.date.getFullYear()),
                month = parseInt(_that.date.getMonth());
            if (month === 11) {
                _that.date = new Date(year + 1, 0, 1);
            } else {
                _that.date = new Date(year, month + 1, 1);
            }
            _that['showUI'](function () {
            });
        });

        var rightDiv = document.createElement('div');
        rightDiv.innerHTML = '&lt';
        rightDiv.setAttribute("class", "date-top--next");
        rightDiv.addEventListener('click', function (event) {
            var year = parseInt(_that.date.getFullYear()),
                month = parseInt(_that.date.getMonth());
            if (month === 0) {
                _that.date = new Date(year - 1, 11, 1);
            } else {
                _that.date = new Date(year, month - 1, 1);
            }
            _that['showUI'](function () {
            });

        });

        var timeDiv = document.createElement('div');
        timeDiv.setAttribute("class", "date-top--cen");
        timeDiv.innerHTML = this.date.getFullYear() + '年' + (this.date.getMonth() + 1) + '月';

        dateBox.appendChild(leftDiv);
        dateBox.appendChild(timeDiv);
        dateBox.appendChild(rightDiv);
        this.div.appendChild(dateBox);
    };

    //增加星期
    Calendar.prototype._addWeekday = function () {
        var exist = document.getElementById('week_0');
        if (!!exist) {
            for (var i = 0; i < 7; i++) {
                var node = document.getElementById('week_' + i);
                node.onclick = null;
                this.div.removeChild(node);
            }
        }

        for (var n = 0; n < 7; n++) {
            var weekday = document.createElement('div');
            weekday.style.width = parseInt(this.width) / 7 + 'px';
            weekday.setAttribute("class", "head-week");
            weekday.innerHTML = Calendar.week[n];
            weekday.id = "week_" + n;
            this.div.appendChild(weekday);

        }

    };
    Date.prototype.format = function (format) {
        var o = {
            "M+": this.getMonth() + 1, //month
            "d+": this.getDate(), //day
            "h+": this.getHours(), //hour
            "m+": this.getMinutes(), //minute
            "s+": this.getSeconds(), //second
            "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
            "S": this.getMilliseconds() //millisecond
        }

        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }

        for (var k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }

    Calendar.prototype._monthPanel = function (date) {
        //如果传递了Date对象，则按Date对象进行计算月份面板
        //否则，按照当前月份计算面板
        var myDate = date || new Date(),
            year = myDate.getFullYear(),
            month = myDate.getMonth(),
            day = myDate.getDate(),
            week = myDate.getDay(),
            currentDays = new Date(year, month + 1, 0).getDate(),
            preDays = new Date(year, month, 0).getDate(),
            firstDay = new Date(year, month, 1),
            firstCell = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1,
            bottomCell = 42 - currentDays - firstCell;
        //前一个月该显示多少天
        var preMonth = [];
        for (var p = firstCell; p > 0; p--) {
            preMonth.push(new Date(year, month - 1, preDays - p + 1));
        }
        var len = preMonth.length;
        //本月
        var currentMonth = [];
        for (var c = 0; c < currentDays; c++) {
            currentMonth.push(new Date(year, month, c + 1));
        }
        //下一个月
        var nextMonth = [];
        for (var n = 0; n < bottomCell; n++) {
            nextMonth.push(new Date(year, month + 1, n + 1));
        }

        preMonth = preMonth.concat(currentMonth, nextMonth);
        return {
            date: preMonth,
            preLen: len,
            currentLen: currentMonth.length
        };
    };

    return Calendar;

})();