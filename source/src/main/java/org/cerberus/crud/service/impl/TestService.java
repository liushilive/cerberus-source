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
package org.cerberus.crud.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cerberus.crud.dao.ITestDAO;
import org.cerberus.engine.entity.MessageGeneral;
import org.cerberus.crud.entity.Test;
import org.cerberus.exception.CerberusException;
import org.cerberus.crud.service.ITestService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {Insert class description here}
 *
 * @author Tiago Bernardes
 * @version 1.0, 07/01/2013
 * @since 0.9.0
 */
@Service
public class TestService implements ITestService {

    @Autowired
    private ITestDAO testDao;

    @Override
    public List<String> getListOfTests() {
        List<String> result = new ArrayList<String>();

        List<Test> listOfTests = this.testDao.findAllTest();

        for (Test lot : listOfTests) {
            result.add(lot.getTest());
        }

        return result;
    }

    @Override
    public List<Test> getListOfTest() {
        return testDao.findAllTest();
    }

    @Override
    public Test findTestByKey(String test) {
        return testDao.findTestByKey(test);
    }

    @Override
    public List<Test> findTestBySystems(List<String> systems) {
        return testDao.findListOfTestBySystems(systems);
    }

    @Override
    public AnswerItem readByKey(String test) {
        return testDao.readByKey(test);
    }

    @Override
    public AnswerList readDistinctBySystem(String system) {
        return testDao.readDistinctBySystem(system);
    }

    @Override
    public AnswerList readByCriteria(int start, int amount, String colName, String dir, String searchTerm, Map<String, List<String>> individualSearch) {
        return testDao.readByCriteria(start, amount, colName, dir, searchTerm, individualSearch);
    }

    @Override
    public Answer create(Test test) {
        return testDao.create(test);
    }

    @Override
    public Answer update(String keyTest, Test test) {
        return testDao.update(keyTest, test);
    }

    @Override
    public Answer delete(Test test) {
        return testDao.delete(test);
    }

    @Override
    public Test convert(AnswerItem answerItem) throws CerberusException {
        if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (Test) answerItem.getItem();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public List<Test> convert(AnswerList answerList) throws CerberusException {
        if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (List<Test>) answerList.getDataList();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public void convert(Answer answer) throws CerberusException {
        if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return;
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public AnswerList<List<String>> readDistinctValuesByCriteria(String searchTerm, Map<String, List<String>> individualSearch, String columnName) {
        return testDao.readDistinctValuesByCriteria(searchTerm, individualSearch, columnName);
    }
}
