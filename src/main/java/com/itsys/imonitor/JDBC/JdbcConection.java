package com.itsys.imonitor.JDBC;

import com.itsys.imonitor.flume.entities.Models;
import com.itsys.imonitor.flume.entities.QueryRelationGroup;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcConection {


    private C3P0ConnentionProvider c3P0ConnentionProvider;
    private static final Logger log = Logger.getLogger(JdbcConection.class);

    public JdbcConection() {

        this.c3P0ConnentionProvider = C3P0ConnentionProvider.getInstance();

    }

    public Connection getConnection() throws SQLException {
        return c3P0ConnentionProvider.getConnection();

    }

    public Models findRelation(QueryRelationGroup qrg) throws SQLException {
        PreparedStatement pre = null;
        Statement s = null;
        ResultSet resultSet = null;
        Connection connection = getConnection();
        Models models = new Models();
        String sourceip = qrg.getRemoteIp();
        String targetip = qrg.getLocalIp();
        String pid = qrg.getProcessId();
        String FIND_FIND_RELATION_SQL_STRING = "SELECT CASE WHEN count(*) = 0 THEN ( SELECT id FROM `t_node_relation` WHERE sourceIp = '" + sourceip + "' AND targetIp = '" + targetip + "' AND pid = " + pid + " AND endDate IS NOT NULL ORDER BY endDate DESC LIMIT 1 ) "
                + "ELSE A.id END AS id, CASE WHEN count(*) = 0 THEN ( SELECT sourcePort FROM `t_node_relation` WHERE sourceIp = '" + sourceip + "' AND targetIp = '" + targetip + "' AND pid = " + pid + " AND endDate IS NOT NULL ORDER BY endDate DESC LIMIT 1 ) "
                + "ELSE A.sourcePort END AS PORT FROM ( SELECT * FROM `t_node_relation` WHERE sourceIp = '" + sourceip + "' AND targetIp = '" + targetip + "' AND pid = " + pid + " AND endDate IS NULL ) A";

        //id, parentSShId,parentSShIp,parentSShPort,username
        try {
            s = connection.createStatement();
            resultSet = s.executeQuery(FIND_FIND_RELATION_SQL_STRING);

//            pre.setString(1, qrg.getRemoteIp());
//            pre.setString(2, qrg.getLocalIp());
//            pre.setInt(3, Integer.parseInt(qrg.getProcessId()));
//            pre.setString(4, qrg.getRemoteIp());
//            pre.setString(5, qrg.getLocalIp());
//            pre.setInt(6, Integer.parseInt(qrg.getProcessId()));
//            pre.setString(7, qrg.getRemoteIp());
//            pre.setString(8, qrg.getLocalIp());
//            pre.setInt(9, Integer.parseInt(qrg.getProcessId()));
            if (resultSet.next()) {
                models.setId(resultSet.getInt(1));
                models.setPort(resultSet.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            resultSet.close();
            close(pre);
            cl(connection);
        }
        return models;
    }

    public void close(PreparedStatement pre) {
        try {
            if (pre != null) {
                pre.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cl(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
