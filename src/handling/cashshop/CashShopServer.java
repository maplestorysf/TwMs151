package handling.cashshop;

import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import handling.mina.MapleCodecFactory;
import java.net.InetSocketAddress;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class CashShopServer {

    private static String ip;
    private static IoAcceptor acceptor;
    private static PlayerStorage players, playersMTS;
    private static boolean finishedShutdown = false;
    private static int port = ServerConstants.CashShopPort;

    public static void run_startup_configurations() {
        ip = ServerConstants.SERVER_IP + ":" + port;

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));
        players = new PlayerStorage();
        playersMTS = new PlayerStorage();

        try {
            acceptor.setHandler(new MapleServerHandler(-1, -1, true));
            acceptor.bind(new InetSocketAddress(port));
            ((SocketSessionConfig) acceptor.getSessionConfig()).setTcpNoDelay(true); // O.o
        } catch (final Exception e) {
            System.err.println("Binding to port " + port + " failed");
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static PlayerStorage getPlayerStorageMTS() {
        return playersMTS;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("儲存所有在現金商城的客戶端...");
        players.disconnectAll();
        //      playersMTS.disconnectAll();
        //      MTSStorage.getInstance().saveBuyNow(true);
        System.out.println("正在關閉商城伺服器...");
        acceptor.unbind();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
