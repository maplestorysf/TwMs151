package handling.login.handler;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.World;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;
import tools.packet.LoginPacket.Server;
import tools.packet.PacketHelper;

public class CharLoginHandler {

    public static String fakeBan = "";
    public static Server world = Server.菇菇寶貝;

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 5;
    }

    public static void login(final LittleEndianAccessor slea, final MapleClient c) {
        int loginok = 0;
        String login = slea.readMapleAsciiString();
        String pwd = slea.readMapleAsciiString();
        boolean isBanned = c.hasBannedIP() || c.hasBannedMac();
        if (ServerConstants.getAR()) {
            if (!AutoRegister.getAccountExists(login) && !isBanned) {
                AutoRegister.createAccount(login, pwd, c.getSession().getRemoteAddress().toString());
                if (AutoRegister.success) {
                    c.getSession().write(LoginPacket.getLoginFailed(1));
                    c.getSession().write(CWvsContext.serverNotice(1, "你的帳號已經完成創建!\r\n帳號:"+login.toString() + "\r\n密碼:" + pwd.toString() + "\r\n請好好保存!"));
                    AutoRegister.success = true;
                    return;
                }
            }
        }
        loginok = c.login(login, pwd, isBanned);

        final Calendar tempbannedTill = c.getTempBanCalendar();
        if (loginok == 0 && isBanned) { // just incase? o.o
            loginok = 3;
            MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account ", false);
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                if (loginok == 3) {
                    c.getSession().write(CWvsContext.serverNotice(1, c.showBanReason(login, true)));
                    c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                } else {
                    c.getSession().write(LoginPacket.getLoginFailed(loginok));
                }
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.getSession().write(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
            if (ServerConstants.SavePW) {
                FileoutputUtil.logToFile("logs/紀錄/帳號.txt", "\r\n帳號: " + login + " 密碼: " + pwd);
            }
        }
    }

    public static void ServerListRequest(final MapleClient c) {
        c.getSession().write(LoginPacket.getLoginWelcome());

        LoginServer.getWorlds().forEach((iWorld) -> {
            c.getSession().write(LoginPacket.getServerList(iWorld.getWorldId(), Server.getById(iWorld.getWorldId()).toString(), iWorld.getFlag(), iWorld.getEventMessage(), iWorld.getChannels()));
        });

        c.getSession().write(LoginPacket.getEndOfServerList());
        c.getSession().write(LoginPacket.selectWorld(c.getLastWorld())); // TODO: last world selected

        String eventMessage = LoginServer.getInstance().getWorld(0).getEventMessage();
        eventMessage = eventMessage.replaceAll("#b", "");
        eventMessage = eventMessage.replaceAll("#r", "");
        eventMessage = eventMessage.replaceAll("#d", "");

        c.getSession().write(LoginPacket.sendRecommended(world.getId(), eventMessage));
    }

    public static void ServerStatusRequest(final LittleEndianAccessor slea, final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int worldId = slea.readByte();
        final List<Integer> count = new ArrayList<>();
        PlayerStorage strg = LoginServer.getInstance().getWorld(worldId).getPlayerStorage();
        strg.getAllCharacters().stream().filter((chrs) -> (chrs.getClient().getWorld() == worldId)).forEachOrdered((chrs) -> {
            count.add(chrs.getId());
        });
        final int numPlayer = count.size();
        final int userLimit = LoginServer.getInstance().getWorld(worldId).getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }

    public static void 顯示(String in) {
        if (ServerConstants.Admin_Only) {
            System.out.println(in);
        }
    }

    public static void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getCloseSession()) {
            return;
        }
        顯示("1");
        if (!c.isLoggedIn()) {
            c.getSession().close(true);
            return;
        }
        顯示("2");
        slea.readByte(); //2?
        final int server = slea.readByte();
        world = Server.getById(server);
        c.setLastWorld(server);
        final int channel = slea.readByte() + 1;

        if (!World.isChannelAvailable(server, channel)) {
            c.getSession().write(LoginPacket.getLoginFailed(10));
            return;
        }
        顯示("3");
        //顯示("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        FileoutputUtil.log("logs/紀錄/LogIPs.txt", "\r\nIP 地址 : " + c.getSession().getRemoteAddress().toString().split(":")[0] + " | 帳號 : " + c.getAccountName() + "\r\n");
        顯示("4");
        final List<MapleCharacter> chars = c.loadCharacters(server);
        顯示("4.1");
        if (chars != null && ChannelServer.getInstance(server, channel) != null) {

            c.setWorld(server);
            顯示("5");
            c.setChannel(channel);
            顯示("6");
            c.getSession().write(LoginPacket.getSecondAuthSuccess(c));
            顯示("7");
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword(), chars, 15));
            顯示("8");
        } else {
            顯示("9");
            c.getSession().close(true);
        }
    }

    public static void CheckCharName(final String name, final MapleClient c) {
        c.getSession().write(LoginPacket.charNameResponse(name, !(MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()))));
    }

    public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close(true);
            return;
        }
        final String name = slea.readMapleAsciiString();
        final JobType jobType = JobType.getByType(slea.readInt()); // BIGBANG: 0 = Resistance, 1 = Adventurer, 2 = Cygnus, 3 = Aran, 4 = Evan, 5 = mercedes
        final short db = slea.readShort(); //whether dual blade = 1 or adventurer = 0
        final byte gender = slea.readByte(); //??idk corresponds with the thing in addCharStats
        byte skinColor = slea.readByte(); // 01
        int hairColor = 0;
        int weapon3 = 0;
        final byte unk2 = slea.readByte(); // 08
        boolean jettPhantom = (jobType == LoginInformationProvider.JobType.幻影俠盜);
        final int face = slea.readInt();
        final boolean mercedes = (jobType == JobType.精靈遊俠);
        final boolean demon = (jobType == JobType.惡魔殺手);
        final int hair = slea.readInt();
        if (!jettPhantom && !mercedes && !demon) { //mercedes/demon dont need hair color since its already in the hair
            hairColor = slea.readInt();
            skinColor = (byte) slea.readInt();
        }
        final int demonMark = demon ? slea.readInt() : 0;
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();
        if (jettPhantom) {
            weapon3 = slea.readInt();
        }

        int shield = jobType == LoginInformationProvider.JobType.幻影俠盜 ? 1352100 : mercedes ? 1352000 : demon ? slea.readInt() : 0;
        if (jobType == JobType.惡魔殺手) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, demonMark) || (skinColor != 0 && skinColor != 13)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, shoes)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, weapon) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shield)) {
                return;
            }
        } else if (jobType == JobType.精靈遊俠) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, top) || (skinColor != 0 && skinColor != 12)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, weapon)) {
                return;
            }
        } else if (jobType != JobType.幻影俠盜) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.type, hairColor) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.type, skinColor)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.type, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.type, bottom)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.type, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 7, jobType.type, weapon)) {
                return;
            }
        }
        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setWorld((byte) world.getId());
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);
        newchar.setDemonMarking(demonMark);

        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);

        Item item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        if (bottom > 0) { //resistance have overall
            item = li.getEquipById(bottom);
            item.setPosition((byte) (jettPhantom ? -5 : -6));
            equip.addFromDB(item);
        }

        item = li.getEquipById(shoes);
        item.setPosition((byte) (jettPhantom ? -9 : -7));
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) (jettPhantom ? -7 : -11));
        equip.addFromDB(item);

        if (weapon3 > 0) {
            item = li.getEquipById(weapon3);
            item.setPosition((byte) (-11));
            equip.addFromDB(item);
        }

        if (shield > 0) {
            item = li.getEquipById(shield);
            item.setPosition((byte) -10);
            equip.addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));

        switch (jobType) {
            case 反抗軍:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 冒險家:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 皇家騎士團:
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null);
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case 狂狼勇士:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
            case 龍魔島:
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
                break;
            case 幻影俠盜:
                final Map<Skill, SkillEntry> ss3 = new HashMap<>();
                ss3.put(SkillFactory.getSkill(20031204), new SkillEntry((byte) 1, (byte) 1, -1));
                ss3.put(SkillFactory.getSkill(20031206), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss3, false);
                break;
            case 蒼龍俠客:
                final Map<Skill, SkillEntry> ss4 = new HashMap<>();
                ss4.put(SkillFactory.getSkill(228), new SkillEntry((byte) 1, (byte) 1, -1));
                ss4.put(SkillFactory.getSkill(0001214), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss4, false);
                break;
            case 米哈逸:
                final Map<Skill, SkillEntry> ss5 = new HashMap<>();
                ss5.put(SkillFactory.getSkill(50001214), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss5, false);
                break;
        }

        //    if ((!newchar.hasEquipped(top)) && (!newchar.hasEquipped(weapon))) {
        //        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[AutoBan] Hahaha some new player tried packet editing their equips! Let's laugh at there ban!"));
        //        c.banMacs(); 
        //        c.getSession().close();
        //        return;
        //    }
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, db);
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
        //    c.changeSecondPassword();
        //      c.updateSecondPassword();
    }

    public static final void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if ((!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 910000000 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld()))) {
            c.getPlayer().dropMessage(1, "建立終極冒險家失敗：\r\n1.等級不夠120級\r\n2.不在自由市場\r\n3.職業不是騎士團職業\r\n4.已創建過終極冒險家的角色");
            return;
        }
        if (!c.canMakeCharacter(c.getPlayer().getWorld())) {
            c.getPlayer().dropMessage(1, "建立終極冒險家失敗：\r\n1.您的角色欄位不足");
            return;
        }
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt();
        final int face = slea.readInt();
        final int hair = slea.readInt();

        int weapon = slea.readInt();
        if ((job == 110) || (job == 120)) {
            weapon = 1402092;
        } else if (job == 130) {
            weapon = 1432082;
        } else if ((job == 210) || (job == 220) || (job == 230)) {
            weapon = 1372081;
        } else if (job == 310) {
            weapon = 1452108;
        } else if (job == 320) {
            weapon = 1462095;
        } else if (job == 410) {
            weapon = 1472119;
        } else if (job == 420) {
            weapon = 1332127;
        } else if (job == 510) {
            weapon = 1482081;
        } else if (job == 520) {
            weapon = 1492082;
        } else {
            weapon = 1402092;
        }
        final byte gender = c.getPlayer().getGender();

        //JobType errorCheck = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, errorCheck.type, face)) {
        //    c.getPlayer().dropMessage(1, "An error occurred.");
        //    c.getSession().write(CField.createUltimate(0));
        //    return;
        //}
        JobType jobType = JobType.終極冒險家;

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        //TODO: Make this GMS - Like
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        //TODO: Make this GMS - Like
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        Item item;
        //-1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Topwear 
        //-6 Bottom | -7 Shoes |-8 glove | -9 Cape | -10 Shield | -11 Weapon |-49 medal
        //看 InventoryConstants.java 
        item = li.getEquipById(1003159);//ok
        item.setPosition((byte) -1);
        equip.addFromDB(item);
        item = li.getEquipById(1052304);//ok
        item.setPosition((byte) -5);
        equip.addFromDB(item);
        item = li.getEquipById(1072476);//ok 
        item.setPosition((byte) -7);
        equip.addFromDB(item);
        item = li.getEquipById(1082290);//ok 
        item.setPosition((byte) -8);
        equip.addFromDB(item);
        item = li.getEquipById(weapon);//ok 
        item.setPosition((byte) -11);
        equip.addFromDB(item);

        item = li.getEquipById(1142257);//ok 
        // item.setPosition((byte) -46);
        item.setPosition((byte) -49);
        equip.addFromDB(item);

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(CField.createUltimate(0));
            c.getPlayer().dropMessage(1, "生成終極冒險家角色。\r\n遊戲結束後，在連線的話\r\n可以選擇終極冒險家的角色。\r\n如果刪除生成的終極冒險家，則無法在生成");

            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[系統公告] 恭喜玩家 " + c.getPlayer().getName() + " 創建了終極冒險家。"));
        } else {
            c.getSession().write(CField.createUltimate(3));
        }
    }

    public static final void CreateUltimate_old(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld())) {
            c.getPlayer().dropMessage(1, "You have no character slots.");
            c.getSession().write(CField.createUltimate(1));
            return;
        }
        //System.out.println(slea.toString());
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt(); //job ID

        final int face = slea.readInt();
        final int hair = slea.readInt();

        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();
        JobType jobType = JobType.冒險家;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)) {
        //    c.getPlayer().dropMessage(1, "An error occurred.");
        //    c.getSession().write(CField.createUltimate(0));
        //    return;
        //}

        jobType = JobType.終極冒險家;

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        newchar.getStat().hp += 150; //Beginner 10 levels
        newchar.getStat().mp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                newchar.getStat().hp += 600; //Job Advancement
                newchar.getStat().hp += 2000; //Levelup 40 times
                newchar.getStat().mp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                newchar.getStat().mp += 600;
                newchar.getStat().hp += 500; //Levelup 40 times
                newchar.getStat().mp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 900; //Levelup 40 times
                newchar.getStat().mp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                newchar.getStat().hp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 450; //Levelup 20 times
                newchar.getStat().mp += 300;
                newchar.getStat().hp += 800; //Levelup 20 times
                newchar.getStat().mp += 400;
                break;
            default:
                return;
        }
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(1195 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        c.getPlayer().fakeRelog();
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            c.createdChar(newchar.getId());
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(CField.createUltimate(0));
        } else {
            c.getSession().write(CField.createUltimate(1));
        }
    }

    public static void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        String Secondpw_Client = GameConstants.GMS ? slea.readMapleAsciiString() : null;
        if (Secondpw_Client == null) {
            if (slea.readByte() > 0) { // Specific if user have second password or not
                Secondpw_Client = slea.readMapleAsciiString();
            }
            slea.readMapleAsciiString();
        }

        final int Character_ID = slea.readInt();

        if (!c.login_Auth(Character_ID) || !c.isLoggedIn() || loginFailCount(c)) {
            c.getSession().close(true);
            return; // Attempting to delete other character
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close(true);
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static final void Character_WithoutSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean haspic, final boolean view) {
        if (c.getCloseSession()) {
            return;
        }
        slea.readByte(); // 1?
        slea.readByte(); // 1?
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        final String currentpw = c.getSecondPassword();
        if (!c.isLoggedIn() || loginFailCount(c) || (currentpw != null && (!currentpw.equals("") || haspic)) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null || c.getWorld() != world.getId()) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        String macs = slea.readMapleAsciiString();
        c.updateMacs(macs);
        c.setNowMacs(macs);
        slea.readMapleAsciiString();
        if (slea.available() != 0) {
            final String setpassword = slea.readMapleAsciiString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
                return;
            }
        } else if (GameConstants.GMS && haspic) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());
        World.clearChannelChangeDataByAccountId(c.getAccID());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        String[] socket = LoginServer.getInstance().getIP(c.getWorld(), c.getChannel()).split(":");
        try {
            c.announce(CField.getServerIP(c, InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
        } catch (UnknownHostException | NumberFormatException e) {
        }
    }

    public static final void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
        if (c.getCloseSession()) {
            return;
        }
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null || c.getWorld() != world.getId()) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        if (GameConstants.GMS) {
            String macs = slea.readMapleAsciiString();
            c.updateMacs(macs);
            c.setNowMacs(macs);
        }
        if ((c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16)) {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());
            World.clearChannelChangeDataByAccountId(c.getAccID());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);

            String[] socket = LoginServer.getInstance().getIP(c.getWorld(), c.getChannel()).split(":");
            try {
                c.announce(CField.getServerIP(c, InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } catch (UnknownHostException | NumberFormatException e) {
            }
        } else {
            c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
        }
    }

    public static void PartTimeJob(LittleEndianAccessor slea, MapleClient c) {
        boolean complete = slea.readByte() == 2;
        int charId = slea.readInt();
        int type = slea.readByte();

        Pair info = c.getPartTimeJob(charId);
        if (complete) {
            if ((((Byte) info.getLeft()) <= 0) || (((Long) info.getRight()) <= -2)) {
                System.out.println("7");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 3, 0, 0, false, false));
                return;
            }
            int hoursFromLogin = Math.min((int) ((System.currentTimeMillis() - ((Long) info.getRight())) / 3600000L), 6);
            boolean insert = c.updatePartTimeJob(charId, (byte) (hoursFromLogin > 0 ? -((Byte) info.getLeft()) : 0), hoursFromLogin > 0 ? -hoursFromLogin - 10 : -2);
            if (insert) {
                System.out.println("6");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 0, 0, ((Long) info.getRight()), hoursFromLogin != 0, hoursFromLogin == 6));
            } else {
                System.out.println("5");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 2, 0, 0, false, false));
            }
        } else {
            if ((((Byte) info.getLeft()) > 0) || (((Long) info.getRight()) > 0L) || (!c.canMakePartTimeJob())) {
                System.out.println("1");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 3, 0, 0, false, false));
                return;
            }
            if (((Byte) info.getLeft()) < 0) {
                System.out.println("2");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 1, 0, 0, false, false));
                return;
            }
            long start = System.currentTimeMillis();
            boolean insert = c.updatePartTimeJob(charId, (byte) type, start);
            if (insert) {
                System.out.println("3");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 0, type, start, false, false));
            } else {
                System.out.println("4");
                c.getSession().write(LoginPacket.partTimeJobRequest(charId, 2, 0, 0, false, false));
            }
        }
    }

    public static void ViewChar(LittleEndianAccessor slea, MapleClient c) {
        Map<Byte, ArrayList<MapleCharacter>> worlds = new HashMap<>();
        List<MapleCharacter> chars = c.loadCharacters(world.getId()); //TODO multi world
        c.getSession().write(LoginPacket.showAllCharacter(chars.size()));
        chars.stream().filter((chr) -> (chr != null)).forEachOrdered((chr) -> {
            ArrayList<MapleCharacter> chrr;
            if (!worlds.containsKey(chr.getWorld())) {
                chrr = new ArrayList<>();
                worlds.put(chr.getWorld(), chrr);
            } else {
                chrr = worlds.get(chr.getWorld());
            }
            chrr.add(chr);
        });
        worlds.entrySet().forEach((w) -> {
            c.getSession().write(LoginPacket.showAllCharacterInfo(w.getKey(), w.getValue(), c.getSecondPassword()));
        });
    }

    public static final void updateCCards(LittleEndianAccessor slea, MapleClient c) {
        if ((slea.available() != 24) || (!c.isLoggedIn())) {
            c.getSession().close(true);
            return;
        }
        Map<Integer, Integer> cids = new LinkedHashMap();
        for (int i = 1; i <= 6; i++) {
            int charId = slea.readInt();
            if (((!c.login_Auth(charId)) && (charId != 0)) || (ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null)) {
                c.getSession().close(true);
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static void SetGenderRequest(LittleEndianAccessor slea, MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        byte gender = slea.readByte();
        if (gender > 1 || gender < 0) {
            c.getSession().close();
            return;
        }
        if (c.getAccountName().equals(username) && c.getSecondPassword() == null) {
            c.setGender(gender);
            c.setSecondPassword(password);
            c.update2ndPassword();
            c.updateGender();
            c.sendPacket(LoginPacket.getGenderChanged(c));
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
            FileoutputUtil.logToFile("logs/Data/註冊第二組.txt", "\r\n 時間　[" + FileoutputUtil.NowTime() + "] 帳號：　" + username + " 第二組密碼：" + password + " IP：/" + c.getSessionIPAddress(), false, false);
        } else {
            c.getSession().close();
        }
    }
}
