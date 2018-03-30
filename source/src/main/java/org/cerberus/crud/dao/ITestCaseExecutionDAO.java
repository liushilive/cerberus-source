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
package org.cerberus.crud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.cerberus.crud.entity.TestCaseExecution;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

/**
 * {Insert class description here}
 *
 * @author Tiago Bernardes
 * @version 1.0, 02/01/2013
 * @since 2.0.0
 */
public interface ITestCaseExecutionDAO {

    /**
     * @param tCExecution TestCaseExecution Object to insert in
     * TestcaseExecution table
     * @throws org.cerberus.exception.CerberusException
     * @return execution id (long)
     */
    long insertTCExecution(TestCaseExecution tCExecution) throws CerberusException;

    void updateTCExecution(TestCaseExecution tCExecution) throws CerberusException;

    List<String> getIDListOfLastExecutions(String test, String testcase, String country);

    /**
     * Gets the last execution from the database following the defined criteria.
     *
     * @param application
     * @return
     */
    AnswerItem readLastByCriteria(String application);

    /**
     *
     * @param test Test Criteria
     * @param testcase TestCase Criteria
     * @param environment Environment Criteria
     * @param country Country Criteria
     * @param build
     * @param revision
     * @return TestCaseExecution Object created only with attributes from
     * database
     */
    TestCaseExecution findLastTCExecutionByCriteria(String test, String testcase, String environment, String country,
            String build, String revision) throws CerberusException;

    TestCaseExecution findLastTCExecutionByCriteria(String test, String testCase, String environment, String country,
            String build, String revision, String browser, String browserVersion,
            String ip, String port, String tag);

    /**
     * @param dateLimitFrom The limit start date of the executions from which
     * the selection is done. Mandatory parameter.
     * @param test filter on the test
     * @param testCase filter on the testCase
     * @param application filter on the application.
     * @param country filter on the country
     * @param environment filter on the environment
     * @param controlStatus filter on the control status (RC of the execution)
     * @param status filter on the status (Status of the testCase when execution
     * was made)
     * @return a list of testCaseExecution done after the dateLimitFrom
     * parameter and following the other criteria.
     * @throws CerberusException when no executions can be found.
     */
    List<TestCaseExecution> findExecutionbyCriteria1(String dateLimitFrom, String test, String testCase, String application, String country, String environment, String controlStatus, String status) throws CerberusException;

    /**
     *
     * @param id of the test case execution
     * @return the test case execution object
     * @throws CerberusException
     */
    TestCaseExecution findTCExecutionByKey(long id) throws CerberusException;


    /**
     * @param withUUIDTag determine of we must retreive UUID tag or not
     * @return a list of String tag
     * @throws CerberusException when no tags can be found.
     */
    List<String> findDistinctTag(boolean withUUIDTag) throws CerberusException;

    TestCaseExecution findLastTestCaseExecutionNotPE(String test, String testCase) throws CerberusException;

    /**
     *
     * @param test
     * @param testCase
     * @param environment
     * @param country
     * @param build
     * @param revision
     * @param browser
     * @param browserVersion
     * @param ip
     * @param port
     * @param tag
     * @return
     */
    public TestCaseExecution findLastTCExecutionInGroup(String test, String testCase, String environment, String country,
            String build, String revision, String browser, String browserVersion,
            String ip, String port, String tag);

    /**
     * Set Tag to an execution
     *
     * @param id : ID of the execution
     * @param tag : Tag to set to the execution
     */
    public void setTagToExecution(long id, String tag) throws CerberusException;

    AnswerList findTagList(int tagnumber) throws CerberusException;

    /**
     *
     * @param tag
     * @param start
     * @param amount
     * @param sort
     * @param searchTerm
     * @param individualSearch
     * @return
     * @throws CerberusException
     */
    public AnswerList readByTagByCriteria(String tag, int start, int amount, String sort, String searchTerm, Map<String, List<String>> individualSearch) throws CerberusException;

    /**
     * Read TestCaseExecution By Tag
     * @param tag Tag used to filter execution
     * @return AnswerList that contains a list of TestCaseExecution object enriched with TestCase and Application objects
     * @throws CerberusException
     */
    public AnswerList readByTag(String tag) throws CerberusException;

    /**
     *
     * @param start
     * @param amount
     * @param sort
     * @param searchTerm
     * @param individualSearch
     * @return
     * @throws CerberusException
     */
    public AnswerList readByCriteria(int start, int amount, String sort, String searchTerm, Map<String, List<String>> individualSearch, List<String> individualLike) throws CerberusException;

    /**
     *
     * @param tag
     * @return
     */
    @Deprecated
    public AnswerList readDistinctEnvCoutnryBrowserByTag(String tag);

    /**
     *
     * @param tag
     * @param env
     * @param country
     * @param browser
     * @param app
     * @return
     */
    @Deprecated
    public AnswerList readDistinctColumnByTag(String tag, boolean env, boolean country, boolean browser, boolean app);

    /**
     *
     * @param system
     * @param testList
     * @param applicationList
     * @param projectList
     * @param tcstatusList
     * @param groupList
     * @param tcactiveList
     * @param priorityList
     * @param targetsprintList
     * @param targetrevisionList
     * @param creatorList
     * @param implementerList
     * @param buildList
     * @param revisionList
     * @param environmentList
     * @param countryList
     * @param browserList
     * @param tcestatusList
     * @param ip
     * @param port
     * @param tag
     * @param browserversion
     * @param comment
     * @param bugid
     * @param ticket
     * @return
     */
    public AnswerList readBySystemByVarious(String system, List<String> testList, List<String> applicationList, List<String> projectList, List<String> tcstatusList, List<String> groupList, List<String> tcactiveList, List<String> priorityList, List<String> targetsprintList, List<String> targetrevisionList, List<String> creatorList, List<String> implementerList, List<String> buildList, List<String> revisionList, List<String> environmentList, List<String> countryList, List<String> browserList, List<String> tcestatusList, String ip, String port, String tag, String browserversion, String comment, String bugid, String ticket);

    /**
     *
     * @param executionId
     * @return
     */
    public AnswerItem readByKey(long executionId);

    /**
     * Uses data of ResultSet to create object {@link TestCaseExecution}
     *
     * @param resultSet ResultSet relative to select from table TestCaseExecution
     * @return object {@link TestCaseExecution}
     * @throws SQLException when trying to get value from
     * {@link java.sql.ResultSet#getString(String)}
     * @see FactoryTestCaseExecution
     */
    public TestCaseExecution loadFromResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Get the distinct value of the specified colum
     * @param system
     * @param test
     * @param searchParameter
     * @param individualSearch
     * @param columnName Name of the column
     * @return object {@link TestCaseExecution}
     */
    public AnswerList<List<String>> readDistinctValuesByCriteria(String system, String test, String searchParameter, Map<String, List<String>> individualSearch, String columnName);

}
