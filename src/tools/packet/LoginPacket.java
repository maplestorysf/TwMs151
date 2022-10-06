package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import java.util.LinkedList;
import java.util.List;
import server.Randomizer;
import tools.HexTool;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class LoginPacket {

    public enum Server {

        雪吉拉(0),
        菇菇寶貝(1),
        星光精靈(2),
        緞帶肥肥(3),
        藍寶(4),
        綠水靈(5),
        三眼章魚(6),
        木妖(7),
        火獨眼獸(8),
        蝴蝶精(9),
        巴洛古(10),
        海怒斯(11),
        電擊象(12),
        鯨魚號(13),
        皮卡啾(14),
        神獸(15),
        泰勒熊(16);

        final int id;

        private Server(int serverId) {
            id = serverId;
        }

        public int getId() {
            return id;
        }

        public static Server getById(int id) {
            for (Server server : values()) {
                if (server.getId() == id) {
                    return server;
                }
            }
            return null;
        }
    }

    public static byte[] getHello(final short mapleVersion, final byte[] sendIv, final byte[] recvIv) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(14); // length of the packet
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(6); // 7 = MSEA, 8 = GlobalMS, 5 = Test Server

        return mplew.getPacket();
    }

    public static final byte[] getLoginAUTH() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_AUTH.getValue());
        final int rand = Randomizer.nextInt(3);
        mplew.writeMapleAsciiString("MapLogin" + ((rand == 1 || rand == 2) ? "" : "0"));
        mplew.writeInt(GameConstants.getCurrentDate());
        mplew.write(2);

        return mplew.getPacket();
    }

    public static byte[] getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);

        mplew.writeShort(SendPacketOpcode.PING.getValue());

        return mplew.getPacket();
    }

   public static byte[] getAuthSuccessRequest(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.writeShort(client.isGm() ? 1 : 0); 
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeZeroBytes(24);
        return mplew.getPacket();
    }

    public static byte[] getLoginFailed(final int reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        // -1/6/8/9 : Trouble logging in
        // 2/3 : Id deleted or blocked
        // 4: Incorrect password
        // 5: Not a registered ID
        // 7: Logged in    
        // 10: Too many requests
        // 11: 20 years older can use
        // 13: Unable to log on as master at IP
        // 14/15: Redirect to nexon + buttons    
        // 16/21: Verify account
        // 17: Selected the wrong gateway
        // 25: Logging in outside service region
        // 23: License agreement
        // 27: Download full client
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] sendToS() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.SEND_EULA.getValue());
        mplew.write(23);
        //mplew.write(0);
        //mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2); // Account is banned
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static byte[] getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        // 99 : You have been blocked for typing in an invalid password or pincode 5 times.
        // 199 : You have been blocked for typing in an invalid password or pincode 10 times.
        // 299 : You have been blocked for typing in an invalid password or pincode more than 10 times.			
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601.

        return mplew.getPacket();
    }

    public static final byte[] getGenderChanged(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SECOND_PASSWORD_SET.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }

    public static final byte[] getGenderNeeded(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GET_SET_SECOND_PASSWORD.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }

    public static byte[] getSecondAuthSuccess(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_SECOND.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        // mplew.write(client.gmLevel() > 3 ? 1 : 0); // Flash Jump in JQs
        mplew.write(0); // Admin byte - Find, Trade, etc.
        mplew.writeShort(2);
        mplew.write(0); // Admin byte - Commands
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(3); //0 for new accounts
        mplew.write(0); // quiet ban
        mplew.writeLong(0); // quiet ban time
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis())); //really create date
        mplew.writeInt(4); //idk
        mplew.writeLong(Randomizer.nextLong()); //randomizer.nextLong(), remote hack check.
        mplew.write(0); // a boolean, 1/0

        return mplew.getPacket();
    }

    public static byte[] deleteCharResponse(final int cid, final int state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static byte[] secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] enableReport() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.REPORT_STATUS.getValue());
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] enableRecommended() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.writeInt(0); //worldID with most characters
        return mplew.getPacket();
    }

    public static byte[] selectWorld(int world) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.writeInt(world); //worldID with most characters
        return mplew.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
        mplew.write(message != null ? 1 : 0); //amount of messages
        if (message != null) {
            mplew.writeInt(world);
            mplew.writeMapleAsciiString(message);
        }
        return mplew.getPacket();
    }

    public static byte[] getServerList(final int serverId, String serverName, int flag, String eventMessage, List<ChannelServer> channelLoad) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());

        mplew.write(serverId); // 0 = Aquilla, 1 = bootes, 2 = cass, 3 = delphinus
        // final String worldName = LoginServer.getTrueServerName(); //remove the SEA
        mplew.writeMapleAsciiString(serverName);
        mplew.write(flag);
        mplew.writeMapleAsciiString(eventMessage);
        mplew.writeShort(100);
        mplew.writeShort(100);
        mplew.write(0);
        mplew.write(channelLoad.size());

        channelLoad.stream().map((ch) -> {
            mplew.writeMapleAsciiString(serverName + "-" + ch.getChannel());
            return ch;
        }).map((ch) -> {
            mplew.writeInt((ch.getConnectedClients() * 1200) / ServerConstants.CHANNEL_LOAD);
            return ch;
        }).forEachOrdered((ch) -> {
            mplew.write(1);
            mplew.writeShort(ch.getChannel() - 1);
        });

        mplew.writeShort(ServerConstants.getBalloons().size());
        ServerConstants.getBalloons().stream().map((balloon) -> {
            mplew.writeShort(balloon.nX);
            return balloon;
        }).map((balloon) -> {
            mplew.writeShort(balloon.nY);
            return balloon;
        }).forEachOrdered((balloon) -> {
            mplew.writeMapleAsciiString(balloon.sMessage);
        });
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getEndOfServerList() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(0xFF);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendEULA() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_EULA.getValue());
        mplew.write(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] getLoginWelcome() {
        List<Pair<String, Integer>> flags = new LinkedList<>();
        flags.add(new Pair<>("20120808", 0));
        flags.add(new Pair<>("20120815", 0));

        //flags.add(new Pair<>("20120111", 0));
        //flags.add(new Pair<>("returnLegend2", 0));
        return CField.spawnFlags(flags);
    }

    public static byte[] getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static byte[] getChannelSelected() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANNEL_SELECTED.getValue());
        mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    public static final byte[] getCustomEncryption(String link) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeInt(ServerConstants.number1);
        mplew.writeShort(ServerConstants.linkNumber);
        mplew.writeMapleAsciiString(link);
        return mplew.getPacket();
    }

    public static byte[] getCharList(final String secondpw, final List<MapleCharacter> chars, int charslots) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.write(chars.size());
        chars.forEach((chr) -> {
            addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 30, false);
        });
        mplew.write(secondpw != null && secondpw.length() > 0 ? 1 : (secondpw != null && secondpw.length() <= 0 ? 2 : 0)); // second pw request
        mplew.write(0);
        mplew.writeInt(charslots);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);

        return mplew.getPacket();
    }

    public static byte[] charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static void addCharEntry(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, boolean ranking, boolean viewAll) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());
        if (!viewAll) {
            mplew.write(0);
        }
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }

    public static byte[] showAllCharacter(int chars) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(1); //bIsChar
        mplew.writeInt(chars);
        mplew.writeInt(chars + (3 - chars % 3)); //rowsize
        return mplew.getPacket();
    }

    public static byte[] showAllCharacterInfo(int worldid, List<MapleCharacter> chars, String pic) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(chars.isEmpty() ? 5 : 0); //5 = cannot find any
        mplew.write(worldid);
        mplew.write(chars.size());
        chars.forEach((chr) -> {
            addCharEntry(mplew, chr, true, true);
        });
        mplew.write(pic == null ? 0 : (pic.equals("") ? 2 : 1)); //writing 2 here disables PIC		
        return mplew.getPacket();
    }

    public static byte[] enableSpecialCreation(int accid, boolean enable) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPECIAL_CREATION.getValue());
        mplew.writeInt(accid);
        mplew.write(enable ? 0 : 1);
        mplew.write(0); // amount of legends created

        return mplew.getPacket();
    }

    public static byte[] partTimeJobRequest(int cid, int mode, int jobType, long time, boolean finish, boolean bonus) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PART_TIME_JOB.getValue());
        mplew.writeInt(cid);
        mplew.write(mode);

        mplew.write(jobType);
        mplew.writeReversedLong(PacketHelper.getTime(time));
        mplew.writeInt(finish ? 1 : 0);
        mplew.write(bonus ? 1 : 0);

        return mplew.getPacket();
    }

}
