angular.module('flowableModeler').controller('FlowableProcessFormselecterCtrl',
    ['$scope', '$modal', 'editorManager', function ($scope, $modal, $editorManager) {

        console.log("selectedShape", $scope.selectedShape)

        let modelJson = $editorManager.getModel();
        let resourceId = $scope.selectedShape.resourceId;
        let selectShape = undefined;
        for (let i in modelJson.childShapes) {
            let tempResourceId = modelJson.childShapes[i].resourceId;
            if (tempResourceId != undefined && resourceId === tempResourceId) {
                selectShape = modelJson.childShapes[i];
                break;
            }
        }
        if (selectShape && selectShape.stencil.id == "StartNoneEvent") {
            alert("开始节点不需要设置表单")
            return;
        }
        // Config for the modal window
        var opts = {
            template: 'editor-app/configuration/properties/process-flowable-formselecter-property-write-popup-template.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        _internalCreateModal(opts, $modal, $scope);
    }]);


angular.module('flowableModeler').controller('FlowableProcessFormselecterPopupCtrl', ['$scope', '$http', 'editorManager', function ($scope, $http, $editorManager) {


    $scope.tables = [];
    $scope.onlineFields = [];
    $scope.activeTab = 1;
    $scope.formProperties = {};
    $scope.authList = []
    if (typeof $scope.property.value == "string") {
        let jsonString = $scope.property.value;
        if (jsonString.indexOf("{") == 0) {
            $scope.property.value = JSON.parse(jsonString)
        } else {
            $scope.property.value = {
                formKey: $scope.property.value,
                formType: "DEVELOP",
            }
        }
    }
    if ($scope.property.value != undefined) {
        $scope.formProperties = angular.copy($scope.property.value);
        $scope.formProperties = $scope.formProperties || {};
    }

    setDefaualtValue("formKey", "");
    setDefaualtValue("formType", "ONLINE");
    setDefaualtValue("tableId", "");
    setDefaualtValue("fieldAuths", []);
    setDefaualtValue("writeBackProccessStatusField", "");


    function setDefaualtValue(field, defaualtValue) {
        if ($scope.formProperties[field] == undefined) {
            $scope.formProperties[field] = defaualtValue;
        }
    }

    $scope.init = function () {
        $http({method: 'GET', ignoreErrors: true, url: FLOWABLE.APP_URL.getListAllOnlineFormUrl()})
            .success(function (data) {
                $scope.tables = data.map(function (item) {
                    return {
                        selected: $scope.formProperties.tableId == item.id,
                        id: item.id,
                        name: item.name,
                    };
                });
                $scope.changeOnlineFormValue(function () {
                    // 设置权限回写
                    $scope.authList = $scope.formProperties.fieldAuths;
                    console.log($scope.authList)
                })
            })
    };


    $scope.changeAuthListValueByIndex = function (event, $index) {
        $scope.authList[$index] = {
            field: event.field.fieldId,
            auth: $scope.authList[$index].auth
        }
    }

    $scope.changeOnlineFormValue = function (callback) {
        if ($scope.formProperties.formKey != undefined && $scope.formProperties.formKey != "") {
            $http({
                method: 'POST', data: "tableId=" + $scope.formProperties.formKey,
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                ignoreErrors: true, url: FLOWABLE.APP_URL.getDataTableAllFiledsUrl()
            }).success(function (data) {
                $scope.onlineFields = data.data;
                // 设置默认权限
                $scope.authList = data.data.map(function (item) {
                    return {
                        field: item.fieldId,
                        auth: 'r',
                    };
                });
                $scope.writeBackFieldList = [];
                data.data.map(function (item) {
                    if (item.auths.includes("rw")) {
                        $scope.writeBackFieldList.push({
                            id: item.fieldId,
                            name: item.name,
                            selected: $scope.formProperties.writeBackProccessStatusField == item.fieldId,
                        })
                    }
                });
                callback && callback();
            })
        }
    };

    $scope.save = function () {
        const data = {
            fieldAuths: $scope.authList,
            formKey: $scope.formProperties.formKey,
            tableId: $scope.formProperties.formKey,
            formType: $scope.formProperties.formType,
            writeBackProccessStatusField: $scope.formProperties.writeBackProccessStatusField,
        }
        console.log(data)
        if (data.formType == "ONLINE" && data.fieldAuths.length == 0) {
            alert("请设置表单字段权限")
        } else {
            $scope.property.value = data
            console.log("保存：", $scope.property)
            $scope.updatePropertyInModel($scope.property);
            $scope.close();
        }
    };

    $scope.cancel = function () {
        $scope.$hide();
        $scope.property.mode = 'read';
    };

    $scope.close = function () {
        $scope.$hide();
        $scope.property.mode = 'read';
    };

    $scope.init();

}]);
