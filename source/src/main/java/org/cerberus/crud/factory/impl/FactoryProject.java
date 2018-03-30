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

import org.cerberus.crud.entity.Project;
import org.cerberus.crud.factory.IFactoryProject;
import org.springframework.stereotype.Service;

/**
 * @author bcivel
 */
@Service
public class FactoryProject implements IFactoryProject {

    @Override
    public Project create(String idProject, String code, String description, String active, String dateCreation) {
        Project project = new Project();
        project.setActive(active);
        project.setCode(code);
        project.setDateCreation(dateCreation);
        project.setDescription(description);
        project.setIdProject(idProject);
        return project;
    }
}
