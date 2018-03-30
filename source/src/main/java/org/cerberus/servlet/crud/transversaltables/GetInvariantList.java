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
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Invariant;
import org.cerberus.crud.service.IInvariantService;
import org.cerberus.crud.service.impl.InvariantService;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.answer.AnswerList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet(name = "GetInvariantList", urlPatterns = {"/GetInvariantList"})
public class GetInvariantList extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(GetInvariantList.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("idName");
        String idName = ParameterParserUtil.parseStringParam(id, "");

        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        IInvariantService invariantService = appContext.getBean(InvariantService.class);

        JSONObject jsonResponse = new JSONObject();

        String action = request.getParameter("action");

        try {
            if (request.getParameter("action") != null) {
                    //gets a list of invariants in the same call, it can be useful if we want to
                //retrieve all the information in one client call
                if ("getNInvariant".equals(action)) {
                    //gets a list of invariants
                    JSONObject listOfInvariants = new JSONObject(idName);
                    for (int i = 0; i < listOfInvariants.length(); i++) {
                        String invariantName = (String) listOfInvariants.get(String.valueOf(i));
                        JSONArray array = new JSONArray();
                        AnswerList answer = invariantService.readByIdname(invariantName); //TODO: handle if the response does not turn ok
                        for (Invariant myInvariant : (List<Invariant>) answer.getDataList()) {
                            array.put(myInvariant.getValue());
                        }
                        jsonResponse.put(invariantName, array);
                    }
                }
            } else {
                    //gets one item

                AnswerList answer = invariantService.readByIdname(idName); //TODO: handle if the response does not turn ok
                for (Invariant myInvariant : (List<Invariant>) answer.getDataList()) {
                    jsonResponse.put(myInvariant.getValue(), myInvariant.getValue());
                }
            }
            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());
        } catch (JSONException e) {
            LOG.warn(e);
            response.setContentType("text/html");
            response.getWriter().print(e.getMessage());
        }
    }
}
