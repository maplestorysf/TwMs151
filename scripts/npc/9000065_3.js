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
        cm.sendSimple("您好，我是#e#d測服小幫手#k\r\n#k#L11#等級提升(最高至150等)#L12#自動轉職#L13#職業轉換#L14#該職業技能點滿#L15#領取該職業裝備#L16#名聲加至50");
        } else if (status == 1) {
        if (selection == 11) {
        if (cm.getLevel() > 150){
        cm.sendOk("您已超過150等.");
		cm.dispose();
		return;
        }
		else
		{
        cm.getPlayer().levelUp();
        cm.dispose();
		}
       	} else if (selection == 12) {
		cm.dispose();
		cm.openNpc(2007);
		} else if (selection == 13){
	   cm.dispose();
		cm.openNpc(9000065,1);
		} else if (selection == 14) {
		cm.maxSkillsByJob();
		cm.dispose();
		} else if (selection == 15) {
		cm.dispose();
		cm.openNpc(9000065,2);
		} else if (selection == 16){
		cm.getPlayer().setFame(50);
		cm.dispose();
		}
		}
        }
		}
