/*
	Map : Mu Lung Training Center
	Npc : So Gong
        Desc : Training Center Start
 */

var status = -1;
var sel;
var mapid;

function start() {
    mapid = cm.getMapId();

    if (mapid == 925020001) {
	cm.sendSimple("我的師父是武陵裡最強大的人. 你說想要挑戰我師父? 不要說我沒提醒你. \r #b#L0# 我想要自己挑戰.#l \n\r #L1# 我想要組隊挑戰.#l \n\r #L2# 我要兌換腰帶.#l \n\r #L3# 我想要清除我的積分.#l \n\r #L5# 什麼是武陵道館?#l                                                                                            ");
    } else if (isRestingSpot(mapid)) {
	cm.sendSimple("我很驚呀!?沒想到你已經到達這種水準了. 怎麼樣呢? 要堅持下去?#b \n\r #L0# 是的,我會繼續堅持下去的.#l \n\r #L1# 我要離開#l \n\r #L2# 我要儲存至本次的積分.#l                                              ");
    } else {
	cm.sendYesNo("什麼? 你已經準備好要退出了? 你不要繼續挑戰嗎. 你確定真的要退出?                                              ");
    }
}

function action(mode, type, selection) {
    if (mapid == 925020001) {
	if (mode == 1) {
	    status++;
	} else {
	    cm.dispose();
		return;
	}
	if (status == 0) {
	    sel = selection;

	    if (sel == 5) {
		cm.sendNext("我的師父是武陵裡最強大的人. and he is responsible for erecting this amazing Mu Lung Training Tower. Mu Lung Training Tower is a colossal training facility that consists of 38 floors. Each floor represents additional levels of difficulty. Of course, with your skills, reaching the top floor will be impossible...");
		cm.dispose();
	    } else if (sel == 3) {
		cm.sendYesNo("你要刷掉之前的關卡紀錄嗎? 那下次就要從頭開始摟!                                              ");
	    } else if (sel == 2) {
		cm.sendSimple("你所擁有的積分 #b"+cm.getDojoPoints()+"#k. 你可以使用積分兌換以下道具, 旦你需要足夠的積分才可兌換...\n\r #L0##i1132000:# #t1132000#(200)#l \n\r #L1##i1132001:# #t1132001#(1800)#l \n\r #L2##i1132002:# #t1132002#(4000)#l \n\r #L3##i1132003:# #t1132003#(9200)#l \r\n                                              ");
	    } else if (sel == 1) {
		if (cm.getParty() != null) {
		    if (cm.isLeader()) {
			cm.sendYesNo("你現在就要進入了嗎?                                              ");
		    } else {
			cm.sendOk("嘿, 你不是隊長唷. 難道你想強行闖入!? 請你的隊長和我對話吧...                                              ");
		    }
		}
	    } else if (sel == 0) {
		if (cm.getParty() != null) {
			cm.sendOk("請離開組隊.                                              ");
			cm.dispose();
			return;
		}
		var record = cm.getQuestRecord(150000);
		var data = record.getCustomData();

		if (data != null) {
		    var idd = get_restinFieldID(parseInt(data));
		    if (idd != 925020002) {
		        cm.dojoAgent_NextMap(true, true, idd);
		        record.setCustomData(null);
		    } else {
			cm.sendOk("請稍候一下子.                                              ");
		    }
		} else {
		    cm.start_DojoAgent(true, false);
		}
		cm.dispose();
	    // cm.sendYesNo("The last time you took the challenge yourself, you were able to reach Floor #18. I can take you straight to that floor, if you want. Are you interested?");
	    }
	} else if (status == 1) {
	    if (sel == 3) {
		cm.setDojoRecord(true);
		//cm.sendOk("我要將我的積分歸零.                                              ");
	    } else if (sel == 2) {
		var record = cm.getDojoRecord();
		var required = 0;
		
		switch (record) {
		    case 0:
			required = 200;
			break;
		    case 1:
			required = 1800;
			break;
		    case 2:
			required = 4000;
			break;
		    case 3:
			required = 9200;
			break;
		}

		if (record == selection && cm.getDojoPoints() >= required) {
		    var item = 1132000 + record;
		    if (cm.canHold(item)) {
			cm.gainItem(item, 1);
			cm.setDojoRecord(false);
		    } else {
			cm.sendOk("請確認你是否擁有該道具.                                              ");
		    }
		} else {
		    cm.sendOk("你沒有足夠的積分可以兌換,要不要換其他的腰帶呢?                                              ");
		}
		cm.dispose();
	    } else if (sel == 1) {
		cm.start_DojoAgent(true, true);
		cm.dispose();
	    }
	}
    } else if (isRestingSpot(mapid)) {
	if (mode == 1) {
	    status++;
	} else {
	    cm.dispose();
	    return;
	}

	if (status == 0) {
	    sel = selection;

	    if (sel == 0) {
		if (cm.getParty() == null || cm.isLeader()) {
		    cm.dojoAgent_NextMap(true, true);
		} else {
		    cm.sendOk("請你的隊長與我對話.                                              ");
		}
		//cm.getQuestRecord(150000).setCustomData(null);
		cm.dispose();
	    } else if (sel == 1) {
		cm.askAcceptDecline("你想要退出? 你真的想離開這裡?                                              ");
	    } else if (sel == 2) {
		if (cm.getParty() == null) {
			var stage = get_stageId(cm.getMapId());

			cm.getQuestRecord(150000).setCustomData(stage);
			cm.sendOk("我已經幫你記錄好了,下次挑戰時我會幫你傳送到此關卡.                                              ");
			cm.dispose();
		} else {
			cm.sendOk("嘿...你不能記錄積分於團隊模式                                              ");
			cm.dispose();
		}
	    }
	} else if (status == 1) {
	    if (sel == 1) {
		if (cm.isLeader()) {
			cm.warpParty(925020002);
		} else {
			cm.warp(925020002);
		}
	    }
	    cm.dispose();
	}
    } else {
	if (mode == 1) {
		if (cm.isLeader()) {
			cm.warpParty(925020002);
		} else {
			cm.warp(925020002);
		}
	}
	cm.dispose();
    }
}

function get_restinFieldID(id) {
	var idd = 925020002;
    switch (id) {
	case 1:
	    idd =  925020600;
	    break;
	case 2:
	    idd =  925021200;
	    break;
	case 3:
	    idd =  925021800;
	    break;
	case 4:
	    idd =  925022400;
	    break;
	case 5:
	    idd =  925023000;
	    break;
	case 6:
	    idd =  925023600;
	    break;
    }
    for (var i = 0; i < 10; i++) {
	var canenterr = true;
	for (var x = 1; x < 39; x++) {
		var map = cm.getMap(925020000 + 100 * x + i);
		if (map.getCharactersSize() > 0) {
			canenterr = false;
			break;
		}
	}
	if (canenterr) {
		idd += i;
		break;
	}
}
	return idd;
}

function get_stageId(mapid) {
    if (mapid >= 925020600 && mapid <= 925020614) {
	return 1;
    } else if (mapid >= 925021200 && mapid <= 925021214) {
	return 2;
    } else if (mapid >= 925021800 && mapid <= 925021814) {
	return 3;
    } else if (mapid >= 925022400 && mapid <= 925022414) {
	return 4;
    } else if (mapid >= 925023000 && mapid <= 925023014) {
	return 5;
    } else if (mapid >= 925023600 && mapid <= 925023614) {
	return 6;
    }
    return 0;
}

function isRestingSpot(id) {
    return (get_stageId(id) > 0);
}