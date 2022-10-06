package server;

import java.sql.SQLException;

import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import server.Timer.*;
import tools.packet.CWvsContext;

public class ShutdownServer implements ShutdownServerMBean {

    public static ShutdownServer instance = new ShutdownServer();

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ShutdownServer();
            mBeanServer.registerMBean(instance, new ObjectName("server:type=ShutdownServer"));
        } catch (Exception e) {
            System.out.println("Error registering Shutdown MBean");
            e.printStackTrace();
        }
    }

    public static ShutdownServer getInstance() {
        return instance;
    }

    public int mode = 0;

    public void shutdown() {//can execute twice
        run();
    }

    @Override
    public void run() {
        if (mode == 0) {
            int ret = 0;
            for (World worlds : LoginServer.getWorlds()) {
                for (ChannelServer cs : worlds.getChannels()) {
                    cs.setShutdown();
                    cs.setServerMessage("此世界即將被關閉");
                    ret += cs.closeAllMerchant();
                }
            }
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
            System.out.println("關閉伺服器第 1 階段已經完成. 共儲存了: " + ret + " 個精靈商人");
            mode++;
        } else if (mode == 1) {
            mode++;
            System.out.println("關閉伺服器第 2 階段執行中...");
            try {
                World.Broadcast.broadcastMessage(-1, CWvsContext.serverNotice(0, "此世界即將被關閉.")); // -1 : all world servers
                Integer[] chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
                for (int i : chs) {
                    try {
                        LoginServer.getWorlds().stream().map((w) -> ChannelServer.getInstance(w.getWorldId(), i)).forEachOrdered((cs) -> {
                            synchronized (this) {
                                cs.shutdown();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                LoginServer.shutdown();
                CashShopServer.shutdown();
                DatabaseConnection.closeAll();
            } catch (SQLException e) {
                System.err.println("THROW" + e);
            }
            WorldTimer.getInstance().stop();
            MapTimer.getInstance().stop();
            BuffTimer.getInstance().stop();
            EventTimer.getInstance().stop();
            EtcTimer.getInstance().stop();
            PingTimer.getInstance().stop();
            System.out.println("關閉伺服器第 2 階段已經完成.");
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                //shutdown
            }
            System.exit(0); //not sure if this is really needed for ChannelServer
        }
    }
}
