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
package org.cerberus.crud.factory;

import java.sql.Timestamp;
import org.cerberus.crud.entity.Label;

/**
 * @author bcivel
 */
public interface IFactoryLabel {

    /**
     * 
     * @param id
     * @param system
     * @param label
     * @param type
     * @param color
     * @param parentLabel
     * @param reqType
     * @param reqStatus
     * @param reqCriticity
     * @param description
     * @param longDesc
     * @param usrCreated
     * @param dateCreated
     * @param usrModif
     * @param dateModif
     * @return 
     */
    Label create(Integer id, String system, String label, String type, String color, String parentLabel, String reqType, String reqStatus, String reqCriticity, String description, String longDesc
            , String usrCreated, Timestamp dateCreated, String usrModif, Timestamp dateModif);


}
