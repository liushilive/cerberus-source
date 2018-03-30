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

import org.cerberus.crud.entity.MyVersion;

/**
 *
 * @author bdumont
 */
public interface IMyVersionService {

    /**
     *
     * @param key
     * @return MyVersion that correspond to the key.
     */
    MyVersion findMyVersionByKey(String key);

    /**
     * This method can be used in order to retrieve a parameter directly in
     * String format.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getMyVersionStringByKey(String key, String defaultValue);

    /**
     *
     * @param key
     * @param value
     * @return true if the update was done. False in case there were an issue.
     */
    boolean UpdateMyVersionString(String key, String value);
    
    /**
     *
     * @param myversion
     * @return true if the update was done. False in case there were an issue.
     */
    boolean UpdateMyVersionTable(MyVersion myversion);

}
