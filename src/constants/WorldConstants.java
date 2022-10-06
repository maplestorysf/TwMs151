/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import java.util.LinkedList;
import java.util.List;
import server.ServerProperties;
import tools.Pair;
import tools.packet.LoginPacket.Server;

/**
 *
 * @author Eric
 *
 * Global World Properties.
 */
public class WorldConstants {

    // 通用設定 : 處理 世界,頻道 的數量 伺服器總人數, 各世界帳號的角色數量 , 和 Event 腳本
    public static final int defaultserver = Server.雪吉拉.getId(); //預設世界（供NPC腳本使用﹚
    public static int Worlds = 1; // 最大 : 23 (跳過24 ~32,然後由33~39繼續,實際最大值:40)
    public static int Channels = 5; //各世界頻道總數(最大值為20)
    public static int UserLimit = 1500; //伺服器總人數限制(1M最大可負載約30人,依實際情形增減)
    public static int maxCharacters = 15; //各世界帳號的人物角色數目限制

    public static String GLOBAL_EVENT_MSGS = "上方公告";
    public static int GLOBAL_EXP_RATE = 5; //經驗,預設:10
    public static int GLOBAL_MESO_RATE = 3; //金錢,預設:5
    public static int GLOBAL_DROP_RATE = 2; //調寶,預設:2
    public static int GLOBAL_CASH_RATE = 1; //掉點,預設:1
    public static int GLOBAL_TRAIT_RATE = 3; //性向,預設:3

    public static boolean GLOBAL_CH_AMOUNTS = true;
    public static boolean GLOBAL_EVENT_MSG = true;
    public static boolean GLOBAL_RATES = true; // When true, all worlds use the above rates

    // Scripts TODO: Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,ZakumPQ,ProtectTylus,GuildQuest,Ravana_EASY,Ravna_MED,Ravana_HARD (untested or not working)
    public static String Events = "" // event scripts, programmed per world but i'll keep them the same
            + "PinkZakum,"
            + "elevator,AriantPQ1,Aswan,autoSave,MonsterPark,Trains,Boats,Flight,PVP,Visitor,cpq2,cpq,Rex,AirPlane,CygnusBattle,ScarTarBattle,VonLeonBattle,Ghost,"
            + "Prison,HillaBattle,AswanOffSeason,ArkariumBattle,OrbisPQ,HenesysPQ,Juliet,Dragonica,Pirate,BossQuestEASY,BossQuestMED,BossQuestHARD,BossQuestHELL,Ellin,"
            + "HorntailBattle,LudiPQ,KerningPQ,ZakumBattle,MV,MVBattle,DollHouse,Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,PinkBeanBattle,ZakumPQ,ProtectTylus,ChaosHorntail,"
            + "ChaosZakum,Ravana_EASY,Ravana_HARD,Ravana_MED,GuildQuest";

    public static List<Pair<Integer, Byte>> flag = new LinkedList<>();

    public enum Flags {
        None((byte) 0), Event((byte) 1), New((byte) 2), Hot((byte) 3);
        final byte id;

        private Flags(byte flagId) {
            id = flagId;
        }

        public byte getId() {
            return id;
        }
    }

    public static List<Pair<Integer, Integer>> chAmounts = new LinkedList<>();
    public static List<Pair<Integer, Integer>> expRates = new LinkedList<>();
    public static List<Pair<Integer, Integer>> mesoRates = new LinkedList<>();
    public static List<Pair<Integer, Integer>> dropRates = new LinkedList<>();
    public static List<Pair<Integer, String>> eventMessages = new LinkedList<>();

    public static void init() {

        // 倍率
        GLOBAL_EXP_RATE = ServerProperties.getProperty("server.AllexpRate", GLOBAL_EXP_RATE);
        GLOBAL_MESO_RATE = ServerProperties.getProperty("server.AllmesoRate", GLOBAL_MESO_RATE);
        GLOBAL_DROP_RATE = ServerProperties.getProperty("server.AlldropRate", GLOBAL_DROP_RATE);
        GLOBAL_CASH_RATE = ServerProperties.getProperty("server.AllcashRate", GLOBAL_CASH_RATE);
        GLOBAL_TRAIT_RATE = ServerProperties.getProperty("server.AlltraitRate", GLOBAL_TRAIT_RATE);

        // 世界頻道設定
        Worlds = ServerProperties.getProperty("server.worlds", Worlds);
        Channels = ServerProperties.getProperty("server.Channels", Channels);

        //遊戲設定
        UserLimit = ServerProperties.getProperty("server.UserLimit", UserLimit);
        maxCharacters = ServerProperties.getProperty("server.maxChair", maxCharacters);

        // 世界通用設定
        GLOBAL_RATES = ServerProperties.getProperty("server.AllRates", GLOBAL_RATES);
        GLOBAL_EVENT_MSG = ServerProperties.getProperty("server.AllEventMsg", GLOBAL_EVENT_MSG);
        GLOBAL_CH_AMOUNTS = ServerProperties.getProperty("server.AllChAmount", GLOBAL_CH_AMOUNTS);

        int default_ = Channels;
        String arg = "server.Channels";
        // Channel
        chAmounts.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_CH_AMOUNTS ? Channels : Channels))); // Default World
        chAmounts.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "2", default_))));
        chAmounts.add(new Pair<>(Server.星光精靈.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "3", default_))));
        chAmounts.add(new Pair<>(Server.緞帶肥肥.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "4", default_))));
        chAmounts.add(new Pair<>(Server.藍寶.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "5", default_))));
        chAmounts.add(new Pair<>(Server.綠水靈.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "6", default_))));
        chAmounts.add(new Pair<>(Server.三眼章魚.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "7", default_))));
        chAmounts.add(new Pair<>(Server.木妖.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "8", default_))));
        chAmounts.add(new Pair<>(Server.火獨眼獸.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "9", default_))));
        chAmounts.add(new Pair<>(Server.蝴蝶精.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "10", default_))));
        chAmounts.add(new Pair<>(Server.巴洛古.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "11", default_))));
        chAmounts.add(new Pair<>(Server.海怒斯.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "12", default_))));
        chAmounts.add(new Pair<>(Server.電擊象.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "13", default_))));
        chAmounts.add(new Pair<>(Server.鯨魚號.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "14", default_))));
        chAmounts.add(new Pair<>(Server.皮卡啾.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "15", default_))));
        chAmounts.add(new Pair<>(Server.神獸.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "16", default_))));
        chAmounts.add(new Pair<>(Server.泰勒熊.getId(), (GLOBAL_CH_AMOUNTS ? Channels : ServerProperties.getProperty(arg + "17", default_))));

        // Flags
        flag.add(new Pair<>(Server.雪吉拉.getId(), Flags.Hot.getId())); // Default World
        flag.add(new Pair<>(Server.菇菇寶貝.getId(), Flags.Event.getId()));
        flag.add(new Pair<>(Server.星光精靈.getId(), Flags.Event.getId()));
        flag.add(new Pair<>(Server.緞帶肥肥.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.藍寶.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.綠水靈.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.三眼章魚.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.木妖.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.火獨眼獸.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.蝴蝶精.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.巴洛古.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.海怒斯.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.電擊象.getId(), Flags.None.getId()));
        flag.add(new Pair<>(Server.鯨魚號.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.皮卡啾.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.神獸.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.泰勒熊.getId(), Flags.New.getId()));

        default_ = GLOBAL_EXP_RATE;
        arg = "server.expRate";
        expRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg, default_))));
        expRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "2", default_))));
        expRates.add(new Pair<>(Server.星光精靈.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "3", default_))));
        expRates.add(new Pair<>(Server.緞帶肥肥.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "4", default_))));
        expRates.add(new Pair<>(Server.藍寶.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "5", default_))));
        expRates.add(new Pair<>(Server.綠水靈.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "6", default_))));
        expRates.add(new Pair<>(Server.三眼章魚.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "7", default_))));
        expRates.add(new Pair<>(Server.木妖.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "8", default_))));
        expRates.add(new Pair<>(Server.火獨眼獸.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "9", default_))));
        expRates.add(new Pair<>(Server.蝴蝶精.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "10", default_))));
        expRates.add(new Pair<>(Server.巴洛古.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "11", default_))));
        expRates.add(new Pair<>(Server.海怒斯.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "12", default_))));
        expRates.add(new Pair<>(Server.電擊象.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "13", default_))));
        expRates.add(new Pair<>(Server.鯨魚號.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "14", default_))));
        expRates.add(new Pair<>(Server.皮卡啾.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "15", default_))));
        expRates.add(new Pair<>(Server.神獸.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "16", default_))));
        expRates.add(new Pair<>(Server.泰勒熊.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : ServerProperties.getProperty(arg + "17", default_))));

        default_ = GLOBAL_MESO_RATE;
        arg = "server.mesoRate";
        mesoRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg, default_))));
        mesoRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "2", default_))));
        mesoRates.add(new Pair<>(Server.星光精靈.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "3", default_))));
        mesoRates.add(new Pair<>(Server.緞帶肥肥.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "4", default_))));
        mesoRates.add(new Pair<>(Server.藍寶.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "5", default_))));
        mesoRates.add(new Pair<>(Server.綠水靈.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "6", default_))));
        mesoRates.add(new Pair<>(Server.三眼章魚.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "7", default_))));
        mesoRates.add(new Pair<>(Server.木妖.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "8", default_))));
        mesoRates.add(new Pair<>(Server.火獨眼獸.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "9", default_))));
        mesoRates.add(new Pair<>(Server.蝴蝶精.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "10", default_))));
        mesoRates.add(new Pair<>(Server.巴洛古.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "11", default_))));
        mesoRates.add(new Pair<>(Server.海怒斯.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "12", default_))));
        mesoRates.add(new Pair<>(Server.電擊象.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "13", default_))));
        mesoRates.add(new Pair<>(Server.鯨魚號.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "14", default_))));
        mesoRates.add(new Pair<>(Server.皮卡啾.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "15", default_))));
        mesoRates.add(new Pair<>(Server.神獸.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "16", default_))));
        mesoRates.add(new Pair<>(Server.泰勒熊.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : ServerProperties.getProperty(arg + "17", default_))));

        default_ = GLOBAL_DROP_RATE;
        arg = "server.dropRate";
        // Drop rates
        dropRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg, default_))));
        dropRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "2", default_))));
        dropRates.add(new Pair<>(Server.星光精靈.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "3", default_))));
        dropRates.add(new Pair<>(Server.緞帶肥肥.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "4", default_))));
        dropRates.add(new Pair<>(Server.藍寶.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "5", default_))));
        dropRates.add(new Pair<>(Server.綠水靈.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "6", default_))));
        dropRates.add(new Pair<>(Server.三眼章魚.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "7", default_))));
        dropRates.add(new Pair<>(Server.木妖.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "8", default_))));
        dropRates.add(new Pair<>(Server.火獨眼獸.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "9", default_))));
        dropRates.add(new Pair<>(Server.蝴蝶精.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "10", default_))));
        dropRates.add(new Pair<>(Server.巴洛古.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "11", default_))));
        dropRates.add(new Pair<>(Server.海怒斯.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "12", default_))));
        dropRates.add(new Pair<>(Server.電擊象.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "13", default_))));
        dropRates.add(new Pair<>(Server.鯨魚號.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "14", default_))));
        dropRates.add(new Pair<>(Server.皮卡啾.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "15", default_))));
        dropRates.add(new Pair<>(Server.神獸.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "16", default_))));
        dropRates.add(new Pair<>(Server.泰勒熊.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : ServerProperties.getProperty(arg + "17", default_))));

        String defaults = ServerConstants.serverMessage;
        arg = "server.serverMessage";
        // Event messages
        eventMessages.add(new Pair<>(Server.雪吉拉.getId(), ("歡迎來到 #b" + ServerConstants.SERVER_NAME + "!#k\r\n" + ServerConstants.getTip())));
        eventMessages.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "2", defaults))));
        eventMessages.add(new Pair<>(Server.星光精靈.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "3", defaults)));
        eventMessages.add(new Pair<>(Server.緞帶肥肥.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "4", defaults)));
        eventMessages.add(new Pair<>(Server.藍寶.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "5", defaults)));
        eventMessages.add(new Pair<>(Server.綠水靈.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "6", defaults)));
        eventMessages.add(new Pair<>(Server.三眼章魚.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "7", defaults)));
        eventMessages.add(new Pair<>(Server.木妖.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "8", defaults)));
        eventMessages.add(new Pair<>(Server.火獨眼獸.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "9", defaults)));
        eventMessages.add(new Pair<>(Server.蝴蝶精.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "10", defaults)));
        eventMessages.add(new Pair<>(Server.巴洛古.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "11", defaults)));
        eventMessages.add(new Pair<>(Server.海怒斯.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "12", defaults)));
        eventMessages.add(new Pair<>(Server.電擊象.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "13", defaults)));
        eventMessages.add(new Pair<>(Server.鯨魚號.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "14", defaults)));
        eventMessages.add(new Pair<>(Server.皮卡啾.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "15", defaults)));
        eventMessages.add(new Pair<>(Server.神獸.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "16", defaults)));
        eventMessages.add(new Pair<>(Server.泰勒熊.getId(), GLOBAL_EVENT_MSG ? GLOBAL_EVENT_MSGS : ServerProperties.getProperty(arg + "17", defaults)));
    }
    /*
    public static void init() {
        UserLimit = ServerProperties.getProperty("server.UserLimit", UserLimit);
        Channels = ServerProperties.getProperty("server.Channels", Channels);
        Worlds = ServerProperties.getProperty("server.worlds", Worlds);
        maxCharacters = ServerProperties.getProperty("server.maxChair", maxCharacters);
        GLOBAL_RATES = ServerProperties.getProperty("server.AllRates", GLOBAL_RATES);
        GLOBAL_EXP_RATE = ServerProperties.getProperty("server.AllexpRate", GLOBAL_EXP_RATE);
        GLOBAL_MESO_RATE = ServerProperties.getProperty("server.AllmesoRate", GLOBAL_MESO_RATE);
        GLOBAL_DROP_RATE = ServerProperties.getProperty("server.AlldropRate", GLOBAL_DROP_RATE);
        GLOBAL_CASH_RATE = ServerProperties.getProperty("server.AllcashRate", GLOBAL_CASH_RATE);
        GLOBAL_TRAIT_RATE = ServerProperties.getProperty("server.AlltraitRate", GLOBAL_TRAIT_RATE);

        // 世界1的倍率 
        wexp1 = ServerProperties.getProperty("server.wexp1", wexp1);
        wmeso1 = ServerProperties.getProperty("server.wmeso1", wmeso1);
        wdrop1 = ServerProperties.getProperty("server.wdrop1", wdrop1);
        wflag1 = ServerProperties.getProperty("server.wflag1", wflag1);
        // 世界2的倍率 
        wexp2 = ServerProperties.getProperty("server.wexp2", wexp2);
        wmeso2 = ServerProperties.getProperty("server.wmeso2", wmeso2);
        wdrop2 = ServerProperties.getProperty("server.wdrop2", wdrop2);
        wflag2 = ServerProperties.getProperty("server.wflag2", wflag2);

        // Flags
        flag.add(new Pair<>(Server.雪吉拉.getId(), wflag1)); // Default World
        flag.add(new Pair<>(Server.菇菇寶貝.getId(), wflag2));
        flag.add(new Pair<>(Server.Broa.getId(), Flags.Event.getId()));
        flag.add(new Pair<>(Server.Windia.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Khaini.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Bellocan.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Mardia.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Kradia.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Yellonde.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Demethos.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Galicia.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.El_Nido.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Zenith.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Arcania.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Chaos.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Nova.getId(), Flags.New.getId()));
        flag.add(new Pair<>(Server.Renegades.getId(), Flags.New.getId()));

        // Exp rates
        expRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : wexp1)));
        expRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : wexp2)));
        expRates.add(new Pair<>(Server.Broa.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Windia.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Khaini.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Bellocan.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Mardia.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Kradia.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Yellonde.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Demethos.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Galicia.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.El_Nido.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Zenith.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Arcania.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Chaos.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Nova.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));
        expRates.add(new Pair<>(Server.Renegades.getId(), (GLOBAL_RATES ? GLOBAL_EXP_RATE : 5)));

        // Meso rates
        mesoRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : wmeso1)));
        mesoRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : wmeso2)));
        mesoRates.add(new Pair<>(Server.Broa.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Windia.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Khaini.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Bellocan.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Mardia.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Kradia.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Yellonde.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Demethos.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Galicia.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.El_Nido.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Zenith.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Arcania.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Chaos.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Nova.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));
        mesoRates.add(new Pair<>(Server.Renegades.getId(), (GLOBAL_RATES ? GLOBAL_MESO_RATE : 3)));

        // Drop rates
        dropRates.add(new Pair<>(Server.雪吉拉.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : wdrop1)));
        dropRates.add(new Pair<>(Server.菇菇寶貝.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : wdrop2)));
        dropRates.add(new Pair<>(Server.Broa.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Windia.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Khaini.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Bellocan.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Mardia.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Kradia.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Yellonde.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Demethos.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Galicia.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.El_Nido.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Zenith.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Arcania.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Chaos.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Nova.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));
        dropRates.add(new Pair<>(Server.Renegades.getId(), (GLOBAL_RATES ? GLOBAL_DROP_RATE : 2)));

        // Event messages
        eventMessages.add(new Pair<>(Server.雪吉拉.getId(), ("歡迎來到 #b" + ServerConstants.SERVER_NAME + "!#k\r\n" + ServerConstants.getTip())));
        eventMessages.add(new Pair<>(Server.菇菇寶貝.getId(), "菇菇寶貝!"));
        eventMessages.add(new Pair<>(Server.Broa.getId(), "Broa!"));
        eventMessages.add(new Pair<>(Server.Windia.getId(), "Windia!"));
        eventMessages.add(new Pair<>(Server.Khaini.getId(), "Khaini!"));
        eventMessages.add(new Pair<>(Server.Bellocan.getId(), "Bellocan!"));
        eventMessages.add(new Pair<>(Server.Mardia.getId(), "Mardia!"));
        eventMessages.add(new Pair<>(Server.Kradia.getId(), "Kradia!"));
        eventMessages.add(new Pair<>(Server.Yellonde.getId(), "Yellonde!"));
        eventMessages.add(new Pair<>(Server.Demethos.getId(), "Demethos!"));
        eventMessages.add(new Pair<>(Server.Galicia.getId(), "Galicia!"));
        eventMessages.add(new Pair<>(Server.El_Nido.getId(), "El Nido!"));
        eventMessages.add(new Pair<>(Server.Zenith.getId(), "Zenith!"));
        eventMessages.add(new Pair<>(Server.Arcania.getId(), "Arcania!"));
        eventMessages.add(new Pair<>(Server.Chaos.getId(), "Chaos!"));
        eventMessages.add(new Pair<>(Server.Nova.getId(), "Nova!"));
        eventMessages.add(new Pair<>(Server.Renegades.getId(), "Renegades!"));
    }*/
}
