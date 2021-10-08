/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var FLOWABLE = FLOWABLE || {};
FLOWABLE.PROPERTY_CONFIG =
    {
        "string": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/string-property-write-mode-template.html"
        },
        "boolean": {
            "templateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/boolean-property-template.html"
        },
        "text": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/text-property-write-template.html"
        },
        "flowable-calledelementtype": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/calledelementtype-property-write-template.html"
        },
        "flowable-multiinstance": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/multiinstance-property-write-template.html"
        },
        "flowable-processhistorylevel": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/process-historylevel-property-write-template.html"
        },
        "flowable-externalformexecutor": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/process-flowable-externalformexecutor-property-read-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/process-flowable-externalformexecutor-property-write-template.html"
        },
        "flowable-ordering": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/ordering-property-write-template.html"
        },
        "oryx-dataproperties-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/data-properties-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/data-properties-write-template.html"
        },
        "oryx-formproperties-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/form-properties-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/form-properties-write-template.html"
        },
        "oryx-executionlisteners-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/execution-listeners-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/execution-listeners-write-template.html"
        },
        "oryx-tasklisteners-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/task-listeners-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/task-listeners-write-template.html"
        },
        "oryx-eventlisteners-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/event-listeners-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/event-listeners-write-template.html"
        },
        "oryx-usertaskassignment-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/assignment-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/assignment-write-template.html"
        },
        "oryx-usertaskexpansion-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/expansion-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/expansion-write-template.html"
        },
        "oryx-servicetaskfields-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/fields-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/fields-write-template.html"
        },
        "oryx-callactivityinparameters-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/in-parameters-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/in-parameters-write-template.html"
        },
        "oryx-callactivityoutparameters-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/out-parameters-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/out-parameters-write-template.html"
        },
        "oryx-subprocessreference-subprocess-link": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/subprocess-reference-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/subprocess-reference-write-template.html"
        },
        "oryx-formreference-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/form-reference-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/form-reference-write-template.html"
        },
        "oryx-sequencefloworder-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/sequenceflow-order-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/sequenceflow-order-write-template.html"
        },
        "oryx-conditionsequenceflow-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/condition-expression-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/condition-expression-write-template.html"
        },
        "oryx-signaldefinitions-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/signal-definitions-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/signal-definitions-write-template.html"
        },
        "oryx-signalref-string": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/signal-property-write-template.html"
        },
        "oryx-messagedefinitions-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/message-definitions-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/message-definitions-write-template.html"
        },
        "oryx-messageref-string": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/message-property-write-template.html"
        },
        "oryx-duedatedefinition-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/duedate-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/duedate-write-template.html"
        },
        "oryx-decisiontaskdecisiontablereference-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/decisiontable-reference-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/decisiontable-reference-write-template.html"
        },
        "oryx-casetaskcasereference-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/case-reference-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/case-reference-write-template.html"
        },
        "oryx-processtaskprocessreference-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/process-reference-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/process-reference-write-template.html"
        },
        "oryx-processtaskinparameters-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/in-parameters-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/in-parameters-write-template.html"
        },
        "oryx-processtaskoutparameters-complex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/out-parameters-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/out-parameters-write-template.html"
        },
        "oryx-planitemlifecyclelisteners-multiplecomplex": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/plan-item-lifecycle-listeners-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/plan-item-lifecycle-listeners-write-template.html"
        },
        "flowable-transitionevent": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/default-value-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/transition-event-write-template.html"
        },
        "flowable-planitem-dropdown": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/planitem-dropdown-read-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/planitem-dropdown-write-template.html"
        },
        "flowable-http-request-method": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/http-request-method-display-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/http-request-method-property-write-template.html"
        },
        "flowable-triggermode": {
            "readModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/trigger-mode-read-template.html",
            "writeModeTemplateUrl": FLOWABLE.CONFIG.webContextRoot + "editor-app/configuration/properties/trigger-mode-write-template.html"
        },
    };
