/*
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */

/***
 * Open the modal with testcase information.
 * @param {String} queueID - id of the queue to open the modal
 * @param {String} mode - mode to open the modal. Can take the values : ADD, DUPLICATE, EDIT
 * @returns {null}
 */
function openModalTestCaseExecutionQueue(queueID, mode) {

    // We only load the Labels and bind the events once for performance optimisations.
    if ($('#editExecutionQueueModal').data("initLabel") === undefined) {
        initModalTestcaseExecutionQueue();
        $('#editExecutionQueueModal').data("initLabel", true);
    }

    if (mode === "EDIT") {
        editExecutionQueueClick(queueID);
    } else {
        duplicateExecutionQueueClick(queueID);
    }
}

function initModalTestcaseExecutionQueue() {

    var doc = new Doc();
    $("[name='buttonClose']").html(doc.getDocLabel("page_global", "buttonClose"));
    $("[name='buttonAdd']").html(doc.getDocLabel("page_global", "btn_add"));
    // Tracability
    $("[name='lbl_created']").html(doc.getDocOnline("transversal", "DateCreated"));
    $("[name='lbl_creator']").html(doc.getDocOnline("transversal", "UsrCreated"));
    $("[name='lbl_lastModified']").html(doc.getDocOnline("transversal", "DateModif"));
    $("[name='lbl_lastModifier']").html(doc.getDocOnline("transversal", "UsrModif"));

    $("#submitExecutionQueueButton").off("click");
    $("#submitExecutionQueueButton").click(function () {
        confirmExecutionQueueModalHandler("EDIT", "toQUEUED", "save");
    });

    $("#saveExecutionQueueButton").off("click");
    $("#saveExecutionQueueButton").click(function () {
        confirmExecutionQueueModalHandler("EDIT", "", "save");
    });

    $("#cancelExecutionQueueButton").off("click");
    $("#cancelExecutionQueueButton").click(function () {
        confirmExecutionQueueModalHandler("EDIT", "toCANCELLED", "");
    });

    $("#duplicateExecutionQueueButton").off("click");
    $("#duplicateExecutionQueueButton").click(function () {
        confirmExecutionQueueModalHandler("DUPLICATE", "toQUEUED", "save");
    });

    $("#test").bind("change", function (event) {
        feedTestCase($(this).val(), "#testCase");
    });
    $("#robot").bind("change", function (event) {
        robot_change();
    });
}

/***
 * Open the modal with queue information.
 * @param {String} queueID - type selected
 * @returns {null}
 */
function editExecutionQueueClick(queueID) {

    clearResponseMessage($('#editExecutionQueueModal'));

    // When editing the execution queue, we can modify, modify and run or cancel.
    $('#submitExecutionQueueButton').attr('class', 'btn btn-primary');
    $('#submitExecutionQueueButton').removeProp('hidden');
    $('#saveExecutionQueueButton').attr('class', 'btn btn-primary');
    $('#saveExecutionQueueButton').removeProp('hidden');
    $('#cancelExecutionQueueButton').attr('class', 'btn btn-primary');
    $('#cancelExecutionQueueButton').removeProp('hidden');

    // We cannot duplicate.
    $('#duplicateExecutionQueueButton').attr('class', '');
    $('#duplicateExecutionQueueButton').attr('hidden', 'hidden');

    feedExecutionQueueModal(queueID, "editExecutionQueueModal", "EDIT");
}

/***
 * Open the modal with queue information.
 * @param {String} queueID - id of the queue to duplicate.
 * @returns {null}
 */
function duplicateExecutionQueueClick(queueID) {

    clearResponseMessage($('#editExecutionQueueModal'));

    $('#submitExecutionQueueButton').attr('class', '');
    $('#submitExecutionQueueButton').attr('hidden', 'hidden');
    $('#saveExecutionQueueButton').attr('class', '');
    $('#saveExecutionQueueButton').attr('hidden', 'hidden');
    $('#cancelExecutionQueueButton').attr('class', '');
    $('#cancelExecutionQueueButton').attr('hidden', 'hidden');

    $('#duplicateExecutionQueueButton').attr('class', 'btn btn-primary');
    $('#duplicateExecutionQueueButton').removeProp('hidden');

    feedExecutionQueueModal(queueID, "editExecutionQueueModal", "DUPLICATE");
}

/***
 * Function that support the modal confirmation. Will call servlet to comit the transaction.
 * @param {String} mode - either ADD, EDIT or DUPLICATE in order to define the purpose of the modal.
 * @param {String} queueAction - will be sent in actionState of the servlet in order to trigger the change of state to the Execution queue. ex : "toQUEUED", "toCANCELLED"
 * @param {String} saveAction - will be sent in actionSave of the servlet in order to trigger the save of the data. ex : "save"
 * @returns {null}
 */
function confirmExecutionQueueModalHandler(mode, queueAction, saveAction) {
    clearResponseMessage($('#editExecutionQueueModal'));

    var formEdit = $('#editExecutionQueueModal #editExecutionQueueModalForm');

    // Enable the test combo before submit the form.
    if (mode === 'EDIT') {
        formEdit.find("#id").removeAttr("disabled");
    }
    // Calculate servlet name to call.
    var myServlet = "UpdateTestCaseExecutionQueue";
    if ((mode === "ADD") || (mode === "DUPLICATE")) {
        myServlet = "CreateTestCaseExecutionQueue";
    }

    // Get the header data from the form.
    var data = convertSerialToJSONObject(formEdit.serialize());

    var tcElement = formEdit.find("#testCase");
    if (isEmpty(data.testCase)) {
        tcElement.parents("div.form-group").addClass("has-error");
        var localMessage = new Message("danger", "Please specify a Test Case value !");
        showMessage(localMessage, $('#editExecutionQueueModal'));
        return;
    } else {
        tcElement.parents("div.form-group").removeClass("has-error");
    }

    showLoaderInModal('#editExecutionQueueModal');

    $.ajax({
        url: myServlet,
        async: true,
        method: "POST",
        data: {
            id: data.id,
            tag: data.tag,
            test: data.test,
            testCase: data.testCase,
            environment: data.environment,
            country: data.country,
            manualURL: data.manualURL,
            manualHost: data.manualHost,
            manualContextRoot: data.manualContextRoot,
            manualLoginRelativeURL: data.manualLoginRelativeURL,
            manualEnvData: data.manualEnvData,
            robot: data.robot,
            robotIP: data.robotIP,
            robotPort: data.robotPort,
            browser: data.browser,
            browserVersion: data.browserVersion,
            platform: data.platform,
            screenSize: data.screenSize,
            verbose: data.verbose,
            screenshot: data.screenshot,
            pageSource: data.pageSource,
            seleniumLog: data.seleniumLog,
            timeout: data.timeout,
            retries: data.retries,
            priority: data.priority,
            debugFlag: data.debugFlag,
            manualExecution: data.manualExecution,
            actionState: queueAction,
            actionSave: saveAction
        },
        success: function (data) {
//            data = JSON.parse(data);
            hideLoaderInModal('#editExecutionQueueModal');
            if (getAlertType(data.messageType) === "success") {
                var oTable = $("#executionsTable").dataTable();
                oTable.fnDraw(true);
                $('#editExecutionQueueModal').data("Saved", true);
                $('#editExecutionQueueModal').modal('hide');
                if (data.addedEntries === 1) {
                    data.message = data.message + "<a href='TestCaseExecution.jsp?executionQueueId=" + data.testCaseExecutionQueueList[0].id + "'><button class='btn btn-primary' id='goToExecution'>Get to Execution</button></a>";
                }
                showMessageMainPage(getAlertType(data.messageType), data.message, false, 60000);
            } else {
                showMessage(data, $('#editExecutionQueueModal'));
            }
        },
        error: showUnexpectedError
    });
    if (mode === 'EDIT') { // Disable back the test combo before submit the form.
        formEdit.find("#id").prop("disabled", "disabled");
    }

}

/***
 * Feed the TestCase modal with all the data from the TestCase.
 * @param {String} modalId - type selected
 * @returns {null}
 */
function feedNewExecutionQueueModal(modalId) {
    clearResponseMessageMainPage();

    var formEdit = $('#' + modalId);

    // Feed the data to the screen and manage authorities.
    feedExecutionQueueModalData(undefined, modalId, "ADD", true);

    formEdit.modal('show');
}

/***
 * Feed the TestCase modal with all the data from the TestCase.
 * @param {String} queueid - id of the execution queue to load
 * @param {String} modalId - modal id to feed.
 * @param {String} mode - either ADD, EDIT or DUPLICATE in order to define the purpose of the modal.
 * @returns {null}
 */
function feedExecutionQueueModal(queueid, modalId, mode) {
    clearResponseMessageMainPage();

    var formEdit = $('#' + modalId);

    $.ajax({
        url: "ReadTestCaseExecutionQueue?queueid=" + queueid,
        async: true,
        method: "GET",
        success: function (data) {
            if (data.messageType === "OK") {

                // Feed the data to the screen and manage authorities.
                var exeQ = data.contentTable;
                var hasPermissions = data.hasPermissions;

                if (mode === "EDIT") {
                    // Cannot Cancel an execution that is already cancelled.
                    if (exeQ.state === "CANCELLED") {
                        $('#cancelExecutionQueueButton').attr('class', '');
                        $('#cancelExecutionQueueButton').prop('hidden', 'hidden');
                    }
                    // Cannot modify an execution currently running.
                    if ((exeQ.state === "STARTING") || (exeQ.state === "EXECUTING") || (exeQ.state === "DONE")) {
                        $('#cancelExecutionQueueButton').attr('class', '');
                        $('#cancelExecutionQueueButton').prop('hidden', 'hidden');
                        $('#saveExecutionQueueButton').attr('class', '');
                        $('#saveExecutionQueueButton').prop('hidden', 'hidden');
                        $('#submitExecutionQueueButton').attr('class', '');
                        $('#submitExecutionQueueButton').prop('hidden', 'hidden');
                        hasPermissions = false;
                    }
                    // Cannot resubmit an execution that is already in queue.
                    if ((exeQ.state === "QUEUED")) {
                        $('#submitExecutionQueueButton').attr('class', '');
                        $('#submitExecutionQueueButton').prop('hidden', 'hidden');
                    }
                }

                feedExecutionQueueModalData(exeQ, modalId, mode, hasPermissions);

                formEdit.modal('show');
            } else {
                showUnexpectedError();
            }
        },
        error: showUnexpectedError
    });

}

/***
 * Feed the TestCase select with all the testcase from test defined.
 * @param {String} test - test in order to filter the testcase values.
 * @param {String} selectElement - id of select to refresh.
 * @param {String} defaultTestCase - id of testcase to select.
 * @returns {null}
 */
function feedTestCase(test, selectElement, defaultTestCase) {

    var testCList = $(selectElement);
    testCList.empty();

    var jqxhr = $.getJSON("ReadTestCase", "test=" + test);
    $.when(jqxhr).then(function (data) {

        for (var index = 0; index < data.contentTable.length; index++) {
            testCList.append($('<option></option>').text(data.contentTable[index].testCase + " - " + data.contentTable[index].description).val(data.contentTable[index].testCase));
        }
        if (!isEmpty(defaultTestCase)) {
            testCList.prop("value", defaultTestCase);
        }
    });

}

/***
 * Feed the TestCase modal with all the data from the TestCase.
 * @param {String} exeQ - service object to be loaded.
 * @param {String} modalId - id of the modal form where to feed the data.
 * @param {String} mode - either ADD, EDIT or DUPLICATE in order to define the purpose of the modal.
 * @param {String} hasPermissionsUpdate - boolean if premition is granted.
 * @returns {null}
 */
function feedExecutionQueueModalData(exeQ, modalId, mode, hasPermissionsUpdate) {
    var formEdit = $('#' + modalId);
    var doc = new Doc();
    var isEditable = (((hasPermissionsUpdate) && (mode === "EDIT") && ((exeQ.state === "WAITING") || (exeQ.state === "QUEUED") || (exeQ.state === "ERROR") || (exeQ.state === "CANCELLED")))
            || (mode === "DUPLICATE"));

    console.info(isEditable);
    console.info(mode);
    console.info(hasPermissionsUpdate);
    console.info(exeQ.state);

    formEdit.find("#test").empty();
    formEdit.find("#testCase").empty();


    if (isEditable) {
        var jqxhr = $.getJSON("ReadTest", "");
        $.when(jqxhr).then(function (data) {
            var testList = formEdit.find("#test");

            for (var index = 0; index < data.contentTable.length; index++) {
                testList.append($('<option></option>').text(data.contentTable[index].test).val(data.contentTable[index].test));
            }
            formEdit.find("#test").prop("value", exeQ.test);

            feedTestCase(exeQ.test, "#" + modalId + " #testCase", exeQ.testCase);

        });

    } else {
        //If we cannot edit, we just put the value of the exe queue in the combo. No need to call the test and testcase list.
        var testList = $("#test");
        testList.empty();
        testList.append($('<option></option>').text(exeQ.test).val(exeQ.test));

        var testCaseList = $("#testCase");
        testCaseList.empty();
        testCaseList.append($('<option></option>').text(exeQ.testCase).val(exeQ.testCase));
    }


    formEdit.find("#robot").empty();
    var jqxhr = $.getJSON("ReadRobot", "");
    $.when(jqxhr).then(function (data) {
        var robotList = $("#robot");

        robotList.append($('<option></option>').text("").val(""));
        for (var index = 0; index < data.contentTable.length; index++) {
            robotList.append($('<option></option>').text(data.contentTable[index].robot).val(data.contentTable[index].robot));
        }
        formEdit.find("#robot").prop("value", exeQ.robot);
        if (isEditable) {
            robot_change();
        }
    });

    formEdit.find("#debugFlag").empty();
    displayInvariantList("debugFlag", "QUEUEDEBUGFLAG", false, exeQ.debugFlag);
    formEdit.find("#country").empty();
    displayInvariantList("country", "COUNTRY", false, exeQ.country);
    formEdit.find("#environment").empty();
    displayInvariantList("environment", "ENVIRONMENT", false, exeQ.environment);
    formEdit.find("#browser").empty();
    displayInvariantList("browser", "BROWSER", false, exeQ.browser, "");
    formEdit.find("#platform").empty();
    displayInvariantList("platform", "PLATFORM", false, exeQ.platform, "");

    formEdit.find("#verbose").empty();
    displayInvariantList("verbose", "VERBOSE", false, exeQ.verbose);
    formEdit.find("#screenshot").empty();
    displayInvariantList("screenshot", "SCREENSHOT", false, exeQ.screenshot);
    formEdit.find("#pageSource").empty();
    displayInvariantList("pageSource", "PAGESOURCE", false, exeQ.pageSource);
    formEdit.find("#seleniumLog").empty();
    displayInvariantList("seleniumLog", "SELENIUMLOG", false, exeQ.seleniumLog);
    formEdit.find("#retries").empty();
    displayInvariantList("retries", "RETRIES", false, exeQ.retries);

    formEdit.find("#manualExecution").empty();
    displayInvariantList("manualExecution", "MANUALEXECUTION", false, exeQ.manualExecution);


    formEdit.find("#manualURL").empty();
    displayInvariantList("manualURL", "MANUALURL", false, exeQ.manualURL);
    formEdit.find("#manualHost").prop("value", exeQ.manualHost);
    formEdit.find("#manualContextRoot").prop("value", exeQ.manualContextRoot);
    formEdit.find("#manualLoginRelativeURL").prop("value", exeQ.manualLoginRelativeURL);
    formEdit.find("#manualEnvData").empty();
    displayInvariantList("manualEnvData", "ENVIRONMENT", true, exeQ.manualEnvData, "");

    formEdit.find("#originalId").prop("value", exeQ.id);

    // Data Feed.
    if (mode === "EDIT") {
        $("[name='editSoapLibraryField']").html(doc.getDocOnline("page_global", "btn_duplicate"));
        formEdit.find("#id").prop("value", exeQ.id);
        formEdit.find("#usrcreated").prop("value", exeQ.UsrCreated);
        formEdit.find("#datecreated").prop("value", exeQ.DateCreated);
        formEdit.find("#usrmodif").prop("value", exeQ.UsrModif);
        formEdit.find("#datemodif").prop("value", getDate(exeQ.DateModif));
        formEdit.find("#priority").prop("value", exeQ.priority);
        formEdit.find("#debugFlag").prop("value", exeQ.debugFlag);
    } else { // DUPLICATE or ADD
        formEdit.find("#usrcreated").prop("value", "");
        formEdit.find("#datecreated").prop("value", "");
        formEdit.find("#usrmodif").prop("value", "");
        formEdit.find("#datemodif").prop("value", "");
        formEdit.find("#priority").prop("value", "1000");
        formEdit.find("#debugFlag").prop("value", "N");
        $("[name='editSoapLibraryField']").html(doc.getDocOnline("page_global", "btn_duplicate"));
        formEdit.find("#id").prop("value", exeQ.id);
    }
    if (isEmpty(exeQ)) {
        formEdit.find("#originalid").prop("value", "");
        formEdit.find("#tag").prop("value", "");
        formEdit.find("#requestDate").prop("value", "");
        formEdit.find("#state").prop("value", "GET");
        formEdit.find("#comment").prop("value", "");
        formEdit.find("#exeId").prop("value", "");
        formEdit.find("#test").prop("value", "");
        formEdit.find("#testCase").text("");
        formEdit.find("#country").prop("value", "");
        formEdit.find("#environment").prop("value", "");
        formEdit.find("#robotIP").prop("value", "");
        formEdit.find("#robotPort").prop("value", "");
        formEdit.find("#browserVersion").prop("value", "");
        formEdit.find("#screenSize").prop("value", "");
        formEdit.find("#timeout").prop("value", "");
    } else {
        formEdit.find("#tag").val(exeQ.tag);
        formEdit.find("#requestDate").val(exeQ.requestDate);
        formEdit.find("#state").val(exeQ.state);
        formEdit.find("#comment").prop("value", exeQ.comment);
        formEdit.find("#exeId").prop("value", exeQ.exeId);
        formEdit.find("#test").prop("value", exeQ.test);
        formEdit.find("#testCase").prop("value", exeQ.testCase);
        formEdit.find("#country").prop("value", exeQ.country);
        formEdit.find("#environment").prop("value", exeQ.environment);
        formEdit.find("#robotIP").prop("value", exeQ.robotIP);
        formEdit.find("#robotPort").prop("value", exeQ.robotPort);
        formEdit.find("#browserVersion").prop("value", exeQ.browserVersion);
        formEdit.find("#screenSize").prop("value", exeQ.screenSize);
        formEdit.find("#timeout").prop("value", exeQ.timeout);
    }

    // Authorities
    formEdit.find("#id").prop("disabled", "disabled");

    //We desactivate or activate the access to the fields depending on if user has the credentials to edit.
    if (isEditable) { // If readonly, we readonly all fields
        formEdit.find("#tag").prop("readonly", false);
        formEdit.find("#test").removeAttr("disabled");
        formEdit.find("#testCase").removeAttr("disabled");
        formEdit.find("#environment").removeAttr("disabled");
        formEdit.find("#country").removeAttr("disabled");
        formEdit.find("#manualURL").removeAttr("disabled");
        formEdit.find("#manualHost").prop("readonly", false);
        formEdit.find("#manualContextRoot").prop("readonly", false);
        formEdit.find("#manualLoginRelativeURL").prop("readonly", false);
        formEdit.find("#manualEnvData").removeAttr("disabled");
        formEdit.find("#robot").removeAttr("disabled");
        formEdit.find("#robotIP").prop("readonly", false);
        formEdit.find("#robotPort").prop("readonly", false);
        formEdit.find("#browser").removeAttr("disabled");
        formEdit.find("#browserVersion").prop("readonly", false);
        formEdit.find("#platform").removeAttr("disabled");
        formEdit.find("#screenSize").prop("readonly", false);
        formEdit.find("#verbose").removeAttr("disabled");
        formEdit.find("#screenshot").removeAttr("disabled");
        formEdit.find("#pageSource").removeAttr("disabled");
        formEdit.find("#seleniumLog").removeAttr("disabled");
        formEdit.find("#timeout").prop("readonly", false);
        formEdit.find("#retries").removeAttr("disabled");
        formEdit.find("#manualExecution").removeAttr("disabled");
        formEdit.find("#priority").prop("readonly", false);
        formEdit.find("#debugFlag").removeAttr("disabled");
    } else {
        formEdit.find("#tag").prop("readonly", "readonly");
        formEdit.find("#test").prop("disabled", "disabled");
        formEdit.find("#testCase").prop("disabled", "disabled");
        formEdit.find("#environment").prop("disabled", "disabled");
        formEdit.find("#country").prop("disabled", "disabled");
        formEdit.find("#manualURL").prop("disabled", "disabled");
        formEdit.find("#manualHost").prop("readonly", "readonly");
        formEdit.find("#manualContextRoot").prop("readonly", "readonly");
        formEdit.find("#manualLoginRelativeURL").prop("readonly", "readonly");
        formEdit.find("#manualEnvData").prop("disabled", "disabled");
        formEdit.find("#priority").prop("readonly", "readonly");
        formEdit.find("#debugFlag").prop("disabled", "disabled");
        formEdit.find("#robot").prop("disabled", "disabled");
        formEdit.find("#robotIP").prop("readonly", "readonly");
        formEdit.find("#robotPort").prop("readonly", "readonly");
        formEdit.find("#browser").prop("disabled", "disabled");
        formEdit.find("#browserVersion").prop("readonly", "readonly");
        formEdit.find("#platform").prop("disabled", "disabled");
        formEdit.find("#screenSize").prop("readonly", "readonly");
        formEdit.find("#verbose").prop("disabled", "disabled");
        formEdit.find("#screenshot").prop("disabled", "disabled");
        formEdit.find("#pageSource").prop("disabled", "disabled");
        formEdit.find("#seleniumLog").prop("disabled", "disabled");
        formEdit.find("#timeout").prop("readonly", "readonly");
        formEdit.find("#retries").prop("disabled", "disabled");
        formEdit.find("#manualExecution").prop("disabled", "disabled");
    }
}

function robot_change() {
    var formEdit = $('#editExecutionQueueModal');
    if (!isEmpty(formEdit.find("#robot").val())) {
        formEdit.find("#robotIP").prop("readonly", "readonly");
        formEdit.find("#robotPort").prop("readonly", "readonly");
        formEdit.find("#browser").prop("disabled", "disabled");
        formEdit.find("#browserVersion").prop("readonly", "readonly");
        formEdit.find("#platform").prop("disabled", "disabled");
        formEdit.find("#screenSize").prop("readonly", "readonly");
    } else {
        formEdit.find("#robotIP").prop("readonly", false);
        formEdit.find("#robotPort").prop("readonly", false);
        formEdit.find("#browser").removeAttr("disabled");
        formEdit.find("#browserVersion").prop("readonly", false);
        formEdit.find("#platform").removeAttr("disabled");
        formEdit.find("#screenSize").prop("readonly", false);
    }
}

function enableDisableJob() {
    openModalParameter('cerberus_queueexecution_enable', getSys());
    // Trap closure of modal in order to trigger that refresh.
    displayAndRefresh_jobStatus();
}
