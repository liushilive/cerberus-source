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
<%-- 
    Document   : ReportingExecutionByTag2
    Created on : 3 août 2015, 11:02:49
    Author     : cerberus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ include file="include/global/dependenciesInclusions.html" %>
        <script type="text/javascript" src="dependencies/Tinymce-4.2.6/tinymce.min.js"></script>
        <link rel="stylesheet" href="css/pages/ReportingExecutionByTag.css" type="text/css"/>
        <script type="text/javascript" src="dependencies/D3js-3.x.x/js/d3.min.js"></script>
        <script type="text/javascript" src="dependencies/D3-tip-0.6.7/js/index.js"></script>
        <script type="text/javascript" src="js/pages/ReportingExecutionByTag.js"></script>
        <script type="text/javascript" src="js/transversalobject/TestCaseExecutionQueue.js"></script>
        <script type="text/javascript" src="js/transversalobject/TestCase.js"></script>
        <title id="pageTitle">Campaign Reporting</title>
    </head>
    <body>
        <%@ include file="include/global/header.html" %>
        <div class="container-fluid center" id="page-layout">
            <%@ include file="include/global/messagesArea.html"%>
            <%@ include file="include/transversalobject/TestCaseExecutionQueue.html"%>
            <%@ include file="include/transversalobject/TestCase.html"%>

            <h1 class="page-title-line" id="title">Execution reporting by tag</h1>
            <div class="row">
                <div class="col-lg-6" id="FiltersPanel">
                    <div class="panel panel-default">
                        <div class="panel-heading card">
                            <span class="fa fa-tag fa-fw"></span>
                            <label id="filters">Filters</label>
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-lg-12" id="filterContainer">
                                    <label for="selectTag">Tag :</label>
                                    <div class="row" id="tagFilter">
                                        <div class="input-group">
                                            <select class="form-control col-lg-7" name="Tag" id="selectTag"></select>
                                            <div class="input-group-btn">
                                                <button type="button" class="btn btn-default" style="margin-left: 10px;" id="loadbutton" onclick="loadAllReports()">Load</button>
                                            </div>
                                        </div>
                                    </div>
                                    <label id="countryLabel" class="bold">Country :</label>
                                    <button id="countrySelectAll" class="glyphicon glyphicon-check" title="select all countries"></button>
                                    <button id="countryUnselectAll" class="glyphicon glyphicon-unchecked" title="unselect all countries"></button>
                                    <div class="form-group" id="countryFilter">
                                    </div>
                                    <label id="statusLabel" class="bold">Status :</label>
                                    <button id="statusSelectAll" class="glyphicon glyphicon-check" title="select all countries"></button>
                                    <button id="statusUnselectAll" class="glyphicon glyphicon-unchecked" title="unselect all countries"></button>
                                    <div class="form-group marginBottom20" id="statusFilter">
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="OK" checked/>
                                            OK
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="KO" checked/>
                                            KO
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="FA" checked/>
                                            FA
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="NA" checked/>
                                            NA
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="NE" checked/>
                                            NE
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="QU" checked/>
                                            QU
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="PE" checked/>
                                            PE
                                        </label>
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="CA" checked/>
                                            CA
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-default" id="BugReportByStatusPanel">
                        <div class="panel-heading card" data-toggle="collapse" data-target="#BugReportByStatus">
                            <span class="fa fa-pie-chart fa-fw"></span>
                            <label id="bugStatus">Bug Status</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="BugReportByStatus">
                            <div class="row">
                                <div class="col-xs-8" id="BugReportDetailTable">
                                    <table id="bugTable" name="bugTable" class="table table-hover display">
                                        <thead id="bugTableHeader">
                                            <tr>
                                                <td style="text-align: center">Bug</td>
                                                <td style="text-align: center">Last Exe</td>
                                                <td style="text-align: center">Test Case</td>
                                                <td style="text-align: center">#Exe</td>
                                            </tr>                                    
                                        </thead>
                                        <tbody id="bugTableBody"></tbody>
                                    </table>
                                </div>
                                <div class="col-xs-4" id="BugReportTable"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6" id="ReportByStatusPanel">
                    <div class="panel panel-default">
                        <div class="panel-heading card" data-toggle="collapse" data-target="#ReportByStatus">
                            <span class="fa fa-pie-chart fa-fw"></span>
                            <label id="reportStatus">Report by Status</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="ReportByStatus">
                            <div class="row">
                                <div class="col-xs-6" id="ReportByStatusTable"></div>
                                <div class="col-xs-6" id="statusChart"></div>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-default">
                        <div class="panel-heading card" data-toggle="collapse" data-target="#TagDetail">
                            <span class="fa fa-pie-chart fa-fw"></span>
                            <label id="TagDetailLab">Tag detail</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="TagDetail">
                            <div class="row">
                                <div class="col-lg-12">
                                    <label for="startExe">Start :</label>
                                    <input type="text" class="form-control" name="startExe" id="startExe" readonly aria-describedby="basic-addon1" >
                                </div>
                            </div>
                            <div class="row" id="panelDuration">
                                <div class="col-lg-6">
                                    <label for="endExe">End : (When all execution queue has been closed)</label>
                                    <input type="text" class="form-control" name="endExe" id="endExe" readonly aria-describedby="basic-addon1" >
                                </div>
                                <div class="col-lg-6">
                                    <label for="durExe">Duration (Minutes) :</label>
                                    <input type="text" class="form-control" name="durExe" id="durExe" readonly aria-describedby="basic-addon1" >
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-lg-12">
                                    <label for="endLastExe">Last Execution :</label>
                                    <input type="text" class="form-control" name="endLastExe" id="endLastExe" readonly aria-describedby="basic-addon1" >
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-lg-6">
                                    <label for="TagUsrCreated">Created by :</label>
                                    <input type="text" class="form-control" name="TagUsrCreated" id="TagUsrCreated" readonly aria-describedby="basic-addon1" >
                                </div>
                                <div class="col-lg-6">
                                    <label for="Tagcampaign">Campaign :</label>
                                    <input type="text" class="form-control" name="Tagcampaign" id="Tagcampaign" readonly aria-describedby="basic-addon1" >
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" id="ReportByFunctionPanel">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading card" data-toggle="collapse" data-target="#functionChart">
                            <span class="fa fa-bar-chart fa-fw"></span>
                            <label id="reportFunction">Report by Function</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="functionChart">
                            <div class="row">
                                <div class="col-xs-12" id="ReportByfunctionChart"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12" id="reportByEnvCountryBrowser">
                    <div class="panel panel-default">
                        <div class="panel-heading card clearfix" data-toggle="collapse" data-target="#reportEnvCountryBrowser">
                            <label id="envCountryBrowser">Report by EnvCountryBrowser</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                            <ul class="nav nav-tabs pull-right">
                                <li class="active" id="graph"><a>Graph</a></li>
                                <li class="" id="tab"><a>Tab</a></li>
                            </ul>
                        </div>
                        <div class="panel-body collapse in" id="reportEnvCountryBrowser">
                            <label id="splitLabel" class="bold">Split by :</label>
                            <div class="form-group marginBottom20" id="splitFilter">
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="env" checked/>
                                    Environment
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="country" checked/>
                                    Country
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="robotDecli" checked/>
                                    Robot Decli
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="app" checked/>
                                    Application
                                </label>
                            </div>
                            <div id="progressEnvCountryBrowser">

                            </div>
                            <div id="summaryTableDiv" style="display: none;">

                                <table id="summaryTable" name="summaryTable" class="table table-hover display">
                                    <thead id="summaryTableHeader">
                                        <tr>
                                            <td id="summaryTableHeaderEnvironment" class="width80">Environment</td>
                                            <td id="summaryTableHeaderCountry" style="text-align: center">Country</td>
                                            <td id="summaryTableHeaderEnvironment" class="width80">Browser</td>
                                            <td id="summaryTableHeaderApplication" style="text-align: center" class="width130 center-text">Application</td>
                                            <td style="text-align: center">OK</td>
                                            <td style="text-align: center">KO</td>
                                            <td style="text-align: center">FA</td>
                                            <td style="text-align: center">NA</td>
                                            <td style="text-align: center">NE</td>
                                            <td style="text-align: center">PE</td>
                                            <td style="text-align: center">CA</td>
                                            <td style="text-align: center">NOT OK</td>
                                            <td style="text-align: center">TOTAL</td>
                                            <td style="text-align: center">% OK</td>
                                            <td style="text-align: center">% KO</td>
                                            <td style="text-align: center">% FA</td>
                                            <td style="text-align: center">% NA</td>
                                            <td style="text-align: center">% NE</td>
                                            <td style="text-align: center">% PE</td>
                                            <td style="text-align: center">% CA</td>
                                            <td class="width80" style="text-align: center">% NOT OK</td>
                                        </tr>                                    
                                    </thead>
                                    <tbody id="summaryTableBody"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12" id="reportByLabel">
                    <div class="panel panel-default">
                        <div class="panel-heading card clearfix" data-toggle="collapse" data-target="#reportLabel">
                            <label id="envCountryBrowser">Report by Label</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="reportLabel">
                            <label id="splitLabel" class="bold">Show :</label>
                            <div class="form-group marginBottom20" id="splitLabelFilter">
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="stickers" checked/>
                                    Stickers
                                </label>
                                <label class="checkbox-inline">
                                    <input type="checkbox" name="requirement" checked/>
                                    Requirement
                                </label>
                            </div>
                            <div id="progressLabel">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12" id="ListPanel">
                    <div class="panel panel-default">
                        <div class="panel-heading card" data-toggle="collapse" data-target="#listReport">
                            <span class="glyphicon glyphicon-list"></span>
                            <label id="List">List</label>
                            <span class="toggle glyphicon glyphicon-chevron-right pull-right"></span>
                        </div>
                        <div class="panel-body collapse in" id="listReport">
                            <div id="tableArea">
                                <form id="massActionForm" name="massActionForm"  title="" role="form">
                                    <table id="listTable" class="table display" name="listTable"></table>
                                </form>
                                <div class="marginBottom20"></div>
                            </div>
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
