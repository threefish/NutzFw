(function (win) {
    var Class = function () {
    };
    Class.prototype.auto = new Object();
    Class.prototype.auto.treeasyncok = true;
    Class.prototype.auto.ckStatus = 0;
    Class.prototype.auto.timeer = new Object()
    Class.prototype.auto.indexOk = true;
    Class.prototype.auto.timerWhile;
    Class.prototype.auto.max = 0;
    Class.prototype.auto.fun = function () {
    };
    Class.prototype.auto.tree = undefined;
    Class.prototype.auto.dataArr = new Array();

    Class.prototype.init = function (tree, dataArr, max, fun) {
        //自动异步勾选字段
        Class.prototype.auto.fun = fun;
        Class.prototype.auto.tree = tree;
        Class.prototype.auto.max = max;
        Class.prototype.auto.dataArr = dataArr;
        this.openTableCkFieldWhile();
    };

    Class.prototype.destory = function () {
        clearInterval(Class.prototype.auto.timerWhile);
        for (var i in Class.prototype.auto.timeer) {
            var timerkey = Class.prototype.auto.timeer[i];
            clearInterval(timerkey);
        }
    };

    Class.prototype.setTreeAsyncOk = function (status) {
        Class.prototype.auto.treeasyncok = status;
    }
    Class.prototype.getTreeAsyncOk = function () {
        return Class.prototype.auto.treeasyncok;
    }
    Class.prototype.setCkStatus = function (status) {
        Class.prototype.auto.ckStatus = status;
    }
    Class.prototype.getCkStatus = function () {
        return Class.prototype.auto.ckStatus;
    }
    /**
     * 自动选择字段
     */
    Class.prototype.openTableCkFieldWhile = function () {
        Class.prototype.auto.timerWhile = setInterval(function () {
            if (Class.prototype.auto.dataArr.length > 0) {
                if (Class.prototype.auto.indexOk == true) {
                    Class.prototype.auto.ckStatus = 0;
                    Class.prototype.auto.treeasyncok = true;
                    var node = Class.prototype.auto.dataArr[0];
                    Class.prototype.ckTable(node);
                }
            } else {
                clearInterval(Class.prototype.auto.timerWhile);
            }
        }, 200);
    }
    Class.prototype.updateAtuoStatus = function () {
        if (Class.prototype.auto.treeasyncok == false) {
            Class.prototype.auto.treeasyncok = true;
            Class.prototype.auto.ckStatus = Class.prototype.auto.ckStatus + 1;
        }
    }
    Class.prototype.openNode = function (node) {
        if (node.zAsync) { //节点已经加载
            this.updateAtuoStatus();
            Class.prototype.auto.ckStatus = Class.prototype.auto.ckStatus + 1;
        } else {
            Class.prototype.auto.treeasyncok = false;
            Class.prototype.auto.tree.expandNode(node);
        }
    }
    Class.prototype.ckTable = function (data) {
        Class.prototype.auto.indexOk = false;
        Class.prototype.auto.timeer[data.timerkey] = setInterval(function () {
            if (Class.prototype.auto.treeasyncok == true) {
                Class.prototype.auto.fun(data);
                if (Class.prototype.auto.ckStatus >= Class.prototype.auto.max) {
                    clearInterval(Class.prototype.auto.timeer[data.timerkey]);
                    this.updateAtuoStatus();
                    Class.prototype.auto.dataArr.shift();
                    Class.prototype.auto.indexOk = true;
                }
            }
        }, 50);
    }
    win.autoCheckField = Class;
}(window));




