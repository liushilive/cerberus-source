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

import java.util.List;
import java.util.Map;
import org.cerberus.crud.entity.Test;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

/**
 * {Insert class description here}
 *
 * @author Tiago Bernardes
 * @version 1.0, 19/Dez/2012
 * @since 2.0.0
 */
public interface ITestDAO {

    List<Test> findAllTest();

    List<Test> findTestByCriteria(Test test);

    boolean createTest(Test test) throws CerberusException;


    /**
     *
     * @param test Name of the test
     * @return Test Object
     */
    Test findTestByKey(String test);

    List<Test> findListOfTestBySystems(List<String> systems);

    public AnswerItem readByKey(String test);

    public AnswerList readDistinctBySystem(String system);
    
    public AnswerList readByCriteria(int start, int amount, String colName, String dir, String searchTerm, Map<String, List<String>> individualSearch);

    /**
     *
     * @param test
     * @return
     */
    public Answer create(Test test);

    /**
     *
     * @param keyTest
     * @param test
     * @return
     */
    public Answer update(String keyTest, Test test);
    
    /**
     *
     * @param test
     * @return
     */
    public Answer delete(Test test);
    
    /**
     *
     * @param searchTerm
     * @param individualSearch
     * @param columnName
     * @return
     */
    public AnswerList<List<String>> readDistinctValuesByCriteria(String searchTerm, Map<String, List<String>> individualSearch, String columnName);

}
