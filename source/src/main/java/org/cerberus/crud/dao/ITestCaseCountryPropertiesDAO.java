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

import org.cerberus.crud.entity.TestCaseCountryProperties;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerList;

/**
 * {Insert class description here}
 *
 * @author Tiago Bernardes
 * @version 1.0, 28/Dez/2012
 * @since 2.0.0
 */
public interface ITestCaseCountryPropertiesDAO {

    List<TestCaseCountryProperties> findListOfPropertyPerTestTestCase(String test, String testcase);
    
    List<TestCaseCountryProperties> findOnePropertyPerTestTestCase(String test, String testcase, String oneproperty);

    List<TestCaseCountryProperties> findDistinctPropertiesOfTestCase(String test, String testcase);

    List<String> findCountryByProperty(TestCaseCountryProperties testCaseCountryProperties);

    List<String> findCountryByPropertyNameAndTestCase(String test, String testcase, String property);

    List<TestCaseCountryProperties> findListOfPropertyPerTestTestCaseCountry(String test, String testcase, String country);

    TestCaseCountryProperties findTestCaseCountryPropertiesByKey(String test, String testcase, String country, String property) throws CerberusException;

    void insertTestCaseCountryProperties(TestCaseCountryProperties testCaseCountryProperties) throws CerberusException;

    void updateTestCaseCountryProperties(TestCaseCountryProperties testCaseCountryProperties) throws CerberusException;

    public void deleteTestCaseCountryProperties(TestCaseCountryProperties tccp) throws CerberusException;

    AnswerList findTestCaseCountryPropertiesByValue1(int testDataLib, String name, String country, String propertyType);

    Answer createTestCaseCountryPropertiesBatch(List<TestCaseCountryProperties> listOfPropertiesToInsert);

    Answer create(TestCaseCountryProperties object);

    Answer delete(TestCaseCountryProperties object);

    Answer update(TestCaseCountryProperties object);

}
