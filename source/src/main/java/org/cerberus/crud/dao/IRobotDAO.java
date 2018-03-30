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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.cerberus.crud.entity.Robot;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

/**
 * @author bcivel
 */
public interface IRobotDAO {

    /**
     *
     * @param robotid
     * @return
     */
    AnswerItem<Robot> readByKeyTech(Integer robotid);

    /**
     *
     * @param robot
     * @return
     */
    Robot readByKey(String robot) throws CerberusException;

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
    AnswerList<Robot> readByCriteria(int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch);

    /**
     *
     * @param robot
     * @return
     */
    Answer create(Robot robot);

    /**
     *
     * @param robot
     * @return
     */
    Answer delete(Robot robot);

    /**
     *
     * @param robot
     * @return
     */
    Answer update(Robot robot);

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    Robot loadFromResultSet(ResultSet rs) throws SQLException;

    /**
     * 
     * @param searchParameter
     * @param individualSearch
     * @param columnName
     * @return 
     */
    public AnswerList<List<String>> readDistinctValuesByCriteria(String searchParameter, Map<String, List<String>> individualSearch, String columnName);

}
