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
package org.cerberus.crud.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.cerberus.crud.dao.IUserSystemDAO;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.crud.entity.User;
import org.cerberus.database.DatabaseSpring;
import org.cerberus.engine.entity.MessageGeneral;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.crud.entity.UserSystem;
import org.cerberus.exception.CerberusException;
import org.cerberus.crud.factory.IFactoryUserSystem;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bcivel
 */
@Repository
public class UserSystemDAO implements IUserSystemDAO {

    @Autowired
    private DatabaseSpring databaseSpring;
    @Autowired
    private IFactoryUserSystem factoryUserSystem;

    /**
     * Declare SQL queries used by this {@link UserSystem}
     *
     * @author Aurelien Bourdon
     */
    private static interface Query {

        /**
         * Get list of {@link UserSystem} associated with the given
         * {@link User}'s name
         */
        String READ_BY_USER = "SELECT * FROM usersystem uss WHERE uss.`login` = ? ";

        /**
         * Create a new {@link UserSystem}
         */
        String CREATE = "INSERT INTO `usersystem` (`login`, `system`) VALUES (?, ?)";

        /**
         * Remove an existing {@link UserSystem}
         */
        String DELETE = "DELETE FROM `usersystem` WHERE `login` = ? AND `system` = ?";

    }

    /**
     * The associated {@link Logger} to this class
     */
    private static final Logger LOG = LogManager.getLogger(UserSystemDAO.class);

    /**
     * The associated entity name to this DAO
     */
    private static final String OBJECT_NAME = UserSystem.class.getSimpleName();

    @Override
    public UserSystem findUserSystemByKey(String login, String system) throws CerberusException {
        UserSystem result = null;
        final String query = "SELECT * FROM usersystem u WHERE u.`login` = ? and u.`system` = ?";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, login);
                preStat.setString(2, system);

                ResultSet resultSet = preStat.executeQuery();
                try {
                    if (resultSet.first()) {
                        result = this.loadUserSystemFromResultSet(resultSet);
                    }
                } catch (SQLException exception) {
                    LOG.warn("Unable to execute query : " + exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.warn(e.toString());
            }
        }
        return result;
    }

    @Override
    public List<UserSystem> findallUser() throws CerberusException {
        List<UserSystem> list = null;
        final String query = "SELECT * FROM usersystem ORDER BY `login`";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                ResultSet resultSet = preStat.executeQuery();
                try {
                    list = new ArrayList<UserSystem>();
                    while (resultSet.next()) {
                        UserSystem user = this.loadUserSystemFromResultSet(resultSet);
                        list.add(user);
                    }
                } catch (SQLException exception) {
                    LOG.warn("Unable to execute query : " + exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.warn(e.toString());
            }
        }
        return list;
    }

    @Override
    public List<UserSystem> findUserSystemByUser(String login) throws CerberusException {
        List<UserSystem> list = null;
        final String query = "SELECT * FROM usersystem u WHERE u.`login` = ? ";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, login);

                ResultSet resultSet = preStat.executeQuery();
                try {
                    list = new ArrayList<UserSystem>();
                    while (resultSet.next()) {
                        UserSystem user = this.loadUserSystemFromResultSet(resultSet);
                        list.add(user);
                    }
                } catch (SQLException exception) {
                    LOG.warn("Unable to execute query : " + exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.warn(e.toString());
            }
        }
        return list;
    }

    @Override
    public List<UserSystem> findUserSystemBySystem(String system) throws CerberusException {
        List<UserSystem> list = null;
        final String query = "SELECT * FROM usersystem u WHERE u.`system` = ? ";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, system);

                ResultSet resultSet = preStat.executeQuery();
                try {
                    list = new ArrayList<UserSystem>();
                    while (resultSet.next()) {
                        UserSystem user = this.loadUserSystemFromResultSet(resultSet);
                        list.add(user);
                    }
                } catch (SQLException exception) {
                    LOG.warn("Unable to execute query : " + exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.warn(e.toString());
            }
        }
        return list;
    }

    @Override
    public void insertUserSystem(UserSystem userSystem) throws CerberusException {
        final String query = "INSERT INTO usersystem (`login`, `system`) VALUES (?, ?)";

        
        try(Connection connection = this.databaseSpring.connect();
        		PreparedStatement preStat = connection.prepareStatement(query);) {
            try {
                preStat.setString(1, userSystem.getLogin());
                preStat.setString(2, userSystem.getSystem());
                preStat.execute();
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
                throw new CerberusException(new MessageGeneral(MessageGeneralEnum.CANNOT_UPDATE_TABLE));
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
            throw new CerberusException(new MessageGeneral(MessageGeneralEnum.CANNOT_UPDATE_TABLE));
        } 
    }

    @Override
    public void deleteUserSystem(UserSystem userSystem) throws CerberusException {
        final String query = "DELETE FROM usersystem WHERE `login` = ? and `system` = ?";

        
        try(Connection connection = this.databaseSpring.connect();
        		PreparedStatement preStat = connection.prepareStatement(query);) {
            try {
                preStat.setString(1, userSystem.getLogin());
                preStat.setString(2, userSystem.getSystem());
                preStat.execute();
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
                throw new CerberusException(new MessageGeneral(MessageGeneralEnum.CANNOT_UPDATE_TABLE));
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
            throw new CerberusException(new MessageGeneral(MessageGeneralEnum.CANNOT_UPDATE_TABLE));
        }
    }

    @Override
    public void updateUserSystem(UserSystem userSystem) throws CerberusException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AnswerList<UserSystem> readByUser(String login) {
        AnswerList ans = new AnswerList();
        MessageEvent msg = null;

        try (Connection connection = databaseSpring.connect();
                PreparedStatement preStat = connection.prepareStatement(Query.READ_BY_USER)) {
            // Prepare and execute query
            preStat.setString(1, login);
            try(ResultSet resultSet = preStat.executeQuery();){
            	// Parse query
                List<UserSystem> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(loadUserSystemFromResultSet(resultSet));
                }
                ans.setDataList(result);

                // Set the final message
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK).resolveDescription("ITEM", OBJECT_NAME)
                        .resolveDescription("OPERATION", "GET");
            }catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } 
        } catch (Exception e) {
            LOG.warn("Unable to read userSystem: " + e.getMessage());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED).resolveDescription("DESCRIPTION",
                    e.toString());
        } finally {
            ans.setResultMessage(msg);
        }

        return ans;
    }

    @Override
    public Answer create(UserSystem sys) {
        Answer ans = new Answer();
        MessageEvent msg = null;

        try (Connection connection = databaseSpring.connect();
                PreparedStatement preStat = connection.prepareStatement(Query.CREATE)) {
            // Prepare and execute query
            preStat.setString(1, sys.getLogin());
            preStat.setString(2, sys.getSystem());
            preStat.executeUpdate();

            // Set the final message
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK).resolveDescription("ITEM", OBJECT_NAME)
                    .resolveDescription("OPERATION", "CREATE");
        } catch (Exception e) {
            LOG.warn("Unable to create UserSystem: " + e.getMessage());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED).resolveDescription("DESCRIPTION",
                    e.toString());
        } finally {
            ans.setResultMessage(msg);
        }

        return ans;
    }

    @Override
    public Answer remove(UserSystem sys) {
        Answer ans = new Answer();
        MessageEvent msg = null;

        try (Connection connection = databaseSpring.connect();
                PreparedStatement preStat = connection.prepareStatement(Query.DELETE)) {
            // Prepare and execute query
            preStat.setString(1, sys.getLogin());
            preStat.setString(2, sys.getSystem());
            preStat.executeUpdate();

            // Set the final message
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK).resolveDescription("ITEM", OBJECT_NAME)
                    .resolveDescription("OPERATION", "DELTE");
        } catch (Exception e) {
            LOG.warn("Unable to delete UserSystem: " + e.getMessage());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED).resolveDescription("DESCRIPTION",
                    e.toString());
        } finally {
            ans.setResultMessage(msg);
        }

        return ans;
    }

    private UserSystem loadUserSystemFromResultSet(ResultSet rs) throws SQLException {
        String login = ParameterParserUtil.parseStringParam(rs.getString("login"), "");
        String system = ParameterParserUtil.parseStringParam(rs.getString("system"), "");
        return factoryUserSystem.create(login, system);
    }

}
