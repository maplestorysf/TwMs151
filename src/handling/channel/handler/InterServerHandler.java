package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.*;
import handling.world.exped.MapleExpedition;
import handling.world.guild.MapleGuild;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import scripting.NPCScriptManager;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.ExpeditionPacket;
import tools.packet.CWvsContext.FamilyPacket;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.LoginPacket;
import tools.packet.MTSCSPacket;

public class InterServerHandler {

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (c.getCloseSession()) {
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        NPCScriptManager.getInstance().start(c, 9000086, 55);

    }

    public static final void Loggedin(final int playerid, final MapleClient c) {
        if (c.getCloseSession()) {
            return;
        }
        boolean allowLogin = false;
        final int state = c.getLoginState();
        final ChannelServer channelServer = c.getChannelServer();
        MapleCharacter player;
        CharacterTransfer transfer = null;

        outterLoop:
        for (World wserv : LoginServer.getWorlds()) {
            for (ChannelServer cserv : wserv.getChannels()) {
                transfer = cserv.getPlayerStorage().getPendingCharacter(playerid);
                if (transfer != null) {
                    break outterLoop;
                }
            }
        }
        if (transfer == null) {
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }

        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }

        if (state == MapleClient.LOGIN_SERVER_TRANSITION) {
            for (String charName : c.loadCharacterNames(c.getWorld())) {
                if (World.isConnected(charName)) {
                    System.err.print(charName + " 已經被伺服器踢出");
                    LoginServer.getInstance().getWorld(c.getWorld()).getChannels().forEach((chan) -> {
                        chan.getPlayerStorage().getAllCharacters().stream().filter((chr) -> (chr.getAccountID() == player.getAccountID())).map((chr) -> {
                            chr.saveToDB(true, false);
                            return chr;
                        }).map((chr) -> {
                            chr.getClient().getSession().close(true);
                            return chr;
                        }).forEachOrdered((chr) -> {
                            chr.getMap().removePlayer(chr);
                        });
                    });
                    c.getSession().write(CWvsContext.serverNotice(1, "您被卡住了。\r\n 請重新登錄."));
                    break;
                }
            }
        }
        if (World.isCSConnected(c.loadCharacterIds(c.getWorld()))) {
            // this won't happen anymore actually, i managed to fix the cash shop glitch when doing multi-worlds.
            c.getSession().write(CWvsContext.serverNotice(1, "哦!\r\n看起來您被卡在購物商城了!\r\n\r\n請回到登入畫面再重新登入一次"));
            MapleCharacter victim = CashShopServer.getPlayerStorage().getCharacterByName(player.getName());
            CashShopServer.getPlayerStorage().deregisterPlayer(victim);
            CashShopServer.getPlayerStorage().deregisterPendingPlayer(victim.getId());
            CashShopServer.getPlayerStorage().getCharacterById(victim.getId()).getClient().getSession().close();
            allowLogin = false;
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (c.getLastLogin() + 3 * 1000 < currentTime.getTime()) {
            c.setReceiving(false);
            c.getSession().close();
            return;
        }
        if (!c.CheckIPAddress()) {
            c.getSession().close(true);
            return;
        }

        if (!allowLogin) {
            c.getSession().close(true);
            return;
        }
        c.setAccID(player.getAccountID());
        c.setPlayer(player);
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);

        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
        player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
        player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
        c.getSession().write(CField.getCharInfo(player));
        player.getMap().addPlayer(player);
        World world = LoginServer.getInstance().getWorld(c.getWorld());
        world.getPlayerStorage().addPlayer(player);
        c.getSession().write(MTSCSPacket.enableCSUse());
        c.getSession().write(CWvsContext.temporaryStats_Reset()); //?

        try {
            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                final MapleParty party = player.getParty();
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

                if (party != null && party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.getSession().write(ExpeditionPacket.expeditionStatus(me, false));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            c.getSession().write(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getWorld(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(GuildPacket.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        packetList.stream().filter((pack) -> (pack != null)).forEachOrdered((pack) -> {
                            c.getSession().write(pack);
                        });
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }

            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.getSession().write(FamilyPacket.getFamilyData());
            c.getSession().write(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        player.getClient().getSession().write(CWvsContext.serverMessage(channelServer.getServerMessage()));
        player.sendMacros();
        player.showNote();
        player.sendImp();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        c.getSession().write(CField.getKeymap(player.getKeyLayout()));
        c.getSession().write(LoginPacket.enableReport());
        player.updatePetAuto();
        player.expirationTask(true, player == null);
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
        player.spawnSavedPets();
        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon).getEffect(1).applyTo(player);
        }
        player.loadQuests(c);
        c.getSession().write(CWvsContext.getFamiliarInfo(player));
        if (World.getShutdown()) {
            player.getClient().getSession().write(CWvsContext.getMidMsg("伺服器即將關閉, 請玩家盡快下線!", true, 1));
        }
        // if (player.getMap().getId() == MapConstants.STARTER_MAP) {
        // World.Broadcast.broadcastMessage(player.getWorld(), CWvsContext.yellowChat("[" + ServerConstants.SERVER_NAME + " ]  " + c.getPlayer().getName() + " 加入了我們的伺服器"));
        //  player.dropMessage(6, "歡迎來到 " + ServerConstants.SERVER_NAME );
        //}
        if (player.haveItem(ServerConstants.Currency, 1000, false, true) && !player.isDonator() && player.getReborns() < 50 && !player.isSuperDonor() && !player.isGM()) {
            player.sendGMMessage(6, "[GM 通知]: " + player.getName() + " 有超過 1000 個 Munny, 而且轉生不到50轉.");
        }
        if (player.haveItem(ServerConstants.Currency, 50000, false, true) && !player.isGM()) {
            player.sendGMMessage(6, "[GM 通知]: " + player.getName() + " 有超過 50,000 Munny. 請檢查他是否正常!");
        }
        MapleCharacter ch = player;
        int lv = ch.getGMLevel();
        int Ch = c.getChannel();
        int PvPch = c.getChannelServer().PvPis();
        int Hellch = c.getChannelServer().HellChis();
        String pname = ch.getName();
        String nickname = (lv == 0 ? "玩家" : "GM");
        String mode = "";
        if (Ch == PvPch) {
            mode = "PVP";
        } else if (Ch == Hellch) {
            mode = "混沌";
        }
        if (Ch == PvPch) {
            String mg = "親愛的玩家：" + pname + " 您好 \r\n本頻道為 " + mode + " 頻道";
            ch.dropMessage(6, mg);
        }

        if (lv == 0) {
            System.out.println(nickname + " [ " + pname + " ] 連接 世界 " + c.getWorld() + " 頻道 " + Ch + " 成功.");
            getAllCharacters().stream().filter((chr) -> (chr.getPLMSG())).forEachOrdered((chr) -> {
                chr.dropMessage(6, nickname + " [ " + pname + " ] 連接 世界 " + c.getWorld() + " 頻道 " + Ch + " 成功.");
            });

            String mg = "在對話框輸入 @help 可以查看當前能使用的命令!";
            ch.dropMessage(-1, mg);
        } else if (lv > 0) {
            ch.dropMessage("輸入 !hide 可以隱身 !unhide 可以解除隱身");
            System.out.println(nickname + " [ " + pname + " ] <權限 " + lv + " > 連接 世界 " + c.getWorld() + " 頻道 " + Ch + " 成功.");
            World.Broadcast.broadcastGMMessage(ch.getWorld(), CWvsContext.serverNotice(6, nickname + " < " + pname + " > <權限 " + lv + " > 連接 頻道  " + Ch));
        }
        if (ch.getOneTimeLog("首登") == 0) {
            ch.setOneTimeLog("首登");
            World.Broadcast.broadcastMessage(ch.getWorld(), CWvsContext.yellowChat("<新玩家> 歡迎" + ch.getName() + " 來到我們的世界~! 大家好好關照" + (ch.getGender() == 0 ? "他" : "她") + "吧:)"));
        }
        //   player.saveToDB(false, false);
        //final List<Pair<Integer, String>> ii = new LinkedList<>();
        //ii.add(new Pair<>(10000, "Pio"));
        //player.getClient().getSession().write(CField.NPCPacket.setNPCScriptable(ii));
    }

    public static void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (c.getCloseSession()) {
            return;
        }
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 50) {
            chr.dropMessage(1, "伺服器目前高乘載中.　請稍後再試.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        slea.readInt();
        if (!World.isChannelAvailable(c.getWorld(), chc)) {
            chr.dropMessage(1, "本頻道已滿，請稍候在試。");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "本頻道已滿，請稍候在試。");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (chr.inJQ()) {
            chr.dropMessage(1, "若要題開跳忍活動請輸入@exit.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (GameConstants.isJail(chr.getMapId())) {
            //      chr.dropMessage(1, "You can't Change Channels in Jail, fgt.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "您已經在 " + chr.getMap().getMapName());
                    c.getSession().write(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "本頻道已滿，請稍候在試。");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
            chr.saveToDB(false, false);
        }
    }

    public static List<MapleCharacter> getAllCharacters() { //取得全世界玩家
        List<MapleCharacter> chrlist = new ArrayList<>();
        LoginServer.getWorlds().forEach((worlds) -> {
            worlds.getChannels().forEach((cs) -> {
                cs.getPlayerStorage().getAllCharacters().forEach((chra) -> {
                    chrlist.add(chra);
                });
            });
        });
        return chrlist;
    }
}
