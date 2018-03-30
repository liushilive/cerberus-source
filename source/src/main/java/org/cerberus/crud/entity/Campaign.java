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
package org.cerberus.crud.entity;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author memiks
 * @Entity
 * @Table(catalog = "cerberus", schema = "", uniqueConstraints = {
 * @UniqueConstraint(columnNames = {"campaign"})})
 * @XmlRootElement
 * @NamedQueries({
 * @NamedQuery(name = "Campaign.findAll", query = "SELECT c FROM campaign c"),
 * @NamedQuery(name = "Campaign.findByCampaignID", query = "SELECT c FROM
 * campaign c WHERE c.campaignID = :campaignID"),
 * @NamedQuery(name = "Campaign.findByCampaign", query = "SELECT c FROM campaign
 * c WHERE c.campaign = :campaign"),
 * @NamedQuery(name = "Campaign.findByDescription", query = "SELECT c FROM
 * campaign c WHERE c.description = :description")})
 */
public class Campaign implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer campaignID;
    private String campaign;
    private String distribList;
    private String notifyStartTagExecution;
    private String notifyEndTagExecution;
    private String description;

    private List<CampaignParameter> campaignParameterList;

    /**
     * Invariant PROPERTY TYPE String.
     */
    public static final String NOTIFYSTARTTAGEXECUTION_Y = "Y";
    public static final String NOTIFYSTARTTAGEXECUTION_N = "N";
    public static final String NOTIFYSTARTTAGEXECUTION_CIKO = "CIKO";

    public Campaign() {
    }

    public Campaign(Integer campaignID) {
        this.campaignID = campaignID;
    }

    public Campaign(Integer campaignID, String campaign, String description) {
        this.campaignID = campaignID;
        this.campaign = campaign;
        this.description = description;
    }

    public Integer getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(Integer campaignID) {
        this.campaignID = campaignID;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getDistribList() {
        return distribList;
    }

    public void setDistribList(String distribList) {
        this.distribList = distribList;
    }

    public String getNotifyStartTagExecution() {
        return notifyStartTagExecution;
    }

    public void setNotifyStartTagExecution(String notifyStartTagExecution) {
        this.notifyStartTagExecution = notifyStartTagExecution;
    }

    public String getNotifyEndTagExecution() {
        return notifyEndTagExecution;
    }

    public void setNotifyEndTagExecution(String notifyEndTagExecution) {
        this.notifyEndTagExecution = notifyEndTagExecution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<CampaignParameter> getCampaignParameterList() {
        return campaignParameterList;
    }

    public void setCampaignParameterList(List<CampaignParameter> campaignParameterList) {
        this.campaignParameterList = campaignParameterList;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (campaignID != null ? campaignID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Campaign)) {
            return false;
        }
        Campaign other = (Campaign) object;
        if ((this.campaignID == null && other.campaignID != null) || (this.campaignID != null && !this.campaignID.equals(other.campaignID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.cerberus.crud.entity.Campaign[ campaignID=" + campaignID + " ]";
    }

}
