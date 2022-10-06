load('nashorn:mozilla_compat.js');
importPackage(Packages.tools.packet);

var status = 0;

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    mode == 1 ? status++ : status--;
    if (status == 1) {
        cm.sendNextS("(是目前的女帝和騎士們嗎。。。？氣氛不是很好。搭家都擺出相當不悅的表情。也是，以目前的情況來說，　這也是理所當然的。)", 17);
    } else if (status == 2)
        cm.sendNextPrevS("(議員們的氣氛似乎也不太尋常。他們會如何判斷這種情況呢？悄悄走進去吧？)", 17);
    else if (status == 3)
        cm.sendNextS("西格諾斯竟然不是真正的女皇。。。會是真的嗎？", 5, 1402200);
    else if (status == 4)
        cm.sendNextPrevS("你說話太過分了。什麼叫做不是真正的女皇？難道我們在伺候冒牌的女黃嗎？西格諾斯現在還是女皇!", 5, 1402201);
    else if (status == 5)
        cm.sendNextPrevS("雖然不清楚是真是假。。。她的正統性備受質疑是不爭的事實不是嗎？倘若她真的事擁有耶雷佛寶物的人。。。", 5, 1402203);
    else if (status == 6)
        cm.sendNextPrevS("艾莉亞先皇留下的寶物。。。那項紀錄相當確時。.", 5, 1402202);
    else if (status == 7)
        cm.sendNextPrevS("雖然無法背叛一值以來率領耶雷佛的西格諾斯。。。對於真正女皇的血統視若無睹也並非正確之舉。。。真是令人煩悶阿。.", 5, 1402200);
    else if (status == 8)
        cm.sendNextPrevS("呼。。。很困難。若是那項寶物能證明真正的女皇，真正女皇血統是西格諾斯以外的人的話。。。我們該怎麼做呢？", 5, 1402203);
    else if (status == 9)
        cm.sendNextPrevS("楓之谷世界好不容易終於誕生了結合為一個聯盟。。。那些全都是相信西格諾斯而加入聯盟的人。除了西格諾斯以外的人成為女皇的話，聯盟也會受到動搖的.", 5, 1402202);
    else if (status == 10)
        cm.sendNextPrevS("光靠我們自己討論，能夠找出答案嗎？主張自己擁有真正血統的那個人是神麼樣的人呢。。。當務之急是先確認這一點。", 5, 1402201);
    else if (status == 11)
        cm.sendNextPrevS("噓。。。好像終於到了。", 5, 1402201);
    else if (status == 12)
        cm.sendNextS("(。。。終於，這一點也不有趣的戲劇編劇者登場了。)", 17);
    else if (status == 13) {
        cm.dispose();
        cm.showHilla();
    }
}