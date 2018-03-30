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
package org.cerberus.crud.service;

import java.util.List;
import java.util.Map;
import org.cerberus.crud.entity.CampaignLabel;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

/**
 *
 * @author vertigo
 */
public interface ICampaignLabelService {

    /**
     *
     * @param campaignLabelId
     * @return
     */
    AnswerItem readByKeyTech(Integer campaignLabelId);

    /**
     *
     * @param campaign
     * @param labelId
     * @return
     */
    AnswerItem readByKey(String campaign, Integer labelId);

    /**
     *
     * @return
     */
    AnswerList readAll();

    /**
     *
     * @param campaign
     * @return
     */
    AnswerList readByVarious(String campaign);

    /**
     *
     * @param startPosition
     * @param length
     * @param columnName
     * @param sort
     * @param searchParameter
     * @param individualSearch
     * @return
     */
    AnswerList readByCriteria(int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch);

    /**
     *
     * @param campaign
     * @param startPosition
     * @param length
     * @param columnName
     * @param sort
     * @param searchParameter
     * @param individualSearch
     * @return
     */
    AnswerList readByVariousByCriteria(String campaign, int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch);

    /**
     *
     * @param campaign
     * @param labelId
     * @return true is application exist or false is application does not exist
     * in database.
     */
    boolean exist(String campaign, Integer labelId);

    /**
     *
     * @param object
     * @return
     */
    Answer create(CampaignLabel object);

    /**
     *
     * @param objectList
     * @return
     */
    Answer createList(List<CampaignLabel> objectList);

    /**
     *
     * @param object
     * @return
     */
    Answer delete(CampaignLabel object);

    /**
     *
     * @param objectList
     * @return
     */
    Answer deleteList(List<CampaignLabel> objectList);

    /**
     *
     * @param object
     * @return
     */
    Answer update(CampaignLabel object);

    /**
     *
     * @param answerItem
     * @return
     * @throws CerberusException
     */
    CampaignLabel convert(AnswerItem answerItem) throws CerberusException;

    /**
     *
     * @param answerList
     * @return
     * @throws CerberusException
     */
    List<CampaignLabel> convert(AnswerList answerList) throws CerberusException;

    /**
     *
     * @param answer
     * @throws CerberusException
     */
    void convert(Answer answer) throws CerberusException;

    /**
     *
     * @param campaign
     * @param newList
     * @return
     */
    Answer compareListAndUpdateInsertDeleteElements(String campaign, List<CampaignLabel> newList);

    /**
     *
     * @param campaign
     * @param searchParameter
     * @param individualSearch
     * @param columnName
     * @return
     */
    AnswerList<String> readDistinctValuesByCriteria(String campaign, String searchParameter, Map<String, List<String>> individualSearch, String columnName);
}
