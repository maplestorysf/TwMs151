/**
 * @author: Eric
 * @npc: Cesar
 * @func: Ariant PQ
*/

var status = 0;
var sel;
var empty = [false, false, false];
var closed = false;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection){
    (mode == 1 ? status++ : status--);
    if (status == 0) {
		cm.sendSimple("#e<競爭：大競技場>#n\r\n歡迎來到大競技場裡可以對抗其他戰士和展示你的能力.#b\r\n#L0#請求進入 [Ariant Coliseum].\r\n#L1#關於對 [Ariant Coliseum]\r\n#L2#[Ariant Coliseum] 評價標準\r\n#L3#檢查今天的剩餘挑戰計數.\r\n#L4#接收到競技場獎勵.");
	} else if (status == 1) {
		if (selection == 0) {
			if (closed || (cm.getPlayer().getLevel() < 50 && !cm.getPlayer().isGM())) {
				cm.sendOk(closed ? "大競技場是獲得一個好的事情吧。請稍後再回來." : "你不在 level 50 and 200. 對不起，您可能不參加.");
				cm.dispose();
				return;
			}
			var text = "你想要什麼?#b";
			for(var i = 0; i < 3; i += 1)
				if (cm.getPlayerCount(980010100 + (i * 100)) > 0)
					if (cm.getPlayerCount(980010101 + (i * 100)) > 0)
						continue;
					else
						text += "\r\n#L" + i + "# Battle Arena " + (i + 1) + " (" + cm.getPlayerCount(980010100 + (i * 100)) + "/" + cm.getPlayer().getAriantSlotsRoom(i) + " Users. Leader: " + cm.getPlayer().getAriantRoomLeaderName(i) + ")#l";
				else {
					empty[i] = true;
					text += "\r\n#L" + i + "# Battle Arena " + (i + 1) + " (Empty)#l";
					if (cm.getPlayer().getAriantRoomLeaderName(i) != "")
						cm.getPlayer().removeAriantRoom(i);
				}
			cm.sendSimple(text);
		} else if (selection == 1) {
			cm.sendNext("大競技場是一個激烈的戰場，真正的戰士將被清理！即使你是個膽小鬼，你也不要把你的眼睛放在上面！一個探險家誰使阿列達喜愛的寶石最會被選為最好的戰鬥機！很簡單，是吧？\r\n - #eLevel#n : 50 or above#r(Recommended Level : 50 - 80 )#k\r\n - #eTime Limit#n : 8 minutes\r\n - #ePlayers#n : 2-6\r\n - #eItem Acqusition#n :\r\n#i1113048:# Champion Ring");
			cm.dispose();
		} else if (selection == 2) {
			status = 9;
			cm.sendNext("你想知道怎麼 #r例外的冠軍#k 得到 #b分類#k? 多麼雄心勃勃！好，我會向你解釋.");
		} else if (selection == 3) {
			var ariant = cm.getQuestRecord(150139);
			var data = ariant.getCustomData();
			if (data == null) {
				ariant.setCustomData("10");
				data = "10";
			}
			cm.sendNext("#r#h ##k, 你可以參加到體育館 #b" + parseInt(data) + "#k time(s) today.");
			cm.dispose();
		} else if (selection == 4) {
			status = 4;
			cm.sendNext("你有什麼本事在大競技場！如果你的競技場分數高於 150, 你將得到 #i1113048:# #b冠軍戒指#k.\r\n這是真正的鬥士的象徵.");
		}
	} else if (status == 2) {
		var sel = selection;
		if(cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
            empty[sel] = false;
        else if(cm.getPlayer().getAriantRoomLeaderName(sel) != "") {
			cm.warp(980010100 + (sel * 100));
            cm.dispose();
            return;
        }
        if (!empty[sel]) {
            cm.sendNext("另一個戰士創造了競技場第一。我建議你要麼建立一個新的，要麼加入戰鬥競技場已經建立.");
            cm.dispose();
            return;
        }
		cm.getPlayer().setApprentice(sel);
        cm.sendGetNumber("有多少與會者可以參加這場比賽? (2~6 ppl)", 0, 2, 6);
	} else if (status == 3) {
		var sel = cm.getPlayer().getApprentice(); // how 2 final in javascript.. const doesn't work for shit
		if (cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
			empty[sel] = false;
        if (!empty[sel]) {
            cm.sendNext("另一個戰士創造了競技場第一。我建議你要麼建立一個新的，要麼加入戰鬥競技場已經建立.");
            cm.dispose();
            return;
        }
        cm.getPlayer().setAriantRoomLeader(sel, cm.getPlayer().getName());
        cm.getPlayer().setAriantSlotRoom(sel, selection);
        cm.warp(980010100 + (sel * 100));
		cm.getPlayer().setApprentice(0);
        cm.dispose();
	} else if (status == 5) {
		cm.sendNextPrev("問題是，你的競技場分數只有 #b0#k. 你必須得分高於 #b150#k 得到 #b冠軍戒指#k. 足夠高的分數來證明你有資格獲得這一.");
	} else if (status == 6) { // todo: code champion rings :c
		cm.dispose();
	} else if (status == 10) {
		cm.sendNextPrev("讓我告訴你最簡單的規則。最偉大的冠軍 #b靈魂寶石#k將被選為最佳的冠軍。當然，如果你贏得了一場比賽的話，你會得到更高的讚美 #b無數冠軍#k.\r\n\r\n(#b當比賽結束時，你的排名將會被你所擁有的精神珠寶所決定。此�，如果更多的参与者继续，您将获得更多的奖励.)#k");
	} else if (status == 11) {
		cm.sendNextPrev("即使你不够坚强，也不要担心。如果你能做 #b至少 15#k 灵魂的宝石，没有人敢否认的事实，你是一个伟大的战士.\r\n\r\n(如果你做 #b至少 15 珠宝首饰, 你将获得平均报酬.)#k");
	} else if (status == 12) {
		cm.sendNextPrev("如果你赚的多 15?当然，我们会对这种特殊的冠军奖励更多的奖励！这并不意味着你会得到 #r无限量的奖励#k, 虽然。如果你做 #b30#k 宝石，你会得到的 #r最佳报酬#k.\r\n\r\n(使 #b30 精神珠宝项目获得最高质量奖励.)#k");
	} else if (status == 13) {
		cm.sendNextPrev("如果你不做至少15颗宝石，那就意味着你不会得到任何奖励? 不，那不可能是这样！我们美丽的女王阿列达吩咐我们给予一定的奖励 #b冠军谁甚至失败了至少 15#k 宝石。在这种情况下，你会得到 #r较少的奖励#k. 有什么抱怨吗？如果你不喜欢它，训练你的技能和执行一个竞技场比赛中！\r\n\r\n(如果你做 #b少于15 灵魂宝石项目，你将获得低质量的奖励.)#k");
	} else if (status == 14) {
		cm.sendNextPrev("当然，一个臭名昭著的坏冠军不值得被对待，以及其他人。即使 #b6 珠宝首饰#k 都太多了 #r你要做的#k, 那么，这仅仅意味着你没有达到标准。不管怎样，你几乎不受 #r任何奖励#k 为比赛中的比赛。所以，试着得到至少6个或更多的宝石.\r\n\r\n(如果你做 #b5 或更少的灵魂宝石项目，你将获得几乎任何奖励.)#k");
	} else if (status == 15) {
		cm.sendNextPrev("最后, #r懦夫#k 和不能完成任务的冠军 #b时间限制#k 将获得一些奖励的基础上 #r过去的时间#k.\r\n\r\n(#b如果体育馆就在它的中间停了下来，奖励将根据经过的时间了.)#k");
		cm.dispose();
	}
}
