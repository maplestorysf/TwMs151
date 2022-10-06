package server;

import client.MapleCharacter;
import constants.GameConstants;
import handling.world.World;
import tools.packet.CWvsContext;

public class MapleAchievement {

    private String name;
    private int reward;
    private boolean notice;

    public MapleAchievement(String name, int reward) {
        this.name = name;
        this.reward = reward;
        this.notice = true;
    }

    public MapleAchievement(String name, int reward, boolean notice) {
        this.name = name;
        this.reward = reward;
        this.notice = notice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public boolean getNotice() {
        return notice;
    }

    public void finishAchievement(MapleCharacter chr) {
        chr.modifyCSPoints(1, reward, false);
        chr.setAchievementFinished(MapleAchievements.getInstance().getByMapleAchievement(this));
        if (notice && !chr.isGM()) {
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[成就系統] 祝賀您 " + chr.getName() + " 因為 " + name + " 得到了 " + (GameConstants.GMS ? (reward / 2) : reward) + " 點 !"));
        } else {
            chr.getClient().getSession().write(CWvsContext.serverNotice(5, "[成就系統] 您獲得了 " + (GameConstants.GMS ? (reward / 2) : reward) + " 點數 因為您 " + name + "."));
        }
    }
}
