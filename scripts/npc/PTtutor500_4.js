var status = -1;

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    mode == 1 ? status++ : status--;
    if (status == 0)
        cm.sendNextS("還沒到下結論的時候吧?", 17);
    else if (status == 1) {
        cm.dispose();
        cm.showPhantomMovie();
    }
}