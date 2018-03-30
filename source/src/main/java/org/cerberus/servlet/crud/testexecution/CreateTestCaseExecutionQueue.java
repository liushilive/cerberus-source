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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.TestCaseExecutionQueue;
import org.cerberus.crud.service.ILogEventService;
import org.cerberus.crud.service.ITestCaseExecutionQueueService;
import org.cerberus.crud.factory.IFactoryTestCaseExecutionQueue;
import org.cerberus.crud.service.ITagService;
import org.cerberus.crud.service.impl.LogEventService;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.engine.threadpool.IExecutionThreadPoolService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.exception.FactoryCreationException;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerUtil;
import org.cerberus.util.servlet.ServletUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author bcivel
 */
@WebServlet(name = "CreateTestCaseExecutionQueue", urlPatterns = {"/CreateTestCaseExecutionQueue"})
public class CreateTestCaseExecutionQueue extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(CreateTestCaseExecutionQueue.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, CerberusException, JSONException {
        JSONObject jsonResponse = new JSONObject();
        JSONObject executionQueue = new JSONObject();
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        Answer ans = new Answer();
        AnswerItem ansItem = new AnswerItem();
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
        ans.setResultMessage(msg);
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        String charset = request.getCharacterEncoding();

        response.setContentType("application/json");

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);

        /**
         * Parsing and securing all required parameters.
         */
        // Parameter that are already controled by GUI (no need to decode) --> We SECURE them
        String actionState = policy.sanitize(request.getParameter("actionState"));
        String actionSave = policy.sanitize(request.getParameter("actionSave"));
        String environment = policy.sanitize(request.getParameter("environment"));
        String country = policy.sanitize(request.getParameter("country"));
        String manualEnvData = policy.sanitize(request.getParameter("manualEnvData"));
        // Parameter that needs to be secured --> We SECURE+DECODE them
        String test = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("test"), "", charset);
        String testcase = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("testCase"), "", charset);
        int manualURL = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("manualURL"), 0, charset);
        String manualHost = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("manualHost"), "", charset);
        String manualContextRoot = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("manualContextRoot"), "", charset);
        String manualLoginRelativeURL = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("manualLoginRelativeURL"), "", charset);
        String tag = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("tag"), "", charset);
        String robot = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("robot"), "", charset);
        String robotIP = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("robotIP"), "", charset);
        String robotPort = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("robotPort"), "", charset);
        String browser = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("browser"), "", charset);
        String browserVersion = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("browserVersion"), "", charset);
        String platform = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("platform"), "", charset);
        String screenSize = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("screenSize"), "", charset);

        int verbose = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("verbose"), 1, charset);
        int screenshot = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("screenshot"), 0, charset);
        int pageSource = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("pageSource"), 0, charset);
        int seleniumLog = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("seleniumLog"), 0, charset);
        String timeout = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("timeout"), "", charset);
        int retries = ParameterParserUtil.parseIntegerParamAndDecode(request.getParameter("retries"), 0, charset);
        String manualExecution = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("manualExecution"), "", charset);
        String debugFlag = ParameterParserUtil.parseStringParamAndDecodeAndSanitize(request.getParameter("debugFlag"), "N", charset);

        // Parameter that we cannot secure as we need the html --> We DECODE them
        String[] myIds = request.getParameterValues("id");
        if (myIds == null) {
            myIds = new String[1];
            myIds[0] = "0";
        }
        long id = 0;
        Integer priority = TestCaseExecutionQueue.PRIORITY_DEFAULT;
        boolean prio_error = false;
        try {
            if (request.getParameter("priority") != null && !request.getParameter("priority").equals("")) {
                priority = Integer.valueOf(policy.sanitize(request.getParameter("priority")));
            }
        } catch (Exception ex) {
            prio_error = true;
        }

        boolean id_error = false;

        IExecutionThreadPoolService executionThreadPoolService = appContext.getBean(IExecutionThreadPoolService.class);

        // Create Tag when exist.
        if (!StringUtil.isNullOrEmpty(tag)) {
            // We create or update it.
            ITagService tagService = appContext.getBean(ITagService.class);
            tagService.createAuto(tag, "", request.getRemoteUser());
        }

        // Prepare the final answer.
        MessageEvent msg1 = new MessageEvent(MessageEventEnum.GENERIC_OK);
        Answer finalAnswer = new Answer(msg1);
        List<TestCaseExecutionQueue> insertedList = new ArrayList();

        for (String myId : myIds) {

            id_error = false;
            id = 0;
            try {
                id = Long.valueOf(myId);
            } catch (NumberFormatException ex) {
                id_error = true;
            }
            /**
             * Checking all constrains before calling the services.
             */
            if (id_error) {
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
                msg.setDescription(msg.getDescription().replace("%ITEM%", "Execution Queue")
                        .replace("%OPERATION%", "Update")
                        .replace("%REASON%", "Could not manage to convert id to an integer value."));
                ans.setResultMessage(msg);
                finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
            } else if (prio_error || priority > 2147483647 || priority < -2147483648) {
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
                msg.setDescription(msg.getDescription().replace("%ITEM%", "Execution Queue")
                        .replace("%OPERATION%", "Update")
                        .replace("%REASON%", "Could not manage to convert priority to an integer value."));
                ans.setResultMessage(msg);
                finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
            } else {
                try {
                    /**
                     * All data seems cleans so we can call the services.
                     */
                    ITestCaseExecutionQueueService executionQueueService = appContext.getBean(ITestCaseExecutionQueueService.class);
                    IFactoryTestCaseExecutionQueue executionQueueFactory = appContext.getBean(IFactoryTestCaseExecutionQueue.class);

                    if (actionSave.equals("save")) {
                        /**
                         * The service was able to perform the query and confirm
                         * the object exist, then we can update it.
                         */
                        TestCaseExecutionQueue executionQueueData;
                        if (id == 0) {
                            // If id is not defined, we build the execution queue from all request datas.
                            executionQueueData = executionQueueFactory.create(test, testcase, country, environment, robot, robotIP, robotPort, browser, browserVersion,
                                    platform, screenSize, manualURL, manualHost, manualContextRoot, manualLoginRelativeURL, manualEnvData, tag, screenshot, verbose, timeout,
                                    pageSource, seleniumLog, 0, retries, manualExecution, priority, request.getRemoteUser(), null, null, null);
                            executionQueueData.setDebugFlag(debugFlag);
                        } else {
                            // If id is defined, we get the execution queue from database.
                            executionQueueData = executionQueueService.convert(executionQueueService.readByKey(id));
                            executionQueueData.setState(TestCaseExecutionQueue.State.QUEUED);
                            executionQueueData.setComment("");
                            executionQueueData.setDebugFlag("N");
                            executionQueueData.setPriority(TestCaseExecutionQueue.PRIORITY_DEFAULT);
                            executionQueueData.setUsrCreated(request.getRemoteUser());
                        }
                        ansItem = executionQueueService.create(executionQueueData);

                        finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ansItem);
                        if (ansItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
                            TestCaseExecutionQueue addedExecution = (TestCaseExecutionQueue) ansItem.getItem();
                            insertedList.add(addedExecution);
                        }

                        if (myIds.length <= 1) {
                            if (ansItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
                                /**
                                 * Update was successful. Adding Log entry.
                                 */
                                ILogEventService logEventService = appContext.getBean(LogEventService.class);
                                logEventService.createForPrivateCalls("/CreateTestCaseExecutionQueue", "CREATE", "Created ExecutionQueue : ['" + id + "']", request);
                            }
                        }

                    }

                } catch (FactoryCreationException ex) {
                    LOG.warn(ex);
                }

            }
        }

        // Update is done, we now check what action needs to be performed.
        if (actionState.equals("toQUEUED")) {
            executionThreadPoolService.executeNextInQueueAsynchroneously(false);
        }

        /**
         * Formating and returning the json result.
         */
        jsonResponse.put("messageType", finalAnswer.getResultMessage().getMessage().getCodeString());
        jsonResponse.put("message", finalAnswer.getResultMessage().getDescription());
        if (insertedList.isEmpty()) {
            jsonResponse.put("addedEntries", 0);
        } else {
            JSONArray executionList = new JSONArray();

            for (TestCaseExecutionQueue testCaseExecutionQueue : insertedList) {
                JSONObject myExecution = new JSONObject();
                myExecution.append("id", testCaseExecutionQueue.getId());
                executionList.put(myExecution);
            }
            jsonResponse.put("testCaseExecutionQueueList", executionList);
            jsonResponse.put("addedEntries", insertedList.size());
        }

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
}
