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
package org.cerberus.service.sql;

import java.util.HashMap;
import java.util.List;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.crud.entity.TestCaseCountryProperties;
import org.cerberus.crud.entity.TestCaseExecution;
import org.cerberus.crud.entity.TestCaseExecutionData;
import org.cerberus.exception.CerberusEventException;
import org.cerberus.util.answer.AnswerList;

/**
 *
 * @author bcivel
 */
public interface ISQLService {

    /**
     *
     * @param testCaseExecutionData
     * @param testCaseProperties
     * @param tCExecution
     * @return
     */
    TestCaseExecutionData calculateOnDatabase(TestCaseExecutionData testCaseExecutionData, TestCaseCountryProperties testCaseProperties, TestCaseExecution tCExecution);

    /**
     * Performs a query in the database
     *
     * @param connectionName
     * @param sql
     * @param limit
     * @param defaultTimeOut
     * @return
     * @throws CerberusEventException
     */
    List<String> queryDatabase(String connectionName, String sql, int limit, int defaultTimeOut) throws CerberusEventException;

    /**
     *
     * @param system
     * @param country
     * @param environment
     * @param db
     * @param sql
     * @return
     */
    MessageEvent executeUpdate(String system, String country, String environment, String db, String sql);

    /**
     *
     * @param system
     * @param country
     * @param environment
     * @param db
     * @param sql
     * @return
     */
    MessageEvent executeCallableStatement(String system, String country, String environment, String db, String sql);

    /**
     *
     * @param connectionName
     * @param sql
     * @param rowLimit
     * @param defaultTimeOut
     * @param system
     * @param columnsToGet
     * @return
     */
    AnswerList queryDatabaseNColumns(String connectionName, String sql, int rowLimit, int defaultTimeOut, String system, HashMap<String, String> columnsToGet);

}
