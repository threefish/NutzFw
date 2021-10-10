angular.module('flowableModeler').controller('FlowableProcessExternalFormExecutorCtrl', ['$scope', '$http', function ($scope, $http) {

    $scope.options = [];


    $scope.init = function () {
        $http({method: 'GET', ignoreErrors: true, url: FLOWABLE.APP_URL.getListExternalFormExecutorUrl()})
            .success(function (data, status, headers, config) {
                $scope.options = data.map(function (item) {
                    item.selected = item.id === $scope.property.value;
                    return item;
                })
            })
    };

    $scope.changeValue = function () {
        $scope.updatePropertyInModel($scope.property);
        console.log($scope.property)
    };

    $scope.init();

}]);
