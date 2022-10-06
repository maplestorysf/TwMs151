var status = -1;

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0)
        cm.sendNextS("終於到了決戰的時間.", 17);
    else if (status == 1)
        cm.sendNextPrevS("沒想到還真叫人緊張的呢？是因為太久没進行活動的關係嗎？雖然也不是很沒有自信.", 17);
    else if (status == 2)
        cm.sendNextPrevS("應該已經準備好了吧？若是再繼續拖拖拉拉導致錯失良機的話，一定會顏面掃地的，雖然有點敢，不過快點行動吧！", 17);
    else if (status == 3) {
        cm.introEnableUI(0);
        cm.dispose();
    }
}