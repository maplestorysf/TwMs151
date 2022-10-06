package tools.packet;

import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.SendPacketOpcode;
import java.awt.Point;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class MobPacket {

    public static byte[] damageMonster(final int oid, final int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        if (damage > Integer.MAX_VALUE || damage < 0) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt(damage);
        }

        return mplew.getPacket();
    }

    public static byte[] damageFriendlyMob(final MapleMonster mob, long damage, final boolean display) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2); //false for when shammos changes map!
        if (damage > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) damage);
        }
        if (mob.getHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) ((mob.getHp() / mob.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        return mplew.getPacket();
    }

    public static byte[] killMonster(final int oid, final int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation); // 0 = dissapear, 1 = fade out, 2+ = special
        if (animation == 4) {
            mplew.writeInt(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] killAswanMonster(final int oid, final int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation); // 0 = dissapear, 1 = fade out, 2+ = special
        if (animation == 4) {
            mplew.writeInt(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] suckMonster(final int oid, final int chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(4);
        mplew.writeInt(chr);

        return mplew.getPacket();
    }

    public static byte[] healMonster(final int oid, final int heal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);
        mplew.writeZeroBytes(100);
        return mplew.getPacket();
    }

    public static byte[] MobToMobDamage(final int oid, final int dmg, final int mobid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOB_TO_MOB_DAMAGE.getValue());
        mplew.writeInt(oid);
        mplew.write(0); // looks like the effect, must be > -2
        mplew.writeInt(dmg);
        mplew.writeInt(mobid);
        mplew.write(1); // ?

        return mplew.getPacket();
    }

    public static byte[] getMobSkillEffect(final int oid, final int skillid, final int cid, final int skilllevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT_MOB.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(skillid); // 3110001, 3210001, 13110009, 2210000
        mplew.writeInt(cid);
        mplew.writeShort(skilllevel);

        return mplew.getPacket();
    }

    public static byte[] getMobCoolEffect(final int oid, final int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ITEM_EFFECT_MOB.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid); // 2022588

        return mplew.getPacket();
    }

    public static byte[] showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static byte[] showCygnusAttack(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CYGNUS_ATTACK.getValue());
        mplew.writeInt(oid); // mob must be 8850011

        return mplew.getPacket();
    }

    public static byte[] showMonsterResist(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MONSTER_RESIST.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writeShort(1); // resist >0
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] showBossHP(final MapleMonster mob) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(mob.getId() == 9400589 ? 9300184 : mob.getId()); //hack: MV cant have boss hp bar
        if (mob.getHp() > Integer.MAX_VALUE) {
            mplew.writeInt((int) (((double) mob.getHp() / mob.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        mplew.write(mob.getStats().getTagColor());
        mplew.write(mob.getStats().getTagBgColor());
        mplew.writeZeroBytes(30);
        return mplew.getPacket();
    }

    public static byte[] showBossHP(final int monsterId, final long currentHp, final long maxHp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId); //has no image
        if (currentHp > Integer.MAX_VALUE) {
            mplew.writeInt((int) (((double) currentHp / maxHp) * Integer.MAX_VALUE));
        } else {
            mplew.writeInt((int) (currentHp <= 0 ? -1 : currentHp));
        }
        if (maxHp > Integer.MAX_VALUE) {
            mplew.writeInt(Integer.MAX_VALUE);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(6);
        mplew.write(5);

        //colour legend: (applies to both colours)
        //1 = red, 2 = dark blue, 3 = light green, 4 = dark green, 5 = black, 6 = light blue, 7 = purple
        mplew.writeZeroBytes(30);
        return mplew.getPacket();
    }

    public static byte[] moveMonster(boolean useskill, int skill, int unk, int oid, Point startPos, List<LifeMovementFragment> moves) {
        return moveMonster(useskill, skill, unk, oid, startPos, moves, null, null);
    }

    public static byte[] moveMonster(boolean useskill, int skill, int unk, int oid, Point startPos, List<LifeMovementFragment> moves, final List<Integer> unk2, final List<Pair<Integer, Integer>> unk3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(useskill ? 1 : 0);
        mplew.write(skill);
        mplew.writeInt(unk);
        mplew.write(unk3 == null ? 0 : unk3.size()); // For each, 2 short
        if (unk3 != null) {
            unk3.stream().map((i) -> {
                mplew.writeShort(i.left);
                return i;
            }).forEachOrdered((i) -> {
                mplew.writeShort(i.right);
            });
        }
        mplew.write(unk2 == null ? 0 : unk2.size()); // For each, 1 short
        if (unk2 != null) {
            unk2.forEach((i) -> {
                mplew.writeShort(i);
            });
        }
        mplew.writePos(startPos);
        mplew.writeShort(8);
        mplew.writeShort(1);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] movePokemon(int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(0); // For each, 2 short
        mplew.write(0); // For each, 1 short
        mplew.writePos(startPos);
        mplew.writeShort(8);
        mplew.writeShort(1);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnMonster(MapleMonster life, int spawnType, int link) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        switch (life.getId()) {
            case 8910000: // 混沌班班
            case 8910100: // 斑斑
            case 9990033:
                mplew.write(0);
                break;
        }
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        mplew.write(life.getCarnivalTeam());

        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (life.getChangedStats() != null ? (int) life.getChangedStats().getHp() : (int) life.getHp()));
        // mplew.writeInt(63000);

        mplew.writeInt(0);
        switch (life.getId()) {
            case 9300498:
            case 9300507:
                mplew.writeInt(-2350);
                mplew.writeInt(-1750);
                mplew.writeInt(150);
                mplew.writeInt(100);
                break;
        }
        mplew.writeInt(0);
        mplew.write(-1);
        return mplew.getPacket();
    }

    public static byte[] spawnAswanMonster(MapleMonster life, int spawnType, int link) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ASWAN_SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(63000);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(-1);
//        System.out.println("spawnAswanMonster: " + mplew.getPacket());
        return mplew.getPacket();
    }

    public static void addMonsterStatus(MaplePacketLittleEndianWriter mplew, MapleMonster life) {
        if (life.getStati().size() <= 1) {
            life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
        }
        mplew.write(life.getChangedStats() != null ? 1 : 0);
        if (life.getChangedStats() != null) {
            mplew.writeInt(life.getChangedStats().hp > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) life.getChangedStats().hp);
            mplew.writeInt(life.getChangedStats().mp);
            mplew.writeInt(life.getChangedStats().exp);
            mplew.writeInt(life.getChangedStats().watk);
            mplew.writeInt(life.getChangedStats().matk);
            mplew.writeInt(life.getChangedStats().PDRate);
            mplew.writeInt(life.getChangedStats().MDRate);
            mplew.writeInt(life.getChangedStats().acc);
            mplew.writeInt(life.getChangedStats().eva);
            mplew.writeInt(life.getChangedStats().pushed);
            mplew.writeInt(life.getChangedStats().level);
        }
        final boolean ignore_imm = life.getStati().containsKey(MonsterStatus.WEAPON_DAMAGE_REFLECT) || life.getStati().containsKey(MonsterStatus.MAGIC_DAMAGE_REFLECT);
        Collection<MonsterStatusEffect> buffs = life.getStati().values();
        getLongMask_NoRef(mplew, buffs, ignore_imm);
        buffs.stream().filter((buff) -> (buff != null && buff.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT && buff.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT && (!ignore_imm || (buff.getStati() != MonsterStatus.WEAPON_IMMUNITY && buff.getStati() != MonsterStatus.MAGIC_IMMUNITY && buff.getStati() != MonsterStatus.DAMAGE_IMMUNITY)))).map((buff) -> {
            if (buff.getStati() != MonsterStatus.SUMMON && buff.getStati() != MonsterStatus.EMPTY_3) {
                if (null == buff.getStati()) {
                    mplew.writeInt(buff.getX());
                } else switch (buff.getStati()) {
                    case EMPTY_1:
                    case EMPTY_2:
                    case EMPTY_3:
                    case EMPTY_4:
                    case EMPTY_5:
                    case EMPTY_6:
                        mplew.writeShort(Integer.valueOf((int) System.currentTimeMillis()).shortValue());
                        mplew.writeShort(0);
                        break;
                    case EMPTY_7:
                        mplew.write(0);
                        break;
                    default:
                        mplew.writeInt(buff.getX());
                        break;
                }
                if (buff.getMobSkill() != null) {
                    mplew.writeShort(buff.getMobSkill().getSkillId());
                    mplew.writeShort(buff.getMobSkill().getSkillLevel());
                } else if (buff.getSkill() > 0) {
                    mplew.writeInt(buff.getSkill());
                }
            }
            return buff;
        }).filter((buff) -> (buff.getStati() != MonsterStatus.EMPTY_7)).map((buff) -> {
            mplew.writeShort(buff.getStati() == MonsterStatus.HYPNOTIZE ? 40 : (buff.getStati().isEmpty() ? 0 : 1));
            return buff;
        }).forEachOrdered((buff) -> {
            if (buff.getStati() == MonsterStatus.EMPTY_1 || buff.getStati() == MonsterStatus.EMPTY_3) {
                mplew.writeShort(0);
            } else if (buff.getStati() == MonsterStatus.EMPTY_4 || buff.getStati() == MonsterStatus.EMPTY_5) {
                mplew.writeInt(0);
            }
        });
    }

    public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(aggro ? 2 : 1);
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);

        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        switch (life.getId()) {
            case 8910000: // 混沌班班
            case 8910100: // 斑斑
            case 9990033:
                mplew.write(0);
                break;
        }
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (life.getChangedStats() != null ? (int) life.getChangedStats().getHp() : (int) life.getHp()));
        // mplew.writeInt(63000);

        mplew.writeInt(0);
        switch (life.getId()) {
            case 9300498:
            case 9300507:
                mplew.writeInt(-2350);
                mplew.writeInt(-1750);
                mplew.writeInt(150);
                mplew.writeInt(100);
                break;
        }
        mplew.writeInt(0);
        mplew.write(-1);

        return mplew.getPacket();
    }

    public static byte[] controlAswanMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ASWAN_SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(aggro ? 2 : 1);
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        addMonsterStatus(mplew, life);

        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(63000);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(-1);

        // System.out.println("controlAswanMonster: " + mplew.getPacket());
        return mplew.getPacket();
    }

    public static byte[] stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] stopControllingAswanMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ASWAN_SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        //System.out.println("stopControllingAswanMonster: " + mplew.getPacket());
        return mplew.getPacket();
    }

    public static byte[] makeAswanMonsterInvisible(MapleMonster life) {
        //System.out.println("accessing makeAswanMonsterInvisible");
        return spawnAswanMonster(life, -4, 0);
    }

    public static byte[] makeMonsterReal(MapleMonster life) {
        return spawnMonster(life, -1, 0);
    }

    public static byte[] makeAswanMonsterReal(MapleMonster life) {
//        System.out.println("accessing makeAswanMonsterReal");
        return spawnAswanMonster(life, -1, 0);
    }

    public static byte[] makeMonsterFake(MapleMonster life) {
        return spawnMonster(life, -4, 0);
    }

    public static byte[] makeMonsterEffect(MapleMonster life, int effect) {
        return spawnMonster(life, effect, 0);
    }

    public static byte[] moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills) {
        return moveMonsterResponse(objectid, moveid, currentMp, useSkills, 0, 0);
    }

    public static byte[] moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static Object movePokemon(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());

        mplew.writeInt(objectid);
        mplew.write(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeInt(currentMp);

        // mplew.write(skillId);
        // mplew.write(skillLevel);
        // mplew.writeInt(0);
        mplew.write(0); // For each, 2 short
        mplew.write(0); // For each, 1 short
        mplew.writePos(startPos);
        mplew.writeShort(8);
        mplew.writeShort(1);
        PacketHelper.serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }

    private static void getLongMask_NoRef(MaplePacketLittleEndianWriter mplew, Collection<MonsterStatusEffect> ss, boolean ignore_imm) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        ss.stream().filter((statup) -> (statup != null && statup.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT && statup.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT && (!ignore_imm || (statup.getStati() != MonsterStatus.WEAPON_IMMUNITY && statup.getStati() != MonsterStatus.MAGIC_IMMUNITY && statup.getStati() != MonsterStatus.DAMAGE_IMMUNITY)))).forEachOrdered((statup) -> {
            mask[statup.getStati().getPosition() - 1] |= statup.getStati().getValue();
        });
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[i - 1]);
        }
    }

    public static byte[] applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, mse);

        mplew.writeInt(x);
        mplew.writeShort(skil.getSkillId());
        mplew.writeShort(skil.getSkillLevel());
        mplew.writeShort(mse.isEmpty() ? 1 : 0);

        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        PacketHelper.writeSingleMask(mplew, ms.getStati());

        mplew.writeInt(ms.getX());
        if (ms.isMonsterSkill()) {
            mplew.writeShort(ms.getMobSkill().getSkillId());
            mplew.writeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            mplew.writeInt(ms.getSkill());
        }
        mplew.writeShort(ms.getStati().isEmpty() ? 1 : 0);

        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return CWvsContext.enableActions();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
        if (ms.getStati() == MonsterStatus.POISON) {
            PacketHelper.writeSingleMask(mplew, MonsterStatus.EMPTY);
            mplew.write(mse.size());
            mse.stream().map((m) -> {
                mplew.writeInt(m.getFromID());
                return m;
            }).map((m) -> {
                if (m.isMonsterSkill()) {
                    mplew.writeShort(m.getMobSkill().getSkillId());
                    mplew.writeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    mplew.writeInt(m.getSkill());
                }
                return m;
            }).map((m) -> {
                mplew.writeInt(m.getX());
                return m;
            }).map((_item) -> {
                mplew.writeInt(1000);
                return _item;
            }).map((_item) -> {
                mplew.writeInt(0);
                return _item;
            }).map((_item) -> {
                mplew.writeInt(5);
                return _item;
            }).forEachOrdered((_item) -> {
                mplew.writeInt(0);
            });
            mplew.writeShort(300);
            mplew.write(1);
            mplew.write(1);
        } else {
            PacketHelper.writeSingleMask(mplew, ms.getStati());

            mplew.writeInt(ms.getX());
            if (ms.isMonsterSkill()) {
                mplew.writeShort(ms.getMobSkill().getSkillId());
                mplew.writeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                mplew.writeInt(ms.getSkill());
            }
            mplew.writeShort(0);

            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection, MobSkill skil) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeMask(mplew, stati.keySet());

        stati.entrySet().stream().map((mse) -> {
            mplew.writeInt(((Integer) mse.getValue()));
            return mse;
        }).map((_item) -> {
            mplew.writeShort(skil.getSkillId());
            return _item;
        }).map((_item) -> {
            mplew.writeShort(skil.getSkillLevel());
            return _item;
        }).forEachOrdered((_item) -> {
            mplew.writeShort(0);
        });
        reflection.forEach((ref) -> {
            mplew.writeInt(ref);
        });
        mplew.writeLong(0L);
        mplew.writeShort(0);

        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        mplew.write(size);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] cancelMonsterStatus(int oid, MonsterStatus stat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, stat);
        mplew.write(1); // reflector is 3~!??
        mplew.write(2); // ? v97

        return mplew.getPacket();
    }

    public static byte[] cancelPoison(int oid, MonsterStatusEffect m) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, MonsterStatus.EMPTY);
        mplew.writeInt(0);
        mplew.writeInt(1); //size probably
        mplew.writeInt(m.getFromID()); //character ID
        if (m.isMonsterSkill()) {
            mplew.writeShort(m.getMobSkill().getSkillId());
            mplew.writeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            mplew.writeInt(m.getSkill());
        }
        mplew.write(3); // ? v97

        return mplew.getPacket();
    }

    public static final int getlength(final String str) {
        byte[] bt = str.getBytes(Charset.forName("BIG5"));
        return bt.length;
    }

    public static byte[] talkMonster(int oid, int itemId, int seconds, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(seconds > 0 ? (seconds * 1000) : 500);
        mplew.writeInt(itemId);
        mplew.write(itemId <= 3 ? 0 : 1);
        mplew.write(msg == null || getlength(msg) <= 0 ? 0 : 1);
        if (msg != null && getlength(msg) > 0) {
            mplew.writeMapleAsciiString(msg);
        }
        mplew.writeInt(1); // unknow -> LS
        return mplew.getPacket();
    }

    public static byte[] removeTalkMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        return mplew.getPacket();
    }

    public static byte[] getNodeProperties(final MapleMonster objectid, final MapleMap map) {
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MONSTER_PROPERTIES.getValue());
        mplew.writeInt(objectid.getObjectId());
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        map.getNodes().stream().map((mni) -> {
            mplew.writeInt(mni.x);
            return mni;
        }).map((mni) -> {
            mplew.writeInt(mni.y);
            return mni;
        }).map((mni) -> {
            mplew.writeInt(mni.attr);
            return mni;
        }).filter((mni) -> (mni.attr == 2)).forEachOrdered((_item) -> {
            //msg
            mplew.writeInt(500); //? talkMonster
        });
        mplew.writeInt(0);
        mplew.write(0); // tickcount, extra 1 int
        mplew.write(0);
        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }

    public static byte[] showMagnet(int mobid, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
        mplew.writeInt(mobid);
        mplew.write(success ? 1 : 0);
        mplew.write(0); // times, 0 = once, > 0 = twice

        return mplew.getPacket();
    }

    public static byte[] catchMonster(int mobid, int itemid, byte success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CATCH_MONSTER.getValue());
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);
        return mplew.getPacket();
    }

    public static byte[] showBossHPPlayer(int monsterId, long currentHp, long maxHp, byte tagColor, byte tagbgcolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId);
        if (currentHp > 2147483647L) {
            mplew.writeInt((int) (currentHp / maxHp * 2147483647.0D));
        } else {
            mplew.writeInt((int) (currentHp <= 0L ? -1L : currentHp));
        }
        if (maxHp > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(tagColor);
        mplew.write(tagbgcolor);

        return mplew.getPacket();
    }
}
