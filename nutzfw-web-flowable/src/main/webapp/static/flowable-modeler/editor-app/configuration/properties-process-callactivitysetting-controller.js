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
    console.log("$scope", $scope)
    console.log("selectedShape", $scope.selectedShape)
    console.log("selectedShape", $scope.property)

    $scope.expansionProperties = {};


    $scope.save = function () {

        $scope.property.value = {};
        $scope.property.value.expansionProperties = $scope.expansionProperties;

        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    $scope.cancel = function () {
        $scope.$hide();
        $scope.property.mode = 'read';
    };

    $scope.close = function () {
        $scope.$hide();
        $scope.property.mode = 'read';
    };


}]);
