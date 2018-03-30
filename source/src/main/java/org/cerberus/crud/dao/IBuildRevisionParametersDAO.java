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
import org.cerberus.crud.entity.BuildRevisionParameters;

import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;

public interface IBuildRevisionParametersDAO {

    /**
     *
     * @param id
     * @return
     */
    AnswerItem readByKeyTech(int id);

    /**
     *
     * @param system
     * @return
     */
    AnswerItem readLastBySystem(String system);

    /**
     *
     * @param build
     * @param revision
     * @param release
     * @param application
     * @return A list of BuildRevisionParameters object for a build, revision,
     * release, application
     */
    AnswerItem readByVarious2(String build, String revision, String release, String application);

    /**
     *
     * @param system
     * @param application
     * @param build
     * @param revision
     * @param start
     * @param amount
     * @param column
     * @param dir
     * @param searchTerm
     * @param individualSearch
     * @return
     */
    AnswerList readByVarious1ByCriteria(String system, String application, String build, String revision, int start, int amount, String column, String dir, String searchTerm, Map<String, List<String>> individualSearch);

    /**
     *
     * @param system
     * @param build
     * @param revision
     * @param lastBuild
     * @param lastRevision
     * @return for each application included in the build content between
     * lastbuild / lastrevision and build / revision, we return the release that
     * correspond to the max svn number. This is returned only if jenkinsbuildid
     * fiels if feed.
     */
    AnswerList readMaxSVNReleasePerApplication(String system, String build, String revision, String lastBuild, String lastRevision);

    /**
     *
     * @param system
     * @param build
     * @param revision
     * @param lastBuild
     * @param lastRevision
     * @return
     */
    AnswerList readNonSVNRelease(String system, String build, String revision, String lastBuild, String lastRevision);

    /**
     *
     * @param brp
     * @return
     */
    Answer create(BuildRevisionParameters brp);

    /**
     *
     * @param brp
     * @return
     */
    Answer delete(BuildRevisionParameters brp);

    /**
     *
     * @param brp
     * @return
     */
    Answer update(BuildRevisionParameters brp);

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    BuildRevisionParameters loadFromResultSet(ResultSet rs) throws SQLException;

    /**
     * 
     * @param system
     * @param searchParameter
     * @param individualSearch
     * @param columnName
     * @return 
     */
    public AnswerList<List<String>> readDistinctValuesByCriteria(String system, String searchParameter, Map<String, List<String>> individualSearch, String columnName);
}
