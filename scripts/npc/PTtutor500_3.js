load('nashorn:mozilla_compat.js');
importPackage(Packages.tools.packet);

var status = 42;

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    mode == 1 ? status++ : status--;
    if (status == 43) {
        cm.sendNextS("阿...", 5, 1402100);
    } else if (status == 44)
        cm.sendNextPrevS("果然完全沒有發光。這樣還不足以當作證明嗎？", 5, 1402400);
    else if (status == 45)
        cm.sendNextPrevS("...", 5, 1402100);
    else if (status == 46)
        cm.sendNextPrevS("現在下定論還太早了！.", 5, 1402102);
    else if (status == 47)
        cm.sendNextPrevS("沒錯，女皇。老實說，該如何知道那個光芒的真假呢？", 5, 1402106);
    else if (status == 48)
        cm.sendNextPrevS("沒錯！我也會使用散發光芒的魔法!", 5, 1402103);
    else if (status == 49)
        cm.sendNextPrevS("神獸回來後，一切就會真相大白的！西格諾斯。絕對不能相信那個女人的話。.", 5, 1402104);
    else if (status == 50)
        cm.sendNextPrevS("若是你動搖的話，我們騎士也全都會動搖的！振作一點！", 5, 1402105);
    else if (status == 51)
        cm.sendNextPrevS("現在才剛進入組成聯盟，楓之谷世界合而為一的初期而已，西格諾斯。說不定這只是為了動搖我們，讓我們這段時間的信任瓦解的陰謀。千萬不能被一個來路不明的女人所迷惑。.", 5, 1402101);
    else if (status == 52)
        cm.sendNextPrevS("所有...", 5, 1402100);
    else if (status == 53)
        cm.sendNextPrevS("嗯…看來你的騎士們想要否定真相呢。.", 5, 1402400);
    else if (status == 54)
        cm.sendNextPrevS("這段時間以證明的方式在耶雷佛率領騎士，然後領導楓之谷世界的女地…我並非否定妳的辛勞。不過，就是因為你夠英明趁一切還來得及的時候，請做出正確的選擇。!", 5, 1402400);
    else if (status == 55)
        cm.sendNextPrevS("承認誰才是真正的女皇，讓出女皇的位子吧。", 5, 1402400);
    else if (status == 56)
        cm.sendNextPrevS("然後將這項事實告訴聯盟。", 5, 1402400);
    else if (status == 57)
        cm.sendNextPrevS("當然，我並非在催促妳。相信妳一定很混亂，我會給妳時間整理這一切的。若是懷疑我的話，可以繼續相關的調查。", 5, 1402400);
    else if (status == 58)
        cm.sendNextPrevS("但是，最後還是會知道真相的。楓之谷世界真正的女皇是我希拉…", 5, 1402400);
    else if (status == 59)
        cm.sendNextPrevS("（阿普雷德應該已經準備好了吧…輪到我上場了嗎？好，那麼先深呼吸，一、二、三！）", 17);
    else if (status == 60) {
        cm.dispose();
        cm.showPhantomWait();
    }
}