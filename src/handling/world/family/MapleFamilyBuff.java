package handling.world.family;

import client.MapleBuffStat;
import client.MapleCharacter;
import java.util.EnumMap;
import java.util.concurrent.ScheduledFuture;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer.BuffTimer;
import tools.packet.CWvsContext.BuffPacket;

public enum MapleFamilyBuff {
    Teleport("Family Reunion", "[Target] Me\n[Effect] Teleport directly to the Family member of your choice.", 0, 0, 0, 300, 190000),
    Summon("Summon Family", "[Target] 1 Family member\n[Effect] Summon a Family member of choice to the map you're in.", 1, 0, 0, 500, 190001),
    Drop_12_15("My Drop Rate 1.2x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 15, 120, 700, 190002),
    EXP_12_15("My EXP Rate 1.2x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 15, 120, 800, 190003),
    Drop_12_30("My Drop Rate 1.2x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 120, 1000, 190004),
    EXP_12_30("My EXP Rate 1.2x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 30, 120, 1200, 190005),
    Drop_15_15("My Drop Rate 1.5x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 15, 150, 1500, 190009),
    Drop_15_30("My Drop Rate 1.5x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 150, 2000, 190010),
    Bonding("Family Bonding (30min)", "[Target] At least 6 Family members online that are below me in the Pedigree\n[Time] 30 min.\n[Effect] Monster drop rate and EXP earned will be increased #c1.5x#. \n* If the EXP event is in progress, this will be nullified.", 4, 30, 150, 3000, 190006),
    Drop_Party_12("My Party Drop Rate 1.2x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 120, 4000, 190007),
    EXP_Party("My Party EXP Rate 1.2x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 30, 120, 5000, 190008),
    Drop_Party_15("My Party Drop Rate 1.5x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 150, 7000, 190011);
    // 0=tele, 1=summ, 2=drop, 3=exp, 4=both

    public String name, desc;
    public int rep, type, questID, duration, effect;
    public EnumMap<MapleBuffStat, Integer> effects;

    private MapleFamilyBuff(String name, String desc, int type, int duration, int effect, int rep, int questID) {
        this.name = name;
        this.desc = desc;
        this.rep = rep;
        this.type = type;
        this.questID = questID;
        this.duration = duration;
        this.effect = effect;
        setEffects();
    }

    public int getEffectId() {
        switch (type) {
            case 2: //drop
                return 2022694;
            case 3: //exp
                return 2450018;
        }
        return 2022332; //custom
    }

    public final void setEffects() {
        //custom
        this.effects = new EnumMap<>(MapleBuffStat.class);
        switch (type) {
            case 2: //drop
                effects.put(MapleBuffStat.DROP_RATE, effect);
                effects.put(MapleBuffStat.MESO_RATE, effect);
                break;
            case 3: //exp
                effects.put(MapleBuffStat.EXPRATE, effect);
                break;
            case 4: //both
                effects.put(MapleBuffStat.EXPRATE, effect);
                effects.put(MapleBuffStat.DROP_RATE, effect);
                effects.put(MapleBuffStat.MESO_RATE, effect);
                break;
        }
    }

    public void applyTo(MapleCharacter chr) {
        chr.getClient().getSession().write(BuffPacket.giveBuff(-getEffectId(), duration * 60000, effects, null, chr));
        final MapleStatEffect eff = MapleItemInformationProvider.getInstance().getItemEffect(getEffectId());
        chr.cancelEffect(eff, true, -1, effects);
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(chr, eff, starttime, effects);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, duration * 60000);
        chr.registerEffect(eff, starttime, schedule, effects, false, duration, chr.getId());
    }
}
