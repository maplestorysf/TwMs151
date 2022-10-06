package server.maps;

import client.*;
import client.MapleCharacter.DojoMode;
import constants.GameConstants;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.Timer.EventTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.maps.MapleNodes.DirectionInfo;
import server.quest.MapleQuest;
import server.quest.MapleQuest.MedalQuest;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;

public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects_BK = {
        "I have been waiting for you! If you have an ounce of courage in you, you'll be walking in that door right now!",
        "How brave of you to take on Mu Lung Training Tower!",
        "I will make sure you will regret taking on Mu Lung Training Tower!",
        "I do like your intestinal fortitude! But don't confuse your courage with recklessness!",
        "If you want to step on the path to failure, by all means to do so!"};
    private static final String[] mulungEffects = {
        "膽子真夠大的！別把魯莽和勇敢混為一談！",
        "想挑戰武陵道場…還真有勇氣！",
        "我等你！還有勇氣的話，歡迎再來挑戰！",
        "挑戰武陵道場的傢伙，我一定會讓他(她)後悔！！",
        "想被稱呼為失敗者嗎？歡迎來挑戰！"
    };

    private static enum onFirstUserEnter {

        dojang_Eff,
        dojang_Msg,
        PinkBeen_before,
        onRewordMap,
        mpark_mobRegen,
        StageMsg_together,
        StageMsg_crack,
        StageMsg_davy,
        boss_Ani,
        hauntedMaskFirstIn,
        HWguest1stIn,
        StageMsg_goddess,
        party6weatherMsg,
        Saint_eventMob,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        boss_Ravana_mirror,
        killing_BonusSetting,
        enter_secretGarden,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        boss_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        summon_pepeking,
        Xerxes_summon,
        VanLeon_Before,
        cygnus_Summon,
        storymap_scenario,
        shammos_FStart,
        kenta_mapEnter,
        iceman_FEnter,
        iceman_Boss,
        prisonBreak_mapEnter,
        Visitor_Cube_poison,
        Visitor_Cube_Hunting_Enter_First,
        VisitorCubePhase00_Start,
        spaceGaGa_start,
        spaceGaGa_sMap,
        MD_eventMob,
        visitorCube_addmobEnter,
        Visitor_Cube_PickAnswer_Enter_First_1,
        visitorCube_medicroom_Enter,
        visitorCube_iceyunna_Enter,
        Visitor_Cube_AreaCheck_Enter_First,
        visitorCube_boomboom_Enter,
        visitorCube_boomboom2_Enter,
        CubeBossbang_Enter,
        pyramidWeather,
        MalayBoss_Int,
        mPark_summonBoss,
        NULL;

        private static onFirstUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum onUserEnter {

        babyPigMap,
        crash_Dragon,
        evanleaveD,
        ht_reward_enter,
        orbisPQ_1stIn,
        EnterWaterField,
        hauntedMaskCheck,
        getDragonEgg,
        ludi_time_path,
        patrty6_1stIn,
        meetWithDragon,
        space_first,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        q31165e,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        crossHunter_q1608,
        q1601_summon,
        nMC_out0,
        pyramidEnter,
        TD_MC_Openning,
        check_q20748,
        TD_MC_gasi,
        TD_MC_title,
        cygnusJobTutorial,
        cygnusTest,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        dollCave02,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        findvioleta,
        pepeking_effect,
        TD_MC_keycheck,
        TD_MC_gasi2,
        in_secretroom,
        sealGarden,
        TD_NC_title,
        TD_neo_BossEnter,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        Resi_tutor10,
        Resi_tutor20,
        Resi_tutor30,
        Resi_tutor40,
        Resi_tutor50,
        Resi_tutor60,
        Resi_tutor70,
        Resi_tutor80,
        Resi_tutor50_1,
        summonSchiller,
        q31102e,
        q31103s,
        jail,
        VanLeon_ExpeditionEnter,
        cygnus_ExpeditionEnter,
        knights_Summon,
        TCMobrevive,
        mPark_stageEff,
        moonrabbit_takeawayitem,
        StageMsg_crack,
        shammos_Start,
        iceman_Enter,
        prisonBreak_1stageEnter,
        VisitorleaveDirectionMode,
        visitorPT_Enter,
        VisitorCubePhase00_Enter,
        visitor_ReviveMap,
        cannon_tuto_01,
        cannon_tuto_direction,
        cannon_tuto_direction1,
        cannon_tuto_direction2,
        userInBattleSquare,
        merTutorDrecotion00,
        merTutorDrecotion10,
        merTutorDrecotion20,
        merStandAlone,
        merOutStandAlone,
        merTutorSleep00,
        merTutorSleep01,
        merTutorSleep02,
        EntereurelTW,
        ds_tuto_ill0,
        ds_tuto_0_0,
        ds_tuto_1_0,
        ds_tuto_3_0,
        ds_tuto_3_1,
        ds_tuto_4_0,
        enter_training,
        achieve_davy,
        ds_tuto_5_0,
        ds_tuto_2_prep,
        ds_tuto_1_before,
        ds_tuto_2_before,
        ds_tuto_home_before,
        ds_tuto_ani,
        enter_edelstein,
        NLC_renew_1,
        henesys_first,
        q3143_clear,
        enter_underbase,
        magicLibrary,
        enter_park100,
        d_test01,
        visitCity,
        TD_LC_title,
        boss_summon,
        standbyAswan,
        aswan_stageEff,
        PTtutor000,
        PTtutor100,
        PTtutor200,
        PTtutor300,
        PTtutor301,
        PTtutor400,
        PTtutor500,
        tutorialSkip,
        PTjob1,
        ExitCheck,
        NULL;

        private static onUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum directionInfo {

        merTutorDrecotion01,
        merTutorDrecotion02,
        merTutorDrecotion03,
        merTutorDrecotion04,
        merTutorDrecotion05,
        merTutorDrecotion12,
        merTutorDrecotion21,
        ds_tuto_0_1,
        ds_tuto_0_2,
        ds_tuto_0_3,
        NULL;

        private static directionInfo fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    public static void startScript_FirstUser(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }//o_O
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(-1, "[系統提示]您已經建立與地圖腳本:" + onUserEnter.fromString(scriptName) + "的連接。");
        } else if (scriptName == null && c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(-1, "[系統提示]本地圖的地圖腳本參數異常，請前去檢查。");
        }

        switch (onFirstUserEnter.fromString(scriptName)) {
            case dojang_Eff: {
                int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));
                if ((c.getPlayer().getMapId() >= 925020100 && c.getPlayer().getMapId() <= 925020109)) {
                    if (c.getPlayer().getDojoMode() == DojoMode.RANKED) {
                        c.getPlayer().getMap().startMapEffect("Don't forget that you have a 10-minute time limit! Defeat the monster quickly, and head to the next floor!", 5120024);
                    } else {
                        c.getPlayer().getMap().startMapEffect("Don't forget that you have to clear it within the time limit! Take down the monster and head to the next floor!", 5120024);
                    }
                }
                c.getPlayer().dojoStartTime = System.currentTimeMillis();
                sendDojoClock(c, c.getPlayer().getDojoMode() != DojoMode.RANKED ? (getTiming(stage) * 60) : 600); // how to reload the clock back to current time? always resets..
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(c);
                break;
            }
            //5120019 = orbis(start_itemTake - onUser)
            case moonrabbit_mapEnter: {
                c.getPlayer().getMap().startMapEffect("收集迎月花的種子並保護月妙!", 5120016);
                break;
            }
            case StageMsg_goddess: {
                switch (c.getPlayer().getMapId()) {
                    case 920010000:
                        c.getPlayer().getMap().startMapEffect("請收集 20 個雲朵的碎片 !", 5120019);
                        break;
                    case 920010100:
                        c.getPlayer().getMap().startMapEffect("找出分散在各地的女神像碎片復原女神像 !", 5120019);
                        break;
                    case 920010200:
                        c.getPlayer().getMap().startMapEffect("請擊退怪物收集 30 個小碎片 !", 5120019);
                        break;
                    case 920010300:
                        c.getPlayer().getMap().startMapEffect("打倒 <倉庫> 的紅獨角獅五隻 !", 5120019);
                        break;
                    case 920010400:
                        c.getPlayer().getMap().startMapEffect("找出正確的女神之 LP 讓留聲機啟動 !", 5120019);
                        break;
                    case 920010500:
                        c.getPlayer().getMap().startMapEffect("找出正確的組合 !", 5120019);
                        break;
                    case 920010600:
                        c.getPlayer().getMap().startMapEffect("打倒所有怪物並復原女神像 !", 5120019);
                        break;
                    case 920010700:
                        c.getPlayer().getMap().startMapEffect("請找出正確的踏板上去最上層操作手把 !", 5120019);
                        break;
                    case 920010800:
                        c.getPlayer().getMap().startMapEffect("把食人花的種子種到花盆，找出黑食人花盆 !", 5120019);
                        break;
                }
                break;
            }
            case StageMsg_crack: {
                switch (c.getPlayer().getMapId()) {
                    case 922010100:
                        c.getPlayer().getMap().startMapEffect("擊退所有異次元的發條鼠 !", 5120018);
                        break;
                    case 922010200:
                        c.getPlayer().getMap().startSimpleMapEffect("收集所有通行證 !", 5120018);
                        break;
                    case 922010300:
                        c.getPlayer().getMap().startMapEffect("打倒所有怪物 !", 5120018);
                        break;
                    case 922010400:
                        c.getPlayer().getMap().startMapEffect("找出躲在暗黑空間中的怪物並打倒吧 !", 5120018);
                        break;
                    case 922010500:
                        c.getPlayer().getMap().startMapEffect("收集每個迷宮室的通行證 !", 5120018);
                        break;
                    case 922010600:
                        c.getPlayer().getMap().startMapEffect("解出被隱藏箱子的暗號，上去頂峰吧 !", 5120018);
                        break;
                    case 922010700:
                        c.getPlayer().getMap().startMapEffect("擊退這地方的所有泥人領導者吧 !", 5120018);
                        break;
                    case 922010800:
                        c.getPlayer().getMap().startSimpleMapEffect("聽取問題上去答案對的箱子上方 !", 5120018);
                        break;
                    case 922010900:
                        c.getPlayer().getMap().startMapEffect("打倒巨型戰鬥機 !", 5120018);
                        break;

                }
                break;
            }
            case StageMsg_together: {
                switch (c.getPlayer().getMapId()) {
                    case 103000800:
                        c.getPlayer().getMap().startMapEffect("聽取問題，蒐集相對應的通行證數量 !", 5120017);
                        break;
                    case 103000801:
                        c.getPlayer().getMap().startMapEffect("找出可開啟前往下一階段之門的線", 5120017);
                        break;
                    case 103000802:
                        c.getPlayer().getMap().startMapEffect("找出可開啟前往下一階段之門的踏板", 5120017);
                        break;
                    case 103000803:
                        c.getPlayer().getMap().startMapEffect("找出可開啟前往下一階段之門的木桶", 5120017);
                        break;
                    case 103000804:
                        c.getPlayer().getMap().startMapEffect("打倒超級綠水靈 !", 5120017);
                        break;
                }
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926100000:
                        c.getPlayer().getMap().startMapEffect("探索研究室找出秘密門 !", 5120021);
                        break;
                    case 926100001:
                        c.getPlayer().getMap().startMapEffect("請擊退所有怪物 !", 5120021);
                        break;
                    case 926100100:
                        c.getPlayer().getMap().startMapEffect("請利用打倒怪物後所得到的液體，來裝滿有裂縫的燒杯 !", 5120021);
                        break;
                    case 926100200:
                        c.getPlayer().getMap().startMapEffect("向怪物們拿門禁卡後，進去研究室找出實驗資料 !", 5120021);
                        break;
                    case 926100203:
                        c.getPlayer().getMap().startMapEffect("請打倒所有怪物 !", 5120021);
                        break;
                    case 926100300:
                        c.getPlayer().getMap().startMapEffect("請通過 4 個保安通道", 5120021);
                        break;
                    case 926100401:
                        c.getPlayer().getMap().startMapEffect("請打倒法郎肯洛伊德，保護羅密歐 !", 5120021);
                        break;
                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926110000:
                        c.getPlayer().getMap().startMapEffect("探索研究室找出秘密門!", 5120022);
                        break;
                    case 926110001: // gms v151
                        c.getPlayer().getMap().startMapEffect("請擊退所有怪物!", 5120022);
                        break;
                    case 926110100:
                        c.getPlayer().getMap().startMapEffect("請利用打倒怪物後所得到的液體，來裝滿有裂縫的燒杯!", 5120022);
                        break;
                    case 926110200: // gms v151
                        c.getPlayer().getMap().startMapEffect("向怪物們拿門禁卡後，進去研究室找出實驗資料 !", 5120022);
                        break;
                    case 926110201: // gms v151
                    case 926110202: // these should be the same
                        c.getPlayer().getMap().startSimpleMapEffect("Find the experimental data in the laboratory and bring it to Juliet.", 5120022);
                        break;
                    case 926110203: // gms v151
                        c.getPlayer().getMap().startMapEffect("請打倒所有怪物!", 5120022);
                        break;
                    case 926110300:
                        c.getPlayer().getMap().startMapEffect("請通過 4 個保安通道!", 5120022);
                        break;
                    case 926110301:
                    case 926110302:
                    case 926110303:
                    case 926110304: // gms v151
                        c.getPlayer().getMap().startMapEffect("Please find the platform with the correct answer and go up to the top of the passageway!", 5120022);
                        break;
                    case 926110401:
                        c.getPlayer().getMap().startMapEffect("請打倒法郎肯洛伊德，保護茱麗葉 !", 5120022);
                        break;
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930000000:
                        c.getPlayer().getMap().startMapEffect("通過中央傳送點進入，現在我要對你施加變身魔法了 !", 5120023);
                        break;
                    case 930000100:
                        c.getPlayer().getMap().startMapEffect("遠古木妖害森林汙染了，請打倒所有遠古木妖 !", 5120023);
                        break;
                    case 930000200:
                        c.getPlayer().getMap().startMapEffect("把毒放進水槽換成稀釋的毒，再拿稀釋的毒消滅荊棘草 !", 5120023);
                        break;
                    case 930000300:
                        c.getPlayer().getMap().startMapEffect("大家都去哪兒了 ? 請通過傳送點到我這裡來 !", 5120023);
                        break;
                    case 930000400:
                        c.getPlayer().getMap().startMapEffect("使用淨化之珠，捕捉怪物然後收集怪物之珠 20 個交給隊長 !", 5120023);
                        break;
                    case 930000500:
                        c.getPlayer().getMap().startMapEffect("打開怪人書桌前的箱子取得紫色魔力石 !", 5120023);
                        break;
                    case 930000600:
                        c.getPlayer().getMap().startMapEffect("將紫色魔力石放在怪人的祭壇上 !", 5120023);
                        break;
                }
                break;
            }
            case prisonBreak_mapEnter: {
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925100000:
                        c.getPlayer().getMap().startMapEffect("請在限定時間內打倒所有怪物，並搭乘海盜船 !", 5120020);
                        break;
                    case 925100100:
                        c.getPlayer().getMap().startMapEffect("請在限定時間內，蒐集各階段海賊的象徵各 20 個 !", 5120020);
                        break;
                    case 925100200:
                        c.getPlayer().getMap().startMapEffect("請在限定時間內打倒所有怪物並往前進 !", 5120020);
                        break;
                    case 925100300:
                        c.getPlayer().getMap().startMapEffect("請在限定時間內打倒所有怪物並往前進 !", 5120020);
                        break;
                    case 925100400:
                        c.getPlayer().getMap().startMapEffect("請在限定時間內從怪物身上取得鑰匙，並關閉所有的門 !", 5120020);
                        break;
                    case 925100500:
                        c.getPlayer().getMap().startMapEffect("請打倒金勾海賊團的船長－金勾海賊王 !", 5120020);
                        break;
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9300107 : 9300119; //lord pirate
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1:
                            mobId = Randomizer.nextBoolean() ? 9300119 : 9300105; //angry
                            break;
                        case 2:
                            mobId = Randomizer.nextBoolean() ? 9300106 : 9300105; //enraged
                            break;
                    }
                    final MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                }
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26)); //rough estimate
                break;
            }
            case boss_Ravana_mirror:
            case boss_Ravana: { //event handles this so nothing for now until i find out something to do with it
                c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(5, "六手邪神出現了"));
                break;
            }
            case killing_BonusSetting: { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                //TODO also find positions to spawn these at
                c.getPlayer().getMap().resetFully();
                c.getSession().write(CField.showEffect("killing/bonus/bonus"));
                c.getSession().write(CField.showEffect("killing/bonus/stage"));
                Point pos1, pos2, pos3;
                int spawnPer;
                int mobId;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else if (c.getPlayer().getMapId() >= 926010070 && c.getPlayer().getMapId() <= 926010089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }

            case mPark_summonBoss: {
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getEventInstance().getProperty("boss") != null && c.getPlayer().getEventInstance().getProperty("boss").equals("0")) {
                    for (int i = 9800119; i < 9800125; i++) {
                        final MapleMonster boss = MapleLifeFactory.getMonster(i);
                        c.getPlayer().getEventInstance().registerMonster(boss);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(boss, new Point(c.getPlayer().getMap().getPortal(2).getPosition()));
                    }
                }
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() >= 921120100 && c.getPlayer().getMapId() <= 921120300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(c.getPlayer().getMapId() == 921120300 ? 9300282 : 9300275);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP"))); // TODO: don't let this surpass the maxHP of 9300275.img.xml
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.getSession().write(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));
                }
                break;
            }
            //5120038 =  dr bing. 5120039 = visitor lady. 5120041 = unknown dr bing.
            case iceman_FEnter: {
                if (c.getPlayer().getMapId() >= 932000100 && c.getPlayer().getMapId() < 932000300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9300438);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.getSession().write(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));
                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 1:
                        c.getPlayer().getMap().startMapEffect("Break the boxes and eliminate the monsters!", 5120033);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("Eliminate the Officer!", 5120033);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("Find the way to the other side!", 5120033);
                        break;
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().startMapEffect("打倒鬼盜船船長 !", 5120033);
                break;
            }
            case boss_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400802), c.getPlayer().getPosition());
                break;
            }
            case summon_pepeking: {
                c.getPlayer().getMap().resetFully();
                final int rand = Randomizer.nextInt(10);
                int mob_ToSpawn;
                if (rand >= 4) { //60%
                    mob_ToSpawn = 3300007;
                } else if (rand >= 1) {
                    mob_ToSpawn = 3300006;
                } else {
                    mob_ToSpawn = 3300005;
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob_ToSpawn), c.getPlayer().getPosition());
                break;
            }
            case Xerxes_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(6160003), c.getPlayer().getPosition());
                break;
            }
            case shammos_FStart:
                c.getPlayer().getMap().startMapEffect("打倒所有怪物 !", 5120035);
                break;
            case kenta_mapEnter:
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 1:
                        c.getPlayer().getMap().startMapEffect("聽得到我的聲音嗎 ? 請消滅所有粗暴的怪物們 !", 5120052);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("空氣已經快不夠了，消滅怪物之後，請幫我收集氣泡 20 個來 !", 5120052);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("突然間怪物開始攻擊，請保護我 3 分鐘 !", 5120052);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("怎麼會有這麼大的魚 ! 那個就是海怒斯 ? 而且還有兩隻 ! 他們在攻擊我們了 ! 請幫我消滅他們 !", 5120052);
                        break;
                }
                break;
            case cygnus_Summon: {
                c.getPlayer().getMap().startMapEffect("已經很久沒有看到來這裡的人了，但是也沒有看過安然無事出去的人。", 5120043);
                break;
            }
            case iceman_Boss: {
                c.getPlayer().getMap().startMapEffect("你將會滅亡 ...", 5120050);
                break;
            }
            case Visitor_Cube_poison: {
                c.getPlayer().getMap().startMapEffect("打倒所有怪物 !", 5120039);
                break;
            }
            case Visitor_Cube_Hunting_Enter_First: {
                c.getPlayer().getMap().startMapEffect("打倒所有外星訪問者 !", 5120039);
                break;
            }
            case visitorCube_boomboom_Enter: {
                c.getPlayer().getMap().startMapEffect("敵人非常強大，請小心 !", 5120039);
                break;
            }
            case visitorCube_boomboom2_Enter: {
                c.getPlayer().getMap().startMapEffect("外星訪客非常強大，請小心 !", 5120039);
                break;
            }
            case VisitorCubePhase00_Start: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the flying monsters!", 5120039);
                break;
            }
            case visitorCube_addmobEnter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the monsters by moving around the map!", 5120039);
                break;
            }
            case Visitor_Cube_PickAnswer_Enter_First_1: {
                c.getPlayer().getMap().startMapEffect("One of the aliens must have a clue to the way out.", 5120039);
                break;
            }
            case visitorCube_medicroom_Enter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Unjust Visitors!", 5120039);
                break;
            }
            case visitorCube_iceyunna_Enter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Speedy Visitors!", 5120039);
                break;
            }
            case Visitor_Cube_AreaCheck_Enter_First: {
                c.getPlayer().getMap().startMapEffect("The switch at the top of the room requires a heavy weight.", 5120039);
                break;
            }
            case CubeBossbang_Enter: {
                c.getPlayer().getMap().startMapEffect("This is it! Give it your best shot!", 5120039);
                break;
            }
            case MalayBoss_Int:
            case storymap_scenario:
            case VanLeon_Before:
            case enter_secretGarden:
            case Saint_eventMob:
            case dojang_Msg:
            case spaceGaGa_start:
            case spaceGaGa_sMap:
            case MD_eventMob:
            case balog_summon:
            case boss_Ani:
            case hauntedMaskFirstIn:
            case HWguest1stIn:
            case pyramidWeather:
            case mpark_mobRegen:
            case easy_balog_summon: { //we dont want to reset
                break;
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: { //not needed
                c.getPlayer().getMap().resetFully();
                break;
            }
            default: {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(-1, "onUserFirstEnter : 找不到腳本　" + scriptName + " 在地圖 " + c.getPlayer().getMapId());
                }
                FileoutputUtil.logToFile("logs/腳本異常/onUserFirstEnter.txt", "腳本名稱 : " + scriptName + " 地圖代碼: " + c.getPlayer().getMapId());
                System.out.println("找不到腳本　腳本名稱 : " + scriptName + ", 種類 : onUserFirstEnter - 地圖代碼 " + c.getPlayer().getMapId());

                //System.out.println("Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId());
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    public static void startScript_User(final MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        } //o_O
        String data = "";
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(-1, "[系統提示]您已經建立與地圖腳本:" + onUserEnter.fromString(scriptName) + "的連接。");
        } else if (scriptName == null && c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(-1, "[系統提示]本地圖的地圖腳本參數異常，請前去檢查。");
        }
        switch (onUserEnter.fromString(scriptName)) {
            case tutorialSkip: {
                c.getSession().write(CField.UIPacket.getDirectionStatus(false));
                c.getSession().write(CField.UIPacket.IntroEnableUI(0));
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.removeClickedNPC();
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case ExitCheck: {
                break;
            }
            case PTtutor000: {
                try {
                    c.getSession().write(UIPacket.playMovie("phantom_memory.avi", true));
                    c.getSession().write(CField.MapEff("phantom/mapname1"));
                    c.getSession().write(CField.UIPacket.IntroEnableUI(1));
                    c.getSession().write(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 0, 0, -110, 1));
                    Thread.sleep(1300);

                } catch (InterruptedException e) {
                }
                c.getSession().write(CField.UIPacket.getDirectionStatus(false));
                c.getSession().write(CWvsContext.enableActions());
                NPCScriptManager.getInstance().start(c, 1402100, "PTtutor000_0");

                break;
            }
            case PTtutor100: {//915000100
                break;
            }
            case PTtutor200: {//915000200
                break;
            }
            case PTtutor300: {//915000300
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                }
                c.getPlayer().getMap().resetFully();
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031211), 1, 0);// 鬼鬼祟祟的移動
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031212), 1, 0);// 擾亂
                c.getSession().write(CField.showEffect("phantom/mapname2"));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300498), new Point(-2050, -1249));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300498), new Point(-2430, -210));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300498), new Point(-2070, -772));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300507), new Point(-2420, -1054));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300507), new Point(-2070, -491));
                NPCScriptManager.getInstance().start(c, 1402001, "PTtutor300_UI");
                //spawn guards packet
                break;
            }
            case PTtutor301: {
                //respawn guards
                break;
            }
            case PTtutor400: {

                break;
            }
            case PTtutor500: {
                c.getSession().write(CField.UIPacket.IntroEnableUI(1));
                c.getSession().write(CField.MapEff("phantom/mapname3"));
                NPCScriptManager.getInstance().start(c, 1402100);
                break;
            }

            case PTjob1: {
                if (c.getPlayer().getLevel() < 10) {
                    while (c.getPlayer().getLevel() < (short) 10) {
                        c.getPlayer().levelUp();
                    }
                    c.getPlayer().changeJob((short) 2400);
                    c.getPlayer().setExp(0);
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031203), 1, 0);// 水晶花園傳送
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031205), 1, 0);// 幻影斗蓬
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031207), 1, 0);// 技能竊取
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031208), 1, 0);// 技能管理
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031209), 1, 0);// 卡牌審判
                    c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20031210), 1, 0);// 審判
                }
                break;
            }
            case cygnusTest: {
                showIntro(c, "Effect/Direction.img/cygnus/Scene" + (c.getPlayer().getMapId() == 913040006 ? 9 : (c.getPlayer().getMapId() - 913040000)));
                break;
            }
            case cygnusJobTutorial: {
                showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913040100));
                break;
            }
            case shammos_Enter: { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == (GameConstants.GMS ? 921120300 : 921120500)) {
                    NPCScriptManager.getInstance().dispose(c); //only boss map.
                    c.removeClickedNPC();
                }
                break;
            }
            case iceman_Enter: { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 932000300) {
                    NPCScriptManager.getInstance().dispose(c); //only boss map.
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2159020);
                }
                break;
            }
            case start_itemTake: { //nothing to go on inside the map
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2013001);
                }
                break;
            }
            case PRaid_W_Enter: {
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_expPenalty", "0"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Point", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Bonus", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Total", "-1"));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_Team", ""));
                c.getSession().write(CWvsContext.sendPyramidEnergy("PRaid_IsRevive", "0"));
                c.getPlayer().writePoint("PRaid_Point", "-1");
                c.getPlayer().writeStatus("Red_Stage", "1");
                c.getPlayer().writeStatus("Blue_Stage", "1");
                c.getPlayer().writeStatus("redTeamDamage", "0");
                c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case jail: {
                // if (!c.getPlayer().isIntern()) {
                c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME)).setCustomData(String.valueOf(System.currentTimeMillis()));
                final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST));
                if (stat.getCustomData() != null) {
                    final int seconds = Integer.parseInt(stat.getCustomData());
                    if (seconds > 0) {
                        c.getPlayer().startMapTimeLimitTask(seconds, c.getChannelServer().getMapFactory().getMap(100000000));
                    }
                }
                //    }
                break;
            }
            case TD_neo_BossEnter:
            case findvioleta: {
                c.getPlayer().getMap().resetFully();
                break;
            }
            case StageMsg_crack:
                if (c.getPlayer().getMapId() == 922010400) { //2nd stage
                    MapleMapFactory mf = c.getChannelServer().getMapFactory();
                    int q = 0;
                    for (int i = 0; i < 5; i++) {
                        q += mf.getMap(922010401 + i).getAllMonstersThreadsafe().size();
                    }
                    if (q > 0) {
                        c.getPlayer().dropMessage(-1, "A total of " + q + "Dark Eyes and Shadow Eyes remain. Find and defeat them all!");
                    } else {
                        c.getPlayer().getMap().startSimpleMapEffect("You've defeated all the Dark Eyes and Shadow Eyes. Talk to the Lime Balloon to proceed to the next stage!", 5120018);
                        c.getPlayer().getMap().broadcastMessage(CField.environmentChange("gate", 2));
                    }
                } else if (c.getPlayer().getMapId() >= 922010401 && c.getPlayer().getMapId() <= 922010405) {
                    if (c.getPlayer().getMap().getAllMonstersThreadsafe().size() > 0) {
                        c.getPlayer().dropMessage(-1, "There are still some monsters remaining in this map.");
                    } else {
                        c.getPlayer().dropMessage(-1, "There are no monsters remaining in this map.");
                    }
                }
                break;
            case q31102e:
                if (c.getPlayer().getQuestStatus(31102) == 1) {
                    MapleQuest.getInstance(31102).forceComplete(c.getPlayer(), 2140000);
                }
                break;
            case q31103s:
                if (c.getPlayer().getQuestStatus(31103) == 0) {
                    MapleQuest.getInstance(31103).forceComplete(c.getPlayer(), 2142003);
                }
                break;
            case Resi_tutor20:
                c.getSession().write(CField.MapEff("resistance/tutorialGuide"));
                break;
            case Resi_tutor30:
                c.getSession().write(EffectPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk"));
                break;
            case Resi_tutor40:
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159012);
                break;
            case Resi_tutor50:
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(CWvsContext.enableActions());
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159006);
                break;
            case Resi_tutor70:
                showIntro(c, "Effect/Direction4.img/Resistance/TalkJ");
                break;
            case prisonBreak_1stageEnter:
            case shammos_Start:
            case moonrabbit_takeawayitem:
            case TCMobrevive:
            case cygnus_ExpeditionEnter:
            case knights_Summon:
            case VanLeon_ExpeditionEnter:
            case Resi_tutor10:
            case Resi_tutor60:
            case Resi_tutor50_1:
            case space_first:
            case sealGarden:
            case in_secretroom:
            case TD_MC_gasi2:
            case TD_MC_keycheck:
            case pepeking_effect:
            case enter_training:
            case achieve_davy:
            case userInBattleSquare:
            case visitCity:
            case summonSchiller:
            case VisitorleaveDirectionMode:
            case visitorPT_Enter:
            case ludi_time_path:
            case VisitorCubePhase00_Enter:
            case visitor_ReviveMap:
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter: //handled by event
            case check_q20748:
            case patrty6_1stIn:
            case PRaid_FailEnter: //also
            case PRaid_Revive: //likely to subtract points or remove a life, but idc rly
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case q31165e:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest: //forest that disappeared 240030102
            case dollCave00:
            case crossHunter_q1608:
            case q1601_summon:
            case nMC_out0:
            case pyramidEnter:
            case dollCave01:
            case dollCave02:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case ht_reward_enter:
            case orbisPQ_1stIn:
            case hauntedMaskCheck:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            case enter_edelstein:
            case henesys_first:
            case NLC_renew_1:
            case q3143_clear:
            case d_test01:
            case magicLibrary:
            case enter_underbase:
            case TD_LC_title:
            case enter_park100:
            case EnterWaterField:
            case merStandAlone:
            case EntereurelTW:
            case aranTutorAlone:
            case evanAlone: { //no idea
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case merOutStandAlone: {
                if (c.getPlayer().getQuestStatus(24001) == 1) {
                    MapleQuest.getInstance(24001).forceComplete(c.getPlayer(), 0);
                    c.getPlayer().dropMessage(5, "Quest complete.");
                }
                break;
            }
            case Resi_tutor80:
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case dojang_Msg: {
                c.getPlayer().getMap().startSimpleMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), false, -1);
                break;
            }
            case boss_summon: {
                c.getPlayer().getMap().setClock(true);
                c.getPlayer().getMap().broadcastMessage(CField.getClock(600)); //quickly change to 12
                break;
            }
            case goAdventure:
                showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case crash_Dragon:
                showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case getDragonEgg:
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case meetWithDragon:
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case PromiseDragon:
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case evanPromotion:
                switch (c.getPlayer().getMapId()) {
                    case 900090000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900090002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900090004:
                        c.getSession().write(UIPacket.IntroDisableUI(false));
                        c.getSession().write(UIPacket.IntroLock(false));
                        c.getSession().write(CWvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                }
                showIntro(c, data);
                break;
            case mPark_stageEff:
                c.getPlayer().dropMessage(-1, "All monsters must be eliminated before proceeding to the next stage.");
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/stage"));
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100) + 1)));
                        break;
                    case 4:
                        if (c.getPlayer().getMapId() / 1000000 == 952) {
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/final"));
                        } else {
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/stage"));
                            c.getSession().write(CField.MapEff("monsterPark/stageEff/number/5"));
                        }
                        break;
                    case 5:
                        c.getSession().write(CField.MapEff("monsterPark/stageEff/final"));
                        break;
                }

                break;
            case TD_MC_title: {
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(CWvsContext.enableActions());
                c.getSession().write(CField.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case TD_NC_title: {
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 0:
                        c.getSession().write(CField.MapEff("temaD/enter/teraForest"));
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        c.getSession().write(CField.MapEff("temaD/enter/neoCity" + ((c.getPlayer().getMapId() / 100) % 10)));
                        break;
                }
                break;
            }
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104000000) {
                    c.getSession().write(UIPacket.IntroDisableUI(false));
                    c.getSession().write(UIPacket.IntroLock(false));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getSession().write(CField.MapNameDisplay(c.getPlayer().getMapId()));
                }
                MedalQuest m = null;
                for (MedalQuest mq : MedalQuest.values()) {
                    for (int i : mq.maps) {
                        if (c.getPlayer().getMapId() == i) {
                            m = mq;
                            break;
                        }
                    }
                }
                if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != 2) {
                    if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                        MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                    }
                    if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                        MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                    }
                    String quest = c.getPlayer().getInfoQuest(m.questid - 2005);
                    if (quest.length() != m.maps.length + 6) { //enter= is 6
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        quest = sb.toString();
                        c.getPlayer().updateInfoQuest(m.questid - 2005, quest);
                    }
                    final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(m.questid - 1995));
                    if (stat.getCustomData() == null) { //just a check.
                        stat.setCustomData("0");
                    }
                    int number = Integer.parseInt(stat.getCustomData());
                    final StringBuilder sb = new StringBuilder("enter=");
                    boolean changedd = false;
                    for (int i = 0; i < m.maps.length; i++) {
                        boolean changed = false;
                        if (c.getPlayer().getMapId() == m.maps[i]) {
                            if (quest.substring(i + 6, i + 7).equals("0")) {
                                sb.append("1");
                                changed = true;
                                changedd = true;
                            }
                        }
                        if (!changed) {
                            sb.append(quest.substring(i + 6, i + 7));
                        }
                    }
                    if (changedd) {
                        number++;
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                        c.getPlayer().dropMessage(-1, "探險了 " + number + "/" + m.maps.length + " 個地區");
                        c.getPlayer().dropMessage(-1, "正在挑戰稱號- " + String.valueOf(m));
                        c.getSession().write(CWvsContext.showQuestMsg("正在挑戰稱號- " + String.valueOf(m) + "。 " + number + "/" + m.maps.length + " 完成"));
                    }
                }
                break;
            }
            case go10000:
            case go1020000:
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(CWvsContext.enableActions());
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case standbyAswan:
            case go1010400: {
                c.getSession().write(CField.MapNameDisplay(c.getPlayer().getMapId()));
                break;
            }
            case goArcher: {
                showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: {
                showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914090010:
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    case 914090011:
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090012:
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090013:
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    case 914090100:
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090200:
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                }
                showIntro(c, data);
                break;
            }
            case iceCave: {
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20000014), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000015), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000016), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000017), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000018), new SkillEntry((byte) -1, (byte) 0, -1));
                c.getPlayer().changeSkillsLevel(sa);
                c.getSession().write(EffectPacket.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin"));
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case rienArrow: {
                if (c.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                    c.getSession().write(EffectPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                }
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21101) == 2 && c.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                break;
            }
            case check_count: {
                if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) { //ravana Map
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }
            case Massacre_first: { //sends a whole bunch of shit.
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                }
                break;
            }
            case aswan_stageEff: {
                //  c.getSession().write(CWvsContext.getTopMsg("Remove all the monsters in the field need to be able to move to the next stage."));
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 1:
                    case 2:
                    case 3:
                        c.getSession().write(CField.showEffect("aswan/stageEff/stage"));
                        c.getSession().write(CField.showEffect("aswan/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100))));
                        break;
                }
                synchronized (MapScriptMethods.class) {
                    c.getPlayer().getMap().getAllMonster().stream().map((mon) -> (MapleMonster) mon).filter((mob) -> (mob.getEventInstance() == null)).forEachOrdered((mob) -> {
                        c.getPlayer().getEventInstance().registerMonster(mob);
                    });
                }
                break;
            }
            case Massacre_result: { //clear, give exp, etc.
                //if (c.getPlayer().getPyramidSubway() == null) {
                c.getSession().write(CField.showEffect("killing/fail"));
                //} else {
                //	c.getSession().write(CField.showEffect("killing/clear"));
                //}
                //left blank because pyramidsubway handles this.
                break;
            }
            default: {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(-1, "onUserEnter : 找不到腳本　" + scriptName + " 在地圖 " + c.getPlayer().getMapId());
                }
                FileoutputUtil.logToFile("logs/腳本異常/onUserEnter.txt", "腳本名稱 : " + scriptName + "地圖代碼: " + c.getPlayer().getMapId());
                System.out.println("找不到腳本　腳本名稱 : " + scriptName + ", 種類 : onUserEnter -  地圖代碼 " + c.getPlayer().getMapId());
                //  FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled script : " + scriptName + ", type : onUserEnter - MAPID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    private static int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        } else if (ids >= 7 && ids <= 11) {
            return 6;
        } else if (ids >= 13 && ids <= 17) {
            return 7;
        } else if (ids >= 19 && ids <= 23) {
            return 8;
        } else if (ids >= 25 && ids <= 29) {
            return 9;
        } else if (ids >= 31 && ids <= 35) {
            return 10;
        } else if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final MapleClient c, final String data) {
        c.getSession().write(UIPacket.IntroDisableUI(true));
        c.getSession().write(UIPacket.IntroLock(true));
        c.getSession().write(EffectPacket.ShowWZEffect(data));
    }

    private static void sendDojoClock(MapleClient c, int time) {
        c.getSession().write(CField.getClock(time));
    }

    private static void sendDojoStart(MapleClient c, int stage) {
        c.getSession().write(CField.environmentChange("Dojang/start", 4));
        c.getSession().write(CField.environmentChange("dojang/start/stage", 3));
        c.getSession().write(CField.environmentChange("dojang/start/number/" + stage, 3));
        c.getSession().write(CField.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();

        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        } else if (level <= 20) {
            mob = 9300368;
        } else if (level <= 30) {
            mob = 9300369;
        } else if (level <= 40) {
            mob = 9300370;
        } else if (level <= 50) {
            mob = 9300371;
        } else if (level <= 60) {
            mob = 9300372;
        } else if (level <= 70) {
            mob = 9300373;
        } else if (level <= 80) {
            mob = 9300374;
        } else if (level <= 90) {
            mob = 9300375;
        } else if (level <= 100) {
            mob = 9300376;
        } else {
            mob = 9300377;
        }
        MapleMonster theMob = MapleLifeFactory.getMonster(mob);
        OverrideMonsterStats oms = new OverrideMonsterStats();
        oms.setOMp(theMob.getMobMaxMp());
        oms.setOExp(theMob.getMobExp());
        oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * (level / 5.0))); //10k to 4m
        theMob.setOverrideStats(oms);
        map.spawnMonsterOnGroundBelow(theMob, witchTowerPos);
    }

    public static void startDirectionInfo(MapleCharacter chr, boolean start) {
        final MapleClient c = chr.getClient();
        DirectionInfo di = chr.getMap().getDirectionInfo(start ? 0 : chr.getDirection());
        if (di != null && di.eventQ.size() > 0) {
            if (start) {
                c.getSession().write(UIPacket.IntroDisableUI(true));
                c.getSession().write(UIPacket.getDirectionInfo(3, 4));
            } else {
                for (String s : di.eventQ) {
                    switch (directionInfo.fromString(s)) {
                        case merTutorDrecotion01: //direction info: 1 is probably the time
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1));
                            break;
                        case merTutorDrecotion02:
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1));
                            break;
                        case merTutorDrecotion03:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1));
                            break;
                        case merTutorDrecotion04:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1));
                            break;
                        case merTutorDrecotion05:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1));
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                                    c.getSession().write(UIPacket.getDirectionStatus(true));
                                    c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.IntroEnableUI(0));
                                    c.getSession().write(CWvsContext.enableActions());
                                }
                            }, 4000);
                            break;
                        case merTutorDrecotion12:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            c.getSession().write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1));
                            c.getSession().write(UIPacket.IntroEnableUI(0));
                            break;
                        case merTutorDrecotion21:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 1));
                            c.getSession().write(UIPacket.getDirectionStatus(true));
                            MapleMap mapto = c.getChannelServer().getMapFactory().getMap(910150005);
                            c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                            break;
                        case ds_tuto_0_2:
                            c.getSession().write(CField.showEffect("demonSlayer/text1"));
                            break;
                        case ds_tuto_0_1:
                            c.getSession().write(UIPacket.getDirectionInfo(3, 2));
                            break;
                        case ds_tuto_0_3:
                            c.getSession().write(CField.showEffect("demonSlayer/text2"));
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text3"));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                                    c.getSession().write(CField.showEffect("demonSlayer/text4"));
                                }
                            }, 6000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text5"));
                                }
                            }, 6500);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 500));
                                    c.getSession().write(CField.showEffect("demonSlayer/text6"));
                                }
                            }, 10500);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(1, 4000));
                                    c.getSession().write(CField.showEffect("demonSlayer/text7"));
                                }
                            }, 11000);
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    c.getSession().write(UIPacket.getDirectionInfo(4, 2159307));
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 2159307);
                                }
                            }, 15000);
                            break;
                    }
                }
            }
            c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
            chr.setDirection(chr.getDirection() + 1);
            if (chr.getMap().getDirectionInfo(chr.getDirection()) == null) {
                chr.setDirection(-1);
            }
        } else if (start) {
            switch (chr.getMapId()) {
                //hack
                case 931050300:
                    while (chr.getLevel() < 10) {
                        chr.levelUp();
                    }
                    final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(931050000);
                    chr.changeMap(mapto, mapto.getPortal(0));
                    break;
            }
        }
    }
}
