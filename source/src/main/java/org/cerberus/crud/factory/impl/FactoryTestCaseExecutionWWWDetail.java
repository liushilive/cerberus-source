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

import org.cerberus.crud.entity.StatisticDetail;
import org.cerberus.crud.factory.IFactoryTestCaseExecutionWWWDetail;
import org.springframework.stereotype.Service;

/**
 * @author bcivel
 */
@Service
public class FactoryTestCaseExecutionWWWDetail implements IFactoryTestCaseExecutionWWWDetail {

    @Override
    public StatisticDetail create(long start, long end, String url, String ext, int status, String method, long bytes, long time, String hostReq, String pageRes, String contentType) {
        StatisticDetail statisticDetail = new StatisticDetail();
        statisticDetail.setBytes(bytes);
        statisticDetail.setContentType(contentType);
        statisticDetail.setEnd(end);
        statisticDetail.setExt(ext);
        statisticDetail.setHostReq(hostReq);
        statisticDetail.setMethod(method);
        statisticDetail.setPageRes(pageRes);
        statisticDetail.setStart(start);
        statisticDetail.setStatus(status);
        statisticDetail.setTime(time);
        statisticDetail.setUrl(url);
        return statisticDetail;
    }

}
