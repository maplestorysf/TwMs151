package constants;

import database.DatabaseProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.ServerProperties;

public class ServerConstants {

    public static boolean MerchantsUseCurrency = false; // 記錄封包 = true 允許其他人通過連接 = false
    public static boolean TESPIA = false; // true =  GMST  ,false = GMS
    public static boolean dropUndroppables = true; // 不能丟的物品是否可丟
    public static boolean moreThanOne = true; // 唯一的物品是否多拿
    public static boolean Admin_Only = false;  // 只有管理員能連接
    public static boolean Use_Localhost = false; // 只有單機能連接
    public static boolean CommandLock = true; // 是否封鎖指令
    public static boolean Drop = false; // 是否開放掉落物品
    public static boolean BlockCS = false;// 是否關閉商城
    public static boolean AutoRegister = true; // 自動註冊
    public static boolean PACKET = false; // 封包記錄
    public static boolean Autoban = true; // 自動封號
    public static boolean SavePW = true; // 記錄帳密
    public static boolean autodc = true; // 自動踢人

    public static boolean LOG_CHALKBOARD = false;
    public static boolean LOG_MERCHANT = true;
    public static boolean LOG_DAMAGE = false;
    public static boolean LOG_CSBUY = false;
    public static boolean LOG_SCROLL = false;
    public static boolean LOG_MEGA = false;
    public static boolean LOG_CHAT = false;
    public static boolean LOG_TRADE = true;
    public static boolean LOG_DC = true;

    public static String SQL_USER = DatabaseProperties.getProperty("user"), // 資料庫設定外連
            SQL_PASSWORD = DatabaseProperties.getProperty("password"), // 同上
            SQL_PORT = DatabaseProperties.getProperty("port"), // 同上
            SQL_IP = DatabaseProperties.getProperty("ip"), // 同上
            SQL_DATABASE = DatabaseProperties.getProperty("database"); // 同上
    public static String SERVER_NAME = "測試117"; //伺服器名稱
    public static String SERVER_IP = "127.0.0.1"; //IP (可改可不改)
    public static String WzRevision = "1.0"; // 客戶端初始版本
    public static String SourceRev = "2.0"; // 端口初始版本
    public static short MAPLE_VERSION = 145; // 版本號
    public static String MAPLE_PATCH = "1"; // 版本號

    public static int CHANNEL_LOAD = 500; // 一個頻道內最大玩家數量
    public static int pvpmaxdamage = 9999; //PVP單下最高傷害
    public static int CashShopPort = 7606; //商城端口
    public static int ChannelPort = 7575; // 頻道端口
    public static int LoginPort = 8484; // 登入端口
    public static int Currency = 4000999;// 貨幣
    public static int HellCh = 3; // 混沌頻道
    public static int pvpch = 5; //PVP頻道

    public static Map<Integer, String> BlackList = new HashMap();

    public static final int number1 = (142449577 + 753356065 + 611816275);
    public static final short number2 = 18773, updateNumber = 18774, linkNumber = 18775, messageNumber = 18776;
    public static final long number3 = 202227478981090217L;

    public static int[] VIP_ROCK_BLOCK = {180000000, 180000001};

    public static final byte[] NEXON_IP = new byte[]{(byte) 8, (byte) 31, (byte) 98, (byte) 53};// NEXONIP 已無用

    //  處理世界訊息
    public static String serverMessage = "歡迎來到 " + SERVER_NAME + " v145  * 輸入 @help 查看我們的指令列表 * 請回報ＢＵＧ在論壇."; //遊戲上方黃色公告
    public static String WELCOME_MESSAGE = "歡迎來到 #r " + SERVER_NAME + " - v145!\r\n#g請回報 #eＢＵＧ#n 到我們的論壇!#k";

    public static String getTip() {// 選擇頻道左方公告
        // 偶爾更新這些 <3
        String[] tips = {
            "#r有最新的裝備可以用!", "#b有獨特的指令可以用!", "#b小遊戲隨便你玩!",
            "#我們支援 #rWindows 8!", ("我們的 #bWZ's 版本 是 #r" + WzRevision),
            ("我們的 #b伺服器版本 是 #r" + SourceRev), "#b哈囉?", "隨時關注 #bFacebook!",
            "上#r百#k種髮型 任您選!!!"
        };
        int tip = (int) Math.floor(Math.random() * tips.length);
        return tips[tip];
    }
    private static final List<Balloon> lBalloon = Arrays.asList(
            new Balloon("歡迎來到" + SERVER_NAME, 236, 122),
            new Balloon("禁止開外掛", 0, 276),
            new Balloon("遊戲愉快", 196, 263));

    public static Map<Integer, String> getBlackList() {
        return BlackList;
    }

    public static void setBlackList(int accid, String name) {
        BlackList.put(accid, name);
    }

    public static boolean getCS() {
        return BlockCS;
    }

    public static void setCS(boolean x) {
        BlockCS = x;
    }

    public static boolean getAR() {
        return AutoRegister;
    }

    public static void setAR(boolean x) {
        AutoRegister = x;
    }

    public static boolean getAB() {
        return Autoban;
    }

    public static void setAB(boolean x) {
        Autoban = x;
    }

    public static boolean getDrop() {
        return Drop;
    }

    public static void setDrop(boolean x) {
        Drop = x;
    }

    public static boolean getPacket() {
        return PACKET;
    }

    public static void setPacket(boolean x) {
        PACKET = x;
    }

    public static boolean getLH() {
        return Use_Localhost;
    }

    public static void setLH(boolean x) {
        Use_Localhost = x;
    }

    public static boolean allowUndroppablesDrop() {
        return dropUndroppables;
    }

    public static boolean allowMoreThanOne() {
        return moreThanOne;
    }

    public static boolean getCommandLock() {
        return CommandLock;
    }

    public static void setCommandLock(boolean x) {
        CommandLock = x;
    }

    public static boolean getAutodc() {
        return autodc;
    }

    public static void setAutodc(boolean x) {
        autodc = x;
    }

    public static boolean getAutoban() {
        return Autoban;
    }

    public static void setAutoban(boolean x) {
        Autoban = x;
    }

    public static List<Balloon> getBalloons() {
        return lBalloon;
    }

    public static enum PlayerGMRank {

        普通玩家(0),
        新實習生(1),
        老實習生(2),
        巡邏者(3),
        領導者(4),
        超級管理員(5),
        神(100);
        private final char commandPrefix;
        private final int level;

        PlayerGMRank(int level) {
            this.commandPrefix = level > 0 ? '!' : '@';
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum VIPRank {

        普通VIP(1),
        進階VIP(2),
        高級VIP(3),
        尊貴VIP(4),
        至尊VIP(5);
        private final char commandPrefix;
        private final int level;

        VIPRank(int level) {
            this.commandPrefix = '#';
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static void loadSetting() {
        SERVER_NAME = ServerProperties.getProperty("server.serverName", SERVER_NAME);
        SERVER_IP = ServerProperties.getProperty("server.ip", SERVER_IP);
        serverMessage = ServerProperties.getProperty("server.serverMessage", serverMessage);
        WELCOME_MESSAGE = ServerProperties.getProperty("server.Wbmsg", WELCOME_MESSAGE);
        LoginPort = ServerProperties.getProperty("server.LoginPort", LoginPort);
        ChannelPort = ServerProperties.getProperty("server.ChannelPort", ChannelPort);
        CashShopPort = ServerProperties.getProperty("server.CashShopPort", CashShopPort);
        HellCh = ServerProperties.getProperty("server.HellCh", HellCh);
        pvpch = ServerProperties.getProperty("server.pvpch", pvpch);
        CHANNEL_LOAD = ServerProperties.getProperty("server.CHANNEL_LOAD", CHANNEL_LOAD);
        Currency = ServerProperties.getProperty("server.Currency", Currency);
        pvpmaxdamage = ServerProperties.getProperty("server.pvpmaxdamage", pvpmaxdamage);
        MerchantsUseCurrency = ServerProperties.getProperty("server.MerchantsUseCurrency", MerchantsUseCurrency);
        TESPIA = ServerProperties.getProperty("server.TESPIA", TESPIA);
        dropUndroppables = ServerProperties.getProperty("server.dropUndroppables", dropUndroppables);
        moreThanOne = ServerProperties.getProperty("server.moreThanOne", moreThanOne);
        Admin_Only = ServerProperties.getProperty("server.AdminOnly", Admin_Only);
        Use_Localhost = ServerProperties.getProperty("server.Use_Localhost", Use_Localhost);
        CommandLock = ServerProperties.getProperty("server.CommandLock", CommandLock);
        Drop = ServerProperties.getProperty("server.Drop", Drop);
        BlockCS = ServerProperties.getProperty("server.BlockCS", BlockCS);
        AutoRegister = ServerProperties.getProperty("server.AutoRegister", AutoRegister);
        PACKET = ServerProperties.getProperty("server.PACKET", PACKET);
        Autoban = ServerProperties.getProperty("server.Autoban", Autoban);
        SavePW = ServerProperties.getProperty("server.SavePW", SavePW);
        autodc = ServerProperties.getProperty("server.AutoDC", autodc);
        LOG_CHALKBOARD = ServerProperties.getProperty("server.LOG_CHALKBOARD", LOG_CHALKBOARD);
        LOG_MERCHANT = ServerProperties.getProperty("server.LOG_MERCHANT", LOG_MERCHANT);
        LOG_DAMAGE = ServerProperties.getProperty("server.LOG_DAMAGE", LOG_DAMAGE);
        LOG_CSBUY = ServerProperties.getProperty("server.LOG_CSBUY", LOG_CSBUY);
        LOG_SCROLL = ServerProperties.getProperty("server.LOG_SCROLL", LOG_SCROLL);
        LOG_MEGA = ServerProperties.getProperty("server.LOG_MEGA", LOG_MEGA);
        LOG_CHAT = ServerProperties.getProperty("server.LOG_CHAT", LOG_CHAT);
        LOG_TRADE = ServerProperties.getProperty("server.LOG_TRADE", LOG_TRADE);
        LOG_DC = ServerProperties.getProperty("server.LOG_DC", LOG_DC);
    }

    static {
        loadSetting();
    }
}
