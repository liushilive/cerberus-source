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
import org.cerberus.crud.dao.IAppServiceDAO;
import org.cerberus.crud.entity.AppService;
import org.cerberus.crud.entity.AppServiceContent;
import org.cerberus.crud.entity.AppServiceHeader;
import org.cerberus.crud.service.IAppServiceContentService;
import org.cerberus.crud.service.IAppServiceHeaderService;
import org.cerberus.crud.service.IAppServiceService;
import org.cerberus.engine.entity.MessageGeneral;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author cte
 */
@Service
public class AppServiceService implements IAppServiceService {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AppServiceService.class);

    @Autowired
    IAppServiceDAO appServiceDao;
    @Autowired
    private IAppServiceContentService appServiceContentService;
    @Autowired
    private IAppServiceHeaderService appServiceHeaderService;

    @Override
    public AppService findAppServiceByKey(String name) throws CerberusException {
        return appServiceDao.findAppServiceByKey(name);
    }

    @Override
    public AnswerList readByLikeName(String name, int limit){
        return appServiceDao.findAppServiceByLikeName(name,limit);
    }

    @Override
    public AnswerList readByCriteria(int startPosition, int length, String columnName, String sort, String searchParameter, Map<String, List<String>> individualSearch) {
        return appServiceDao.readByCriteria(startPosition, length, columnName, sort, searchParameter, individualSearch);
    }

    @Override
    public AnswerItem readByKey(String key) {
        return appServiceDao.readByKey(key);
    }

    @Override
    public AnswerItem readByKeyWithDependency(String key, String activedetail) {
        AnswerItem answerAppService = this.readByKey(key);
        AppService appService = (AppService) answerAppService.getItem();
        try{
            AnswerList content = appServiceContentService.readByVarious(key, activedetail);
            appService.setContentList((List<AppServiceContent>) content.getDataList());
            AnswerList header = appServiceHeaderService.readByVarious(key, activedetail);
            appService.setHeaderList((List<AppServiceHeader>) header.getDataList());
            answerAppService.setItem(appService);
        }catch(Exception e){

        }




        return answerAppService;
    }

    @Override
    public AnswerList readDistinctValuesByCriteria(String searchParameter, Map<String, List<String>> individualSearch, String columnName) {
        return appServiceDao.readDistinctValuesByCriteria(searchParameter, individualSearch, columnName);
    }

    @Override
    public Answer create(AppService object) {
        return appServiceDao.create(object);
    }

    @Override
    public Answer update(String service, AppService object) {
        return appServiceDao.update(service, object);
    }

    @Override
    public Answer delete(AppService object) {
        return appServiceDao.delete(object);
    }

    @Override
    public AppService convert(AnswerItem answerItem) throws CerberusException {
        if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (AppService) answerItem.getItem();
        }
        throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
    }

    @Override
    public List<AppService> convert(AnswerList answerList) throws CerberusException {
        if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
            //if the service returns an OK message then we can get the item
            return (List<AppService>) answerList.getDataList();
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
    public String guessContentType(AppService service, String defaultValue) {
        String result = defaultValue;
        for (AppServiceHeader object : service.getResponseHeaderList()) {
            if (object.getKey().equalsIgnoreCase("Content-Type")) {
                if (object.getValue().contains("application/json")) {
                    LOG.debug("JSON format guessed from header : " + object.getKey() + " : " + object.getValue());
                    return AppService.RESPONSEHTTPBODYCONTENTTYPE_JSON;
                } else if (object.getValue().contains("application/xml")) {
                    LOG.debug("XML format guessed from header : " + object.getKey() + " : " + object.getValue());
                    return AppService.RESPONSEHTTPBODYCONTENTTYPE_XML;
                }
            }
        }
        if (service.getResponseHTTPBody().startsWith("<")) { // TODO find a better solution to guess the format of the request.
            LOG.debug("XML format guessed from 1st caracter of body.");
            return AppService.RESPONSEHTTPBODYCONTENTTYPE_XML;
        } else if (service.getResponseHTTPBody().startsWith("{")) {
            LOG.debug("JSON format guessed from 1st caracter of body.");
            return AppService.RESPONSEHTTPBODYCONTENTTYPE_JSON;
        }
        if (StringUtil.isNullOrEmpty(result)) {
            result = AppService.RESPONSEHTTPBODYCONTENTTYPE_TXT;
        }
        return result;
    }

}
