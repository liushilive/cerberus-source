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

import java.util.List;
import java.util.Map;

import org.cerberus.crud.dao.IBuildRevisionInvariantDAO;
import org.cerberus.crud.entity.BuildRevisionInvariant;
import org.cerberus.engine.entity.MessageGeneral;
import org.cerberus.exception.CerberusException;
import org.cerberus.crud.service.IBuildRevisionInvariantService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildRevisionInvariantService implements IBuildRevisionInvariantService {

    @Autowired
    private IBuildRevisionInvariantDAO BuildRevisionInvariantDAO;

    @Override
    public AnswerItem readByKey(String system, Integer level, Integer seq) {
        return BuildRevisionInvariantDAO.readByKey(system, level, seq);
    }

    @Override
    public AnswerItem readByKey(String system, Integer level, String versionName) {
        return BuildRevisionInvariantDAO.readByKey(system, level, versionName);
    }

    @Override
    public AnswerList readBySystemByCriteria(String system, Integer level, int start, int amount, String column, String dir, String searchTerm, Map<String, List<String>> individualSearch) {
        return BuildRevisionInvariantDAO.readByVariousByCriteria(system, level, start, amount, column, dir, searchTerm, individualSearch);
    }

    @Override
    public AnswerList readBySystemLevel(String system, Integer level) {
        return BuildRevisionInvariantDAO.readByVariousByCriteria(system, level, 0, 0, null, null, null, null);
    }

    @Override
    public AnswerList readBySystem(String system) {
        return BuildRevisionInvariantDAO.readByVariousByCriteria(system, -1, 0, 0, null, null, null, null);
    }

    @Override
    public boolean exist(String system, Integer level, Integer seq) {
        AnswerItem objectAnswer = readByKey(system, level, seq);
        return (objectAnswer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) && (objectAnswer.getItem() != null); // Call was successfull and object was found.
    }

    @Override
    public boolean exist(String system, Integer level, String versionName) {
        AnswerItem objectAnswer = readByKey(system, level, versionName);
        return (objectAnswer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) && (objectAnswer.getItem() != null); // Call was successfull and object was found.
    }

    @Override
    public Answer create(BuildRevisionInvariant buildRevisionInvariant) {
        return BuildRevisionInvariantDAO.create(buildRevisionInvariant);
    }

    @Override
    public Answer delete(BuildRevisionInvariant buildRevisionInvariant) {
        return BuildRevisionInvariantDAO.delete(buildRevisionInvariant);
    }

    @Override
    public Answer update(String system, Integer level, Integer seq, BuildRevisionInvariant buildRevisionInvariant) {
        return BuildRevisionInvariantDAO.update(system, level, seq, buildRevisionInvariant);
    }

    @Override
    public BuildRevisionInvariant convert(AnswerItem answerItem) throws CerberusException {
        if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (BuildRevisionInvariant) answerItem.getItem();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public List<BuildRevisionInvariant> convert(AnswerList answerList) throws CerberusException {
        if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (List<BuildRevisionInvariant>) answerList.getDataList();
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
    public AnswerList<List<String>> readDistinctValuesByCriteria(String system, String searchParameter, Map<String, List<String>> individualSearch, String columnName) {
        return BuildRevisionInvariantDAO.readDistinctValuesByCriteria(system, searchParameter, individualSearch, columnName);
    }

}
