<%--

    Cerberus Copyright (C) 2013 - 2017 cerberustesting
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

    This file is part of Cerberus.

    Cerberus is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Cerberus is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ include file="include/global/dependenciesInclusions.html" %>
        <script type="text/javascript" src="dependencies/Tinymce-4.2.6/tinymce.min.js"></script>
        <script type="text/javascript" src="js/pages/Label.js"></script>
        <script type="text/javascript" src="dependencies/Bootstrap-treeview-1.2.0/js/bootstrap-treeview.js"></script>
        <title id="pageTitle">Label</title>
    </head>
    <body>
        <%@ include file="include/global/header.html" %>
        <div class="container-fluid center" id="page-layout">
            <%@ include file="include/global/messagesArea.html"%>
            <%@ include file="include/utils/modal-confirmation.html"%>
            <%@ include file="include/pages/label/addLabel.html"%> 
            <%@ include file="include/pages/label/editLabel.html"%> 

            <h1 class="page-title-line" id="title">Label</h1>
            <div class="panel panel-default">
                <div class="panel-heading" id="labelListLabel">
                    <span class="glyphicon glyphicon-list"></span>
                    Label List
                </div>
                <div class="panel-body" id="labelList">
                    <ul id="tabsScriptEdit" class="nav nav-tabs" data-tabs="tabs">
                        <li class="active"><a data-toggle="tab" href="#tabDetails" id="editTabDetails" name="tabDetails">List</a></li>
                        <li><a data-toggle="tab" href="#tabRequirement" id="editTabRequirement" name="tabRequirement">Requirement</a></li>
                    </ul>
                    <div class="tab-content">
                        <div class="center marginTop25 tab-pane fade in active" id="tabDetails">
                            <table id="labelsTable" class="table table-bordered table-hover display" name="labelsTable"></table>
                            <div class="marginBottom20"></div>
                        </div>
                        <div class="center marginTop25 tab-pane fade in" id="tabRequirement">
                            <div id="requirementTree"></div>
                        </div>
                    </div>
                </div>
            </div>
            <footer class="footer">
                <div class="container-fluid" id="footer"></div>
            </footer>
        </div>
    </body>
</html>
