/**
 測服專用NPC
 LS製作
**/

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
        cm.sendSimple("請依照你的職業做選擇!#k\r\n#k#L151#劍士#L152#法師#L153#弓箭手#L154#盜賊#L155#海盜");
        } else if (status == 1) {
		 if (selection == 151) { 
        cm.gainItem(1302152,1);
		cm.gainItem(1312065,1);
		cm.gainItem(1322096,1);
		cm.gainItem(1402095,1);
		cm.gainItem(1412065,1);
		cm.gainItem(1422066,1);
		cm.gainItem(1432086,1);
		cm.gainItem(1442116,1);
		cm.gainItem(1003172,1);
		cm.gainItem(1052314,1);
		cm.gainItem(1072485,1);
		cm.gainItem(1082295,1);
		cm.gainItem(1102275,1);
		cm.getPlayer().gainMeso(2147483647-cm.getPlayer().getMeso(),true);
		cm.gainItem(2000005,500);
		} else if (selection == 152) { 
        cm.gainItem(1372084,1);
		cm.gainItem(1382104,1);
		cm.gainItem(1003173,1);
		cm.gainItem(1052315,1);
		cm.gainItem(1072486,1);
		cm.gainItem(1082296,1);
		cm.gainItem(1102276,1);
		cm.getPlayer().gainMeso(2147483647-cm.getPlayer().getMeso(),true);
		cm.gainItem(2000005,500);
		} else if (selection == 153) { 
        cm.gainItem(1452111,1);
		cm.gainItem(1462099,1);
		cm.gainItem(1522018,1);
		cm.gainItem(1003174,1);
		cm.gainItem(1052316,1);
		cm.gainItem(1072487,1);
		cm.gainItem(1082297,1);
		cm.gainItem(1102277,1);
		cm.getPlayer().gainMeso(2147483647-cm.getPlayer().getMeso(),true);
		cm.gainItem(2000005,500);
		} else if (selection == 154) { 
        cm.gainItem(1332130,1);
		cm.gainItem(1342036,1);
		cm.gainItem(1472122,1);
		cm.gainItem(1362019,1);
		cm.gainItem(1003175,1);
		cm.gainItem(1052317,1);
		cm.gainItem(1072488,1);
		cm.gainItem(1082298,1);
		cm.gainItem(1102278,1);
		cm.getPlayer().gainMeso(2147483647-cm.getPlayer().getMeso(),true);
		cm.gainItem(2000005,500);
		} else if (selection == 155) { 
        cm.gainItem(1482084,1);
		cm.gainItem(1492085,1);
		cm.gainItem(1532018,1);
		cm.gainItem(1003176,1);
		cm.gainItem(1052318,1);
		cm.gainItem(1072489,1);
		cm.gainItem(1082299,1);
		cm.gainItem(1102279,1);
		cm.getPlayer().gainMeso(2147483647-cm.getPlayer().getMeso(),true);
		cm.gainItem(2000005,500);

		return;
		}
		cm.dispose();
        }
		}
		}
