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
package org.cerberus.servlet.guipages;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.crud.service.ICountryEnvParamService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.statistics.BuildRevisionStatisticsEnv;
import org.cerberus.statistics.IEnvironmentStatisticsService;
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
 * @author ip100003
 */
@WebServlet(name = "GetEnvironmentsPerBuildRevision", urlPatterns = {"/GetEnvironmentsPerBuildRevision"})
public class GetEnvironmentsPerBuildRevision extends HttpServlet {

    private IEnvironmentStatisticsService envService;
    private ICountryEnvParamService cepService;
    
    private static final Logger LOG = LogManager.getLogger(GetEnvironmentsPerBuildRevision.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String echo = request.getParameter("sEcho");
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        response.setContentType("application/json");

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);

        // Default message to unexpected error.
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));

        /**
         * Parsing and securing all required parameters.
         */
        String system = policy.sanitize(request.getParameter("system"));
        //
        // Global boolean on the servlet that define if the user has permition to edit and delete object.
        boolean userHasPermissions = true;

        // Init Answer with potencial error from Parsing parameter.
        AnswerItem answer = new AnswerItem(new MessageEvent(MessageEventEnum.DATA_OPERATION_OK));

        try {
            JSONObject jsonResponse = new JSONObject();
            if (request.getParameter("system") != null) {
                answer = findBuildRevList(system, appContext, userHasPermissions, request);
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

    private AnswerItem findBuildRevList(String system, ApplicationContext appContext, boolean userHasPermissions, HttpServletRequest request) throws JSONException {

        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();
        envService = appContext.getBean(IEnvironmentStatisticsService.class);
        cepService = appContext.getBean(ICountryEnvParamService.class);

        AnswerList resp = envService.getEnvironmentStatistics(system);

        JSONArray jsonArray = new JSONArray();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (BuildRevisionStatisticsEnv buildRevStat : (List<BuildRevisionStatisticsEnv>) resp.getDataList()) {
                jsonArray.put(convertToJSONObject(buildRevStat));

            }
        }

        object.put("contentTable", jsonArray);
        object.put("iTotalRecords", resp.getTotalRows());
        object.put("iTotalDisplayRecords", resp.getTotalRows());

        item.setItem(object);
        item.setResultMessage(resp.getResultMessage());
        return item;
    }

    private JSONObject convertToJSONObject(BuildRevisionStatisticsEnv buildRevStat) throws JSONException {

        Gson gson = new Gson();
        JSONObject result = new JSONObject(gson.toJson(buildRevStat));
        return result;
    }

}
