angular.module('flowableModeler').controller('FlowableProcessCallactivitysettingCtrl',
    ['$scope', '$modal', '$timeout', '$translate', function ($scope, $modal, $timeout, $translate) {
        // Config for the modal window
        var opts = {
            template: 'editor-app/configuration/properties/process-flowable-callactivitysetting-property-write-popup-template.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        _internalCreateModal(opts, $modal, $scope);
    }]);


angular.module('flowableModeler').controller('FlowableProcessCallactivitysettingPopupCtrl', ['$scope', '$http', function ($scope, $http) {

    console.log("property.value", $scope.property.value)

    $scope.tables = [];

    $scope.mainField = "";
    $scope.childField = "";

    if ($scope.property.value != undefined && typeof $scope.property.value == "string" && $scope.property.value !="") {
        $scope.expansionProperties = JSON.parse($scope.property.value);
    } else {
        $scope.expansionProperties = {
            childTableId: "",
            mainTableId: "",
            formType: "ONLINE",
            jsonData: "",
            // 主流程字段
            mainFields: [],
            // 子流程字段
            childFields: [],
            bindFields: []
        };
    }

    $scope.init = function () {
        $http({method: 'GET', ignoreErrors: true, url: FLOWABLE.APP_URL.getListAllOnlineFormUrl()})
            .success(function (data) {
                $scope.tables = data.map(function (item) {
                    return {
                        selected: false,
                        id: item.id,
                        name: item.name,
                    };
                });
            })
    };

    $scope.changeMainOnlineFormValue = function () {
        $http({
            method: 'POST', data: "tableId=" + $scope.expansionProperties.mainTableId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            ignoreErrors: true, url: FLOWABLE.APP_URL.getDataTableAllFiledsUrl()
        }).success(function (data) {
            $scope.expansionProperties.mainFields = data.data
        })
    };

    $scope.changeChildOnlineFormValue = function () {
        $http({
            method: 'POST', data: "tableId=" + $scope.expansionProperties.childTableId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            ignoreErrors: true, url: FLOWABLE.APP_URL.getDataTableAllFiledsUrl()
        }).success(function (data) {
            $scope.expansionProperties.childFields = data.data
        })
    };


    $scope.delected = function (index) {
        $scope.expansionProperties.bindFields.splice(index, 1);
    };

    $scope.addbind = function () {
        if ($scope.mainField == "" || $scope.childField == "") {
            alert("请选择主流程字段和对应的子流程字段")
        }
        var mainFieldIndex = $scope.expansionProperties.mainFields.findIndex(value => value.fieldId == $scope.mainField);
        var mainField = $scope.expansionProperties.mainFields[mainFieldIndex];

        var childFieldIndex = $scope.expansionProperties.mainFields.findIndex(value => value.fieldId == $scope.childField);
        var childField = $scope.expansionProperties.childFields[childFieldIndex];
        $scope.expansionProperties.bindFields.push({mainField: mainField, childField: childField});
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


    $scope.save = function () {

        $scope.property.value = JSON.stringify($scope.expansionProperties);

        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

}]);
