package console;

import handling.channel.ChannelServer;
import java.util.Scanner;
import scripting.NPCScriptManager;
import tools.packet.CField;

public class consoleScan {

    public static String commands, victim, reason, commandLC;
    public static boolean error = false;

    private static String joinStringFrom(String arr[], int start) {
        return joinStringFrom(arr, start, arr.length - 1);
    }

    private static String joinStringFrom(String arr[], int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != end) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static void run() {

        while (error == false) {

            System.out.println("請輸入指令: ");
            Scanner scanInput = new Scanner(System.in);
            String input = scanInput.nextLine();
            String[] inputArray = input.split(" ");
            commandLC = inputArray[0].toLowerCase();
            try {
                switch (commandLC) {
                    case "command":
                    case "help":
                        System.out.println("╭〝☆指令列表〞★╮");
                        System.out.println("ServermessageYGM<訊息> - 對GM說話");
                        System.out.println("ServermessageY  <訊息> - 說話");
                        System.out.println("ServerMessageS  <訊息> - 公告修改");
                        System.out.println("ServerMessage   <訊息> - 公告修改+說話");
                        System.out.println("Notice          <訊息> - 彈出式視窗公告");
                        System.out.println("Tip             <訊息> - 黃色Tip公告");
                        System.out.println();
                        System.out.println("Mesorate <倍率> - 變更世界金錢倍率");
                        System.out.println("Droprate <倍率> - 變更世界掉寶倍率");
                        System.out.println("Exprate  <倍率> - 變更世界經驗倍率");
                        System.out.println();
                        System.out.println("GM        <玩家名稱> <權限>       - 變更玩家權限");
                        System.out.println("GMID      <玩家名稱> <權限>       - 變更玩家權限");
                        System.out.println("Cname     <玩家名稱> <新名稱>     - 變更玩家名稱");
                        System.out.println("CnameID   <玩家編號> <新名稱>     - 變更玩家名稱");
                        System.out.println("Job       <玩家名稱> <職業代碼>   - 變更玩家職業");
                        System.out.println("JobID     <玩家編號> <職業代碼>   - 變更玩家職業");
                        System.out.println("Map       <玩家名稱> <地圖代碼>   - 變更玩家地圖");
                        System.out.println("MapID     <玩家編號> <地圖代碼>   - 變更玩家地圖");
                        System.out.println("Level     <玩家名稱> <等級>       - 變更玩家等級");
                        System.out.println("LevelID   <玩家編號> <等級>       - 變更玩家等級");
                        System.out.println("Meso      <玩家名稱> <楓幣>       - 給予玩家楓幣");
                        System.out.println("MesoID    <玩家編號> <楓幣>       - 給予玩家楓幣");
                        System.out.println("Nx        <玩家名稱> <點數>       - 給予玩家點數");
                        System.out.println("NxID      <玩家編號> <點數>       - 給予玩家點數");
                        System.out.println("Ban       <玩家名稱> <理由>       - 封鎖玩家");
                        System.out.println("BanID     <玩家編號> <理由>       - 封鎖玩家");
                        System.out.println("UnBan     <玩家名稱>              - 解鎖玩家");
                        System.out.println("Heal      <玩家名稱>              - 恢復玩家");
                        System.out.println("HealID    <玩家編號>              - 恢復玩家");
                        System.out.println("ReloadP   <玩家名稱>              - 重整角色");
                        System.out.println("ReloadPID <玩家編號>              - 重整角色");
                        System.out.println("DC        <玩家名稱>              - 踢出角色");
                        System.out.println("DCID      <玩家編號>              - 踢出角色");
                        System.out.println("Item      <玩家名稱> <代碼> <數量> - 給予玩家道具");
                        System.out.println("ItemID    <玩家編號> <代碼> <數量> - 給予玩家道具");
                        System.out.println("Q         <玩家名稱> <任務代碼> <數量> - 給予任務點數");
                        System.out.println("QID       <玩家編號> <任務代碼> <數量> - 給予任務點數");
                        System.out.println();
                        System.out.println("Online                         - 查線上玩家");
                        System.out.println("Sid       <玩家名稱>           - 查玩家編號");
                        System.out.println("ip        <玩家名稱>           - 查玩家IP");
                        System.out.println("ipid      <玩家編號>           - 查玩家IP");
                        System.out.println("chinfo    <玩家名稱>           - 查玩家資料");
                        System.out.println("chinfoid  <玩家編號>           - 查玩家資料");
                        System.out.println();
                        System.out.println("DropC     - 開放Drop指令");
                        System.out.println();
                        System.out.println("Downon    - 解除卡帳");
                        System.out.println("Autobans  - 自動封鎖");
                        System.out.println("Reloadwz  - 重新載入WZ");
                        System.out.println("ReloadAll - 重新載入服務端");
                        System.out.println("Shutdown  - 關閉楓之谷伺服器");
                        System.out.println("ShutdownTime  <分鐘數> - 某分鐘後關閉楓之谷伺服器");
                        System.out.println("Register <帳號> <密碼> - 註冊帳號密碼");
                        System.out.println();
                        System.out.println("Packet    - 封包顯示開關");
                        System.out.println("CashShop  - 現金商城開關");
                        System.out.println("LocalHost - 管理模式開關");
                        System.out.println("AutoReg   - 自動註冊開關");
                        System.out.println();
                        System.out.println("Exit - 關閉CCS");
                        break;
                    case "autobans":
                        consoleCommand.autobans();
                        break;
                    case "dropc":
                        consoleCommand.GiveDrop();
                        break;
                    case "packet":
                        consoleCommand.packet();
                        break;
                    case "cashshop":
                        consoleCommand.CSnow();
                        break;
                    case "localhost":
                        consoleCommand.LHnow();
                        break;
                    case "autoreg":
                        consoleCommand.ARnow();
                        break;
                    case "downon":
                        consoleCommand.downon();
                        break;
                    case "online":
                        consoleCommand.online();
                        break;
                    case "reloadall":
                        consoleCommand.reloadall();
                        System.out.println("已經重新載入完成");
                        break;
                    case "reloadwz":
                        consoleCommand.ReloadWz();
                        System.out.println("已經重新載入完成");
                        break;
                    case "saveall":
                        consoleCommand.SaveAll();
                        System.out.println("已經存檔完成");
                        break;
                    case "register":
                        if (inputArray.length < 3) {
                            System.out.println("Register <帳號> <密碼> - 註冊帳號密碼");
                            break;
                        }
                        consoleCommand.Register(inputArray[1], inputArray[2]);
                        break;
                    case "cname":
                        if (inputArray.length < 3) {
                            System.out.println("Cname <玩家名稱> <新名稱> - 變更玩家名稱");
                            break;
                        }
                        consoleCommand.cname(inputArray[1], inputArray[2]);
                        break;
                    case "cnameid":
                        if (inputArray.length < 3) {
                            System.out.println("Cnameid <玩家編號> <新名稱>  - 變更玩家名稱");
                            break;
                        }
                        int PID = Integer.parseInt(inputArray[1]);
                        String PN = inputArray[2];
                        consoleCommand.cnamebyid(PID, PN);
                        break;
                    case "ban":
                        if (inputArray.length < 3) {
                            System.out.println("Ban <玩家名稱> <理由> - 封鎖玩家");
                            break;
                        }
                        consoleCommand.Ban(inputArray[1], inputArray[2]);
                        break;
                    case "banid":
                        if (inputArray.length < 3) {
                            System.out.println("BanID <玩家編號> <理由> - 封鎖玩家");
                            break;
                        }
                        consoleCommand.BanID(Integer.parseInt(inputArray[1]), inputArray[2]);
                        break;
                    case "unban":
                        if (inputArray.length < 2) {
                            System.out.println("UnBan <玩家名稱> - 解鎖玩家");
                            break;
                        }
                        consoleCommand.unBan(inputArray[1]);
                        break;
                    case "mesorate": {
                        if (inputArray.length < 3) {
                            System.out.println("Mesorate <世界> <倍率> - 變更世界金錢倍率");
                            break;
                        }
                        int world = Integer.parseInt(inputArray[1]);
                        int rate = Integer.parseInt(inputArray[2]);
                        consoleCommand.MesoRate(world, rate);
                        System.out.println("您把世界[" + world + "]金錢倍率調整 " + rate + "x");
                        break;
                    }
                    case "droprate": {
                        if (inputArray.length < 3) {
                            System.out.println("Droprate <世界> <倍率> - 變更世界掉寶倍率");
                            break;
                        }
                        int world = Integer.parseInt(inputArray[1]);
                        int rate = Integer.parseInt(inputArray[2]);
                        consoleCommand.DropRate(world, rate);
                        System.out.println("您把世界[" + world + "]掉寶倍率調整 " + rate + "x");
                        break;
                    }
                    case "exprate": {
                        if (inputArray.length < 3) {
                            System.out.println("Exprate <世界> <倍率> - 變更世界經驗倍率");
                            break;
                        }
                        int world = Integer.parseInt(inputArray[1]);
                        int rate = Integer.parseInt(inputArray[2]);
                        consoleCommand.ExpRate(world, rate);
                        System.out.println("您把世界[" + world + "]經驗倍率調整 " + rate + "x");
                        break;
                    }
                    case "gmid":
                        if (inputArray.length < 3) {
                            System.out.println("GMID <玩家名稱> <權限> - 變更玩家權限");
                            break;
                        }
                        int p = Integer.parseInt(inputArray[1]);
                        int lv = Integer.parseInt(inputArray[2]);
                        consoleCommand.gmpersonID(p, (byte) lv);
                        break;
                    case "gm":
                    case "gmperson":
                        if (inputArray.length < 3) {
                            System.out.println("GM <玩家名稱> <權限> - 變更玩家權限");
                            break;
                        }
                        String player = inputArray[1];
                        int level = Integer.parseInt(inputArray[2]);
                        consoleCommand.gmperson(player, (byte) level);

                        break;
                    case "smg":
                    case "servermessage":
                        if (inputArray.length < 2) {
                            System.out.println("ServerMessage <訊息> - 公告修改+說話");
                            break;
                        }
                        consoleCommand.ServerMessage(joinStringFrom(inputArray, 1));
                        System.out.println("您把公告改成[" + joinStringFrom(inputArray, 1) + "]");
                        System.out.println("您說了 [" + joinStringFrom(inputArray, 1) + "]");
                        break;
                    case "servermessagey":
                        if (inputArray.length < 2) {
                            System.out.println("ServermessageY <訊息> - 說話");
                            break;
                        }
                        consoleCommand.ServerMessageY(joinStringFrom(inputArray, 1));
                        System.out.println("您說了 [" + joinStringFrom(inputArray, 1) + "]");
                        break;
                    case "notice":
                        if (inputArray.length < 2) {
                            System.out.println("Notice <訊息> - 彈出式視窗公告");
                            break;
                        }
                        consoleCommand.Notice(joinStringFrom(inputArray, 1));
                        System.out.println("您的通知視窗內容 [" + joinStringFrom(inputArray, 1) + "]");
                        break;
                    case "servermessages":
                        if (inputArray.length < 2) {
                            System.out.println("ServerMessageS <訊息> - 公告修改");
                            break;
                        }
                        consoleCommand.ServerMessageS(joinStringFrom(inputArray, 1));
                        System.out.println("您把公告改成[" + joinStringFrom(inputArray, 1) + "]");
                        break;
                    case "servermessageygm":
                        if (inputArray.length < 2) {
                            System.out.println("ServermessageYGM <訊息> - 對GM說話");
                            break;
                        }
                        consoleCommand.ServerMessageYGM(joinStringFrom(inputArray, 1));
                        System.out.println("您說了 [" + joinStringFrom(inputArray, 1) + "]");
                        break;
                    case "tip":
                        if (inputArray.length < 2) {
                            System.out.println("Tip <訊息> - 黃色Tip公告");
                            break;
                        }
                        consoleCommand.Tip(joinStringFrom(inputArray, 1));
                        break;
                    case "stop":
                    case "shutdown":
                        System.out.println("伺服器將要關閉!");
                        consoleCommand.shutdownServer();
                    case "shutdowntime":
                        if (inputArray.length > 1) {
                            System.out.println("伺服器將要在" + inputArray[1] + "分鐘後關閉!");
                            if (!consoleCommand.getNow()) {
                                consoleCommand.shutdownTime(Integer.parseInt(inputArray[1]));
                            } else {
                                System.out.println("伺服器已經在進行關閉作業了唷!");
                            }
                        } else {
                            System.out.println("使用方法為: shutdowntime 關閉分鐘數");
                        }
                        break;
                    case "sid":
                        if (inputArray.length < 2) {
                            System.out.println("Sid <玩家名稱> - 查玩家編號");
                            break;
                        }
                        consoleCommand.searchId(inputArray[1]);
                        break;
                    case "dispose":
                        if (inputArray.length > 1) {
                            ChannelServer.getAllInstances().stream().map((cs) -> cs.getPlayerStorage().getCharacterByName(inputArray[1])).map((chr) -> chr.getClient()).map((c) -> {
                                c.getSession().write(CField.UIPacket.getDirectionStatus(false));
                                return c;
                            }).map((c) -> {
                                c.getSession().write(CField.UIPacket.IntroEnableUI(0));
                                return c;
                            }).map((c) -> {
                                c.getSession().write(CField.UIPacket.IntroDisableUI(false));
                                return c;
                            }).map((c) -> {
                                NPCScriptManager.getInstance().dispose(c);
                                return c;
                            }).forEachOrdered((c) -> {
                                c.removeClickedNPC();
                            });
                        } else {
                            System.out.println("使用方法為: dispose 角色名稱");
                        }
                        break;
                    case "reloadp":
                        if (inputArray.length < 2) {
                            System.out.println("Reload <玩家名稱> - 重新載入玩家");
                            break;
                        }
                        consoleCommand.ReloadP(inputArray[1]);
                        System.out.println("角色重整完成!");
                        break;
                    case "reloadpid":
                        if (inputArray.length < 2) {
                            System.out.println("Reloadid <玩家編號> - 重新載入玩家");
                            break;
                        }
                        consoleCommand.ReloadPID(Integer.parseInt(inputArray[1]));
                        System.out.println("角色重整完成!");
                        break;
                    case "map":
                        if (inputArray.length < 3) {
                            System.out.println("Map <玩家名稱> <地圖代碼> - 傳送玩家");
                            break;
                        }
                        consoleCommand.map(inputArray[1], Integer.parseInt(inputArray[2]));
                        break;
                    case "mapid":
                        if (inputArray.length < 3) {
                            System.out.println("Mapid <玩家編號> <地圖代碼> - 傳送玩家");
                            break;
                        }
                        consoleCommand.mapID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]));
                        break;
                    case "job":
                        if (inputArray.length < 3) {
                            System.out.println("Job  <玩家名稱> <職業代碼>   - 變更玩家職業");
                            break;
                        }
                        consoleCommand.jb(inputArray[1], Integer.parseInt(inputArray[2]));
                        break;
                    case "jobid":
                        if (inputArray.length < 3) {
                            System.out.println("JobID  <玩家編號> <職業代碼>   - 變更玩家職業");
                            break;
                        }
                        consoleCommand.jbID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]));
                        break;
                    case "item":
                        if (inputArray.length < 4) {
                            System.out.println("Item  <玩家名稱> <代碼> <數量> - 給予玩家道具");
                            break;
                        }
                        consoleCommand.Item(inputArray[1], Integer.parseInt(inputArray[2]), Integer.parseInt(inputArray[3]));
                        break;
                    case "itemid":
                        if (inputArray.length < 4) {
                            System.out.println("ItemID <玩家名稱> <代碼> <數量> - 給予玩家道具");
                            break;
                        }
                        consoleCommand.ItemID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]), Integer.parseInt(inputArray[3]));

                        break;
                    case "level":
                        if (inputArray.length < 3) {
                            System.out.println("Level <玩家名稱> <等級> - 變更玩家等級");
                            break;
                        }
                        consoleCommand.Lv(inputArray[1], Integer.parseInt(inputArray[2]));

                        break;
                    case "levelid":
                        if (inputArray.length < 3) {
                            System.out.println("LevelID <玩家編號> <等級> - 變更玩家等級");
                            break;
                        }
                        consoleCommand.LvID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]));

                        break;
                    case "meso":
                        if (inputArray.length < 3) {
                            System.out.println("Meso <玩家名稱> <楓幣> - 給予玩家楓幣");
                            break;
                        }
                        consoleCommand.mo(inputArray[1], Integer.parseInt(inputArray[2]));

                        break;
                    case "mesoid":
                        if (inputArray.length < 3) {
                            System.out.println("MesoID <玩家編號> <楓幣> - 給予玩家楓幣");
                            break;
                        }
                        consoleCommand.moID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]));

                        break;
                    case "nx":
                        if (inputArray.length < 3) {
                            System.out.println("Nx <玩家名稱> <點數> - 給予玩家點數");
                            break;
                        }
                        consoleCommand.nx(inputArray[1], Integer.parseInt(inputArray[2]));

                        break;
                    case "nxid":
                        if (inputArray.length < 3) {
                            System.out.println("NxID <玩家編號> <點數> - 給予玩家點數");
                            break;
                        }
                        consoleCommand.nxID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]));

                        break;
                    case "heal":
                        if (inputArray.length < 2) {
                            System.out.println("Heal <玩家名稱> - 恢復玩家");
                            break;
                        }
                        consoleCommand.heal(inputArray[1]);

                        break;
                    case "healid":
                        if (inputArray.length < 2) {
                            System.out.println("HealID <玩家編號> - 恢復玩家");
                            break;
                        }
                        consoleCommand.healID(Integer.parseInt(inputArray[1]));

                        break;
                    case "q":
                        if (inputArray.length < 4) {
                            System.out.println("Q <玩家名稱> <任務代碼> <數量> - 給予任務點數");
                            break;
                        }
                        consoleCommand.Q(inputArray[1], Integer.parseInt(inputArray[2]), Integer.parseInt(inputArray[3]));

                        break;
                    case "qid":
                        if (inputArray.length < 4) {
                            System.out.println("QID  <玩家編號> <任務代碼> <數量> - 給予任務點數");
                            break;
                        }
                        consoleCommand.QID(Integer.parseInt(inputArray[1]), Integer.parseInt(inputArray[2]), Integer.parseInt(inputArray[3]));

                        break;
                    case "ip":
                        if (inputArray.length < 2) {
                            System.out.println("ip  <玩家名稱> - 查玩家IP");
                            break;
                        }
                        consoleCommand.ip(inputArray[1]);
                        break;
                    case "ipid":
                        if (inputArray.length < 2) {
                            System.out.println("ipid <玩家編號> - 查玩家IP");
                            break;
                        }
                        consoleCommand.ipID(Integer.parseInt(inputArray[1]));
                        break;
                    case "chinfo":
                        if (inputArray.length < 2) {
                            System.out.println("chinfo    <玩家名稱> - 查玩家資料");
                            break;
                        }
                        consoleCommand.chinfo(inputArray[1]);
                        break;
                    case "chinfoid":
                        if (inputArray.length < 2) {
                            System.out.println("chinfoid  <玩家編號> - 查玩家資料");
                            break;
                        }
                        consoleCommand.chinfoID(Integer.parseInt(inputArray[1]));
                        break;
//                    case "dc":
//                        if (inputArray.length < 2) {
//                            System.out.println("DC <玩家名稱> - 踢出角色");
//                            break;
//                        }
//                        consoleCommand.DC(inputArray[1]);
//                        break;
//                    case "dcid":
//                        if (inputArray.length < 2) {
//                            System.out.println("DCID <玩家編號> - 踢出角色");
//                            break;
//                        }
//                        consoleCommand.DCID(Integer.parseInt(inputArray[1]));
//                        break;
                    case "exit":
                        error = true;
                        break;
                    default:
                        System.out.println("查無本指令");
                        break;
                }

            } catch (NumberFormatException Windyboy) {
                System.out.println("處理中出現異常 01 : 請輸入數字");
                run();
            } catch (ArrayIndexOutOfBoundsException Windyboy) {
                System.out.println("處理中出現異常 02 : 缺少輸入資料");
                run();
            }
        }
    }
}
