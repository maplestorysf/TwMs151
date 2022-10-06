package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import console.consoleStart;
import constants.ServerConstants;
import constants.WorldConstants;

import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import server.Timer.BuffTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.PingTimer;
import server.Timer.WorldTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.quest.MapleQuest;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static final consoleStart CCSinstance = new consoleStart();

    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
    public static int itemSize = 0;

    public void run() throws InterruptedException {
        if (System.getProperty("net.sf.odinms.wzpath") == null) {
            System.setProperty("net.sf.odinms.wzpath", "wz");
        }
        ServerConstants.loadSetting();
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        System.out.println("正在開起 " + ServerConstants.SERVER_NAME + "...\n版本:" + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);
        if (!ServerConstants.BlockCS) {
            System.out.println("購物商城 : 開起");
        }
        if (ServerConstants.Autoban) {
            System.out.println("自動封鎖 : 開起");
        }
        if (ServerConstants.SavePW) {
            System.out.println("記錄帳密 : 開起 ");
        }
        if (ServerConstants.PACKET) {
            System.out.println("記錄封包 : 開起");
        }
        // 世界

        WorldConstants.init();
        World.init();
        // 記時器
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        // 伺服器處理器
        MapleServerHandler.initiate();
        // 伺服器列表
        LoginServer.run_startup_configurations();
        CashShopServer.run_startup_configurations();
        World.registerRespawn();
        // 資料處理器
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        MapleItemInformationProvider.getInstance().runItems();
        System.out.println("一共載入了 " + itemSize + " 個物品.");
        LoginServer.setOn();
        // 其他主要的緩存 :)
        SkillFactory.load();
        LoginInformationProvider.getInstance();
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll(); //(this); 
        MapleFamily.loadAll(); //(this); 
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
        RandomRewards.load();
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        //CharacterCardFactory.getInstance().initialize();
        MobSkillFactory.getInstance();
        SpeedRunner.loadSpeedRuns();
        MapleInventoryIdentifier.getInstance();
        CashItemFactory.getInstance().initialize();
        //World.隨機怪物活動();
        World.自由掛網獎勵();
        World.尿尿活動();
        //World.比武大賽();
        World.在線時間獎勵();
        //World.自動解卡帳();
        World.黃色廣播();
        MapleMonsterInformationProvider.getInstance().addExtra();
        //RankingWorker.run();
        System.out.println("[訊息] 伺服器啟動總共花費 " + ((System.currentTimeMillis() - startTime) / 1000) + " 秒.");
    }

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
        try {
            CCSinstance.run();
        } catch (Exception ex) {

        }
    }
}
