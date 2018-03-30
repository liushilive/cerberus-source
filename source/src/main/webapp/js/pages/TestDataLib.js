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
$.when($.getScript("js/global/global.js")).then(function () {
    $(document).ready(function () {
        initPage();

      
    });
});

function initPage() {

    var doc = new Doc();
    displayPageLabel();
    
    $('#editTestDataLibModal').on('hidden.bs.modal', {extra: "#editTestLibData"}, buttonCloseHandler);
    
    $('#testCaseListModal').on('hidden.bs.modal', getTestCasesUsingModalCloseHandler);
    
    /*
     * Handles the change of the type when adding a new test data lib entry
     */
   
    $("#editTestDataLibModal #service").change(function () {
        activateSOAPServiceFields("#editTestDataLibModal", $(this).val());
    });

    var configurations = new TableConfigurationsServerSide("listOfTestDataLib", "ReadTestDataLib", "contentTable", aoColumnsFuncTestDataLib("listOfTestDataLib"), [2, 'asc']);

    //creates the main table and draws the management buttons if the user has the permissions
    $.when(createDataTableWithPermissions(configurations, renderOptionsForTestDataLib, "#testdatalib", undefined, true)).then(function () {
        $("#listOfTestDataLib_wrapper div.ColVis .ColVis_MasterButton").addClass("btn btn-default");
    });

}

function activateSOAPServiceFields(modal, serviceValue) {
    if (serviceValue === "") {
        $(modal + " #servicepath").prop("readonly", false);
        $(modal + " #method").prop("readonly", false);
        var editor = ace.edit($(modal + " #envelope")[0]);
        editor.container.style.opacity = 1;
        editor.renderer.setStyle("disabled", false);
    } else {
        $(modal + " #servicepath").prop("readonly", true);
        $(modal + " #method").prop("readonly", true);
        var editor = ace.edit($(modal + " #envelope")[0]);
        editor.container.style.opacity = 0.5;
        editor.renderer.setStyle("disabled", true);
    }
}

/**
 * After table feeds, 
 * @returns {undefined}
 */
function afterTableLoad() {
    $.each($("pre[name='envelopeField']"), function (i, e) {
        //Highlight envelop on modal loading
        var editor = ace.edit($(e).get(0));
        editor.setTheme("ace/theme/chrome");
        editor.getSession().setMode("ace/mode/xml");
        editor.setOptions({
            maxLines: 1,
            showLineNumbers: false,
            showGutter: false,
            highlightActiveLine: false,
            highlightGutterLine: false,
            readOnly: true
        });
        editor.renderer.$cursorLayer.element.style.opacity = 0;
    });
    $.each($("pre[name='scriptField']"), function (i, e) {
        //Highlight envelop on modal loading
        var editor = ace.edit($(e).get(0));
        editor.setTheme("ace/theme/chrome");
        editor.getSession().setMode("ace/mode/sql");
        editor.setOptions({
            maxLines: 1,
            showLineNumbers: false,
            showGutter: false,
            highlightActiveLine: false,
            highlightGutterLine: false,
            readOnly: true
        });
        editor.renderer.$cursorLayer.element.style.opacity = 0;
    });

}

function displayPageLabel() {
    var doc = new Doc();

    displayHeaderLabel(doc);
    displayGlobalLabel(doc);
    displayFooter(doc);
    $("#pageTitle").html(doc.getDocLabel("page_testdatalib", "page_title"));
    $("#title").html(doc.getDocOnline("page_testdatalib", "title"));
    displayFooter(doc);
}

function renderOptionsForTestDataLib(data) {
    //check if user has permissions to perform the add and import operations
    var doc = new Doc();

    if (data["hasPermissions"]) {
        if ($("#createLibButton").length === 0) {
            var contentToAdd = "<div class='marginBottom10'><button id='createLibButton' type='button' class='btn btn-default'><span class='glyphicon glyphicon-plus-sign'></span> ";
            contentToAdd += doc.getDocLabel("page_testdatalib", "btn_create"); //translation for the create button;
            contentToAdd += "</button></div>";

            $("#listOfTestDataLib_wrapper #listOfTestDataLib_length").before(contentToAdd);
            
        	if (window.matchMedia("(max-width: 768px)").matches) {
        		$("#createLibButton").addClass("pull-right")
        	} 
            
            $("#createLibButton").off("click");
            $('#createLibButton').click(function() {
                openModalDataLib(null, undefined, "ADD");
                
            });

        }
    } else {
        $("#testdatalibFirstColumnHeader").html(doc.getDocLabel("page_global", "columnAction"));
    }
}

function deleteTestDataLibHandlerClick() {
    var testDataLibID = $('#confirmationModal').find('#hiddenField1').prop("value");
    var jqxhr = $.post("DeleteTestDataLib", {testdatalibid: testDataLibID}, "json");
    $.when(jqxhr).then(function (data) {
        var messageType = getAlertType(data.messageType);
        if (messageType === "success") {
            //redraw the datatable
            var oTable = $("#listOfTestDataLib").dataTable();
            oTable.fnDraw(true);
            var info = oTable.fnGetData().length;

            if (info === 1) {//page has only one row, then returns to the previous page
                oTable.fnPageChange('previous');
            }

        }
        //show message in the main page
        showMessageMainPage(messageType, data.message, false);
        //close confirmation window
        $('#confirmationModal').modal('hide');
    }).fail(handleErrorAjaxAfterTimeout);
}

function deleteTestDataLibClick(testDataLibID, name, system, environment, country, type) {
    var doc = new Doc();

    var systemLabel = system === '' ? doc.getDocLabel("page_global", "lbl_all") : system;
    var environmentLabel = environment === '' ? doc.getDocLabel("page_global", "lbl_all") : environment;
    var countryLabel = country === '' ? doc.getDocLabel("page_global", "lbl_all") : country;

    var messageComplete = doc.getDocLabel("page_testdatalib", "message_delete").replace("%ENTRY%", name).replace("%ID%", testDataLibID).replace("%SYSTEM%", systemLabel)
            .replace("%ENVIRONMENT%", environmentLabel).replace("%COUNTRY%", countryLabel);
    showModalConfirmation(deleteTestDataLibHandlerClick, undefined, doc.getDocLabel("page_testdatalib_delete", "title"), messageComplete, testDataLibID, "", "", "");

}

/**
 * Function that loads all test cases that are associated with the selected entry 
 * @param {type} testDataLibID testdatalib id
 * @param {type} name entry name
 * @param {type} country where the entry is available
 */


function getTestCasesUsing(testDataLibID, name, country) {
    clearResponseMessageMainPage();
    showLoaderInModal('#testCaseListModal');
    var jqxhr = $.getJSON("ReadTestDataLib", "testdatalibid=" + testDataLibID + "&name=" + name + "&country=" + country);

    var doc = new Doc();

    $.when(jqxhr).then(function (result) {

        $('#testCaseListModal #totalTestCases').text(doc.getDocLabel("page_testdatalib_m_gettestcases", "nrTests") + " " + result["TestCasesList"].length);
        var htmlContent = "";

        $.each(result["TestCasesList"], function (idx, obj) {

            var item = '<b><a class="list-group-item ListItem" data-remote="true" href="#sub_cat' + idx + '" id="cat' + idx + '" data-toggle="collapse" \n\
            data-parent="#sub_cat' + idx + '"><span class="pull-left">' + obj[0] + '</span>\n\
                                        <span style="margin-left: 25px;" class="pull-right">' + doc.getDocLabel("page_testdatalib_m_gettestcases", "nrTestCases") + obj[2] + '</span>\n\
                                        <span class="menu-ico-collapse"><i class="fa fa-chevron-down"></i></span>\n\
                                    </a></b>';
            htmlContent += item;
            htmlContent += '<div class="collapse list-group-submenu" id="sub_cat' + idx + '">';


            $.each(obj[3], function (idx2, obj2) {
                var hrefTest = 'TestCaseScript.jsp?test=' + obj[0] + '&testcase=' + obj2.TestCaseNumber;
                htmlContent += '<span class="list-group-item sub-item ListItem" data-parent="#sub_cat' + idx + '" style="padding-left: 78px;height: 50px;">';
                htmlContent += '<span class="pull-left"><a href="' + hrefTest + '" target="_blank">' + obj2.TestCaseNumber + '- ' + obj2.TestCaseDescription + '</a></span>';
                htmlContent += '<span class="pull-right">' + doc.getDocLabel("page_testdatalib_m_gettestcases", "nrProperties") + " " + obj2.NrProperties + '</span><br/>';
                htmlContent += '<span class="pull-left"> ' + doc.getDocLabel("testcase", "Creator") + ": " + obj2.Creator + ' | '
                        + doc.getDocLabel("testcase", "TcActive") + ": " + obj2.Active + ' | ' + doc.getDocLabel("testcase", "Status") + ": " + obj2.Status + ' | ' +
                        doc.getDocLabel("invariant", "GROUP") + ": " + obj2.Group + ' | ' + doc.getDocLabel("application", "Application") + ": " + obj2.Application + '</span>';
                htmlContent += '</span>';
            });

            htmlContent += '</div>';

        });
        if (htmlContent !== '') {
            $('#testCaseListModal #testCaseListGroup').append(htmlContent);
        }
        hideLoaderInModal('#testCaseListModal');
        $('#testCaseListModal').modal('show');

    }).fail(handleErrorAjaxAfterTimeout);

}


function buttonCloseHandler(event) {
    var modalID = event.data.extra;
    $(modalID).find("#name").attr("disabled", false);
    // reset form values
    $(modalID)[0].reset();
    // remove all errors on the form fields
    $(this).find('div.has-error').removeClass("has-error");
    // clear the response messages of the modal
    clearResponseMessage($(modalID));
}



/**
 * Handler that cleans the test case list modal when it is closed
 */
function getTestCasesUsingModalCloseHandler() {
    //we need to clear the item-groups that were inserted
    $('#testCaseListModal #testCaseListGroup a[id*="cat"]').remove();
    $('#testCaseListModal #testCaseListGroup div[id*="sub_cat"]').remove();
}

function aoColumnsFuncTestDataLib(tableId) {
    var doc = new Doc();

    var aoColumns = [
        {
            "sName": "tdl.TestDataLibID",
            "data": "testDataLibID",
            "bSortable": false,
            "bSearchable": false,
            "sWidth": "150px",
            "title": doc.getDocLabel("testdatalib", "actions"),
            "mRender": function (data, type, obj) {
                var hasPermissions = $("#" + tableId).attr("hasPermissions");
                var editElement = '<button id="editTestDataLib' + data + '"  onclick="openModalDataLib(' + null + ",'" + obj["testDataLibID"] + '\', \'EDIT\'  );" \n\
                                class="editTestDataLib btn btn-default btn-xs margin-right5" \n\
                            name="editTestDataLib" title="' + doc.getDocLabel("page_testdatalib", "tooltip_editentry") + '" type="button">\n\
                            <span class="glyphicon glyphicon-pencil"></span></button>';
                var viewElement = '<button id="editTestDataLib' + data + '"  onclick="openModalDataLib(' + null + ",'" + obj["testDataLibID"] + '\', \'EDIT\'  );" \n\
                                class="editTestDataLib btn btn-default btn-xs margin-right25" \n\
                            name="editTestDataLib" title="' + doc.getDocLabel("page_testdatalib", "tooltip_editentry") + '" type="button">\n\
                            <span class="glyphicon glyphicon-eye-open"></span></button>';
                var deleteElement = '<button onclick="deleteTestDataLibClick(' + obj.testDataLibID + ',\'' + obj.name
                        + '\', ' + '\'' + obj.system + '\', ' + '\'' + obj.environment + '\', ' + '\'' + obj.country + '\', '
                        + '\'' + obj.type + '\');" class="btn btn-default btn-xs margin-right25 " \n\
                            name="deleteTestDataLib" title="' + doc.getDocLabel("page_testdatalib", "tooltip_delete") + '" type="button">\n\
                            <span class="glyphicon glyphicon-trash"></span></button>';
                var duplicateEntryElement = '<button class="btn btn-default btn-xs margin-right5" \n\
                            name="duplicateTestDataLib" title="' + doc.getDocLabel("page_testdatalib", "tooltip_duplicateEntry") + '"\n\
                                 type="button" onclick="openModalDataLib(' + null + ",'" + obj["testDataLibID"] + '\', \'DUPLICATE\'  )">\n\
                                <span class="glyphicon glyphicon-duplicate"></span></button>'; //TODO check if we can add this glyphicon glyphicon-duplicate
                var viewTestCase = '<button class="getTestCasesUsing btn  btn-default btn-xs margin-right5" \n\
                            name="getTestCasesUsing" title="' + doc.getDocLabel("page_testdatalib", "tooltip_gettestcases") + '" type="button" \n\
                            onclick="getTestCasesUsing(' + data + ', \'' + obj.name + '\', \'' + obj.country + '\')"><span class="glyphicon glyphicon-list"></span></button>';

                if (hasPermissions === "true") { //only draws the options if the user has the correct privileges
                    return '<div class="center btn-group width250">' + editElement + duplicateEntryElement + deleteElement + viewTestCase + '</div>';
                } else {
                    return '<div class="center btn-group width250">' + viewElement + viewTestCase + '</div>';
                }
            }
        },
        {
            "sName": "tdl.TestDataLibID",
            "like":true,
            "data": "testDataLibID",
            "sWidth": "50px",
            "title": doc.getDocOnline("testdatalib", "testdatalibid")
        },
        {
            "sName": "tdl.Name",
            "like":true,
            "data": "name",
            "sWidth": "200px",
            "title": doc.getDocOnline("testdatalib", "name")
        },
        {
            "sName": "tdl.System",
            "data": "system",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "system")
        },
        {
            "sName": "tdl.Environment",
            "data": "environment",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "environment")
        },
        {
            "sName": "tdl.Country",
            "data": "country",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "country")
        },
        {
            "sName": "tdl.Group",
            "like":true,
            "data": "group",
            "sWidth": "100px",
            "title": doc.getDocOnline("testdatalib", "group")
        },
        {
            "sName": "tdl.Description",
            "like":true,
            "data": "description",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "description")
        },
        {
            "sName": "tdl.Type",
            "data": "type",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "type")
        },
        {
            "sName": "tdd.value",
            "like":true,
            "data": "subDataValue",
            "sWidth": "150px",
            "bSortable": false,
            "title": doc.getDocOnline("testdatalibdata", "value")
        },
        {
            "sName": "tdl.Database",
            "data": "database",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "database")
        },
        {"data": "script", "sName": "tdl.Script", "sWidth": "450px", "title": doc.getDocLabel("testdatalib", "script"),
            "mRender": function (data, type, obj) {
                return $("<div></div>").append($("<pre name='scriptField' style='height:20px; overflow:hidden; text-overflow:clip; border: 0px; padding:0; margin:0'></pre>").text(obj['script'])).html();
            }},
        {
            "sName": "tdl.DatabaseUrl",
            "data": "databaseUrl",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "databaseUrl")
        },
        {
            "sName": "tdl.Service",
            "data": "service",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "service")
        },
        {
            "sName": "tdl.ServicePath",
            "like":true,
            "data": "servicePath",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "servicepath")
        },
        {
            "sName": "tdl.method",
            "like":true,
            "data": "method",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "method")
        },
        {
            "data": "envelope", "sName": "tdl.envelope", "like":true, "title": doc.getDocLabel("testdatalib", "envelope"), "sWidth": "350px",
            "mRender": function (data, type, obj) {
                return $("<div></div>").append($("<pre name='envelopeField' style='height:20px; overflow:hidden; text-overflow:clip; border: 0px; padding:0; margin:0'></pre>").text(obj['envelope'])).html();
            }
        },
        {
            "sName": "tdl.DatabaseCsv",
            "data": "databaseCsv",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "databaseCsv")
        },
        {
            "sName": "tdl.csvUrl",
            "like":true,
            "data": "csvUrl",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "csvUrl")
        },
        {
            "sName": "tdl.separator",
            "data": "separator",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "separator")
        },
        {
            "sName": "tdl.Created",
            "like":true,
            "data": "created",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "created")
        },
        {
            "sName": "tdl.Creator",
            "data": "creator",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "creator")
        },
        {
            "sName": "tdl.LastModified",
            "like":true,
            "data": "lastModified",
            "sWidth": "150px",
            "title": doc.getDocOnline("testdatalib", "lastmodified"),
            "mRender": function (data, type, oObj) {
                return getDate(oObj["lastModified"]);
            }
        },
        {
            "sName": "tdl.LastModifier",
            "data": "lastModifier",
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalib", "lastmodifier")
        },
        {
            "sName": "tdd.column",
            "like":true,
            "data": "subDataColumn",
            "bSortable": false,
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalibdata", "column")
        },
        {
            "sName": "tdd.ParsingAnswer",
            "like":true,
            "data": "subDataParsingAnswer",
            "bSortable": false,
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalibdata", "parsingAnswer")
        },
        {
            "sName": "tdd.ColumnPosition",
            "data": "subDataColumnPosition",
            "bSortable": false,
            "sWidth": "70px",
            "title": doc.getDocOnline("testdatalibdata", "columnPosition")
        }
    ];

    return aoColumns;

}

