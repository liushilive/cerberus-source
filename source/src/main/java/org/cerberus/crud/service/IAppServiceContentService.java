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

import org.cerberus.crud.entity.AppServiceContent;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

/**
 *
 * @author vertigo
 */
public interface IAppServiceContentService {

    /**
     *
     * @param service
     * @param key
     * @return
     */
    AnswerItem readByKey(String service, String key);

    /**
     *
     * @return
     */
    AnswerList readAll();

    /**
     *
     * @param service
     * @param active
     * @return
     */
    AnswerList readByVarious(String service, String active);

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
     * @param service
     * @param active
     * @param startPosition
     * @param length
     * @param columnName
     * @param sort
     * @param searchParameter
     * @param individualSearch
     * @return
     */
    AnswerList readByServiceByCriteria(String service, String active, int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch);

    /**
     *
     * @param service
     * @param key
     * @return true is application exist or false is application does not exist
     * in database.
     */
    boolean exist(String service, String key);

    /**
     *
     * @param object
     * @return
     */
    Answer create(AppServiceContent object);

    /**
     *
     * @param objectList
     * @return
     */
    Answer createList(List<AppServiceContent> objectList);

    /**
     *
     * @param object
     * @return
     */
    Answer delete(AppServiceContent object);

    /**
     *
     * @param objectList
     * @return
     */
    Answer deleteList(List<AppServiceContent> objectList);

    /**
     *
     * @param object
     * @return
     */
    Answer update(String service, String key, AppServiceContent object);

    /**
     *
     * @param answerItem
     * @return
     * @throws CerberusException
     */
    AppServiceContent convert(AnswerItem answerItem) throws CerberusException;

    /**
     *
     * @param answerList
     * @return
     * @throws CerberusException
     */
    List<AppServiceContent> convert(AnswerList answerList) throws CerberusException;

    /**
     *
     * @param answer
     * @throws CerberusException
     */
    void convert(Answer answer) throws CerberusException;

    /**
     *
     * @param service
     * @param newList
     * @return
     */
    Answer compareListAndUpdateInsertDeleteElements(String service, List<AppServiceContent> newList);

    /**
     *
     * @param service
     * @param searchParameter
     * @param individualSearch
     * @param columnName
     * @return
     */
    AnswerList<String> readDistinctValuesByCriteria(String service, String searchParameter, Map<String, List<String>> individualSearch, String columnName);
}
