package tools.wztosql;

import constants.ServerConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.ServerProperties;

public class FixCharSets {

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (ResultSet rs = con.prepareStatement("SELECT CONCAT('ALTER TABLE `', tbl.`TABLE_SCHEMA`, '`.`', tbl.`TABLE_NAME`, '` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;') FROM `information_schema`.`TABLES` tbl WHERE tbl.`TABLE_SCHEMA` = '" + ServerConstants.SQL_DATABASE + "'").executeQuery()) {
                PreparedStatement ps;
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                    try {
                        ps = con.prepareStatement(rs.getString(1));
                        ps.execute();
                        ps.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();

                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
