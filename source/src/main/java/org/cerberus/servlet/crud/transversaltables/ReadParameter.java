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
package org.cerberus.servlet.crud.transversaltables;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.crud.entity.Parameter;
import org.cerberus.crud.service.IParameterService;
import org.cerberus.crud.service.impl.ParameterService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
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
 * @author foudro
 */
@WebServlet(name = "ReadParameter", urlPatterns = {"/ReadParameter"})
public class ReadParameter extends HttpServlet {

    private IParameterService parameterService;
    private static final Logger LOG = LogManager.getLogger(ReadParameter.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws org.cerberus.exception.CerberusException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, CerberusException {
        String echo = request.getParameter("sEcho");
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf8");

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);

        // Default message to unexpected error.
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        /**
         * Parsing and securing all required parameters.
         */
        String mySystem = request.getParameter("system");
        String columnName = ParameterParserUtil.parseStringParam(request.getParameter("columnName"), "");
        // Nothing to do here as no parameter to check.
        //
        // Global boolean on the servlet that define if the user has permition to edit and delete object.
        boolean userHasPermissions = request.isUserInRole("Administrator");

        // Init Answer with potencial error from Parsing parameter.
        AnswerItem answer = new AnswerItem(new MessageEvent(MessageEventEnum.DATA_OPERATION_OK));

        try {
            JSONObject jsonResponse;

            String system1;
            if (request.getParameter("system1") == null) {
                system1 = "DEFAULT";
            } else {
                system1 = policy.sanitize(request.getParameter("system1"));
            }

            if (request.getParameter("param") == null && Strings.isNullOrEmpty(columnName)) {
                answer = findParameterList(system1, appContext, userHasPermissions, request);
                jsonResponse = (JSONObject) answer.getItem();
            } else if (!Strings.isNullOrEmpty(columnName)) {
                answer = findDistinctValuesOfColumn(system1, appContext, request, columnName);
                jsonResponse = (JSONObject) answer.getItem();
            } else {
                answer = findParameterBySystemByKey(system1, request.getParameter("param"), userHasPermissions, appContext);
                jsonResponse = (JSONObject) answer.getItem();
            }

            jsonResponse.put("messageType", answer.getResultMessage().getMessage().getCodeString());
            jsonResponse.put("message", answer.getResultMessage().getDescription());
            jsonResponse.put("sEcho", echo);

            response.getWriter().print(jsonResponse.toString());

        } catch (JSONException e) {
            LOG.warn(e);
            //returns a default error message with the json format that is able to be parsed by the client-side
            response.getWriter().print(AnswerUtil.createGenericErrorAnswer());
        }
    }

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
        }
    }

    private AnswerItem findParameterList(String system1, ApplicationContext appContext, boolean userHasPermissions, HttpServletRequest request) throws JSONException {

        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();
        parameterService = appContext.getBean(ParameterService.class);

        int startPosition = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayStart"), "0"));
        int length = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayLength"), "0"));
        /*int sEcho  = Integer.valueOf(request.getParameter("sEcho"));*/

        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        int columnToSortParameter = Integer.parseInt(ParameterParserUtil.parseStringParam(request.getParameter("iSortCol_0"), "2"));
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "para,valC,valS,descr");
        String columnToSort[] = sColumns.split(",");
        String columnName = columnToSort[columnToSortParameter];
        String sort = ParameterParserUtil.parseStringParam(request.getParameter("sSortDir_0"), "asc");
        List<String> individualLike = new ArrayList(Arrays.asList(ParameterParserUtil.parseStringParam(request.getParameter("sLike"), "").split(",")));

        Map<String, List<String>> individualSearch = new HashMap<>();
        for (int a = 0; a < columnToSort.length; a++) {
            if (null != request.getParameter("sSearch_" + a) && !request.getParameter("sSearch_" + a).isEmpty()) {
                List<String> search = new ArrayList(Arrays.asList(request.getParameter("sSearch_" + a).split(",")));
                if(individualLike.contains(columnToSort[a])) {
                	individualSearch.put(columnToSort[a]+":like", search);
                }else {
                	individualSearch.put(columnToSort[a], search);
                }            
            }
        }

        AnswerList resp = parameterService.readWithSystem1BySystemByCriteria("", system1, startPosition, length, columnName, sort, searchParameter, individualSearch);

        JSONArray jsonArray = new JSONArray();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (Parameter param : (List<Parameter>) resp.getDataList()) {
                param = parameterService.secureParameter(param);
                jsonArray.put(convertParameterToJSONObject(param));
            }
        }

        object.put("hasPermissions", userHasPermissions);
        object.put("contentTable", jsonArray);
        object.put("iTotalRecords", resp.getTotalRows());
        object.put("iTotalDisplayRecords", resp.getTotalRows());

        item.setItem(object);
        item.setResultMessage(resp.getResultMessage());
        return item;
    }

    private AnswerItem findParameterBySystemByKey(String system1, String key, Boolean userHasPermissions, ApplicationContext appContext) throws JSONException {
        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();

        parameterService = appContext.getBean(ParameterService.class);

        AnswerItem answer = parameterService.readWithSystem1ByKey("", key, system1);
        Parameter p = null;
        if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            p = (Parameter) answer.getItem();
            JSONObject response = convertParameterToJSONObject(parameterService.secureParameter(p));
            object.put("contentTable", response);
        }
        object.put("hasPermissions", userHasPermissions);
        object.put("isSecured", parameterService.isToSecureParameter(p));
        object.put("isSystemManaged", parameterService.isSystemManaged(p));
        item.setItem(object);
        item.setResultMessage(answer.getResultMessage());

        return item;
    }

    private JSONObject convertParameterToJSONObject(Parameter parameter) throws JSONException {

        Gson gson = new Gson();
        JSONObject result = new JSONObject(gson.toJson(parameter));
        return result;
    }

    private AnswerItem findDistinctValuesOfColumn(String system, ApplicationContext appContext, HttpServletRequest request, String columnName) throws JSONException {
        AnswerItem answer = new AnswerItem();
        JSONObject object = new JSONObject();

        parameterService = appContext.getBean(IParameterService.class);

        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "para,valC,valS,descr");
        String columnToSort[] = sColumns.split(",");

        List<String> individualLike = new ArrayList(Arrays.asList(ParameterParserUtil.parseStringParam(request.getParameter("sLike"), "").split(",")));

        Map<String, List<String>> individualSearch = new HashMap<>();
        for (int a = 0; a < columnToSort.length; a++) {
            if (null != request.getParameter("sSearch_" + a) && !request.getParameter("sSearch_" + a).isEmpty()) {
            	List<String> search = new ArrayList(Arrays.asList(request.getParameter("sSearch_" + a).split(",")));
            	if(individualLike.contains(columnToSort[a])) {
                	individualSearch.put(columnToSort[a]+":like", search);
                }else {
                	individualSearch.put(columnToSort[a], search);
                } 
            }
        }

        AnswerList applicationList = parameterService.readDistinctValuesWithSystem1ByCriteria("", system, searchParameter, individualSearch, columnName);

        object.put("distinctValues", applicationList.getDataList());

        answer.setItem(object);
        answer.setResultMessage(applicationList.getResultMessage());
        return answer;
    }
}
