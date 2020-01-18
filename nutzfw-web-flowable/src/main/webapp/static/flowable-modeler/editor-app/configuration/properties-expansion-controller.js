/*
 * user task Expansion
 */
angular.module('flowableModeler').controller('FlowableExpansionCtrl',
    ['$scope', '$modal', '$timeout', '$translate', function ($scope, $modal, $timeout, $translate) {
        // Config for the modal window
        var opts = {
            template: 'editor-app/configuration/properties/expansion-popup.html?version=' + Date.now(),
            scope: $scope
        };
        // Open the dialog
        _internalCreateModal(opts, $modal, $scope);
    }]);
// '$rootScope', '$scope', '$modal', '$http', '$location',
angular.module('flowableModeler').controller('FlowableExpansionPopupCtrl',
    ['$scope', 'editorManager', 'userTaskQueryService', function ($scope, editorManager, userTaskQueryService) {
        let modelJson = editorManager.getModel();
        let resourceId = $scope.selectedShape.resourceId;
        let selectShape = undefined;
        $scope.title = "";
        $scope.activeTab = 1;
        for (let i in modelJson.childShapes) {
            let tempResourceId = modelJson.childShapes[i].resourceId;
            if (tempResourceId != undefined && resourceId === tempResourceId) {
                selectShape = modelJson.childShapes[i];
                $scope.title = selectShape.properties.name;
                break;
            }
        }
        $scope.callBackNodes = [];
        for (let i in modelJson.childShapes) {
            let temp = modelJson.childShapes[i];
            if (temp.resourceId != undefined && temp.stencil.id == "UserTask") {
                $scope.callBackNodes.push({
                    id: temp.resourceId,
                    name: temp.properties.name,
                    checked: false,
                });
            }
        }
        console.log(selectShape.properties)
        //是否是多实例节点
        $scope.multiInstanceNode = false;
        if (selectShape != undefined && selectShape.properties.multiinstance_type != 'None') {
            $scope.multiInstanceNode = true;
        }
        // 将json表示表单属性放在范围上
        if ($scope.property.value !== undefined && $scope.property.value !== null
            && $scope.property.value.expansionProperties !== undefined
            && $scope.property.value.expansionProperties !== null) {
            $scope.expansionProperties = angular.copy($scope.property.value.expansionProperties);
            setDefaualtValue("replyOpinionName", "批复意见");
            setDefaualtValue("replyOpinion", false);
            setDefaualtValue("connectionCallBack", false);
            setDefaualtValue("agreeButtonName", "同意");
            setDefaualtValue("refuseButtonName", "拒绝");
            setDefaualtValue("formDataDynamicAssignment", "");
            setDefaualtValue("beforeCreateCurrentTaskFormDataDynamicAssignment", "");
            setDefaualtValue("afterCreateCurrentTaskFormDataDynamicAssignment", "");
            setDefaualtValue("taskReviewerScope", "");
            setDefaualtValue("iocFlowAssignment", "");
            setDefaualtValue("candidateUsers", []);
            setDefaualtValue("candidateGroups", []);
            setDefaualtValue("multiInstanceLoopCharacteristics", 'None');
        } else {
            $scope.expansionProperties = {
                replyOpinion: false,//是否允许批复意见
                handwritingSignature: false,//是否允许手写签字
                callBackType: 'NONE',//回退类型
                connectionCallBack: false,//连线回退
                callBackNodes: "",//可回退节点
                callBackNodesDesc: "",//可回退节点
                addMultiInstance: false,//是否允许加签
                delMultiInstance: false,//是否允许减签
                delMultiInstanceExecutionIsCompleted: false,//减签后触发父实例完成判断
                agreeButtonName: "同意",//同意按钮文字显示
                refuseButtonName: "拒绝",//拒绝按钮文字显示
                formDataDynamicAssignment: "",//表单数据动态赋值
                beforeCreateCurrentTaskFormDataDynamicAssignment: "",//创建当前任务之前，执行表单数据动态赋值更新
                afterCreateCurrentTaskFormDataDynamicAssignment: "",//创建当前任务之后，执行表单数据动态赋值更新
                taskReviewerScope: "",//指定当前用户步骤任务审核人范围
                assignee: "",//分配给指定用户
                candidateUsers: [],//多个候选用户
                candidateGroups: [],//候选用户角色组
                iocFlowAssignment: '',//JavaIocBean人员选择器
                dynamicFreeChoiceNextReviewerMode: false,//自由选择下一步审核人(下一步流程要确保能通过流程条件正确跳转至用户任务节点)
                signType: false,//通过类型
                signScale: 0,//同意通过比例
                signNrOfInstances: 0,//总实例数量
                signAll: 0,//会签人全部参与处理
                multiInstanceLoopCharacteristics: "None",//会签类型
            };
        }
        $scope.expansionProperties.multiInstanceLoopCharacteristics = selectShape.properties.multiinstance_type;
        let callBackNodes = $scope.expansionProperties.callBackNodes.split(",");
        callBackNodes.forEach(id => {
            for (let i in $scope.callBackNodes) {
                let temp = $scope.callBackNodes[i];
                if (id == temp.id) {
                    $scope.callBackNodes[i].checked = true;
                }
            }
        });

        function setDefaualtValue(field, defaualtValue) {
            if ($scope.expansionProperties[field] == undefined) {
                $scope.expansionProperties[field] = defaualtValue;
            }
        }

        $scope.popup = {
            userResults: [],
            userRoleResults: []
        };
        $scope.$watch('popup.filter', function () {
            $scope.updateFilter();
        });
        $scope.$watch('popup.roleFilter', function () {
            $scope.updateRoleFilter();
        });

        $scope.updateRoleFilter = function () {
            if ($scope.popup.oldFilter == undefined || $scope.popup.oldFilter != $scope.popup.roleFilter) {
                if (!$scope.popup.roleFilter) {
                    $scope.popup.oldFilter = '';
                } else {
                    $scope.popup.oldFilter = $scope.popup.roleFilter;
                }
                if ($scope.popup.roleFilter !== null && $scope.popup.roleFilter !== undefined) {
                    userTaskQueryService.getFilteredUserRoles($scope.popup.roleFilter).then(function (result) {
                        $scope.popup.userRoleResults = result;
                    });
                }
            }
        }
        $scope.updateFilter = function () {
            if ($scope.popup.oldFilter == undefined || $scope.popup.oldFilter != $scope.popup.filter) {
                if (!$scope.popup.filter) {
                    $scope.popup.oldFilter = '';
                } else {
                    $scope.popup.oldFilter = $scope.popup.filter;
                }
                if ($scope.popup.filter !== null && $scope.popup.filter !== undefined) {
                    userTaskQueryService.getFilteredUsers($scope.popup.filter).then(function (result) {
                        $scope.popup.userResults = result;
                    });
                }
            }
        }

        $scope.confirmUser = function (user) {
            if ($scope.expansionProperties.taskReviewerScope == "SINGLE_USER") {
                //分配给指定用户
                $scope.expansionProperties.assignee = user.userName;
            } else if ($scope.expansionProperties.taskReviewerScope == "MULTIPLE_USERS") {
                //多个候选用户
                let isInCandidateUsers = false;
                $scope.expansionProperties.candidateUsers.forEach(value => {
                    if (value.userName == user.userName) {
                        isInCandidateUsers = true;
                    }
                });
                if (!isInCandidateUsers) {
                    $scope.expansionProperties.candidateUsers.push({
                        userName: user.userName,
                        realName: user.realName
                    });
                }
            }
        }
        $scope.removeCandidateUser = function (user) {
            let candidateUsers = [];
            //当前步骤
            $scope.expansionProperties.candidateUsers.forEach(value => {
                if (value.userName != user.userName) {
                    candidateUsers.push(value);
                }
            });
            $scope.expansionProperties.candidateUsers = candidateUsers;
        }
        $scope.confirmRole = function (role) {
            //多个候选用户
            let isInCandidateUsers = false;
            $scope.expansionProperties.candidateGroups.forEach(value => {
                if (value.roleCode == role.roleCode) {
                    isInCandidateUsers = true;
                }
            });
            if (!isInCandidateUsers) {
                $scope.expansionProperties.candidateGroups.push({roleCode: role.roleCode, roleName: role.roleName});
            }
        }

        $scope.removeCandidateGroups = function (user) {
            let candidateGroups = [];
            $scope.expansionProperties.candidateGroups.forEach(value => {
                if (value.roleCode != user.roleCode) {
                    candidateGroups.push(value);
                }
            });
            $scope.expansionProperties.candidateGroups = candidateGroups;
        }
        $scope.updateSelectionRadio = function ($event, id) {
            let checkbox = $event.target;
            for (let i in $scope.callBackNodes) {
                let temp = $scope.callBackNodes[i];
                $scope.callBackNodes[i].checked = false;
                if (temp.id == id) {
                    if (checkbox.checked) {
                        $scope.callBackNodes[i].checked = true;
                        $scope.expansionProperties.callBackNodes = temp.id;
                        $scope.expansionProperties.callBackNodesDesc = temp.name;
                    }
                }
            }
        }
        $scope.updateSelectionCheckbox = function ($event, id) {
            let checkbox = $event.target;
            for (let i in $scope.callBackNodes) {
                let temp = $scope.callBackNodes[i];
                if (temp.id == id) {
                    if (checkbox.checked) {
                        $scope.callBackNodes[i].checked = true;
                    }
                }
            }
            let ids = new Array();
            let names = new Array();
            for (let i in $scope.callBackNodes) {
                let temp = $scope.callBackNodes[i];
                if (temp.checked) {
                    ids.push(temp.id);
                    names.push(temp.name);
                }
            }
            $scope.expansionProperties.callBackNodes = ids.join(",");
            $scope.expansionProperties.callBackNodesDesc = names.join(",");
        };
        $scope.updateCallBackTypeSelection = function () {
            if ($scope.expansionProperties.callBackType == 'PREVIOUS_STEP') {
                $scope.expansionProperties.callBackNodes = "";
                $scope.expansionProperties.callBackNodesDesc = "";
                for (let i in $scope.callBackNodes) {
                    $scope.callBackNodes[i].checked = false;
                }
            }
        }
        // Click handler for save button
        $scope.save = function () {
            if ($scope.expansionProperties) {
                //置空冗余数据
                if ($scope.expansionProperties.taskReviewerScope == "SINGLE_USER") {
                    $scope.expansionProperties.candidateUsers = [];
                    $scope.expansionProperties.candidateGroups = [];
                } else if ($scope.expansionProperties.taskReviewerScope == "MULTIPLE_USERS") {
                    $scope.expansionProperties.assignee = "";
                    $scope.expansionProperties.candidateGroups = [];
                } else if ($scope.expansionProperties.taskReviewerScope == "USER_ROLE_GROUPS") {
                    $scope.expansionProperties.assignee = "";
                    $scope.expansionProperties.candidateUsers = [];
                } else {
                    $scope.expansionProperties.assignee = "";
                    $scope.expansionProperties.candidateUsers = [];
                    $scope.expansionProperties.candidateGroups = [];
                }
                if ($scope.multiInstanceNode) {
                    $scope.expansionProperties.dynamicFreeChoiceNextReviewerMode = false;
                }
                $scope.property.value = {};
                $scope.property.value.expansionProperties = $scope.expansionProperties;
            } else {
                $scope.property.value = null;
            }
            $scope.updatePropertyInModel($scope.property);
            $scope.close();
        };

        $scope.cancel = function () {
            $scope.$hide();
            $scope.property.mode = 'read';
        };

        // Close button handler
        $scope.close = function () {
            $scope.$hide();
            $scope.property.mode = 'read';
        };
    }
    ]);

angular.module('flowableModeler').service('userTaskQueryService', ['$http', '$q',
    function ($http, $q) {

        var httpAsPromise = function (options) {
            var deferred = $q.defer();
            $http(options).success(function (response, status, headers, config) {
                deferred.resolve(response);
            })
                .error(function (response, status, headers, config) {
                    deferred.reject(response);
                });
            return deferred.promise;
        };


        this.getFilteredUsers = function (filterText) {
            var params = {filter: filterText};
            return httpAsPromise({
                method: 'POST',
                url: FLOWABLE.APP_URL.getReviewerUsersUrl(),
                params: params
            });
        };

        this.getFilteredUserRoles = function (filterText) {
            var params = {filter: filterText};
            return httpAsPromise({
                method: 'POST',
                url: FLOWABLE.APP_URL.getReviewerUserRolesUrl(),
                params: params
            });
        };

    }])