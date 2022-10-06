
var status = 0;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)                           						
            status++;
        else
            status--;
        if (status == 0) {
        cm.sendSimple("#l#d#嗨,選一個你想要的職業吧!\r\n#L5024#新手\r\n#L501#英雄\r\n#L502#聖騎士\r\n#L504#大魔島士(火,毒)\r\n#L506#主教\r\n#L507#神射手\r\n#L509#夜使者\r\n#L5010#暗影神偷\r\n#L5012#拳霸\r\n#L5013#槍神\r\n#L5014#海盜\r\n#L5015#貴族\r\n#L5020#煉獄巫師\r\n#L5021#狂爆裂人\r\n#L5022#機甲戰神\r\n#L5016#傳說\r\n#L5023#狂郎勇士\r\n#L5011#影舞者\r\n#L5017#龍魔島士\r\n#L5018#精靈遊俠\r\n#L5019#惡魔殺手\r\n#L5025#幻影配特\r\n#L5026#倉龍俠客\r\n#L5027#系統管理員");
        } else if (status == 1) {
       if (selection == 501) { 
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
		}
        }
		}
		}
