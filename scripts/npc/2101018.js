var status = -1;
function start() {
    if((cm.getPlayer().getLevel() < 19 || cm.getPlayer().getLevel() > 30) && !cm.getPlayer().isGM()){
        cm.sendNext("你如果要參加競技場，你的等級必須在20級~29級。");
        cm.dispose();
        return;
    }
    action(1,0,0);
}

function action(mode, type, selection){
    status++;
    if (status == 4){
        cm.saveLocation("ARIANT");
        cm.warp(980010000, 3);
        cm.dispose();
    }
    if(mode != 1){
        if(mode == 0 && type == 0)
            status -= 2;
        else{
            cm.dispose();
            return;
        }
    }
    if (status == 0)//
        cm.sendNext("我在冒險島中為了偉大的冒險家籌劃了一個大活動, 它被稱為 #b阿里安特競技場挑戰#k.");
    else if (status == 1)
        cm.sendNextPrev("阿里安特競技場挑戰賽是一個與怪物戰鬥技能攻擊別人. 在這場比賽中，你的目標不是要獵殺怪物; 相反，你需要#beliminate一定量的HP從怪物，其次是吸收它有寶石#k. #b結束了與大多數珠寶的戰鬥機將贏得競爭.#k");
    else if (status == 2)
        cm.sendSimple("如果你是一個堅強而勇敢的戰士#bPerion#k, 舞蹈與魔鬼的訓練，然後你參與到競技場挑戰感興趣嗎？?!\r\n#b#L0# 我很願意參加這個偉大的比賽.#l");
    else if (status == 3)
        cm.sendNext("好吧，現在我要派你去戰場。我想看到你的勝利");
}