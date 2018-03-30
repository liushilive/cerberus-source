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
        $('[data-toggle="popover"]').popover({
            'placement': 'auto',
            'container': 'body'}
        );
    });
});

function initPage() {
    displayPageLabel();
    // handle the click for specific action buttons
    $("#addLabelButton").click(addEntryModalSaveHandler);
    $("#editLabelButton").click(editEntryModalSaveHandler);

    //clear the modals fields when closed
    $('#addLabelModal').on('hidden.bs.modal', addEntryModalCloseHandler);
    $('#editLabelModal').on('hidden.bs.modal', editEntryModalCloseHandler);

    $('#editLabelModal #editLabelModalForm #type').on('change', showHideRequirementPanel);

    tinymce.init({
        selector: ".wysiwyg"
    });

    //configure and create the dataTable
    var configurations = new TableConfigurationsServerSide("labelsTable", "ReadLabel?system=" + getUser().defaultSystem, "contentTable", aoColumnsFunc("labelsTable"), [2, 'asc']);
    createDataTableWithPermissions(configurations, renderOptionsForLabel, "#labelList", undefined, true);
}

function displayPageLabel() {
    var doc = new Doc();

    displayHeaderLabel(doc);
    $("#pageTitle").html(doc.getDocLabel("page_label", "title"));
    $("#title").html(doc.getDocOnline("page_label", "title"));
    $("[name='createLabelField']").html(doc.getDocLabel("page_label", "btn_create"));
    $("[name='confirmationField']").html(doc.getDocLabel("page_label", "btn_delete"));
    $("[name='editLabelField']").html(doc.getDocLabel("page_label", "btn_edit"));
    $("[name='buttonAdd']").html(doc.getDocLabel("page_global", "buttonAdd"));
    $("[name='buttonClose']").html(doc.getDocLabel("page_global", "buttonClose"));
    $("[name='buttonConfirm']").html(doc.getDocLabel("page_global", "buttonConfirm"));
    $("[name='buttonDismiss']").html(doc.getDocLabel("page_global", "buttonDismiss"));
    $("[name='labelField']").html(doc.getDocOnline("label", "label"));
    $("[name='descriptionField']").html(doc.getDocOnline("label", "description"));
    $("[name='colorField']").html(doc.getDocOnline("label", "color"));
    $("[name='typeField']").html(doc.getDocOnline("label", "type"));
    $("[name='reqtypeField']").html(doc.getDocOnline("label", "reqtype"));
    $("[name='reqstatusField']").html(doc.getDocOnline("label", "reqstatus"));
    $("[name='reqcriticityField']").html(doc.getDocOnline("label", "reqcriticity"));
    $("[name='longdescField']").html(doc.getDocOnline("label", "longdesc"));
    $("[name='parentLabelField']").html(doc.getDocOnline("label", "parentid"));
    $("[name='tabsEdit1']").html(doc.getDocOnline("page_label", "tabDef"));
    $("[name='tabsEdit2']").html(doc.getDocOnline("page_label", "tabEnv"));

    displayInvariantList("system", "SYSTEM", false, '', '');
    displayInvariantList("type", "LABELTYPE", false);
    displayInvariantList("reqtype", "REQUIREMENTTYPE", false);
    displayInvariantList("reqstatus", "REQUIREMENTSTATUS", false);
    displayInvariantList("reqcriticity", "REQUIREMENTCRITICITY", false);

    refreshParentLabelCombo($("#type").val());
    $("#type").change(function () {
        refreshParentLabelCombo($("#type").val());
    });
    displayFooter(doc);
    generateLabelTree();
}

function generateLabelTree() {
    $.when($.ajax("ReadLabel?iColumns=1&sColumns=type,type&sSearch_0=REQUIREMENT")).then(function (data) {
        var treeObj = new Object();
        for (var i = 0; i < data.contentTable.length; i++) {
            //1st : Create the object
            var ele = new Object();
            ele.text = "<span class='label label-primary' style='background-color:" + data.contentTable[i].color + "' data-toggle='tooltip' data-labelid='" + data.contentTable[i].id + "' title='' data-original-title=''>" + data.contentTable[i].label + "</span>";
            ele.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].reqType + "</span>";
            ele.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].reqStatus + "</span>";
            ele.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].reqCriticity + "</span>";
            //if label has parent label
            if (data.contentTable[i].parentLabel !== "") {
                //if parentLabel already created, find it and add a child
            	
            	if(data.contentTable[i].labelParentObject){
            		if (treeObj.hasOwnProperty(data.contentTable[i].labelParentObject.id)) {
                        var existingParent = treeObj[data.contentTable[i].labelParentObject.id];
                        if (existingParent.nodes === undefined) {
                            existingParent.nodes = [ele];
                        } else {
                            existingParent.nodes.push(ele);
                        }
                        existingParent.tags = [existingParent.tags === undefined ? 1 : parseInt(existingParent.tags) + 1];
                    }else {
                        //else create parent object and add child
                        var parent = new Object();
                        if (parent.nodes === undefined) {
                            parent.nodes = [ele];
                        } else {
                            parent.nodes.push(ele);
                        }

                        parent.text = "<span class='label label-primary' style='background-color:" + data.contentTable[i].labelParentObject.color + "' data-toggle='tooltip' data-labelid='" + data.contentTable[i].labelParentObject.id + "' title='' data-original-title=''>" + data.contentTable[i].labelParentObject.label + "</span>";
                        parent.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].labelParentObject.reqType + "</span>";
                        parent.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].labelParentObject.reqStatus + "</span>";
                        parent.text += "<span class='badge badge-pill badge-secondary'>" + data.contentTable[i].labelParentObject.reqCriticity + "</span>";
                        parent.tags = [1];
                        treeObj[data.contentTable[i].labelParentObject.id] = parent;
                    }
            	}
            	
                 
            } else {
                //if no parent label, push the object if not already exists
                if (!treeObj.hasOwnProperty(data.contentTable[i].id)) {
                    treeObj[data.contentTable[i].id] = ele;
                }
            }
        }
        $('#requirementTree').empty();
        $('#requirementTree').treeview({data: Object.values(treeObj),
            showTags: true});
    });
}


function refreshParentLabelCombo(type) {
    $("[name='parentLabel']").select2(getComboConfigLabel(type));
}

function renderOptionsForLabel(data) {
    var doc = new Doc();
    //check if user has permissions to perform the add and import operations
    if (data["hasPermissions"]) {
        if ($("#createLabelButton").length === 0) {
            var contentToAdd = "<div class='marginBottom10'><button id='createLabelButton' type='button' class='btn btn-default'>\n\
            <span class='glyphicon glyphicon-plus-sign'></span> " + doc.getDocLabel("page_label", "btn_create") + "</button></div>";
            $("#labelsTable_wrapper div#labelsTable_length").before(contentToAdd);
            $('#labelList #createLabelButton').click(addEntryClick);
        }
    }
}

function showHideRequirementPanel() {

    refreshParentLabelCombo($('#editLabelModal #editLabelModalForm #type').val());
    if ($('#editLabelModal #editLabelModalForm #type').val() === "REQUIREMENT") {
        $("#panelReq").show();
    } else {
        $("#panelReq").hide();
    }

}

function deleteEntryHandlerClick() {
    var idLabel = $('#confirmationModal').find('#hiddenField1').prop("value");
    var jqxhr = $.post("DeleteLabel", {id: idLabel}, "json");
    $.when(jqxhr).then(function (data) {
        var messageType = getAlertType(data.messageType);
        if (messageType === "success") {
//redraw the datatable
            var oTable = $("#labelsTable").dataTable();
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

function deleteEntryClick(id, label) {
    clearResponseMessageMainPage();
    var doc = new Doc();
    var messageComplete = doc.getDocLabel("page_global", "message_delete");
    messageComplete = messageComplete.replace("%ENTRY%", id + " - " + label);
    messageComplete = messageComplete.replace("%TABLE%", " label ");
    showModalConfirmation(deleteEntryHandlerClick, undefined, doc.getDocLabel("page_label", "btn_delete"), messageComplete, id, "", "", "");
}

function addEntryModalSaveHandler() {
    clearResponseMessage($('#addLabelModal'));
    var formAdd = $("#addLabelModal #addLabelModalForm");
    var nameElement = formAdd.find("#addLabelModalForm");
    var nameElementEmpty = nameElement.prop("value") === '';
    if (nameElementEmpty) {
        var localMessage = new Message("danger", "Please specify label!");
        nameElement.parents("div.form-group").addClass("has-error");
        showMessage(localMessage, $('#addLabelModal'));
    } else {
        nameElement.parents("div.form-group").removeClass("has-error");
    }

// verif if all mendatory fields are not empty
    if (nameElementEmpty)
        return;
    // Get the header data from the form.
    var dataForm = convertSerialToJSONObject(formAdd.serialize());
    showLoaderInModal('#addLabelModal');
    var jqxhr = $.post("CreateLabel", dataForm);
    $.when(jqxhr).then(function (data) {
        hideLoaderInModal('#addLabelModal');
//        console.log(data.messageType);
        if (getAlertType(data.messageType) === 'success') {
            var oTable = $("#labelsTable").dataTable();
            oTable.fnDraw(true);
            showMessage(data);
            $('#addLabelModal').modal('hide');
        } else {
            showMessage(data, $('#addLabelModal'));
        }
    }).fail(handleErrorAjaxAfterTimeout);
}

function addEntryModalCloseHandler() {
// reset form values
    $('#addLabelModal #addLabelModalForm')[0].reset();
    // remove all errors on the form fields
    $(this).find('div.has-error').removeClass("has-error");
    // clear the response messages of the modal
    clearResponseMessage($('#addLabelModal'));
}

function addEntryClick() {
    clearResponseMessageMainPage();
    // When creating a new label, System takes the default value of the 
    // system already selected in header.
    var formAdd = $('#addLabelModal');
    formAdd.find("#system").prop("value", getUser().defaultSystem);
    $('#addLabelModal').modal('show');
    //ColorPicker
    $("[name='colorDiv']").colorpicker();
    $("[name='colorDiv']").colorpicker('setValue', '#000000');
}

function editEntryModalSaveHandler() {
    clearResponseMessage($('#editLabelModal'));
    var formEdit = $('#editLabelModal #editLabelModalForm');
    tinyMCE.triggerSave();
    // Get the header data from the form.
    var data = convertSerialToJSONObject(formEdit.serialize());
    showLoaderInModal('#editLabelModal');
    $.ajax({
        url: "UpdateLabel",
        async: true,
        method: "POST",
        data: {id: data.id,
            label: data.label,
            color: data.color,
            parentLabel: data.parentLabel,
            system: data.system,
            type: data.type,
            longdesc: data.longdesc,
            reqtype: data.reqtype,
            reqstatus: data.reqstatus,
            reqcriticity: data.reqcriticity,
            description: data.description},
        success: function (data) {
            hideLoaderInModal('#editLabelModal');
            if (getAlertType(data.messageType) === "success") {
                var oTable = $("#labelsTable").dataTable();
                oTable.fnDraw(true);
                $('#editLabelModal').modal('hide');
                showMessage(data);
            } else {
                showMessage(data, $('#editLabelModal'));
            }
        },
        error: showUnexpectedError
    });
}

function editEntryModalCloseHandler() {
// reset form values
    $('#editLabelModal #editLabelModalForm')[0].reset();
    // remove all errors on the form fields
    $(this).find('div.has-error').removeClass("has-error");
    // clear the response messages of the modal
    clearResponseMessage($('#editLabelModal'));
}

function editEntryClick(id, system) {
    clearResponseMessageMainPage();
    var jqxhr = $.getJSON("ReadLabel", "id=" + id);
    $.when(jqxhr).then(function (data) {
        var obj = data["contentTable"];
        var formEdit = $('#editLabelModal');
        formEdit.find("#id").prop("value", id);
        formEdit.find("#label").prop("value", obj["label"]);
        formEdit.find("#color").prop("value", obj["color"]);
        formEdit.find("#type").prop("value", obj["type"]);
        formEdit.find("#description").prop("value", obj["description"]);
        formEdit.find("#longdesc").prop("value", obj["longDesc"]);
        formEdit.find("#reqtype").prop("value", obj["reqType"]);
        formEdit.find("#reqstatus").prop("value", obj["reqStatus"]);
        formEdit.find("#reqcriticity").prop("value", obj["reqCriticity"]);
        formEdit.find("#system").prop("value", obj["system"]);
        if (tinyMCE.get('longdesc') != null)
            tinyMCE.get('longdesc').setContent(obj["longDesc"]);
        if (!(data["hasPermissions"])) { // If readonly, we only readonly all fields
            formEdit.find("#label").prop("readonly", "readonly");
            formEdit.find("#color").prop("readonly", "readonly");
            formEdit.find("#parentLabel").prop("disabled", "disabled");
            formEdit.find("#description").prop("disabled", "disabled");
            formEdit.find("#system").prop("disabled", "disabled");
            $('#editLabelButton').attr('class', '');
            $('#editLabelButton').attr('hidden', 'hidden');
        }
        
        $("#editLabelModal #editLabelModalForm #parentLabel").val(obj.labelParentObject===undefined?"":obj.labelParentObject.id).trigger('change');
        

//ColorPicker
        $("[name='colorDiv']").colorpicker();
        $("[name='colorDiv']").colorpicker('setValue', obj["color"]);
        showHideRequirementPanel();
        formEdit.modal('show');
    });
}

function aoColumnsFunc(tableId) {
    var doc = new Doc();
    var aoColumns = [
        {"data": null,
            "title": doc.getDocLabel("page_global", "columnAction"),
            "bSortable": false,
            "bSearchable": false,
            "sWidth": "50px",
            "mRender": function (data, type, obj) {
                var hasPermissions = $("#" + tableId).attr("hasPermissions");
                var editLabel = '<button id="editLabel" onclick="editEntryClick(\'' + obj["id"] + '\', \'' + obj["system"] + '\');"\n\
                                    class="editLabel btn btn-default btn-xs margin-right5" \n\
                                    name="editLabel" title="' + doc.getDocLabel("page_label", "btn_edit") + '" type="button">\n\
                                    <span class="glyphicon glyphicon-pencil"></span></button>';
                var viewLabel = '<button id="editLabel" onclick="editEntryClick(\'' + obj["id"] + '\', \'' + obj["system"] + '\');"\n\
                                    class="editLabel btn btn-default btn-xs margin-right5" \n\
                                    name="editLabel" title="' + doc.getDocLabel("page_label", "btn_view") + '" type="button">\n\
                                    <span class="glyphicon glyphicon-eye-open"></span></button>';
                var deleteLabel = '<button id="deleteLabel" onclick="deleteEntryClick(\'' + obj["id"] + '\',\'' + obj["label"] + '\');" \n\
                                    class="deleteLabel btn btn-default btn-xs margin-right5" \n\
                                    name="deleteLabel" title="' + doc.getDocLabel("page_label", "btn_delete") + '" type="button">\n\
                                    <span class="glyphicon glyphicon-trash"></span></button>';
                if (hasPermissions === "true") { //only draws the options if the user has the correct privileges
                    return '<div class="center btn-group width150">' + editLabel + deleteLabel + '</div>';
                }
                return '<div class="center btn-group width150">' + viewLabel + '</div>';
            }
        },
        {"data": "id",
            "like": true,
            "sWidth": "30px",
            "sName": "id",
            "title": doc.getDocOnline("label", "id")},
        {"data": "system",
            "sWidth": "50px",
            "sName": "system",
            "title": doc.getDocOnline("label", "system")},
        {"data": "label",
            "sWidth": "50px",
            "sName": "label",
            "title": doc.getDocOnline("label", "label")},
        {"data": "longDesc",
            "like": true,
            "sWidth": "100px",
            "sName": "longDesc",
            "title": doc.getDocOnline("label", "longdesc")},
        {"data": "description",
            "like": true,
            "sWidth": "100px",
            "sName": "description",
            "title": doc.getDocOnline("label", "description")},
        {"data": "type",
            "sWidth": "50px",
            "sName": "type",
            "title": doc.getDocOnline("label", "type")},
        {"data": "color",
            "sWidth": "30px",
            "like": true,
            "sName": "color",
            "title": doc.getDocOnline("label", "color")},
        {"data": "display",
            "sWidth": "80px",
            "sName": "display",
            "title": doc.getDocOnline("page_label", "display"),
            "bSortable": false,
            "bSearchable": false,
            "render": function (data, type, full, meta) {
                return '<span class="label label-primary" style="background-color:' + data.color + '">' + data.label + '</span> ';
            }
        },
        {"sName": "parentLabel",
            "sWidth": "80px",
            "title": doc.getDocOnline("label", "parentid"),
            "data": function (data, type, full, meta) {
                if (data.labelParentObject !== undefined) {
                    //return '<span class="label label-primary" style="background-color:' + data.display.color + '">' + data.display.label + '</span> ';
                    return '<div style="float:left"><span class="label label-primary" onclick="filterOnLabel(this)" style="cursor:pointer;background-color:' + data.labelParentObject.color + '" data-toggle="tooltip" data-labelid="' + data.labelParentObject.id + '" title="' + data.labelParentObject.description + '">' + data.labelParentObject.label + '</span></div> ';
                } else {

                    return '';
                }
            }},
        {"data": "reqType",
            "sWidth": "30px",
            "sName": "reqType",
            "title": doc.getDocOnline("label", "reqtype")},
        {"data": "reqStatus",
            "sWidth": "30px",
            "sName": "reqStatus",
            "title": doc.getDocOnline("label", "reqstatus")},
        {"data": "reqCriticity",
            "sWidth": "30px",
            "sName": "reqCriticity",
            "title": doc.getDocOnline("label", "reqcriticity")},
        {"data": "usrCreated",
            "sWidth": "30px",
            "sName": "usrCreated",
            "title": doc.getDocOnline("transversal", "UsrCreated")},
        {"data": "dateCreated",
            "like": true,
            "sWidth": "80px",
            "sName": "dateCreated",
            "title": doc.getDocOnline("transversal", "DateCreated")},
        {"data": "usrModif",
            "sWidth": "30px",
            "sName": "usrModif",
            "title": doc.getDocOnline("transversal", "UsrModif")
        },
        {"data": "dateModif",
            "like": true,
            "sWidth": "80px",
            "sName": "dateModif",
            "title": doc.getDocOnline("transversal", "DateModif")
        }

    ];
    return aoColumns;
}

function filterOnLabel(element) {
    var newLabel = $(element).attr('data-labelid');
    var colIndex = $(element).parent().parent().get(0).cellIndex;
    $("#labelsTable").dataTable().fnFilter(newLabel, colIndex);
}

function afterTableLoad() {
    generateLabelTree();
}

