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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.cerberus.crud.dao.IUserDAO;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryUser;
import org.cerberus.crud.factory.impl.FactoryUser;
import org.cerberus.database.DatabaseSpring;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.util.ParameterParserUtil;
import org.cerberus.util.SqlUtil;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * {Insert class description here}
 *
 * @author Benoit Dumont
 */
@Repository
public class UserDAO implements IUserDAO {

    /**
     * Description of the variable here.
     */
    @Autowired
    private DatabaseSpring databaseSpring;
    @Autowired
    private IFactoryUser factoryUser;

    private static final Logger LOG = LogManager.getLogger(UserDAO.class);

    private final String OBJECT_NAME = "User";
    private final String SQL_DUPLICATED_CODE = "23000";
    private final int MAX_ROW_SELECTED = 100000;

    @Override
    public User findUserByKey(String login) {
        User result = null;
        final String query = "SELECT * FROM user usr WHERE usr.login = ? ";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, login);

                ResultSet resultSet = preStat.executeQuery();
                try {
                    if (resultSet.first()) {
                        result = this.loadFromResultSet(resultSet);
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
    public List<User> findAllUser() {
        List<User> list = null;
        final String query = "SELECT * FROM user usr ORDER BY userid";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                ResultSet resultSet = preStat.executeQuery();
                try {
                    list = new ArrayList<User>();
                    while (resultSet.next()) {
                        User user = this.loadFromResultSet(resultSet);
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
    public boolean insertUser(User user) {
        boolean bool = false;
        final String query = "INSERT INTO user (Login, Password, Name, Request, ReportingFavorite, RobotHost, DefaultSystem, Team, Language, Email, UserPreferences) VALUES (?, SHA(?), ?, ?, ?, ?, ?, ?, ?, ?, '')";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            try {
                preStat.setString(1, user.getLogin());
                preStat.setString(2, user.getPassword());
                preStat.setString(3, user.getName());
                preStat.setString(4, user.getRequest());
                preStat.setString(5, user.getReportingFavorite());
                preStat.setString(6, user.getRobotHost());
                preStat.setString(7, user.getDefaultSystem());
                preStat.setString(8, user.getTeam());
                preStat.setString(9, user.getLanguage());
                preStat.setString(10, user.getEmail());

                preStat.executeUpdate();
                ResultSet resultSet = preStat.getGeneratedKeys();
                try {
                    if (resultSet.first()) {
                        user.setUserID(resultSet.getInt(1));
                        bool = true;
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
        return bool;
    }

    @Override
    public boolean deleteUser(User user) {
        boolean bool = false;
        final String query = "DELETE FROM user WHERE userid = ?";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setInt(1, user.getUserID());

                bool = preStat.executeUpdate() > 0;
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
        return bool;
    }

    @Override
    public boolean updateUser(User user) {
        boolean bool = false;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE user SET Login = ?, Name = ?, Request = ?, ReportingFavorite = ?, RobotHost = ?,");
        query.append("Team = ?, Language = ?, DefaultSystem = ?, Email= ? , robotPort = ?, ");
        query.append("robotPlatform = ?, ");
        query.append("robotBrowser = ?, robotVersion = ? , robot = ? , userPreferences = ?  WHERE userid = ?");

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                preStat.setString(1, user.getLogin());
                preStat.setString(2, user.getName());
                preStat.setString(3, user.getRequest());
                preStat.setString(4, user.getReportingFavorite());
                preStat.setString(5, user.getRobotHost());
                preStat.setString(6, user.getTeam());
                preStat.setString(7, user.getLanguage());
                preStat.setString(8, user.getDefaultSystem());
                preStat.setString(9, user.getEmail());
                preStat.setString(10, String.valueOf(user.getRobotPort()));
                preStat.setString(11, user.getRobotPlatform());
                preStat.setString(12, user.getRobotBrowser());
                preStat.setString(13, user.getRobotVersion());
                preStat.setString(14, user.getRobot());
                preStat.setString(15, user.getUserPreferences());
                preStat.setInt(16, user.getUserID());

                bool = preStat.executeUpdate() > 0;
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
        return bool;
    }

    @Override
    public AnswerItem<User> updateUserPassword(User user, String password, String requestNewPassword) {
        AnswerItem<User> answer = new AnswerItem<User>();
        MessageEvent msg;
        boolean res = false;
        final String sql = "UPDATE user SET Password = SHA(?) , Request = ? WHERE Login LIKE ?";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(sql);
            try {
                preStat.setString(1, password);
                preStat.setString(2, requestNewPassword);
                preStat.setString(3, user.getLogin());

                res = preStat.executeUpdate() > 0;
            } catch (SQLException exception) {
                LOG.warn("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", "Update Password - Unable to execute query"));
            } finally {
                if (preStat != null) {
                    preStat.close();
                }
            }
        } catch (SQLException exception) {
            LOG.warn("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", "Update Password - Unable to execute query"));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOG.warn(e.toString());
            }
        }

        if (res) {
            answer.setItem(this.findUserByKey(user.getLogin()));
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
            msg.setDescription(msg.getDescription().replace("%ITEM%", "User").replace("%OPERATION%", "Update password"));
        } else {
            answer.setItem(user);
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_EXPECTED);
            msg.setDescription(msg.getDescription().replace("%ITEM%", "User").
                    replace("%OPERATION%", "Update Password").replace("%REASON%", "Your password was not updated. "
                    + "Please contact your Cerberus' administrator to learn more information."));
        }

        answer.setResultMessage(msg);
        return answer;
    }

    @Override
    public Answer clearResetPasswordToken(User user) {
        Answer ans = new Answer();
        MessageEvent msg = null;
        final String sql = "UPDATE user SET resetPasswordToken = '' WHERE Login LIKE ?";

        try (Connection connection = databaseSpring.connect();
                PreparedStatement preStat = connection.prepareStatement(sql)) {
            // Prepare and execute query
            preStat.setString(1, user.getLogin());
            preStat.executeUpdate();

            // Set the final message
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK).resolveDescription("ITEM", OBJECT_NAME)
                    .resolveDescription("OPERATION", "UPDATE");
        } catch (Exception e) {
            LOG.warn("Unable to update user: " + e.getMessage());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED).resolveDescription("DESCRIPTION",
                    e.toString());
        } finally {
            ans.setResultMessage(msg);
        }

        return ans;
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        boolean bool = false;
        final String sql = "SELECT Password, SHA(?) AS currentPassword FROM user WHERE Login LIKE ?";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(sql);
            try {
                preStat.setString(1, password);
                preStat.setString(2, user.getLogin());
                ResultSet rs = preStat.executeQuery();
                try {
                    if (rs.first()) {
                        bool = rs.getString("Password").equals(rs.getString("currentPassword"));
                    }
                } catch (SQLException ex) {
                    LOG.warn(ex.toString());
                } finally {
                    rs.close();
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

        return bool;
    }

    @Override
    public boolean verifyResetPasswordToken(User user, String resetPasswordToken) {
        boolean bool = false;
        final String sql = "SELECT resetPasswordToken, SHA(?) AS currentPassword FROM user WHERE Login LIKE ?";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(sql);
            try {
                preStat.setString(1, resetPasswordToken);
                preStat.setString(2, user.getLogin());
                ResultSet rs = preStat.executeQuery();
                try {
                    if (rs.first()) {
                        bool = rs.getString("resetPasswordToken").equals(rs.getString("currentPassword"));
                    }
                } catch (SQLException ex) {
                    LOG.warn(ex.toString());
                } finally {
                    rs.close();
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

        return bool;
    }

    @Override
    public List<User> findTestDataListByCriteria(int start, int amount, String column, String dir, String searchTerm, String individualSearch) {
        List<User> result = new ArrayList<User>();
        StringBuilder gSearch = new StringBuilder();
        StringBuilder searchSQL = new StringBuilder();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM user usr");

        gSearch.append(" where (usr.`login` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or usr.`name` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or usr.`team` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or usr.`defaultSystem` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or usr.`email` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or usr.`request` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%')");

        if (!searchTerm.equals("") && !individualSearch.equals("")) {
            searchSQL.append(gSearch.toString());
            searchSQL.append(" and ");
            searchSQL.append(individualSearch);
        } else if (!individualSearch.equals("")) {
            searchSQL.append(" where `");
            searchSQL.append(individualSearch);
            searchSQL.append("`");
        } else if (!searchTerm.equals("")) {
            searchSQL.append(gSearch.toString());
        }

        query.append(searchSQL);
        query.append("order by `");
        query.append(column);
        query.append("` ");
        query.append(dir);
        if (amount != -1) {
            query.append(" limit ");
            query.append(start);
            query.append(" , ");
            query.append(amount);
        }

        User user;

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                ResultSet resultSet = preStat.executeQuery();
                try {

                    while (resultSet.next()) {
                        result.add(this.loadFromResultSet(resultSet));
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
    public Integer getNumberOfUserPerCriteria(String searchTerm, String inds) {
        Integer result = 0;
        StringBuilder query = new StringBuilder();
        StringBuilder gSearch = new StringBuilder();
        String searchSQL = "";

        query.append("SELECT count(*) FROM `user` ");

        gSearch.append(" where (`login` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or `name` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or `team` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or `defaultSystem` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or `email` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%'");
        gSearch.append(" or `request` like '%");
        gSearch.append(searchTerm);
        gSearch.append("%')");

        if (!searchTerm.equals("") && !inds.equals("")) {
            searchSQL = gSearch.toString() + " and " + inds;
        } else if (!inds.equals("")) {
            searchSQL = " where " + inds;
        } else if (!searchTerm.equals("")) {
            searchSQL = gSearch.toString();
        }

        query.append(searchSQL);

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                ResultSet resultSet = preStat.executeQuery();
                try {

                    if (resultSet.first()) {
                        result = resultSet.getInt(1);
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
    public List<User> findAllUserBySystem(String system) {
        List<User> list = null;
        final String query = "SELECT * "
                + "FROM `user` usr, usersystem us "
                + "WHERE usr.login = us.login "
                + "AND us.system = ? "
                + "ORDER BY usr.login";

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, system);

                ResultSet resultSet = preStat.executeQuery();
                try {
                    list = new ArrayList<User>();
                    while (resultSet.next()) {
                        User user = this.loadFromResultSet(resultSet);
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
    public AnswerItem readByKey(String login) {
        AnswerItem ans = new AnswerItem();
        User result;
        final String query = "SELECT * FROM `user` usr WHERE usr.`login` = ?";
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));

        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setString(1, login);
                ResultSet resultSet = preStat.executeQuery();
                try {
                    if (resultSet.first()) {
                        result = loadFromResultSet(resultSet);
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                        msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "SELECT"));
                        ans.setItem(result);
                    } else {
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_NO_DATA_FOUND);
                    }
                } catch (SQLException exception) {
                    LOG.error("Unable to execute query : " + exception.toString());
                    msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                    msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }

        //sets the message
        ans.setResultMessage(msg);
        return ans;
    }

    @Override
    public AnswerList readByCriteria(int start, int amount, String column, String dir, String searchTerm, String individualSearch) {
        AnswerList response = new AnswerList();
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
        List<User> applicationList = new ArrayList<User>();
        StringBuilder searchSQL = new StringBuilder();

        StringBuilder query = new StringBuilder();
        //SQL_CALC_FOUND_ROWS allows to retrieve the total number of columns by disrearding the limit clauses that 
        //were applied -- used for pagination p
        query.append("SELECT SQL_CALC_FOUND_ROWS * FROM user usr ");

        searchSQL.append(" where 1=1 ");

        if (!StringUtil.isNullOrEmpty(searchTerm)) {
            searchSQL.append(" and (usr.`login` like ?");
            searchSQL.append(" or usr.`name` like ?");
            searchSQL.append(" or usr.`team` like ?");
            searchSQL.append(" or usr.`language` like ?");
            searchSQL.append(" or usr.`ReportingFavorite` like ?");
            searchSQL.append(" or usr.`robotHost` like ?");
            searchSQL.append(" or usr.`robotPort` like ?");
            searchSQL.append(" or usr.`robotPlatform` like ?");
            searchSQL.append(" or usr.`robotBrowser` like ?");
            searchSQL.append(" or usr.`robotVersion` like ?");
            searchSQL.append(" or usr.`robot` like ?");
            searchSQL.append(" or usr.`DefaultSystem` like ?");
            searchSQL.append(" or usr.`Email` like ?)");
        }
        if (!StringUtil.isNullOrEmpty(individualSearch)) {
            searchSQL.append(" and (`").append(individualSearch).append("`)");
        }
        query.append(searchSQL);

        if (!StringUtil.isNullOrEmpty(column)) {
            query.append(" order by `").append(column).append("` ").append(dir);
        }

        if ((amount <= 0) || (amount >= MAX_ROW_SELECTED)) {
            query.append(" limit ").append(start).append(" , ").append(MAX_ROW_SELECTED);
        } else {
            query.append(" limit ").append(start).append(" , ").append(amount);
        }

        // Debug message on SQL.
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL : " + query.toString());
        }
        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                int i = 1;
                if (!StringUtil.isNullOrEmpty(searchTerm)) {
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                }
                ResultSet resultSet = preStat.executeQuery();
                try {
                    //gets the data
                    while (resultSet.next()) {
                        applicationList.add(this.loadFromResultSet(resultSet));
                    }

                    //get the total number of rows
                    resultSet = preStat.executeQuery("SELECT FOUND_ROWS()");
                    int nrTotalRows = 0;

                    if (resultSet != null && resultSet.next()) {
                        nrTotalRows = resultSet.getInt(1);
                    }

                    if (applicationList.size() >= MAX_ROW_SELECTED) { // Result of SQl was limited by MAX_ROW_SELECTED constrain. That means that we may miss some lines in the resultList.
                        LOG.error("Partial Result in the query.");
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_WARNING_PARTIAL_RESULT);
                        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", "Maximum row reached : " + MAX_ROW_SELECTED));
                        response = new AnswerList(applicationList, nrTotalRows);
                    } else {
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                        msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "SELECT"));
                        response = new AnswerList(applicationList, nrTotalRows);
                    }

                } catch (SQLException exception) {
                    LOG.error("Unable to execute query : " + exception.toString());
                    msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                    msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));

                } finally {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                }

            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } finally {
                if (preStat != null) {
                    preStat.close();
                }
            }

        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (!this.databaseSpring.isOnTransaction()) {
                    if (connection != null) {
                        connection.close();
                    }
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }

        response.setResultMessage(msg);
        response.setDataList(applicationList);
        return response;
    }

    @Override
    public AnswerList readByCriteria(int start, int amount, String column, String dir, String searchTerm, Map<String, List<String>> individualSearch) {
        AnswerList response = new AnswerList();
        MessageEvent msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", ""));
        List<User> applicationList = new ArrayList<User>();
        StringBuilder searchSQL = new StringBuilder();
        List<String> individalColumnSearchValues = new ArrayList<String>();

        StringBuilder query = new StringBuilder();
        //SQL_CALC_FOUND_ROWS allows to retrieve the total number of columns by disrearding the limit clauses that
        //were applied -- used for pagination p
        query.append("SELECT DISTINCT SQL_CALC_FOUND_ROWS usr.* FROM user usr ");

        if (!StringUtil.isNullOrEmpty(searchTerm)) {
            query.append("LEFT JOIN usergroup usg ON usg.`Login` = usr.`Login`");
        }

        searchSQL.append(" where 1=1 ");

        if (!StringUtil.isNullOrEmpty(searchTerm)) {
            searchSQL.append(" and (usr.`login` like ?");
            searchSQL.append(" or usr.`name` like ?");
            searchSQL.append(" or usr.`team` like ?");
            searchSQL.append(" or usr.`language` like ?");
            searchSQL.append(" or usr.`ReportingFavorite` like ?");
            searchSQL.append(" or usr.`robotHost` like ?");
            searchSQL.append(" or usr.`robotPort` like ?");
            searchSQL.append(" or usr.`robotPlatform` like ?");
            searchSQL.append(" or usr.`robotBrowser` like ?");
            searchSQL.append(" or usr.`robotVersion` like ?");
            searchSQL.append(" or usr.`robot` like ?");
            searchSQL.append(" or usr.`DefaultSystem` like ?");
            searchSQL.append(" or usr.`Email` like ?");
            searchSQL.append(" or usg.`GroupName` like ?)");
        }
        if (individualSearch != null && !individualSearch.isEmpty()) {
            searchSQL.append(" and ( 1=1 ");
            for (Map.Entry<String, List<String>> entry : individualSearch.entrySet()) {
                searchSQL.append(" and ");
                searchSQL.append(SqlUtil.getInSQLClauseForPreparedStatement(entry.getKey(), entry.getValue()));
                individalColumnSearchValues.addAll(entry.getValue());
            }
            searchSQL.append(" )");
        }
        query.append(searchSQL);

        if (!StringUtil.isNullOrEmpty(column)) {
            query.append(" order by `").append(column).append("` ").append(dir);
        }

        if ((amount <= 0) || (amount >= MAX_ROW_SELECTED)) {
            query.append(" limit ").append(start).append(" , ").append(MAX_ROW_SELECTED);
        } else {
            query.append(" limit ").append(start).append(" , ").append(amount);
        }

        // Debug message on SQL.
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL : " + query.toString());
        }
        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                int i = 1;
                if (!StringUtil.isNullOrEmpty(searchTerm)) {
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                    preStat.setString(i++, "%" + searchTerm + "%");
                }
                for (String individualColumnSearchValue : individalColumnSearchValues) {
                    preStat.setString(i++, individualColumnSearchValue);
                }
                ResultSet resultSet = preStat.executeQuery();
                try {
                    //gets the data
                    while (resultSet.next()) {
                        applicationList.add(this.loadFromResultSet(resultSet));
                    }

                    //get the total number of rows
                    resultSet = preStat.executeQuery("SELECT FOUND_ROWS()");
                    int nrTotalRows = 0;

                    if (resultSet != null && resultSet.next()) {
                        nrTotalRows = resultSet.getInt(1);
                    }

                    if (applicationList.size() >= MAX_ROW_SELECTED) { // Result of SQl was limited by MAX_ROW_SELECTED constrain. That means that we may miss some lines in the resultList.
                        LOG.error("Partial Result in the query.");
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_WARNING_PARTIAL_RESULT);
                        msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", "Maximum row reached : " + MAX_ROW_SELECTED));
                        response = new AnswerList(applicationList, nrTotalRows);
                    } else {
                        msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                        msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "SELECT"));
                        response = new AnswerList(applicationList, nrTotalRows);
                    }

                } catch (SQLException exception) {
                    LOG.error("Unable to execute query : " + exception.toString());
                    msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                    msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));

                } finally {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                }

            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } finally {
                if (preStat != null) {
                    preStat.close();
                }
            }

        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (!this.databaseSpring.isOnTransaction()) {
                    if (connection != null) {
                        connection.close();
                    }
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }

        response.setResultMessage(msg);
        response.setDataList(applicationList);
        return response;
    }

    @Override
    public Answer create(User user) {
        MessageEvent msg = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO user (Login, Password, Name, Request, ReportingFavorite, RobotHost, DefaultSystem, Team, Language, Email, UserPreferences)");
        query.append("  VALUES (?, SHA(?), ?, ?, ?, ?, ?, ?, ?, ?, '')");

        // Debug message on SQL.
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL : " + query.toString());
        }
        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                preStat.setString(1, user.getLogin());
                preStat.setString(2, user.getPassword());
                preStat.setString(3, user.getName());
                preStat.setString(4, user.getRequest());
                preStat.setString(5, user.getReportingFavorite());
                preStat.setString(6, user.getRobotHost());
                preStat.setString(7, user.getDefaultSystem());
                preStat.setString(8, user.getTeam());
                preStat.setString(9, user.getLanguage());
                preStat.setString(10, user.getEmail());

                preStat.executeUpdate();
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "INSERT"));

            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());

                if (exception.getSQLState().equals(SQL_DUPLICATED_CODE)) { //23000 is the sql state for duplicate entries
                    msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_DUPLICATE);
                    msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "INSERT").replace("%REASON%", exception.toString()));
                } else {
                    msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                    msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
                }
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }
        return new Answer(msg);
    }

    @Override
    public Answer delete(User user) {
        MessageEvent msg = null;
        final String query = "DELETE FROM user WHERE userid = ? ";

        // Debug message on SQL.
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL : " + query);
        }
        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query);
            try {
                preStat.setInt(1, user.getUserID());

                preStat.executeUpdate();
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "DELETE"));
            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }
        return new Answer(msg);
    }

    @Override
    public Answer update(User user) {
        MessageEvent msg = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE user SET Login = ?, Name = ?, Request = ?, ReportingFavorite = ?, RobotHost = ?,");
        query.append(" Team = ?, Language = ?, DefaultSystem = ?, Email= ? , robotPort = ?,");
        query.append(" robotPlatform = ?, robotBrowser = ?, robotVersion = ? , robot = ?, resetPasswordToken = SHA(?), ");
        query.append(" userPreferences = ?   WHERE userid = ?");

        // Debug message on SQL.
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL : " + query);
        }
        Connection connection = this.databaseSpring.connect();
        try {
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            try {
                preStat.setString(1, user.getLogin());
                preStat.setString(2, user.getName());
                preStat.setString(3, user.getRequest());
                preStat.setString(4, user.getReportingFavorite());
                preStat.setString(5, user.getRobotHost());
                preStat.setString(6, user.getTeam());
                preStat.setString(7, user.getLanguage());
                preStat.setString(8, user.getDefaultSystem());
                preStat.setString(9, user.getEmail());
                preStat.setString(10, String.valueOf(user.getRobotPort()));
                preStat.setString(11, user.getRobotPlatform());
                preStat.setString(12, user.getRobotBrowser());
                preStat.setString(13, user.getRobotVersion());
                preStat.setString(14, user.getRobot());
                preStat.setString(15, user.getResetPasswordToken());
                preStat.setString(16, user.getUserPreferences());
                preStat.setInt(17, user.getUserID());

                preStat.executeUpdate();
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_OK);
                msg.setDescription(msg.getDescription().replace("%ITEM%", OBJECT_NAME).replace("%OPERATION%", "UPDATE"));
            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
                msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
                msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            LOG.error("Unable to execute query : " + exception.toString());
            msg = new MessageEvent(MessageEventEnum.DATA_OPERATION_ERROR_UNEXPECTED);
            msg.setDescription(msg.getDescription().replace("%DESCRIPTION%", exception.toString()));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                LOG.warn("Unable to close connection : " + exception.toString());
            }
        }
        return new Answer(msg);
    }

    private User loadFromResultSet(ResultSet rs) throws SQLException {
        int userID = ParameterParserUtil.parseIntegerParam(rs.getString("usr.userid"), 0);
        String login = ParameterParserUtil.parseStringParam(rs.getString("usr.login"), "");
        String password = ParameterParserUtil.parseStringParam(rs.getString("usr.password"), "");
        String resetPasswordToken = ParameterParserUtil.parseStringParam(rs.getString("usr.resetPasswordToken"), "");
        String request = ParameterParserUtil.parseStringParam(rs.getString("usr.request"), "");
        String name = ParameterParserUtil.parseStringParam(rs.getString("usr.name"), "");
        String team = ParameterParserUtil.parseStringParam(rs.getString("usr.team"), "");
        String language = ParameterParserUtil.parseStringParam(rs.getString("usr.language"), "");
        String reportingFavorite = ParameterParserUtil.parseStringParam(rs.getString("usr.reportingFavorite"), "");
        String robotHost = ParameterParserUtil.parseStringParam(rs.getString("usr.robotHost"), "");
        String defaultSystem = ParameterParserUtil.parseStringParam(rs.getString("usr.defaultSystem"), "");
        String email = ParameterParserUtil.parseStringParam(rs.getString("usr.email"), "");
        String robotPort = ParameterParserUtil.parseStringParam(rs.getString("usr.robotPort"), "");
        String robotPlatform = ParameterParserUtil.parseStringParam(rs.getString("usr.robotPlatform"), "");
        String robotBrowser = ParameterParserUtil.parseStringParam(rs.getString("usr.robotBrowser"), "");
        String robotVersion = ParameterParserUtil.parseStringParam(rs.getString("usr.robotVersion"), "");
        String robot = ParameterParserUtil.parseStringParam(rs.getString("usr.robot"), "");
        String userPreferences = ParameterParserUtil.parseStringParam(rs.getString("usr.userPreferences"), "");
        //TODO remove when working in test with mockito and autowired
        factoryUser = new FactoryUser();
        return factoryUser.create(userID, login, password, resetPasswordToken, request, name, team, language, reportingFavorite, robotHost, robotPort, robotPlatform, robotBrowser, robotVersion, robot, defaultSystem, email, userPreferences);
    }

}
