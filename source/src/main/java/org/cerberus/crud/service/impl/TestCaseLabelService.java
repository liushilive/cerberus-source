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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.cerberus.crud.dao.ITestCaseLabelDAO;
import org.cerberus.engine.entity.MessageEvent;

import org.cerberus.engine.entity.MessageGeneral;
import org.cerberus.crud.entity.TestCaseLabel;
import org.cerberus.crud.service.ITestCaseLabelService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.cerberus.util.answer.AnswerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bcivel
 */
@Service
public class TestCaseLabelService implements ITestCaseLabelService {

    @Autowired
    private ITestCaseLabelDAO testCaseLabelDAO;

    private static final Logger LOG = LogManager.getLogger("TestCaseLabelService");

    private final String OBJECT_NAME = "TestCaseLabel";

    @Override
    public AnswerItem readByKeyTech(Integer id) {
        return testCaseLabelDAO.readByKeyTech(id);
    }

    @Override
    public AnswerItem readByKey(String test, String testCase, Integer id) {
        return testCaseLabelDAO.readByKey(test, testCase, id);
    }

    @Override
    public AnswerList readByCriteria(int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch) {
        return testCaseLabelDAO.readByCriteria(startPosition, length, columnName, sort, searchParameter, individualSearch);
    }

    @Override
    public AnswerList readByTestTestCase(String test, String testCase) {
        return testCaseLabelDAO.readByTestTestCase(test, testCase);
    }

    @Override
    public AnswerList readAll() {
        return readByCriteria(0, 0, "sort", "asc", null, null);
    }

    @Override
    public boolean exist(Integer id) {
        AnswerItem objectAnswer = readByKeyTech(id);
        return (objectAnswer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) && (objectAnswer.getItem() != null); // Call was successfull and object was found.
    }

    @Override
    public Answer create(TestCaseLabel object) {
        return testCaseLabelDAO.create(object);
    }

    @Override
    public Answer createList(List<TestCaseLabel> objectList) {
        Answer ans = new Answer(null);
        for (TestCaseLabel objectToCreate : objectList) {
            ans = this.create(objectToCreate);
        }
        return ans;
    }

    @Override
    public Answer delete(TestCaseLabel object) {
        return testCaseLabelDAO.delete(object);
    }

    @Override
    public Answer deleteList(List<TestCaseLabel> objectList) {
        Answer ans = new Answer(null);
        for (TestCaseLabel objectToDelete : objectList) {
            ans = this.delete(objectToDelete);
        }
        return ans;
    }

    @Override
    public Answer update(TestCaseLabel object) {
        return testCaseLabelDAO.update(object);
    }

    @Override
    public TestCaseLabel convert(AnswerItem answerItem) throws CerberusException {
        if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (TestCaseLabel) answerItem.getItem();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public List<TestCaseLabel> convert(AnswerList answerList) throws CerberusException {
        if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (List<TestCaseLabel>) answerList.getDataList();
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
    public Answer compareListAndUpdateInsertDeleteElements(String test, String testCase, List<TestCaseLabel> newList) {
        Answer ans = new Answer(null);

        MessageEvent msg1 = new MessageEvent(MessageEventEnum.GENERIC_OK);
        Answer finalAnswer = new Answer(msg1);

        List<TestCaseLabel> oldList = new ArrayList();
        try {
            oldList = this.convert(this.readByTestTestCase(test, testCase));
        } catch (CerberusException ex) {
            LOG.error(ex);
        }

        /**
         * Update and Create all objects database Objects from newList
         */
        List<TestCaseLabel> listToUpdateOrInsert = new ArrayList(newList);
        listToUpdateOrInsert.removeAll(oldList);
        List<TestCaseLabel> listToUpdateOrInsertToIterate = new ArrayList(listToUpdateOrInsert);

        for (TestCaseLabel objectDifference : listToUpdateOrInsertToIterate) {
            for (TestCaseLabel objectInDatabase : oldList) {
                if (objectDifference.hasSameKey(objectInDatabase)) {
                    ans = this.update(objectDifference);
                    finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
                    listToUpdateOrInsert.remove(objectDifference);
                }
            }
        }

        /**
         * Delete all objects database Objects that do not exist from newList
         */
        List<TestCaseLabel> listToDelete = new ArrayList(oldList);
        listToDelete.removeAll(newList);
        List<TestCaseLabel> listToDeleteToIterate = new ArrayList(listToDelete);

        for (TestCaseLabel tcsDifference : listToDeleteToIterate) {
            for (TestCaseLabel tcsInPage : newList) {
                if (tcsDifference.hasSameKey(tcsInPage)) {
                    listToDelete.remove(tcsDifference);
                }
            }
        }
        if (!listToDelete.isEmpty()) {
            ans = this.deleteList(listToDelete);
            finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
        }

        // We insert only at the end (after deletion of all potencial enreg - linked with #1281)
        if (!listToUpdateOrInsert.isEmpty()) {
            ans = this.createList(listToUpdateOrInsert);
            finalAnswer = AnswerUtil.agregateAnswer(finalAnswer, (Answer) ans);
        }
        return finalAnswer;
    }

    @Override
    public Answer duplicateList(List<TestCaseLabel> dataList, String targetTest, String targetTestCase) {
        Answer ans = new Answer(null);
        List<TestCaseLabel> listToCreate = new ArrayList();
        for (TestCaseLabel objectToDuplicate : dataList) {
            objectToDuplicate.setTest(targetTest);
            objectToDuplicate.setTestcase(targetTestCase);
            listToCreate.add(objectToDuplicate);
        }
        return createList(listToCreate);
    }

}
