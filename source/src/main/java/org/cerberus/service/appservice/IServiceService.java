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
package org.cerberus.service.appservice;

import org.cerberus.crud.entity.AppService;
import org.cerberus.crud.entity.TestCaseExecution;
import org.cerberus.util.answer.AnswerItem;

/**
 *
 * @author bcivel
 */
public interface IServiceService {

    /**
     * Perform a service call and feed the AppService object in return. If URL
     * coming from service object is enriched from the context of either the
     * database or the tCExecution. service is defined, If database is defined
     * the URL is enriched from context is coming from database if not, context
     * will be taken from tCExecution.
     *
     * @param service
     * @param database
     * @param request
     * @param servicePath
     * @param operation
     * @param tCExecution
     * @return
     */
    AnswerItem<AppService> callService(String service, String database, String request, String servicePath, String operation, TestCaseExecution tCExecution);

}
