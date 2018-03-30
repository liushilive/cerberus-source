/**
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
package org.cerberus.servlet.manualtestcase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Robot;
import org.cerberus.crud.entity.RobotCapability;
import org.cerberus.crud.entity.TestCaseStepAction;
import org.cerberus.crud.entity.TestCaseStepActionControlExecution;
import org.cerberus.crud.entity.TestCaseStepActionExecution;
import org.cerberus.crud.entity.TestCaseStepExecution;
import org.cerberus.crud.entity.TestDataLibData;
import org.cerberus.crud.factory.IFactoryRobot;
import org.cerberus.crud.service.ILogEventService;
import org.cerberus.crud.service.IRobotService;
import org.cerberus.engine.execution.IRecorderService;
import org.cerberus.crud.service.ITestCaseStepActionControlExecutionService;
import org.cerberus.crud.service.ITestCaseStepActionExecutionService;
import org.cerberus.crud.service.impl.LogEventService;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.Answer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author ryltar
 */
@WebServlet(name = "CreateUpdateTestCaseExecutionFile", urlPatterns = {"/CreateUpdateTestCaseExecutionFile"})
public class CreateUpdateTestCaseExecutionFile extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(CreateUpdateTestCaseExecutionFile.class);
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws org.cerberus.exception.CerberusException
     * @throws org.json.JSONException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, CerberusException, JSONException {
        JSONObject jsonResponse = new JSONObject();
        Answer ans = new Answer();
        Gson gson = new Gson();
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
        ans.setResultMessage(msg);
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        response.setContentType("application/json");
        String charset = request.getCharacterEncoding();
        
        Map<String, String> fileData = new HashMap<String, String>();
        FileItem file = null;
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> fields = upload.parseRequest(request);
            Iterator<FileItem> it = fields.iterator();
            if (!it.hasNext()) {
                return;
            }
            while (it.hasNext()) {
                FileItem fileItem = it.next();
                boolean isFormField = fileItem.isFormField();
                if (isFormField) {
                    fileData.put(fileItem.getFieldName(),fileItem.getString("UTF-8"));
                } else {
                    file = fileItem;
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        /**
         * Parsing and securing all required parameters
         * */
         
        // Parameter that needs to be secured --> We SECURE+DECODE them

        String description = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(fileData.get("desc"), null, charset);
        String extension = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(fileData.get("type"), "", charset);
        String fileName = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(fileData.get("fileName"), null, charset);
        Integer fileID = ParameterParserUtil.parseIntegerParam(fileData.get("fileID"), 0);
        Integer idex = ParameterParserUtil.parseIntegerParam(fileData.get("idex"), 0);
        boolean action = fileData.get("action") != null ? true : false;
        
    	ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    	IRecorderService recorderService = appContext.getBean(IRecorderService.class);
    	TestCaseStepActionExecution testCaseStepActionExecution = null;
    	TestCaseStepActionControlExecution testCaseStepActionControlExecution = null;
    	JSONArray obj = null;
    	if (action) {
            obj = new JSONArray(fileData.get("action"));
            testCaseStepActionExecution = updateTestCaseStepActionExecutionFromJsonArray(obj, appContext);
        }else {
            obj = new JSONArray(fileData.get("control"));
            testCaseStepActionControlExecution = updateTestCaseStepActionControlExecutionFromJsonArray(obj, appContext);
        }
    	
    	if(description.isEmpty()) {
        	msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
            msg.setDescription(msg.getDescription().replace("%ITEM%", "manual testcase execution file")
                    .replace("%OPERATION%", "Create/Update")
                    .replace("%REASON%", "desc is missing!"));
            ans.setResultMessage(msg);
        }else {
        	ans = recorderService.recordManuallyFile(testCaseStepActionExecution, testCaseStepActionControlExecution, extension, description, file, idex, fileName,fileID);
        }
    	
    	
        if (ans.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            /**
             * Object created. Adding Log entry.
             */
            ILogEventService logEventService = appContext.getBean(LogEventService.class);
            logEventService.createForPrivateCalls("/CreateUpdateTestCaseExecutionFile", "CREATE", "Create execution file", request);
        }
        

        /**
         * Formating and returning the json result.
         */
        jsonResponse.put("messageType", ans.getResultMessage().getMessage().getCodeString());
        jsonResponse.put("message", ans.getResultMessage().getDescription());

        response.getWriter().print(jsonResponse);
        response.getWriter().flush();

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (CerberusException ex) {
            LOG.warn(ex);
        } catch (JSONException ex) {
            LOG.warn(ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (CerberusException ex) {
            LOG.warn(ex);
        } catch (JSONException ex) {
            LOG.warn(ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    /**
     * update control execution with testCaseStepActionControlJson
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     */
    TestCaseStepActionControlExecution updateTestCaseStepActionControlExecutionFromJsonArray( JSONArray controlArray, ApplicationContext appContext) throws JSONException, IOException {

        JSONObject currentControl = controlArray.getJSONObject(0);

        long id = currentControl.getLong("id");
        String test = currentControl.getString("test");
        String testCase = currentControl.getString("testcase");
        int step = currentControl.getInt("step");
        int index = currentControl.getInt("index");
        int sort = currentControl.getInt("sort");
        int sequence = currentControl.getInt("sequence");
        int controlSequence = currentControl.getInt("control");
        String conditionOper = currentControl.getString("conditionOper");
        String conditionVal1Init = currentControl.getString("conditionVal1Init");
        String conditionVal2Init = currentControl.getString("conditionVal2Init");
        String conditionVal1 = currentControl.getString("conditionVal1");
        String conditionVal2 = currentControl.getString("conditionVal2");
        String control  = currentControl.getString("controlType");
        String value1Init = currentControl.getString("value1init");
        String value2Init = currentControl.getString("value2init");
        String value1 = currentControl.getString("value1");
        String value2 = currentControl.getString("value2");
        String fatal = currentControl.getString("fatal");
        String description = currentControl.getString("description");
        String returnCode = currentControl.getString("returnCode");
        //String wrote by the user
        String returnMessage = StringUtil.sanitize( currentControl.getString("returnMessage") );
        if ( returnMessage.equals("Control executed manually") )//default message unchanged
            returnMessage = "Control executed manually";
        
        long start = currentControl.getLong("start");
        long end = currentControl.getLong("end");
        long fullStart = 0;//currentAction.getLong("fullStart");
        long fullEnd = 0;//currentAction.getLong("fullEnd");

        //create this TestCaseStepActionControlExecution and update the bdd with it
        TestCaseStepActionControlExecution currentTestCaseStepActionControlExecution = createTestCaseStepActionControlExecution(id, test, testCase, step, index,sequence, controlSequence, sort, returnCode, returnMessage, conditionOper, conditionVal1Init, conditionVal2Init, conditionVal1, conditionVal2, control, value1Init, value2Init, value1, value2, fatal, start, end, fullStart, fullEnd, description, null, null);
        return currentTestCaseStepActionControlExecution;
    }
    
    /**
     * update action execution with testCaseStepActionJson and all the parameter belonging to it (control)
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     */
    TestCaseStepActionExecution updateTestCaseStepActionExecutionFromJsonArray(JSONArray testCaseStepActionJson, ApplicationContext appContext) throws JSONException, IOException {

        JSONObject currentAction = testCaseStepActionJson.getJSONObject(0);
        
        long id = currentAction.getLong("id");
        String test = currentAction.getString("test");
        String testCase = currentAction.getString("testcase");
        int step = currentAction.getInt("step");
        int index = currentAction.getInt("index");
        int sort = currentAction.getInt("sort");
        int sequence = currentAction.getInt("sequence");
        String conditionOper = currentAction.getString("conditionOper");
        String conditionVal1Init = currentAction.getString("conditionVal1Init");
        String conditionVal2Init = currentAction.getString("conditionVal2Init");
        String conditionVal1 = currentAction.getString("conditionVal1");
        String conditionVal2 = currentAction.getString("conditionVal2");
        String action = currentAction.getString("action");
        String value1Init = currentAction.getString("value1init");
        String value2Init = currentAction.getString("value2init");
        String value1 = currentAction.getString("value1");
        String value2 = currentAction.getString("value2");
        String forceExeStatus = currentAction.getString("forceExeStatus");
        String description = currentAction.getString("description");
        String returnCode = currentAction.getString("returnCode");
        
        //String wrote by the user
        String returnMessage = StringUtil.sanitize( currentAction.getString("returnMessage") );
        //default message unchanged
        if ( returnMessage.equals("Action not executed") )
            returnMessage = "Action executed manually";
        
        long start = currentAction.getLong("start");
        long end = currentAction.getLong("end");
        long fullStart = 0;//currentAction.getLong("fullStart");
        long fullEnd = 0;//currentAction.getLong("fullEnd");

        //create this testCaseStepActionExecution and update the bdd with it
        TestCaseStepActionExecution currentTestCaseStepActionExecution = createTestCaseStepActionExecution(id, test, testCase, step, index, sequence, sort, returnCode, returnMessage, conditionOper, conditionVal1Init, conditionVal2Init, conditionVal1, conditionVal2, action, value1Init, value2Init, value1, value2, forceExeStatus, start, end, fullStart, fullEnd, null, description, null, null);
        return currentTestCaseStepActionExecution;
    }
    
  //create a TestCaseStepActionControlExecution with the parameters
    private TestCaseStepActionControlExecution createTestCaseStepActionControlExecution(long id, String test, String testCase, int step, int index, int sequence, int controlSequence, int sort,
            String returnCode, String returnMessage,
            String conditionOper, String conditionVal1Init, String conditionVal2Init, String conditionVal1, String conditionVal2,
            String control, String value1Init, String value2Init, String value1, String value2,
            String fatal, long start, long end, long startLong, long endLong,
            String description, TestCaseStepActionExecution testCaseStepActionExecution, MessageEvent resultMessage) {
        
        TestCaseStepActionControlExecution testCaseStepActionControlExecution = new TestCaseStepActionControlExecution();
        testCaseStepActionControlExecution.setId(id);
        testCaseStepActionControlExecution.setTest(test);
        testCaseStepActionControlExecution.setTestCase(testCase);
        testCaseStepActionControlExecution.setStep(step);
        testCaseStepActionControlExecution.setIndex(index);
        testCaseStepActionControlExecution.setSequence(sequence);
        testCaseStepActionControlExecution.setControlSequence(controlSequence);
        testCaseStepActionControlExecution.setSort(sort);
        testCaseStepActionControlExecution.setReturnCode(returnCode);
        testCaseStepActionControlExecution.setReturnMessage(returnMessage);
        testCaseStepActionControlExecution.setConditionOper(conditionOper);
        testCaseStepActionControlExecution.setConditionVal1Init(conditionVal1Init);
        testCaseStepActionControlExecution.setConditionVal2Init(conditionVal2Init);
        testCaseStepActionControlExecution.setConditionVal1(conditionVal1);
        testCaseStepActionControlExecution.setConditionVal2(conditionVal2);
        testCaseStepActionControlExecution.setControl(control);
        testCaseStepActionControlExecution.setValue1(value1);
        testCaseStepActionControlExecution.setValue2(value2);
        testCaseStepActionControlExecution.setValue1Init(value1Init);
        testCaseStepActionControlExecution.setValue2Init(value2Init);
        testCaseStepActionControlExecution.setFatal(fatal);
        testCaseStepActionControlExecution.setStart(start);
        testCaseStepActionControlExecution.setEnd(end);
        testCaseStepActionControlExecution.setStartLong(startLong);
        testCaseStepActionControlExecution.setEndLong(endLong);
        testCaseStepActionControlExecution.setTestCaseStepActionExecution(testCaseStepActionExecution);
        testCaseStepActionControlExecution.setControlResultMessage(resultMessage);
        testCaseStepActionControlExecution.setDescription(description);
        
        return testCaseStepActionControlExecution;
    }  
    
  //create a TestCaseStepActionExecution with the parameters
    private TestCaseStepActionExecution createTestCaseStepActionExecution(long id, String test, String testCase, int step, int index, int sequence, int sort, String returnCode, String returnMessage, 
            String conditionOper, String conditionVal1Init, String conditionVal2Init, String conditionVal1, String conditionVal2, String action, String value1Init, String value2Init, String value1, String value2, 
            String forceExeStatus, long start, long end, long startLong, long endLong, MessageEvent resultMessage, String description, TestCaseStepAction testCaseStepAction, 
            TestCaseStepExecution testCaseStepExecution) {
        
        TestCaseStepActionExecution testCaseStepActionExecution = new TestCaseStepActionExecution();
        testCaseStepActionExecution.setAction(action);
        testCaseStepActionExecution.setEnd(end);
        testCaseStepActionExecution.setEndLong(endLong);
        testCaseStepActionExecution.setId(id);
        testCaseStepActionExecution.setConditionOper(conditionOper);
        testCaseStepActionExecution.setConditionVal1Init(conditionVal1Init);
        testCaseStepActionExecution.setConditionVal2Init(conditionVal2Init);
        testCaseStepActionExecution.setConditionVal1(conditionVal1);
        testCaseStepActionExecution.setConditionVal2(conditionVal2);
        testCaseStepActionExecution.setValue1(value1);
        testCaseStepActionExecution.setValue2(value2);
        testCaseStepActionExecution.setValue1Init(value1Init);
        testCaseStepActionExecution.setValue2Init(value2Init);
        testCaseStepActionExecution.setForceExeStatus(forceExeStatus);
        testCaseStepActionExecution.setReturnCode(returnCode);
        testCaseStepActionExecution.setReturnMessage(returnMessage);
        testCaseStepActionExecution.setSequence(sequence);
        testCaseStepActionExecution.setSort(sort);
        testCaseStepActionExecution.setStart(start);
        testCaseStepActionExecution.setStartLong(startLong);
        testCaseStepActionExecution.setStep(step);
        testCaseStepActionExecution.setIndex(index);
        testCaseStepActionExecution.setTest(test);
        testCaseStepActionExecution.setTestCase(testCase);
        testCaseStepActionExecution.setActionResultMessage(resultMessage);
        testCaseStepActionExecution.setTestCaseStepAction(testCaseStepAction);
        testCaseStepActionExecution.setTestCaseStepExecution(testCaseStepExecution);
        testCaseStepActionExecution.setDescription(description);

        return testCaseStepActionExecution;
    }
}
