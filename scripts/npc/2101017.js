/**
 * @author: OdinMS
 * @editor: Eric
 * @npc: Cesar
 * @func: Ariant PQ (Outdated GMS-like text, AriantPQ is closed off and I am unable to get this.)
*/

importPackage(Packages.tools);
importPackage(Packages.client);

var status = -1;
var sel;

function start() {
    if ((cm.getPlayer().getLevel() < 50) && !cm.getPlayer().isGM()) {
		cm.sendNext("你不在 level 20 and 30. 對不起，您可能不參加.");
        cm.dispose();
        return;
    }
    if(cm.getPlayer().getMapId() % 10 == 1)
        cm.sendSimple("你對我有一個請求嗎?\r\n#b#L0# 給我 #t2270002# and #t2100067#.#l\r\n#L1# 我該做什麼?#l\r\n#L2# 讓我離開這裡.#l");
    else
        cm.sendSimple(cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName() ? "你想開始比賽嗎?#b\r\n#b#L3# 準備進入戰場!!#l\r\n#L1# 我想踢另一個角色.#l\r\n#L2# 讓我離開這裡.#l" : "你想要什麼?#b\r\n#L2# 讓我離開這裡.#l");
}

function action(mode, type, selection){
    status++;
    if (mode != 1) {
        if (mode == 0 && type == 0)
            status -= 2;
        else {
            cm.dispose();
            return;
        }
    }
    if (cm.getPlayer().getMapId() % 10 == 1) {
        if (status == 0) {
            if (sel == undefined)
                sel = selection;
            if (sel == 0) {
                if (cm.haveItem(2270002))
                    cm.sendNext("你已經擁有 #b#t2270002##k.");
                else if (cm.canHold(2270002) && cm.canHold(2100067)) {
                    if (cm.haveItem(2100067))
                        cm.removeAll(2100067);
                    cm.gainItem(2270002, 32);
                    cm.gainItem(2100067, 5);
                    cm.sendNext("現在降低怪物的HP，並使用 #b#t2270002##k 吸收他們的力量!");
                } else
                    cm.sendNext("檢查和查看是否使用庫存是全");
                cm.dispose();
            } else if(sel == 1) {
				status = 1;
                cm.sendNext("你需要做什麼？你一定是新來的。請允許我詳細解釋.");
            } else
                cm.sendYesNo("Are you sure you want to leave?"); //No GMS like.
        } else if (status == 1) {
            if (mode == 1) {
                cm.warp(980010020);
                cm.dispose();
                return;
            }
		} else if (status == 2) {
            cm.sendNextPrev("這真的很簡單。你會得到 #b#t2270002##k 從我身上，你的任務就是要消除一個集合的量 HP從怪物，然後使用 #b#t2270002##k 吸取其巨大的力量.");
        } else if (status == 3)
            cm.sendNextPrev("很簡單。如果你吸收了怪物的力量#b#t2270002##k, 然後你會做 #b#t4031868##k, 這是女王阿列達愛。與大多數珠寶戰鬥獲勝。為了贏得比賽，為了防止別人的吸收，這實際上是一個聰明的想法.");
        else if (status == 4)
            cm.sendNextPrev("一件事. #r你可能不會使用寵物.#k理解?~!");
        else if (status == 5)
            cm.dispose();
    } else {
        var nextchar = cm.getMap(cm.getPlayer().getMapId()).getCharacters().iterator();
        if (status == 0) {
            if (sel == undefined)
                sel = selection;
            if (sel == 1)
                if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 1) {
                    var text = "你想從誰的房間裡踢一腳?"; //Not GMS like text
                    var name;
                    for (var i = 0; nextchar.hasNext(); i++) {
                        name = nextchar.next().getName();
                        if (!cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1).equals(name))
                            text += "\r\n#b#L" + i + "#" + name + "#l";
                    }
                    cm.sendSimple(text);
                } else {
                    cm.sendNext("現在沒有什麼可以被踢的角色。");
                    cm.dispose();
                }
            else if (sel == 2) {
                if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) == cm.getPlayer().getName())
                    cm.sendYesNo("你確定你要離開嗎？你是競技場的領袖，所以如果你離開，整個戰鬥競技場將關閉.");
                else
                    cm.sendYesNo("你確定你要離開嗎?"); //No GMS like.
            } else if (sel == 3)
                if (cm.getPlayerCount(cm.getPlayer().getMapId()) > 1)
                    cm.sendYesNo("房間都是一套，沒有其他的角色可以加入這場戰鬥的競技場。你想現在開始遊戲嗎?");
                else {
                    cm.sendNext("你至少需要2名參與者來開始比賽.");
                    cm.dispose();
                }
        } else if (status == 1) {
            if (sel == 1) {
                for (var i = 0; nextchar.hasNext(); i++)
                    if (i == selection) {
                        nextchar.next().changeMap(cm.getMap(980010000));
                        break;
                    } else
                        nextchar.next();
                cm.sendNext("玩家被踢出了舞台."); //Not GMS like
            } else if(sel == 2) {
                if (cm.getPlayer().getAriantRoomLeaderName(((cm.getPlayer().getMapId() / 100) % 10) - 1) != cm.getPlayer().getName())
                    cm.warp(980010000);
                else {
                    cm.getPlayer().removeAriantRoom((cm.getPlayer().getMapId() / 100) % 10);
                    cm.mapMessage(6, cm.getPlayer().getName() + " 已经离开了舞台，所以舞台上现在将关闭.");
                    cm.warpMap(980010000);
                }
            } else {
				cm.startAriantPQ(cm.getPlayer().getMapId() + 1);
            }
            cm.dispose();
        }
    }
}
