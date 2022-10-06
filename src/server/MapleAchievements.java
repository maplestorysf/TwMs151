package server;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Map.Entry;

public class MapleAchievements {

    private Map<Integer, MapleAchievement> achievements = new LinkedHashMap<Integer, MapleAchievement>();
    private static MapleAchievements instance = new MapleAchievements();

    protected MapleAchievements() {
        achievements.put(1, new MapleAchievement("首次通過傳送門", 10, false));
        achievements.put(2, new MapleAchievement("首次等級達到 30 級", 10, false));
        achievements.put(3, new MapleAchievement("首次等級達到 70 級", 30, false));
        achievements.put(4, new MapleAchievement("首次等級達到 100 級", 80, false));
        achievements.put(5, new MapleAchievement("首次等級達到 200 級", 100, false));
        achievements.put(7, new MapleAchievement("首次人氣達到 50 點", 50, false));
        achievements.put(9, new MapleAchievement("首次穿戴重生裝備", 40, false));
        achievements.put(10, new MapleAchievement("首次穿戴永恆裝備", 50, false));
        achievements.put(11, new MapleAchievement("說喜歡我們的遊戲", 10, false));
        achievements.put(12, new MapleAchievement("首次擊敗了黑道大姐頭", 350, false));
        achievements.put(13, new MapleAchievement("首次擊敗了拉圖斯", 250, false));
        achievements.put(14, new MapleAchievement("首次擊敗了海怒斯", 250, false));
        achievements.put(15, new MapleAchievement("首次擊敗了炎魔", 500, false));
        achievements.put(16, new MapleAchievement("首次擊敗了龍王", 1500, false));
        achievements.put(17, new MapleAchievement("首次擊敗了皮卡丘", 3000, false));
        achievements.put(18, new MapleAchievement("首次殺死 1 個 BOSS", 1000, false));
        achievements.put(19, new MapleAchievement("首次完成活動任務 'OX Quiz'", 50, false));
        achievements.put(20, new MapleAchievement("首次完成活動任務 'MapleFitness'", 50, false));
        achievements.put(21, new MapleAchievement("首次完成活動任務 'Ola Ola'", 50, false));
        achievements.put(22, new MapleAchievement("首次擊敗了 BossQuest 地獄難度", 500));
        achievements.put(23, new MapleAchievement("首次擊敗了混沌炎魔", 10000, false));
        achievements.put(24, new MapleAchievement("首次擊敗了混沌龍王", 20000, false));
        achievements.put(25, new MapleAchievement("首次完成活動任務 'Survival Challenge'", 50, false));

        achievements.put(31, new MapleAchievement("首次擁有 1 000 000 金幣", 100, false));
        achievements.put(32, new MapleAchievement("首次擁有 10 000 000 金幣", 200, false));
        achievements.put(33, new MapleAchievement("首次擁有 100 000 000 金幣", 300, false));
        achievements.put(34, new MapleAchievement("首次擁有 1 000 000 000 金幣", 400, false));
        achievements.put(35, new MapleAchievement("首次創了公會", 25, false));
        achievements.put(36, new MapleAchievement("首次組了家族", 25, false));
        achievements.put(37, new MapleAchievement("首次完成 1 個組隊任務", 40, false));
        //       achievements.put(38, new MapleAchievement("首次擊敗了凡雷恩", 25000, false));
        //       achievements.put(39, new MapleAchievement("首次擊敗了黑暗女皇", 100000, false));
        achievements.put(40, new MapleAchievement("首次裝備超過 130 等的武器", 10, false));
        achievements.put(41, new MapleAchievement("首次裝備超過 140 等的武器", 15, false));

    }

    public static MapleAchievements getInstance() {
        return instance;
    }

    public MapleAchievement getById(int id) {
        return achievements.get(id);
    }

    public Integer getByMapleAchievement(MapleAchievement ma) {
        for (Entry<Integer, MapleAchievement> achievement : this.achievements.entrySet()) {
            if (achievement.getValue() == ma) {
                return achievement.getKey();
            }
        }
        return null;
    }
}
