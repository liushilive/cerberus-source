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
package org.cerberus.crud.factory.impl;

import java.sql.Timestamp;
import org.cerberus.crud.entity.Tag;
import org.cerberus.crud.factory.IFactoryTag;
import org.springframework.stereotype.Service;

/**
 * @author vertigo
 */
@Service
public class FactoryTag implements IFactoryTag {

    @Override
    public Tag create(long id, String tag, String description, String campaign, Timestamp dateEndQueue,
            String usrCreated, Timestamp dateCreated, String usrModif, Timestamp dateModif) {
        Tag newObject = new Tag();
        newObject.setId(id);
        newObject.setTag(tag);
        newObject.setDescription(description);
        newObject.setCampaign(campaign);
        newObject.setDateEndQueue(dateEndQueue);
        newObject.setUsrModif(usrModif);
        newObject.setUsrCreated(usrCreated);
        newObject.setDateModif(dateModif);
        newObject.setDateCreated(dateCreated);
        return newObject;
    }

    @Override
    public Tag create(String tag) {
        Tag newObject = new Tag();
        newObject.setTag(tag);
        return newObject;
    }


}
