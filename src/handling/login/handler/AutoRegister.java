package handling.login.handler;

import client.LoginCrypto;
import constants.ServerConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import tools.FileoutputUtil;

public class AutoRegister {

    private static final int ACCOUNTS_PER_IP = 2; //change the value to the amount of accounts you want allowed for each ip
    public static boolean success = false; // DONT CHANGE

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return accountExists;
    }

    public static void createAccount(String login, String pwd, String eip) {
        String sockAddr = eip;
        Connection con;

        //connect to database or halt
        try {
            con = DatabaseConnection.getConnection();
        } catch (Exception ex) {
            System.out.println(ex);
            return;
        }

        try {
            ResultSet rs;
            try (PreparedStatement ipc = con.prepareStatement("SELECT SessionIP FROM accounts WHERE SessionIP = ?")) {
                ipc.setString(1, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                rs = ipc.executeQuery();
                if (rs.first() == false || rs.last() == true) {
                    try {
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH) + 1; // begin 0   
                        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP) VALUES (?, ?, ?, ?, ?, ?)")) {
                            ps.setString(1, login);
                            ps.setString(2, LoginCrypto.hexSha1(pwd));
                            ps.setString(3, "no@email.provided");
                            ps.setString(4, year + "-" + month + "-" + dayOfMonth);//Created day
                            ps.setString(5, "00-00-00-00-00-00");
                            ///  ps.setInt(6, 123456);
                            ps.setString(6, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                            ps.executeUpdate();
                        }
                        if (ServerConstants.SavePW) {
                            FileoutputUtil.logToFile("logs/紀錄/註冊.txt", "\r\n　帳號:" + login + " 密碼: " + pwd);//輸出
                        }
                        success = true;
                    } catch (SQLException ex) {
                        System.out.println(ex);
                        return;
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
}
