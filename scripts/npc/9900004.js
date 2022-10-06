/** Author: nejevoli
	NPC Name: 		NimaKin
	Map(s): 		Victoria Road : Ellinia (180000000)
	Description: 		Maxes out your stats and able to modify your equipment stats
*/
importPackage(java.lang);

var status = 0;
var slot = Array();
var stats = Array( "力量", "敏捷", "智力", "幸運", "HP", "MP", "物理攻擊力", "魔法攻擊力", "物理防禦力", "魔法防禦力", "命中率", "迴避值", "手藝", "移動速度", "跳躍力", "剩餘衝捲次數", "黃金鐵槌", "已衝捲次數", "星捲級數", "第1排潛能", "第2排潛能", "第3排潛能", "第4排潛能", "第5排潛能", "持有者");
var selected;
var statsSel;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	if (cm.getPlayerStat("ADMIN") == 1) {
		cm.sendSimple("你要做什麼?#b\r\n#L9#等級提升\r\n#L10#GM廣播\r\n#L11#隨意點歌\r\n#L12#裝備領取\r\n#L50#管理員轉職NPC\r\n#L0#能力值全滿!#l\r\n#L1#技能全滿!#l\r\n#L2#改變裝備的數值!#l\r\n#L3#Look at potential values#l\r\n#L4#設置AP/SP到0#l\r\n#L5#清除技能#l\r\n#L6#職業技能全滿#l\r\n#L7#清除我的能力值!#b\r\n#L8#全能力50系統\r\n");
	} else if (cm.getPlayerStat("GM") == 1) {
		cm.sendSimple("你要做什麼?#b\r\n#L0#能力值全滿!#l\r\n#L1#技能全滿!#l\r\n#L4設置AP/SP到0#l\r\n#L7#清除我的能力值!#k");
	} else {
	    cm.dispose();
	}
    } else if (status == 1) {
	
	if (selection == 0) {
	    if (cm.getPlayerStat("GM") == 1) {
		cm.maxStats();
		cm.sendOk("您的能力值已全滿");
	    }
	    cm.dispose();
	} else if (selection == 1) {
	    //Beginner
	    if (cm.getPlayerStat("GM") == 1) {
		cm.maxAllSkills();
	    }
	    cm.dispose();
	} else if (selection == 2 && cm.getPlayerStat("ADMIN") == 1) {
	    var avail = "";
	    for (var i = -1; i > -199; i--) {
		if (cm.getInventory(-1).getItem(i) != null) {
		    avail += "#L" + Math.abs(i) + "##t" + cm.getInventory(-1).getItem(i).getItemId() + "##l\r\n";
		}
		slot.push(i);
	    }
	    cm.sendSimple("請選擇你要編輯的裝備?\r\n#b" + avail);
	} else if (selection == 3 && cm.getPlayerStat("ADMIN") == 1) {
		var eek = cm.getAllPotentialInfo();
		var avail = "#L0#搜尋potential 物品#l\r\n";
		for (var ii = 0; ii < eek.size(); ii++) {
			avail += "#L" + eek.get(ii) + "#Potential ID " + eek.get(ii) + "#l\r\n";
		}
		cm.sendSimple("你要修改哪個數值?\r\n#b"+ avail);
		status = 9;
	} else if (selection == 4) {
		cm.getPlayer().resetAPSP();
		cm.dispose();
	} else if (selection == 7) {
	    if (cm.getPlayerStat("GM") == 1) {
		cm.getPlayer().resetStats(4, 4, 4, 4);
		cm.sendOk("我已清除您的能力值");
	    }
	    cm.dispose();

	//等級提升
	} else if (selection == 9) {
		if (cm.getPlayerStat("GM") == 1) {
                cm.getPlayer().levelUp();
		//cm.gainExp(2147483647);
		//cm.gainExp(10000);
	    }
	    cm.dispose();
	//Gm廣播
	} else if (selection == 10) {
		if (cm.getPlayerStat("GM") == 1) {
		//********************黃字公告(跑馬燈)********************
		//↓系統↓ 
		//
		//cm.YellowMessage("『系統公告』:為更新伺服器部分指令，稍後3:30分將進行重啟動作.");
    	//cm.YellowMessage("『遊戲公告』:稍後4點45分將暫時關閉伺服器，預計15分鐘內會再重開，如有不便請見諒，BlueLineMS管理團隊");
		//cm.YellowMessage("『補償公告』: 補償已發放，要領取時請至自由市場，找頭上寫著『無可奈何啊』的NPC兌換.");
		//cm.YellowMessage("『活動公告』: 獎勵已發放，要兌換時請至自由市場，找頭上寫著『無可奈何啊』的NPC兌換.");	
		//cm.YellowMessage("『再次提醒』:若拾獲數字異常的裝備請即時通報遊戲管理員，若未回報並私自留下使用遭查獲時一律視為外掛論.");
		//cm.YellowMessage("『停權公告』:玩家『』，");
		//
		//↓一般↓
		//
		//cm.YellowMessage("今日伺服器關機時間為晚上12點整. Today the server closed time at PM 11:00.");
	    //cm.YellowMessage("");
		//
		//********************藍字公告(世界頻道)********************
		//↓系統↓ 
		//
		//cm.worldMessage("『系統公告』:為更新伺服器部分指令，稍後3:30分將進行重啟動作.");
		//cm.worldMessage("『遊戲公告』:稍後4點45分將暫時關閉伺服器，預計15分鐘內會再重開，如有不便請見諒，BlueLineMS管理團隊");
		//cm.worldMessage("『補償公告』: 補償已發放，要領取時請至自由市場，找頭上寫著『無可奈何啊』的NPC兌換.");	
		//cm.worldMessage("『活動公告』: 獎勵已發放，要兌換時請至自由市場，找頭上寫著『無可奈何啊』的NPC兌換.");	
		//cm.worldMessage("『再次提醒』:若拾獲數字異常的裝備請即時通報遊戲管理員，若未回報並私自留下使用遭查獲時一律視為外掛論");
        //cm.worldMessage("『停權公告』:玩家『』，");
		//
		//↓一般↓
		//
		//cm.worldMessage("1.全面降低幣值掉落機率.");
        //cm.worldMessage("2.打怪掉點做最多5點.");
        //cm.worldMessage("3.遊戲點數兌換調整為5萬:1點.");
		//cm.worldMessage("好消息,現在RC群已加開自由聊天室囉，想開MIC的玩家可以來喔.");
		cm.worldMessage("BlueLineMS-LS:要參加的快登記喔");
		//cm.worldMessage("");
		//
		cm.dispose();
		}
	} else if (selection == 11) {
		if (cm.getPlayerStat("GM") == 1) {
		cm.playMusic("BgmUI/Title");
		cm.dispose();
		}	
	} else if (selection == 12) {
		if (cm.getPlayerStat("GM") == 1) {
		cm.dispose();
		cm.openNpc(9000065, 3)
		//cm.gainItem(1002140,1); //GM裝
	    //cm.gainItem(1042003,1); //GM裝
		//cm.gainItem(1062007,1); //GM裝
		//cm.gainItem(1072010,1); //GM裝
		//cm.gainItem(13220,1); //GM裝
		}		
  //以下皆為轉職NPC↓ 
    } else if (selection == 50){
			cm.sendSimple("#l#d#嗨,管理員專用轉職NPC,選一個你想要的職業吧!\r\n#L5024#新手\r\n#L501#英雄\r\n#L502#聖騎士\r\n#L504#大魔島士(火,毒)\r\n#L506#主教\r\n#L507#神射手\r\n#L509#夜使者\r\n#L5010#暗影神偷\r\n#L5012#拳霸\r\n#L5013#槍神\r\n#L5014#海盜\r\n#L5015#貴族\r\n#L5020#煉獄巫師\r\n#L5021#狂爆裂人\r\n#L5022#機甲戰神\r\n#L5016#傳說\r\n#L5023#狂郎勇士\r\n#L5011#影舞者\r\n#L5017#龍魔島士\r\n#L5018#精靈遊俠\r\n#L5019#惡魔殺手\r\n#L5025#幻影配特\r\n#L5026#倉龍俠客\r\n#L5027#系統管理員");
	} else if (selection == 5) {
		cm.clearSkills();
		cm.dispose();
	} else if (selection == 8) {
		cm.openNpc(2000);
		cm.dispose();
	//	return;
	} else if (selection == 6) {
		//cm.maxSkillsByJob();
                 cm.getPlayer().maxSkillsByJobN();
		cm.dispose();
	} else {
		cm.dispose();
	}
	} else if (status == 51){
	 } else if (selection == 501) { 
        cm.changeJob (112);
        return;	
      } else if (selection == 502) { 
        cm.changeJob (122);
		return;
      } else if (selection == 503) { 
        cm.changeJob (132);
		return;
      } else if (selection == 504) { 
        cm.changeJob (212);
		return;
      } else if (selection == 505) { 
        cm.changeJob (222);
		return;
      } else if (selection == 506) { 
        cm.changeJob (232); 
		cm.dispose();
      } else if (selection == 507) { 
        cm.changeJob (312); 
		cm.dispose();
      } else if (selection == 508) { 
        cm.changeJob (322); 
		cm.dispose();
      } else if (selection == 509) { 
        cm.changeJob (412); 
		cm.dispose();
      } else if (selection == 5010) { 
        cm.changeJob (422); 
		cm.dispose();
      } else if (selection == 5011) { 
        cm.changeJob (434); 
		cm.dispose();
      } else if (selection == 5012) { 
        cm.changeJob (512); 
		cm.dispose();
      } else if (selection == 5013) { 
        cm.changeJob (522); 
		cm.dispose();
      } else if (selection == 5014) { 
        cm.changeJob (532); 
		cm.dispose();
      } else if (selection == 5015) { 
        cm.changeJob (1000); 
		cm.dispose();
      } else if (selection == 5016) { 
        cm.changeJob (2000); 
		cm.dispose();
      } else if (selection == 5017) { 
        cm.changeJob (2218); 
		cm.dispose();
      } else if (selection == 5018) { 
        cm.changeJob (2312); 
		cm.dispose();
      } else if (selection == 5019) { 
        cm.changeJob (3112); 
		cm.dispose();
      } else if (selection == 5020) { 
        cm.changeJob (3212); 
		cm.dispose();
      } else if (selection == 5021) { 
        cm.changeJob (3312); 
		cm.dispose();
      } else if (selection == 5022) { 
        cm.changeJob (3512); 
		cm.dispose();
      } else if (selection == 5023) { 
        cm.changeJob (2112); 
		cm.dispose();
      } else if (selection == 5024) { 
        cm.changeJob (0); 
		cm.dispose();
      } else if (selection == 5025) { 
        cm.changeJob (2003); 
		cm.dispose();
      //} else if (selection == 5026) { 
        //cm.changeJob (572); 
		//cm.dispose();
      } else if (selection == 5027) { 
        cm.dispose();
		cm.changeJob (910); 
		return;
		
//以上皆為轉職NPC↑		
    } else if (status == 2 && cm.getPlayerStat("ADMIN") == 1) {
	selected = selection - 1;
	var text = "";
	for (var i = 0; i < stats.length; i++) {
	    text += "#L" + i + "#" + stats[i] + "#l\r\n";
	}
	cm.sendSimple("你決定修改你的#b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k.\r\n選一項你想修改的能力吧! \r\n#b" + text);
	} else if (status == 3 && cm.getPlayerStat("ADMIN") == 1) {
	statsSel = selection;
	if (selection == 24) {
		cm.sendGetText("你想讓你的#b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k成為誰的" + stats[statsSel] + "呢?");
	} else {
		cm.sendGetNumber("你想要為你的#b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k修改為多少" + stats[statsSel] + "呢?", 0, 0, 60004);
	}
    } else if (status == 4 && cm.getPlayerStat("ADMIN") == 1) {
	cm.changeStat(slot[selected], statsSel, selection);
	cm.sendOk("Your #b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k's " + stats[statsSel] + " has been set to " + selection + ".");
	cm.dispose();
	} else if (status == 10 && cm.getPlayerStat("ADMIN") == 1) {
		if (selection == 0) {
			cm.sendGetText("你要搜尋什麼 (e.g. STR %)");
			return;
		}
		cm.sendSimple("#L3#" + cm.getPotentialInfo(selection) + "#l");
		status = 0;
	} else if (status == 11 && cm.getPlayerStat("ADMIN") == 1) {
		var eek = cm.getAllPotentialInfoSearch(cm.getText());
		for (var ii = 0; ii < eek.size(); ii++) {
			avail += "#L" + eek.get(ii) + "#Potential ID " + eek.get(ii) + "#l\r\n";
		}
		cm.sendSimple("What would you like to learn about?\r\n#b"+ avail);
		status = 9;
	} else {
		cm.dispose();
    }
}
