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
package org.cerberus.servlet.crud.testexecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.TestCaseExecution;
import org.cerberus.crud.entity.TestCaseStepAction;
import org.cerberus.crud.entity.TestCaseStepActionControlExecution;
import org.cerberus.crud.entity.TestCaseStepActionExecution;
import org.cerberus.crud.entity.TestCaseStepExecution;
import org.cerberus.crud.service.ITestCaseExecutionService;
import org.cerberus.crud.service.ITestCaseStepActionControlExecutionService;
import org.cerberus.crud.service.ITestCaseStepActionExecutionService;
import org.cerberus.crud.service.ITestCaseStepExecutionService;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.AnswerUtil;
import org.cerberus.util.servlet.ServletUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 *
 * @author bcivel
 */

@WebServlet(name = "UpdateTestCaseExecution", urlPatterns = {"/UpdateTestCaseExecution"})
public class UpdateTestCaseExecution extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(UpdateTestCaseExecution.class);
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException, CerberusException {

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);
        //Parsing and securing all required parameters.
        StringBuilder sb = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        try{
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            JSONObject testCase = new JSONObject(sb.toString());
            //get all element from Json
            ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
            
            updateTestCaseExecutionFromJsonArray(testCase, appContext);
            response.getWriter().print( new MessageEvent(MessageEventEnum.GENERIC_OK) );
        } catch (JSONException e) {
            response.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        } catch (CerberusException e) {
            response.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        } catch (IOException e) {
            response.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        }
    }
    /**
     * update Test case execution with testCaseJson and all the parameter belonging to it (action, control, step)
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     * @throws CerberusException
     */
    void updateTestCaseExecutionFromJsonArray(JSONObject testCaseJson, ApplicationContext appContext) throws JSONException, IOException, CerberusException  {
        JSONArray stepArray = testCaseJson.getJSONArray("stepArray");
        long executionId = testCaseJson.getLong("executionId");
        ITestCaseExecutionService testCaseExecutionService = appContext.getBean(ITestCaseExecutionService.class);
        String returnCodeOfTestCase = updateTestCaseStepExecutionFromJsonArray(stepArray, appContext );
        
        String returnMessage;
        if ( returnCodeOfTestCase.equals("OK") )
            returnMessage = "The test case finished successfully";
        else if ( returnCodeOfTestCase.equals("FA") )
            returnMessage = "The test case failed to be executed because of an action.";
        else if ( returnCodeOfTestCase.equals("KO") )
            returnMessage = "The test case finished, but failed on validations.";
        else 
            returnMessage = "";
        //get testCaseExecution
        TestCaseExecution executionToUpdate = testCaseExecutionService.findTCExecutionByKey(executionId);
        executionToUpdate.setControlStatus(returnCodeOfTestCase);
        executionToUpdate.setControlMessage(returnMessage);
        testCaseExecutionService.updateTCExecution(executionToUpdate);
    }
    
    /**
     * update Step execution with stepArray and all the parameter belonging to it (action, control)
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     */
    String updateTestCaseStepExecutionFromJsonArray(JSONArray stepArray, ApplicationContext appContext) throws JSONException, IOException {
        String returnCodeOfTestCase = "OK";
                    
        for (int i = 0; i < stepArray.length(); i++) {
            JSONObject currentStep = stepArray.getJSONObject(i);
            
            long id = currentStep.getLong("id");
            String test = currentStep.getString("test");
            String testCase = currentStep.getString("testcase");
            int step = currentStep.getInt("step");
            int index = currentStep.getInt("index");
            int sort = 0;
            String loop = currentStep.getString("loop");
            String conditionOper = currentStep.getString("conditionOper");
            String conditionVal1Init = currentStep.getString("conditionVal1Init");
            String conditionVal2Init = currentStep.getString("conditionVal2Init");
            String conditionVal1 = currentStep.getString("conditionVal1");
            String conditionVal2 = currentStep.getString("conditionVal2");
            String batNumExe = "NULL";
            long start = currentStep.getLong("start");
            long end = currentStep.getLong("end");
            long fullStart = currentStep.getLong("fullStart");
            long fullEnd = currentStep.getLong("fullEnd");
            BigDecimal timeElapsed = new BigDecimal(0);//to change
            String returnCode = currentStep.getString("returnCode");
            //update return code if needed
            if ( returnCode.equals("KO") )
                returnCodeOfTestCase ="KO";
            else if ( returnCode.equals("FA") && !returnCodeOfTestCase.equals("KO") )
                returnCodeOfTestCase ="FA";
            String description = currentStep.getString("description");
            //String possibly wrote by the user
            String returnMessage = StringUtil.sanitize( currentStep.getString("returnMessage") );
            if (returnMessage == "Step not executed")//default message unchanged
                returnMessage = "Step executed manually";

            //create this testCaseStepExecution and update the bdd with it
            TestCaseStepExecution currentTestCaseStepExecution = createTestCaseStepExecution(id, test, testCase, step, index, sort, loop, conditionOper, conditionVal1Init, conditionVal2Init, conditionVal1, conditionVal2, batNumExe, start, end, fullStart, fullEnd, timeElapsed, returnCode, returnMessage, description);
            ITestCaseStepExecutionService testCaseStepExecutionService = appContext.getBean(ITestCaseStepExecutionService.class);
            testCaseStepExecutionService.updateTestCaseStepExecution(currentTestCaseStepExecution);
            //update action list belonging to the current Step
            updateTestCaseStepActionFromJsonArray( currentStep.getJSONArray("actionArr"), appContext);
        }
        return returnCodeOfTestCase;
    }
    /**
     * update action execution with testCaseStepActionJson and all the parameter belonging to it (control)
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     */
    void updateTestCaseStepActionFromJsonArray(JSONArray testCaseStepActionJson, ApplicationContext appContext) throws JSONException, IOException {

        for (int i = 0; i < testCaseStepActionJson.length(); i++) {
            JSONObject currentAction = testCaseStepActionJson.getJSONObject(i);

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
            ITestCaseStepActionExecutionService testCaseStepActionExecutionService = appContext.getBean(ITestCaseStepActionExecutionService.class);
            
            testCaseStepActionExecutionService.updateTestCaseStepActionExecution(currentTestCaseStepActionExecution);
            //update the control list belonging to the current Action
            updateTestCaseStepActionControlExecutionFromJsonArray( currentAction.getJSONArray("controlArr"), appContext);

        }
    }
    
    /**
     * update control execution with testCaseStepActionControlJson
     * @param JSONObject testCaseJson
     * @param ApplicationContext appContext
     * @throws JSONException 
     * @throws IOException
     */
    void updateTestCaseStepActionControlExecutionFromJsonArray( JSONArray controlArray, ApplicationContext appContext) throws JSONException, IOException {

        for (int i = 0; i < controlArray.length(); i++) {
            JSONObject currentControl = controlArray.getJSONObject(i);

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
            ITestCaseStepActionControlExecutionService testCaseStepActionControlExecutionService = appContext.getBean(ITestCaseStepActionControlExecutionService.class);

            testCaseStepActionControlExecutionService.updateTestCaseStepActionControlExecution(currentTestCaseStepActionControlExecution);
        }
    }
    
    //create a TestCaseStepExecution with the parameters
    private TestCaseStepExecution createTestCaseStepExecution(long id, String test, String testCase, int step, int index, int sort, String loop, String conditionOper, String conditionVal1Init,
            String conditionVal2Init, String conditionVal1, String conditionVal2, String batNumExe, long start, long end, long fullStart, long fullEnd, BigDecimal timeElapsed,
            String returnCode, String returnMessage, String description) {  
        
        TestCaseStepExecution testCaseStepExecution = new TestCaseStepExecution();
        testCaseStepExecution.setBatNumExe(batNumExe);
        testCaseStepExecution.setEnd(end);
        testCaseStepExecution.setFullEnd(fullEnd);
        testCaseStepExecution.setFullStart(fullStart);
        testCaseStepExecution.setId(id);
        testCaseStepExecution.setReturnCode(returnCode);
        testCaseStepExecution.setStart(start);
        testCaseStepExecution.setStep(step);
        testCaseStepExecution.setIndex(index);
        testCaseStepExecution.setSort(sort);
        testCaseStepExecution.setLoop(loop);
        testCaseStepExecution.setConditionOper(conditionOper);
        testCaseStepExecution.setConditionVal1Init(conditionVal1Init);
        testCaseStepExecution.setConditionVal2Init(conditionVal2Init);
        testCaseStepExecution.setConditionVal1(conditionVal1);
        testCaseStepExecution.setConditionVal2(conditionVal2);
        testCaseStepExecution.setTest(test);
        testCaseStepExecution.setTestCase(testCase);
        testCaseStepExecution.setTimeElapsed(timeElapsed);
        testCaseStepExecution.setDescription(description);
        testCaseStepExecution.setReturnMessage(returnMessage);
        return testCaseStepExecution;
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
        } catch (JSONException ex) {
            LOG.warn(ex);
        } catch (CerberusException ex) {
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
        } catch (JSONException ex) {
            LOG.warn(ex);
        } catch (CerberusException ex) {
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
    
}  
    
  