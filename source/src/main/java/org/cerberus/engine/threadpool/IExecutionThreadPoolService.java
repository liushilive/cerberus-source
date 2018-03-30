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
package org.cerberus.engine.threadpool;

import org.cerberus.exception.CerberusException;

import java.util.HashMap;

public interface IExecutionThreadPoolService {

    HashMap<String, Integer> getCurrentlyRunning() throws CerberusException;

    HashMap<String, Integer> getCurrentlyPoolSizes() throws CerberusException;

    HashMap<String, Integer> getCurrentlyToTreat() throws CerberusException;

    /**
     * Search any {@link org.cerberus.crud.entity.TestCaseExecutionInQueue}
     * which are currently in queue (QUEUED State) for execution and trigger their executions
     *
     * @param forceExecution
     * @throws CerberusException if an error occurred during search or trigger
     * process
     */
    void executeNextInQueue(boolean forceExecution) throws CerberusException;

    void executeNextInQueueAsynchroneously(boolean forceExecution) throws CerberusException;
}
