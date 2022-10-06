load('nashorn:mozilla_compat.js');
importPackage(Packages.tools.packet);

var status = 12;

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    mode == 1 ? status++ : status--;
    if (status == 13) {
        cm.sendNextS("看來聚集了相當多人呢。這也意味著大家都有仔細聽我說。.", 5, 1402400);
    } else if (status == 14)
        cm.sendNextPrevS("...", 5, 1402100);
    else if (status == 15)
        cm.sendNextPrevS("我們只是為了證明那番話是個謊言才會聚集在這裡的。沒有其他意思。", 5, 1402101);
    else if (status == 16)
        cm.sendNextPrevS("阿阿，當然...我也不認為你們會一次就相信我所說的話。但是，真相中就會大白的。從現在開始，我將要說被耶雷佛許多人遺忘的老故事。黑魔法師氣圖之配風之谷世界的當時的那個女皇。。。關於艾莉亞的事情。。。", 5, 1402400);
    else if (status == 17)
        cm.sendNextPrevS("(艾莉亞...)", 17);
    else if (status == 18)
        cm.sendNextPrevS("相信大家都知道了，當時耶雷佛有許多東西都被黑魔法師破壞，幾乎沒有完整保存下來的紀錄。不過，卻有一個廣為人知的。當時的女皇艾麗亞擁有一個叫做天之星的寶物。", 5, 1402400);
    else if (status == 19)
        cm.sendNextPrevS("艾李亞女皇擁有的耶雷佛的寶物，天之星。。那是楓之谷世界游女皇代代相傳的神祕寶物。具有保護女皇，強化女皇能力的力量！", 5, 1402400);
    else if (status == 20)
        cm.sendNextPrevS("關於天之星的紀錄確實存在著，不過，那個寶石具備何種力量，根本就沒有人知道。.", 5, 1402104);
    else if (status == 21)
        cm.sendNextPrevS("那是當然的。畢竟西格諾斯並為擁有天之星。但我卻不一樣了。因為天之星傳給了我。", 5, 1402400);
    else if (status == 22)
        cm.sendNextPrevS("耶雷佛受到黑魔法師和軍團長們的破壞後，天之星便消失了。。。這是就各為知道的一切。但是，女皇的信物天之星有可能這麼容易消失嗎？如此重要的物品，先祖們會坐視讓它就這麼消失不管嗎？", 5, 1402400);
    else if (status == 23)
        cm.sendNextPrevS("當然不可能。天之星倍暗地悄悄地帶到別的地方去了。為了不背黑魔法師和她的手下攻擊，於是便保守了此一祕密。。。交給擁有真正女皇血統的人。。。就這樣悄悄地傳了數百年。", 5, 1402400);
    else if (status == 24)
        cm.sendNextPrevS("妳想樣主張就是妳嗎?", 5, 1402105);
    else if (status == 25)
        cm.sendNextPrevS("這是不爭的事實.", 5, 1402400);
    else if (status == 26)
        cm.sendNextPrevS("但是。。。妳要如何證明你擁有的天之星是真的呢？妳擁有的可能也是假的！.", 5, 1402103);
    else if (status == 27)
        cm.sendNextPrevS("說的好！天之星只是名聲廣為流傳而以，但是卻幾乎沒有人見過，是相當神秘的寶石。目前在楓之谷世界知道天之星長怎麼樣子的人。。。就只有看過天之星圖畫的各位而已。", 5, 1402400);
    else if (status == 28)
        cm.sendNextPrevS("若是我擁有的天之星和各位所知道的是吻合的話，那達暗就相當簡單了吧?", 5, 1402400);
    else if (status == 29)
        cm.sendNextPrevS("喂，你到底項說什麼呢？寶石不就長那個樣子嗎？也不能斷定其他地區並沒有關於天之星的記錄呀？", 5, 1402106);
    else if (status == 30)
        cm.sendNextPrevS("又不是數百年前的人，不對，就算是數百年前的人，也幾乎沒有人見過天之星。。。老實說，可能性實在太低了？", 5, 1402400);
    else if (status == 31)
        cm.sendNextPrevS("還有其他證據。西格諾斯那脆弱無比的身體也是如此。倘若西格諾斯擁有真正的女皇血統，就不會被神獸的力量壓制住。。。就是因為她並未擁有真正女的血統，身體才會如此虛弱。西格諾斯，你應該也很清楚吧？你之所以會這麼虛弱", 5, 1402400);
    else if (status == 32)
        cm.sendNextPrevS("放肆無禮!", 5, 1402102);
    else if (status == 33)
        cm.sendNextPrevS("哦... 若有冒犯之處，還真是抱歉！不過，我也沒說錯吧?", 5, 1402400);
    else if (status == 34)
        cm.sendNextPrevS("我絕對不是要各位立刻相信我所說的話。不過，個為若是對我所說的話有些許的信任，至少也要深入討論一下巴？這是妳的角色不是嗎，西格諾斯？", 5, 1402400);
    else if (status == 35)
        cm.sendNextPrevS("...沒錯。我之所以能夠生到現今的地位。。。並非因為我很特別。而是天生的命運。", 5, 1402100);
    else if (status == 36)
        cm.sendNextPrevS("若是有人懷疑我是否有正統性。。。這也是理所當然的。弱勢需要的話。。。就儘管提出疑問吧！", 5, 1402100);
    else if (status == 37)
        cm.sendNextPrevS("西格諾斯!", 5, 1402101);
    else if (status == 38)
        cm.sendNextPrevS("只因為是對的，就不斷地將他人捲入戰爭當中。儘管如此，我也只是一昧的在這裡讓大家保護而以。只因為我是女皇的關係，沒有其他理由。但是，倘若我沒有成為女皇的資格...", 5, 1402100);
    else if (status == 39)
        cm.sendNextPrevS("能夠號招楓之谷世界的眾多人的資格。。。也就不會有.", 5, 1402100);
    else if (status == 40)
        cm.sendNextPrevS("(聲音聽起來有所動搖，但是眼神卻相當堅定。看起來很脆弱，但是心智堅定！從絕對難以接收的測試的表情，以及一副要戰鬥的騎士們的態度來看，倒是挺有人脈的呢？果然艾利亞的。。。)", 17);
    else if (status == 41)
        cm.sendNextPrevS("好了，既然如此，妳不需要一一解釋了。我就在此證明誰才擁有真正女皇的血統。天之星會在真正主人的手中發出光芒。耶雷佛的女帝西格諾斯。。。倘若妳擁有真正女皇的血統，舊試這拿起這個天之星。", 5, 1402400);
    else if (status == 42)
        cm.sendNextPrevS("假設你是真正楓之谷世界的女皇，天之星也一定會發光。", 5, 1402400);
    else if (status == 43) {
        cm.dispose();
        cm.showSkaia();
    }
}