package handling.login;

import constants.ServerConstants;
import constants.WorldConstants;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.mina.MapleCodecFactory;
import handling.world.World;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import tools.Pair;
import tools.packet.LoginPacket.Server;

public class LoginServer {

    public static final int PORT = ServerConstants.LoginPort;//8484

    private static List<World> worlds = new ArrayList<>();
    private static List<Map<Integer, String>> channels = new LinkedList<>();
    private static LoginServer instance = null;

    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<>();
    private static int usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;
    private static HashMap<Integer, Pair<String, String>> loginAuth = new HashMap<>();
    private static HashSet<String> loginIPAuth = new HashSet<>();

    public static LoginServer getInstance() {
        if (instance == null) {
            instance = new LoginServer();
        }
        return instance;
    }

    public static void putLoginAuth(int chrid, String ip, String tempIP) {
        loginAuth.put(chrid, new Pair<>(ip, tempIP));
        loginIPAuth.add(ip);
    }

    public static Pair<String, String> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }

    public static void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public static void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }

    public static void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public void removeChannel(int worldid, final int channel) {
        channels.remove(channel);

        World world = worlds.get(worldid);
        if (world != null) {
            world.removeChannel(channel);
        }
    }

    public static ChannelServer getChannel(int channel) {
        for (World world : LoginServer.getWorlds()) {
            return world.getChannel(channel);
        }
        return null;
    }

    public ChannelServer getChannel(int world, int channel) {
        return worlds.get(world).getChannel(channel);
    }

    public List<ChannelServer> getChannelsFromWorld(int world) {
        return worlds.get(world).getChannels();
    }

    public List<ChannelServer> getAllChannels() {
        List<ChannelServer> channelz = new ArrayList<>();
        worlds.forEach((world) -> {
            world.getChannels().forEach((ch) -> {
                channelz.add(ch);
            });
        });
        return channelz;
    }

    public String getIP(int world, int channel) {
        return channels.get(world).get(channel);
    }

    public static void run_startup_configurations() {
        adminOnly = ServerConstants.Admin_Only;

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));
        acceptor.setHandler(new MapleServerHandler());

        byte[] flagg = new byte[17];
        int[] expp = new int[17];
        int[] mesoo = new int[17];
        int[] dropp = new int[17];
        int userLimit = WorldConstants.UserLimit;
        String[] eventMessagee = new String[17];
        WorldConstants.flag.forEach((flags) -> {
            flagg[flags.left] = flags.right;
        });
        WorldConstants.expRates.forEach((loadexp) -> {
            expp[loadexp.left] = loadexp.right;
        });
        WorldConstants.mesoRates.forEach((loadmeso) -> {
            mesoo[loadmeso.left] = loadmeso.right;
        });
        WorldConstants.dropRates.forEach((loaddrop) -> {
            dropp[loaddrop.left] = loaddrop.right;
        });
        WorldConstants.eventMessages.forEach((eventmsg) -> {
            eventMessagee[eventmsg.left] = eventmsg.right;
        });
        for (int i = 0; i < WorldConstants.Worlds; i++) {
            World world = new World(i, flagg[i], eventMessagee[i], expp[i], mesoo[i], dropp[i]);
            worlds.add(world);
            channels.add(new LinkedHashMap<Integer, String>());
            for (int z = 0; z < WorldConstants.Channels; z++) {
                int channelid = z + 1;
                ChannelServer channel = ChannelServer.newInstance(i, channelid);
                world.addChannel(channel);
                world.setUserLimit(userLimit);
                channel.init(); // initialize
                channels.get(i).put(channelid, channel.getIP());
            }
            String Wn = Server.getById(world.getWorldId()).toString();
            int cs = LoginServer.getInstance().getWorld(world.getWorldId()).getChannels().size();
            int er = LoginServer.getInstance().getWorld(world.getWorldId()).getExpRate();
            int mr = LoginServer.getInstance().getWorld(world.getWorldId()).getMesoRate();
            int dr = LoginServer.getInstance().getWorld(world.getWorldId()).getDropRate();
            System.out.println("世界 : " + Wn + " 頻道數: " + cs + " 經驗: " + er + " 金錢: " + mr + " 掉落: " + dr);

        }
        try {
            acceptor.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            System.err.println("Binding to port " + PORT + " failed" + e);
        }
    }

    public World getWorld(int id) {
        return worlds.get(id);
    }

    public static World getWorldStatic(int id) {
        return worlds.get(id);
    }

    public static List<World> getWorlds() {
        return worlds;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("正在關閉登入伺服器...");
        acceptor.unbind();
        finishedShutdown = true; //nothing. lol
    }

    // TODO: remove most/all of this below
    public static Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static int getUsersOn() {
        return usersOn;
    }

    public static int getNumberOfSessions() {
        return acceptor.getManagedSessions().size();
    }

    public static void setAdminOnly(boolean set) {
        adminOnly = set;
    }

    public static boolean isAdminOnly() {
        return adminOnly;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static void setOn() {
        finishedShutdown = false;
    }
}
