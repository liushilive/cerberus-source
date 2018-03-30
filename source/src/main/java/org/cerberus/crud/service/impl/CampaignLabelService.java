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
import org.cerberus.crud.dao.ICampaignLabelDAO;
import org.cerberus.crud.entity.CampaignLabel;
import org.cerberus.crud.service.ICampaignLabelService;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.engine.entity.MessageGeneral;
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
public class CampaignLabelService implements ICampaignLabelService {

    @Autowired
    private ICampaignLabelDAO campaignLabelDAO;

    private static final Logger LOG = LogManager.getLogger(CampaignLabelService.class);

    private final String OBJECT_NAME = "Service Content";

    @Override
    public AnswerItem readByKeyTech(Integer ampaignLabelId) {
        return campaignLabelDAO.readByKeyTech(ampaignLabelId);
    }

    @Override
    public AnswerItem readByKey(String campaign, Integer labelId) {
        return campaignLabelDAO.readByKey(campaign, labelId);
    }

    @Override
    public AnswerList readAll() {
        return readByVariousByCriteria(null, 0, 0, "campaignlabelid", "asc", null, null);
    }

    @Override
    public AnswerList readByVarious(String campaign) {
        return campaignLabelDAO.readByVariousByCriteria(campaign, 0, 0, "campaignlabelid", "asc", null, null);
    }

    @Override
    public AnswerList readByCriteria(int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch) {
        return campaignLabelDAO.readByVariousByCriteria(null, startPosition, length, columnName, sort, searchParameter, individualSearch);
    }

    @Override
    public AnswerList readByVariousByCriteria(String campaign, int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch) {
        return campaignLabelDAO.readByVariousByCriteria(campaign, startPosition, length, columnName, sort, searchParameter, individualSearch);
    }

    @Override
    public boolean exist(String campaign, Integer labelId) {
        AnswerItem objectAnswer = readByKey(campaign, labelId);
        return (objectAnswer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) && (objectAnswer.getItem() != null); // Call was successfull and object was found.
    }

    @Override
    public Answer create(CampaignLabel object) {
        return campaignLabelDAO.create(object);
    }

    @Override
    public Answer createList(List<CampaignLabel> objectList) {
        Answer ans = new Answer(null);
        for (CampaignLabel objectToCreate : objectList) {
            ans = this.create(objectToCreate);
        }
        return ans;
    }

    @Override
    public Answer delete(CampaignLabel object) {
        return campaignLabelDAO.delete(object);
    }

    @Override
    public Answer deleteList(List<CampaignLabel> objectList) {
        Answer ans = new Answer(null);
        for (CampaignLabel objectToDelete : objectList) {
            ans = this.delete(objectToDelete);
        }
        return ans;
    }

    @Override
    public Answer update(CampaignLabel object) {
        return campaignLabelDAO.update(object);
    }

    @Override
    public CampaignLabel convert(AnswerItem answerItem) throws CerberusException {
        if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (CampaignLabel) answerItem.getItem();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public List<CampaignLabel> convert(AnswerList answerList) throws CerberusException {
        if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (List<CampaignLabel>) answerList.getDataList();
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
    public Answer compareListAndUpdateInsertDeleteElements(String campaign, List<CampaignLabel> newList) {
        Answer ans = new Answer(null);

        MessageEvent msg1 = new MessageEvent(MessageEventEnum.GENERIC_OK);
        Answer finalAnswer = new Answer(msg1);

        List<CampaignLabel> oldList = new ArrayList();
        try {
            oldList = this.convert(this.readByVarious(campaign));
        } catch (CerberusException ex) {
            LOG.error(ex);
        }

        /**
         * Update and Create all objects database Objects from newList
         */
        List<CampaignLabel> listToUpdateOrInsert = new ArrayList(newList);
        listToUpdateOrInsert.removeAll(oldList);
        List<CampaignLabel> listToUpdateOrInsertToIterate = new ArrayList(listToUpdateOrInsert);

        for (CampaignLabel objectDifference : listToUpdateOrInsertToIterate) {
            for (CampaignLabel objectInDatabase : oldList) {
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
        List<CampaignLabel> listToDelete = new ArrayList(oldList);
        listToDelete.removeAll(newList);
        List<CampaignLabel> listToDeleteToIterate = new ArrayList(listToDelete);

        for (CampaignLabel tcsDifference : listToDeleteToIterate) {
            for (CampaignLabel tcsInPage : newList) {
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
    public AnswerList<String> readDistinctValuesByCriteria(String campaign, String searchParameter, Map<String, List<String>> individualSearch, String columnName) {
        return campaignLabelDAO.readDistinctValuesByCriteria(campaign, searchParameter, individualSearch, columnName);
    }

}
