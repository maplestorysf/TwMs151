package server.events;

import client.MapleCharacter;
import java.util.LinkedList;
import java.util.List;
import server.Timer.EventTimer;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class MapleCoconut extends MapleEvent {

    private List<MapleCoconuts> coconuts = new LinkedList<>();
    private int[] coconutscore = new int[2];
    private int countBombing = 0;
    private int countFalling = 0;
    private int countStopped = 0;

    public MapleCoconut(final int world, final int channel, final MapleEventType type) {
        super(world, channel, type);
    }

    @Override
    public void finished(MapleCharacter chr) { //do nothing.
    }

    @Override
    public void reset() {
        super.reset();
        resetCoconutScore();
    }

    @Override
    public void unreset() {
        super.unreset();
        resetCoconutScore();
        setHittable(false);
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        super.onMapLoad(chr);
        chr.getClient().getSession().write(CField.coconutScore(getCoconutScore()));
    }

    public MapleCoconuts getCoconut(int id) {
        if (id >= coconuts.size()) {
            return null;
        }
        return coconuts.get(id);
    }

    public List<MapleCoconuts> getAllCoconuts() {
        return coconuts;
    }

    public void setHittable(boolean hittable) {
        coconuts.forEach((nut) -> {
            nut.setHittable(hittable);
        });
    }

    public int getBombings() {
        return countBombing;
    }

    public void bombCoconut() {
        countBombing--;
    }

    public int getFalling() {
        return countFalling;
    }

    public void fallCoconut() {
        countFalling--;
    }

    public int getStopped() {
        return countStopped;
    }

    public void stopCoconut() {
        countStopped--;
    }

    public int[] getCoconutScore() { // coconut event
        return coconutscore;
    }

    public int getMapleScore() { // Team Maple, coconut event
        return coconutscore[0];
    }

    public int getStoryScore() { // Team Story, coconut event
        return coconutscore[1];
    }

    public void addMapleScore() { // Team Maple, coconut event
        coconutscore[0]++;
    }

    public void addStoryScore() { // Team Story, coconut event
        coconutscore[1]++;
    }

    public void resetCoconutScore() {
        coconutscore[0] = 0;
        coconutscore[1] = 0;
        countBombing = 80;
        countFalling = 401;
        countStopped = 20;
        coconuts.clear();
        for (int i = 0; i < 506; i++) {
            coconuts.add(new MapleCoconuts());
        }
    }

    @Override
    public void startEvent() {
        reset();
        setHittable(true);
        getMap(0).broadcastMessage(CWvsContext.serverNotice(5, "活動已經開始!!"));
        getMap(0).broadcastMessage(CField.hitCoconut(true, 0, 0));
        getMap(0).broadcastMessage(CField.getClock(300));

        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (getMapleScore() == getStoryScore()) {
                    bonusTime();
                } else {
                    getMap(0).getCharactersThreadsafe().forEach((chr) -> {
                        if (chr.getTeam() == (getMapleScore() > getStoryScore() ? 0 : 1)) {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/victory"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Victory"));
                        } else {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/lose"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Failed"));
                        }
                    });
                    warpOut();
                }
            }
        }, 300000);
    }

    public void bonusTime() {
        getMap(0).broadcastMessage(CField.getClock(60));
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (getMapleScore() == getStoryScore()) {
                    getMap(0).getCharactersThreadsafe().stream().map((chr) -> {
                        chr.getClient().getSession().write(CField.showEffect("event/coconut/lose"));
                        return chr;
                    }).forEachOrdered((chr) -> {
                        chr.getClient().getSession().write(CField.playSound("Coconut/Failed"));
                    });
                    warpOut();
                } else {
                    getMap(0).getCharactersThreadsafe().forEach((chr) -> {
                        if (chr.getTeam() == (getMapleScore() > getStoryScore() ? 0 : 1)) {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/victory"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Victory"));
                        } else {
                            chr.getClient().getSession().write(CField.showEffect("event/coconut/lose"));
                            chr.getClient().getSession().write(CField.playSound("Coconut/Failed"));
                        }
                    });
                    warpOut();
                }
            }
        }, 60000);

    }

    public void warpOut() {
        setHittable(false);
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                getMap(0).getCharactersThreadsafe().stream().map((chr) -> {
                    if ((getMapleScore() > getStoryScore() && chr.getTeam() == 0) || (getStoryScore() > getMapleScore() && chr.getTeam() == 1)) {
                        givePrize(chr);
                    }
                    return chr;
                }).forEachOrdered((chr) -> {
                    warpBack(chr);
                });
                unreset();
            }
        }, 10000);
    }

    public static class MapleCoconuts {

        private int hits = 0;
        private boolean hittable = false;
        private boolean stopped = false;
        private long hittime = System.currentTimeMillis();

        public void hit() {
            this.hittime = System.currentTimeMillis() + 1000; // test
            hits++;
        }

        public int getHits() {
            return hits;
        }

        public void resetHits() {
            hits = 0;
        }

        public boolean isHittable() {
            return hittable;
        }

        public void setHittable(boolean hittable) {
            this.hittable = hittable;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void setStopped(boolean stopped) {
            this.stopped = stopped;
        }

        public long getHitTime() {
            return hittime;
        }
    }
}
