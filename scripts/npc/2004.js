
var status = -1;

function start() {
cm.sendDirectionStatus(1,30);
cm.dispose();
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if(mode == 0 && status == 0){
            cm.sendYesNo("Do you really want to start your journey right away?");
            return;
        }else if(mode == 0 && status == 1 && type == 0){
            status -= 2;
            start();
            return;
        }else if(mode == 0 && status == 1 && type == 1)
            cm.sendNext("Please talk to me again when you finally made your decision.");
        cm.dispose();
        return;
    }
    if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3){
        if(status == 0){
            cm.sendNext("Ok then, I will let you enter the training camp. Please follow your instructor's lead.");
        }else if(status == 1 && type == 1){
            cm.sendNext("It seems like you want to start your journey without taking the training program. Then, I will let you move on to the training ground. Be careful~");
        }else if(status == 1){
            cm.warp(1);
            dispose();
        }else{
            cm.warp(40000);
            dispose();
        }
    }else
    if(status == 0)
        cm.sendPrev("Once you train hard enough, you will be entitled to occupy a job. You can become a Bowman in Henesys, a Magician in Ellinia, a Warrior in Perion, and a Thief in Kerning City...");
    else
        cm.dispose();
}