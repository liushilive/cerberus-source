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
package org.cerberus.servlet.crud.buildrevisionchange;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Application;
import org.cerberus.crud.entity.BuildRevisionParameters;
import org.cerberus.crud.entity.CountryEnvDeployType;

import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.crud.service.IApplicationService;
import org.cerberus.crud.service.IBuildRevisionParametersService;
import org.cerberus.crud.service.ICountryEnvDeployTypeService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.crud.service.impl.BuildRevisionParametersService;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.StringUtil;
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
 * @author vertigo
 */
@WebServlet(name = "ReadBuildRevisionParameters", urlPatterns = {"/ReadBuildRevisionParameters"})
public class ReadBuildRevisionParameters extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(ReadBuildRevisionParameters.class);
    private IBuildRevisionParametersService brpService;
    private IApplicationService appService;
    private ICountryEnvDeployTypeService cedtService;

    private final String OBJECT_NAME = "BuildRevisionParameters";

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
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf8");

        // Calling Servlet Transversal Util.
        ServletUtil.servletStart(request);

        // Default message to unexpected error.
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));

        /**
         * Parsing and securing all required parameters.
         */
        Integer brpid = 0;
        boolean brpid_error = true;
        try {
            if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
                brpid = Integer.valueOf(policy.sanitize(request.getParameter("id")));
                brpid_error = false;
            }
        } catch (Exception ex) {
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
            msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME));
            msg.setDescription(msg.getDescription().replace("%OPERATION%", "Read"));
            msg.setDescription(msg.getDescription().replace("%REASON%", "id must be an integer value."));
            brpid_error = true;
        }
        String columnName = ParameterParserUtil.parseStringParam(request.getParameter("columnName"), "");

        // Global boolean on the servlet that define if the user has permition to edit and delete object.
        boolean userHasPermissions = request.isUserInRole("Integrator");

        // Init Answer with potencial error from Parsing parameter.
        AnswerItem answer = new AnswerItem(msg);

        try {
            JSONObject jsonResponse = new JSONObject();
            if ((request.getParameter("id") != null) && !(brpid_error)) { // ID parameter is specified so we return the unique record of object.
                answer = findBuildRevisionParametersByKey(brpid, appContext, userHasPermissions);
                jsonResponse = (JSONObject) answer.getItem();
            } else if ((request.getParameter("system") != null) && (request.getParameter("getlast") != null)) { // getlast parameter trigger the last release from the system..
                answer = findlastBuildRevisionParametersBySystem(request.getParameter("system"), appContext, userHasPermissions);
                jsonResponse = (JSONObject) answer.getItem();
            } else if ((request.getParameter("system") != null) && (request.getParameter("build") != null) && (request.getParameter("revision") != null) && (request.getParameter("getSVNRelease") != null)) { // getSVNRelease parameter trigger the list of SVN Release inside he build per Application.
                answer = findSVNBuildRevisionParametersBySystem(request.getParameter("system"), request.getParameter("country"), request.getParameter("environment"), request.getParameter("build"), request.getParameter("revision"), request.getParameter("lastbuild"), request.getParameter("lastrevision"), appContext, userHasPermissions);
                jsonResponse = (JSONObject) answer.getItem();
            } else if ((request.getParameter("system") != null) && (request.getParameter("build") != null) && (request.getParameter("revision") != null) && (request.getParameter("getNonSVNRelease") != null)) { // getNonSVNRelease parameter trigger the list of Manual Release with corresponding links.
                answer = findManualBuildRevisionParametersBySystem(request.getParameter("system"), request.getParameter("build"), request.getParameter("revision"), request.getParameter("lastbuild"), request.getParameter("lastrevision"), appContext, userHasPermissions);
                jsonResponse = (JSONObject) answer.getItem();
            } else if ((request.getParameter("system") != null) && !Strings.isNullOrEmpty(columnName)) {
                answer = findDistinctValuesOfColumn(request.getParameter("system"), appContext, request, columnName);
                jsonResponse = (JSONObject) answer.getItem();
            } else { // Default behaviour, we return the list of objects.
                answer = findBuildRevisionParametersList(request.getParameter("system"), request.getParameter("build"), request.getParameter("revision"), request.getParameter("application"), appContext, userHasPermissions, request);
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private AnswerItem findBuildRevisionParametersList(String system, String build, String revision, String application, ApplicationContext appContext, boolean userHasPermissions, HttpServletRequest request) throws JSONException {

        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();
        brpService = appContext.getBean(BuildRevisionParametersService.class);

        int startPosition = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayStart"), "0"));
        int length = Integer.valueOf(ParameterParserUtil.parseStringParam(request.getParameter("iDisplayLength"), "0"));
        /*int sEcho  = Integer.valueOf(request.getParameter("sEcho"));*/

        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        int columnToSortParameter = Integer.parseInt(ParameterParserUtil.parseStringParam(request.getParameter("iSortCol_0"), "1"));
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "ID,Build,Revision,Release,Application,Project,TicketIDFixed,BugIDFixed,Link,ReleaseOwner,Subject,datecre,jenkinsbuildid,mavengroupid,mavenartifactid,mavenversion");
        String columnToSort[] = sColumns.split(",");
        String columnName = columnToSort[columnToSortParameter];
        String sort = ParameterParserUtil.parseStringParam(request.getParameter("sSortDir_0"), "asc");
        List<String> individualLike = new ArrayList(Arrays.asList(ParameterParserUtil.parseStringParam(request.getParameter("sLike"), "").split(",")));
        
        Map<String, List<String>> individualSearch = new HashMap<String, List<String>>();
        for (int a = 0; a < columnToSort.length; a++) {
            if (null!=request.getParameter("sSearch_" + a) && !request.getParameter("sSearch_" + a).isEmpty()) {
                List<String> search = new ArrayList(Arrays.asList(request.getParameter("sSearch_" + a).split(",")));
                if(individualLike.contains(columnToSort[a])) {
                	individualSearch.put(columnToSort[a]+":like", search);
                }else {
                	individualSearch.put(columnToSort[a], search);
                }            
            }
        }
        
        AnswerList resp = brpService.readByVarious1ByCriteria(system, application, build, revision, startPosition, length, columnName, sort, searchParameter, individualSearch);

        JSONArray jsonArray = new JSONArray();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (BuildRevisionParameters brp : (List<BuildRevisionParameters>) resp.getDataList()) {
                jsonArray.put(convertBuildRevisionParametersToJSONObject(brp));
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

    private AnswerItem findBuildRevisionParametersByKey(Integer id, ApplicationContext appContext, boolean userHasPermissions) throws JSONException, CerberusException {
        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();

        IBuildRevisionParametersService libService = appContext.getBean(IBuildRevisionParametersService.class);

        //finds the project     
        AnswerItem answer = libService.readByKeyTech(id);

        if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item and convert it to JSONformat
            BuildRevisionParameters brp = (BuildRevisionParameters) answer.getItem();
            JSONObject response = convertBuildRevisionParametersToJSONObject(brp);
            object.put("contentTable", response);
        }

        object.put("hasPermissions", userHasPermissions);

        item.setItem(object);
        item.setResultMessage(answer.getResultMessage());

        return item;
    }

    private AnswerItem findlastBuildRevisionParametersBySystem(String system, ApplicationContext appContext, boolean userHasPermissions) throws JSONException, CerberusException {
        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();

        IBuildRevisionParametersService libService = appContext.getBean(IBuildRevisionParametersService.class);

        //finds the project     
        AnswerItem answer = libService.readLastBySystem(system);

        if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item and convert it to JSONformat
            BuildRevisionParameters brp = (BuildRevisionParameters) answer.getItem();
            JSONObject response = convertBuildRevisionParametersToJSONObject(brp);
            object.put("contentTable", response);
        }

        object.put("hasPermissions", userHasPermissions);

        item.setItem(object);
        item.setResultMessage(answer.getResultMessage());

        return item;
    }

    private AnswerItem findSVNBuildRevisionParametersBySystem(String system, String country, String environment, String build, String revision, String lastbuild, String lastrevision, ApplicationContext appContext, boolean userHasPermissions) throws JSONException {

        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();
        brpService = appContext.getBean(IBuildRevisionParametersService.class);
        appService = appContext.getBean(IApplicationService.class);
        cedtService = appContext.getBean(ICountryEnvDeployTypeService.class);

        if (StringUtil.isNullOrEmpty(lastbuild)) {
            lastbuild = build;
        }

        AnswerList resp = brpService.readMaxSVNReleasePerApplication(system, build, revision, lastbuild, lastrevision);

        JSONArray jsonArray = new JSONArray();
        JSONObject newSubObj = new JSONObject();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (BuildRevisionParameters brp : (List<BuildRevisionParameters>) resp.getDataList()) {
                newSubObj = convertBuildRevisionParametersToJSONObject(brp);

                // We get here the links of all corresponding deployTypes.
                Application app;
                try {
                    app = appService.convert(appService.readByKey(brp.getApplication()));
                    for (CountryEnvDeployType JenkinsAgent : cedtService.convert(cedtService.readByVarious(system, country, environment, app.getDeploytype()))) {
                        String DeployURL = "JenkinsDeploy?application=" + brp.getApplication() + "&jenkinsagent=" + JenkinsAgent.getJenkinsAgent() + "&country=" + country + "&deploytype=" + app.getDeploytype() + "&release=" + brp.getRelease() + "&jenkinsbuildid=" + brp.getJenkinsBuildId() + "&repositoryurl=" + brp.getRepositoryUrl();
                        JSONObject newSubObjContent = new JSONObject();
                        newSubObjContent.put("jenkinsAgent", JenkinsAgent.getJenkinsAgent());
                        newSubObjContent.put("link", DeployURL);
                        newSubObj.append("install", newSubObjContent);
                    }
                } catch (CerberusException ex) {
                    LOG.warn(ex);
                }
                jsonArray.put(newSubObj);
            }
        }

        object.put("contentTable", jsonArray);
        object.put("iTotalRecords", resp.getTotalRows());
        object.put("iTotalDisplayRecords", resp.getTotalRows());
        object.put("hasPermissions", userHasPermissions);

        item.setItem(object);
        item.setResultMessage(resp.getResultMessage());
        return item;
    }

    private AnswerItem findManualBuildRevisionParametersBySystem(String system, String build, String revision, String lastbuild, String lastrevision, ApplicationContext appContext, boolean userHasPermissions) throws JSONException {

        AnswerItem item = new AnswerItem();
        JSONObject object = new JSONObject();
        brpService = appContext.getBean(BuildRevisionParametersService.class);

        if (StringUtil.isNullOrEmpty(lastbuild)) {
            lastbuild = build;
        }

        AnswerList resp = brpService.readNonSVNRelease(system, build, revision, lastbuild, lastrevision);

        JSONArray jsonArray = new JSONArray();
        if (resp.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {//the service was able to perform the query, then we should get all values
            for (BuildRevisionParameters brp : (List<BuildRevisionParameters>) resp.getDataList()) {
                jsonArray.put(convertBuildRevisionParametersToJSONObject(brp));
            }
        }

        object.put("contentTable", jsonArray);
        object.put("iTotalRecords", resp.getTotalRows());
        object.put("iTotalDisplayRecords", resp.getTotalRows());
        object.put("hasPermissions", userHasPermissions);

        item.setItem(object);
        item.setResultMessage(resp.getResultMessage());
        return item;
    }

    private JSONObject convertBuildRevisionParametersToJSONObject(BuildRevisionParameters brp) throws JSONException {

        Gson gson = new Gson();
        JSONObject result = new JSONObject(gson.toJson(brp));
        return result;
    }

    private AnswerItem findDistinctValuesOfColumn(String system, ApplicationContext appContext, HttpServletRequest request, String columnName) throws JSONException {
        AnswerItem answer = new AnswerItem();
        JSONObject object = new JSONObject();

        brpService = appContext.getBean(IBuildRevisionParametersService.class);
        
        String searchParameter = ParameterParserUtil.parseStringParam(request.getParameter("sSearch"), "");
        String sColumns = ParameterParserUtil.parseStringParam(request.getParameter("sColumns"), "ID,Build,Revision,Release,Application,Project,TicketIDFixed,BugIDFixed,Link,ReleaseOwner,Subject,datecre,jenkinsbuildid,mavengroupid,mavenartifactid,mavenversion");
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

        AnswerList brpList = brpService.readDistinctValuesByCriteria(system,  searchParameter, individualSearch, columnName);

        object.put("distinctValues", brpList.getDataList());

        answer.setItem(object);
        answer.setResultMessage(brpList.getResultMessage());
        return answer;
    }

}
