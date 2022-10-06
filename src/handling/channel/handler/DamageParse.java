package handling.channel.handler;

import client.*;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.World;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class DamageParse {

    public static void applyAttack(final AttackInfo attack, final Skill theSkill, final MapleCharacter player, int attackCount, final double maxDamagePerMonster, final MapleStatEffect effect, final AttackType attack_type) {
        boolean useAttackCount = !GameConstants.is不檢測次數(attack.skill);

        if (!player.isAlive()) {
            return;
        }

        if (attack.skill == 5221007) {
            player.cancelAllBuffs2();
        } else if (attack.skill == 1311003 && player.getBuffedValue(MapleBuffStat.MORPH) != null) {
            player.cancelAllBuffs3();
            player.cancelAllBuffs4();
            player.cancelAllBuffs5();
        } else if (attack.skill == 5221008) {
            player.cancelAllBuffs2();
        }
        if (attack.skill != 0) {
            if (effect == null) {
                player.getClient().getSession().write(CWvsContext.enableActions());
                return;
            }
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (player.getMapId() / 10000 != 92502) {
                    //AutobanManager.getInstance().autoban(player.getClient(), "Using Mu Lung dojo skill out of dojo maps.");
                    return;
                } else {
                    if (player.getMulungEnergy() < 10000) {
                        return;
                    }
                    player.mulung_EnergyModify(false);
                }
            } else if (GameConstants.isPyramidSkill(attack.skill)) {
                if (player.getMapId() / 1000000 != 926) {
                    //AutobanManager.getInstance().autoban(player.getClient(), "Using Pyramid skill outside of pyramid maps.");
                    return;
                } else {
                    if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                        return;
                    }
                }
            } else if (GameConstants.isInflationSkill(attack.skill)) {
                if (player.getBuffedValue(MapleBuffStat.GIANT_POTION) == null) {
                    return;
                }

            }
        }

        if (attack.hits > 0 && attack.targets > 0) {
            // Don't ever do this. it's too expensive.
            if (!player.getStat().checkEquipDurabilitys(player, -1)) { //i guess this is how it works ?
                player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
                return;
            } //lol
        }
        int totDamage = 0;
        final MapleMap map = player.getMap();

        if (!player.isGM()) {
            if (attack.skill == 9001001 || attack.skill == 9101006) {
                World.Broadcast.broadcastMessage(player.getWorld(), CWvsContext.serverNotice(6, "[自動封鎖] " + player.getName() + " 因為修改封包而遭封鎖!"));
                player.ban("修改封包!", true);
            }
        }
        if (attack.skill == 4211006) { // meso explosion
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    continue;
                }
                final MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);

                if (mapobject != null) {
                    final MapleMapItem mapitem = (MapleMapItem) mapobject;
                    mapitem.getLock().lock();
                    try {
                        if (mapitem.getMeso() > 0) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(CField.explodeDrop(mapitem.getObjectId()));
                            mapitem.setPickedUp(true);
                        } else {
                            return;
                        }
                    } finally {
                        mapitem.getLock().unlock();
                    }
                } else {
                    return; // etc explosion, exploding nonexistant things, etc.
                }
            }
        }
        long fixeddmg, totDamageToOneMonster = 0;
        long hpMob = 0;
        final PlayerStats stats = player.getStat();

        int CriticalDamage = stats.passive_sharpeye_percent();
        int ShdowPartnerAttackPercentage = 0;
        if (attack_type == AttackType.RANGED_WITH_SHADOWPARTNER || attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
            final MapleStatEffect shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.SHADOWPARTNER);
            if (shadowPartnerEffect != null) {
                ShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
            }
            attackCount /= 2; // hack xD
        }
        ShdowPartnerAttackPercentage *= (CriticalDamage + 100) / 100;
        if (attack.skill == 4221001) { //amplifyDamage
            ShdowPartnerAttackPercentage *= 10;
        }
        byte overallAttackCount; // Tracking of Shadow Partner additional damage.
        double maxDamagePerHit = 0;
        MapleMonster monster;
        MapleMonsterStats monsterstats;
        boolean Tempest;

        for (final AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);
            if (monster != null) {
                if (useAttackCount) {
                    int atck = attackCount + GameConstants.getAddAtkCount(attack.skill);
                    int mob = effect == null ? 1 : effect.getMobCount() + GameConstants.getAddMobCount(attack.skill);;
                    if (attack.hits > atck * 2) {
                        if (player.isGM()) {
                            player.dropMessage(-1, "攻擊次數: " + attack.hits + " 服務端判斷攻擊次數: " + atck * 2 + " 請回報技能代碼 " + attack.skill);
                            return;
                        }
                        if (ServerConstants.getAB()) {
                            player.ban(player.getName() + " [" + attack.skill + "] 修改攻擊次數", false);
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[封鎖系統] " + player.getName() + " 因為使用違法程式練功而被管理員永久停權。"));
                            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, player.getName() + " 攻擊次數 " + attack.hits + " 正常攻擊次數 " + atck * 2 + " 技能ID " + attack.skill + " "));
                            FileoutputUtil.logToFile("Logs/外掛封鎖/攻擊次數.txt", "\r\n " + FileoutputUtil.NowTime() + " " + player.getName() + " 職業: " + player.getJob() + " 技能: " + attack.skill + " 攻擊次數 " + attack.hits + " 正常攻擊次數 " + atck * 2 + " 怪物:" + monster.getId() + " 地圖: " + player.getMapId());
                        } else {
                            FileoutputUtil.logToFile("Logs/防外掛偵測/攻擊次數.txt", "\r\n " + FileoutputUtil.NowTime() + " " + player.getName() + " 職業: " + player.getJob() + " 技能: " + attack.skill + " 攻擊次數 " + attack.hits + " 正常攻擊次數 " + atck * 2 + " 怪物:" + monster.getId() + " 地圖: " + player.getMapId());
                        }
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, player.getName() + " 攻擊次數 " + attack.hits + " 正常攻擊次數 " + atck * 2 + " 技能ID " + attack.skill + " "));

                    }
                    if (attack.targets > mob) {
                        if (ServerConstants.getAB()) {
                            player.ban(player.getName() + " [" + attack.skill + "] 修改打怪數量", false);
                            FileoutputUtil.logToFile("logs/外掛封鎖/打怪數量異常.txt", "\r\n 玩家: " + player.getName() + " 技能代碼: " + attack.skill + " 攻擊怪物量 : " + attack.targets + " 正確怪物量 :" + mob);
                            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[封鎖系統] " + player.getName() + " 因為使用違法程式練功而被管理員永久停權。"));
                        } else {
                            FileoutputUtil.logToFile("logs/防外掛偵測/打怪數量異常.txt", "\r\n 玩家: " + player.getName() + " 技能代碼: " + attack.skill + " 攻擊怪物量 : " + attack.targets + " 正確怪物量 :" + mob);
                        }
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM 密語系統] " + player.getName() + " (等級 " + player.getLevel() + ") 攻擊怪物量 " + attack.targets + " 正確怪物量 " + mob + " 技能ID " + attack.skill));
                    }
                }

            }
            if (monster != null && monster.getLinkCID() <= 0) {
                totDamageToOneMonster = 0;
                hpMob = monster.getMobMaxHp();
                monsterstats = monster.getStats();
                fixeddmg = monsterstats.getFixedDamage();
                Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006 || attack.skill == 21120006 || attack.skill == 1221011;
                maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, CriticalDamage);

                overallAttackCount = 0; // Tracking of Shadow Partner additional damage.
                Integer eachd;
                if (monster.getBelongsToSomeone() && monster.getBelongsTo() != player.getId() && (player.getParty() == null || player.getParty().getMemberById(monster.getBelongsTo()) == null) && !player.isGM()) {
                    player.dropMessage("無法攻擊其他人的怪物.");
                    continue;
                }
                for (Pair<Integer, Boolean> eachde : oned.attack) {
                    eachd = eachde.left;
                    overallAttackCount++;

                    if (useAttackCount && overallAttackCount - 1 == attackCount) { // Is a Shadow partner hit so let's divide it once
                        maxDamagePerHit = (maxDamagePerHit / 100) * (ShdowPartnerAttackPercentage * (monsterstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100);
                    }
                    //System.out.println("Client damage : " + eachd + " Server : " + maxDamagePerHit);
                    if (fixeddmg != -1) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = attack.skill != 0 ? 0 : (int) fixeddmg;
                        } else {
                            eachd = (int) fixeddmg;
                        }
                    } else {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = attack.skill != 0 ? 0 : Math.min(eachd, (int) maxDamagePerHit);  // Convert to server calculated damage
                        } else if (!player.isGM()) {
                            if (Tempest) { // Monster buffed with Tempest
                                if (eachd > monster.getMobMaxHp()) {
                                    eachd = (int) Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);

                                }
                            } else if ((player.getJob() >= 3200 && player.getJob() <= 3212 && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) || attack.skill == 23121003 || ((player.getJob() < 3200 || player.getJob() > 3212) && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT))) {
                                if (eachd > maxDamagePerHit) {
                                    if (eachd > maxDamagePerHit * 2) {
                                        //    player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_2, "[Damage: " + eachd + ", Expected: " + maxDamagePerHit + ", Mob: " + monster.getId() + "] [Job: " + player.getJob() + ", Level: " + player.getLevel() + ", Skill: " + attack.skill + "]");
                                        eachd = (int) (maxDamagePerHit * 2); // Convert to server calculated damage
                                    }
                                }
                            } else {
                                if (eachd > maxDamagePerHit) {
                                    eachd = (int) (maxDamagePerHit);
                                }
                            }
                        }
                    }
                    if (player == null) { // o_O
                        return;
                    }
                    totDamageToOneMonster += eachd;
                    //沒Miss也強制Miss. 為了 wz 編輯的特效
                    if ((eachd == 0 || monster.getId() == 9700021) && player.getPyramidSubway() != null) { //miss
                        player.getPyramidSubway().onMiss(player);
                    }
                }

                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                int x = attack.skill;
                int p1 = (int) player.getTruePosition().distanceSq(monster.getTruePosition());
                int p2 = (int) GameConstants.getAttackRange(player, effect);
                boolean elseskill = x == 2221006 || x == 3101005 || x == 31101002;
                if (GameConstants.isEvan(player.getJob())) {
                    elseskill = true;
                }
                if (player.getDebugMessage()) {
                    player.dropMessage(-1, "技能[" + x + "]攻擊範圍:" + p1 + " 預計範圍:" + p2);
                }
//                if ((!monster.getStats().isBoss()) && (p1 > p2) && !elseskill) {
//                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, "攻擊範圍異常，技能:" + attack.skill); // , Double.toString(Math.sqrt(distance))
//                    FileoutputUtil.logToFile("logs/防外掛偵測/攻擊範圍異常.txt", "\r\n " + FileoutputUtil.NowTime() + " 玩家: " + player.getName() + " 技能代碼: " + attack.skill + " 正常範圍 : " + p2 + " 偵測範圍: " + p1);
//                    if (player.isAdmin()) {
//                        player.dropMessage("觸發全圖打");
//                    } else {
//                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM 密語系統] " + player.getName() + "攻擊範圍異常,正常範圍:" + p2 + "偵測範圍:" + p1 + "技能ID:" + attack.skill));
//                    }
//                    return;
//                }
                // pickpocket
                if (player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                    switch (attack.skill) {
                        case 0:
                        case 4001334:
                        case 4201004:
                        case 4201005:
                        case 4211002:
                        case 4211004:
                        case 4221010:
                        case 4211011:
                        case 4221001:
                        case 4221003:
                        case 4221007:
                            handlePickPocket(player, monster, oned);
                            break;
                    }
                }

//                if (totDamageToOneMonster > Integer.MAX_VALUE) {
//                    totDamageToOneMonster = Integer.MAX_VALUE;
//                }
                if (totDamageToOneMonster > 0 || attack.skill == 1221011 || attack.skill == 21120006) {

                    if (GameConstants.isDemon(player.getJob())) {
                        player.handleForceGain(monster.getObjectId(), attack.skill);
                    }
                    if ((GameConstants.isPhantom(player.getJob())) && (attack.skill != 24120002) && (attack.skill != 24100003)) {
                        player.handleCardStack();
                    }
                    if (attack.skill != 1221011) {
                        monster.damage(player, (int) totDamageToOneMonster, true, attack.skill);
                    } else {
                        monster.damage(player, (monster.getStats().isBoss() ? 500000 : (int) (monster.getHp() - 1)), true, attack.skill);
                    }
                    player.onAttack(monster, effect, attack, totDamageToOneMonster, totDamage);
                }
            }
        }

        if (attack.skill == 4331003 && (hpMob <= 0 || totDamageToOneMonster < hpMob)) {
            return;
        }
        if (hpMob > 0 && totDamageToOneMonster > 0) {
            player.afterAttack(attack, effect);
        }
    }

    private static void handlePickPocket(final MapleCharacter player, final MapleMonster mob, AttackPair oned) {
        final int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET);

        oned.attack.stream().map((eachde) -> eachde.left).filter((eachd) -> (player.getStat().pickRate >= 100 || Randomizer.nextInt(99) < player.getStat().pickRate)).forEachOrdered((eachd) -> {
            player.getMap().spawnMesoDrop(Math.min((int) Math.max(((double) eachd / (double) 20000) * (double) maxmeso, (double) 1), maxmeso), new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50), (int) (mob.getTruePosition().getY())), mob, player, false, (byte) 0);
        });
    }

    private static double CalculateMaxWeaponDamagePerHit(final MapleCharacter player, final MapleMonster monster, final AttackInfo attack, final Skill theSkill, final MapleStatEffect attackEffect, double maximumDamageToMonster, final Integer CriticalDamagePercent) {
        final int dLevel = Math.max(monster.getStats().getLevel() - player.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(player.getStat().getAccuracy())) - (int) Math.floor(Math.sqrt(monster.getStats().getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        // if (HitRate <= 0 && !(GameConstants.isBeginnerJob(attack.skill / 10000) && attack.skill % 10000 == 1000) && !GameConstants.isPyramidSkill(attack.skill) && !GameConstants.isMulungSkill(attack.skill) && !GameConstants.isInflationSkill(attack.skill)) { // miss :P or HACK :O
        //   return 0;
        // }
        //  if (player.getMapId() / 1000000 == 914 || player.getMapId() / 1000000 == 927) { //aran
        //    return 999999;
        // }

        List<Element> elements = new ArrayList<>();
        boolean defined = false;
        int CritPercent = CriticalDamagePercent;
        int PDRate = monster.getStats().getPDRate();
        MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.WDEF);
        if (pdr != null) {
            PDRate += pdr.getX(); //x will be negative usually
        }
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            if (GameConstants.isBeginnerJob(theSkill.getId() / 10000)) {
                switch (theSkill.getId() % 10000) {
                    case 1000:
                        maximumDamageToMonster = 40;
                        defined = true;
                        break;
                    case 1020:
                        maximumDamageToMonster = 1;
                        defined = true;
                        break;
                    case 1009:
                        maximumDamageToMonster = (monster.getStats().isBoss() ? monster.getMobMaxHp() / 30 * 100 : monster.getMobMaxHp());
                        defined = true;
                        break;
                }
            }
            switch (theSkill.getId()) {
                case 1311005:
                    PDRate = (monster.getStats().isBoss() ? PDRate : 0);
                    break;
                case 3221001:
                case 33101001:
                    maximumDamageToMonster *= attackEffect.getMobCount();
                    defined = true;
                    break;
                case 3101005:
                    defined = true; //can go past 500000
                    break;
                case 32001000:
                case 32101000:
                case 32111002:
                case 32121002:
                    maximumDamageToMonster *= 1.5;
                    break;
                case 3221007: //snipe
                case 23121003:
                case 1221009: //BLAST FK
                case 4331003: //Owl Spirit
                    if (!monster.getStats().isBoss()) {
                        maximumDamageToMonster = (monster.getMobMaxHp());
                        defined = true;
                    }
                    break;
                case 1221011://Heavens Hammer
                case 21120006: //Combo Tempest
                    maximumDamageToMonster = (monster.getStats().isBoss() ? 500000 : (monster.getHp() - 1));
                    defined = true;
                    break;
                case 3211006: //Sniper Strafe
                    if (monster.getStatusSourceID(MonsterStatus.FREEZE) == 3211003) { //blizzard in effect
                        defined = true;
                        // maximumDamageToMonster = 999999;
                    }
                    break;
            }
        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if (player.getJob() == 311 || player.getJob() == 312 || player.getJob() == 321 || player.getJob() == 322) {
            //FK mortal blow
            Skill mortal = SkillFactory.getSkill(player.getJob() == 311 || player.getJob() == 312 ? 3110001 : 3210001);
            if (player.getTotalSkillLevel(mortal) > 0) {
                final MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if (mort != null && monster.getHPPercent() < mort.getX()) {
                    //    elementalMaxDamagePerMonster = 999999;
                    defined = true;
                    if (mort.getZ() > 0) {
                        player.addHP((player.getStat().getMaxHp() * mort.getZ()) / 100);
                    }
                }
            }
        } else if (player.getJob() == 221 || player.getJob() == 222) {
            //FK storm magic
            Skill mortal = SkillFactory.getSkill(2210000);
            if (player.getTotalSkillLevel(mortal) > 0) {
                final MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if (mort != null && monster.getHPPercent() < mort.getX()) {
                    defined = true;
                }
            }
        }
        if (!defined || (theSkill != null && (theSkill.getId() == 33101001 || theSkill.getId() == 3221001))) {
            if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);

                switch (chargeSkillId) {
                    case 1211003:
                    case 1211004:
                        elements.add(Element.FIRE);
                        break;
                    case 1211005:
                    case 1211006:
                    case 21111005:
                        elements.add(Element.ICE);
                        break;
                    case 1211007:
                    case 1211008:
                    case 15101006:
                        elements.add(Element.LIGHTING);
                        break;
                    case 1221003:
                    case 1221004:
                    case 11111007:
                        elements.add(Element.HOLY);
                        break;
                    case 12101005:
                        //elements.clear(); //neutral
                        break;
                }
            }
            if (player.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
                elements.add(Element.LIGHTING);
            }
            if (player.getBuffedValue(MapleBuffStat.ELEMENT_RESET) != null) {
                elements.clear();
            }
            if (elements.size() > 0) {
                double elementalEffect;

                switch (attack.skill) {
                    case 3211003:
                    case 3111003: // inferno and blizzard
                        elementalEffect = attackEffect.getX() / 100.0;
                        break;
                    default:
                        elementalEffect = (0.5 / elements.size());
                        break;
                }
                for (Element element : elements) {
                    switch (monster.getEffectiveness(element)) {
                        case IMMUNE:
                            elementalMaxDamagePerMonster = 1;
                            break;
                        case WEAK:
                            elementalMaxDamagePerMonster *= (1.0 + elementalEffect + player.getStat().getElementBoost(element));
                            break;
                        case STRONG:
                            elementalMaxDamagePerMonster *= (1.0 - elementalEffect - player.getStat().getElementBoost(element));
                            break;
                    }
                }
            }
            // Calculate mob def
            elementalMaxDamagePerMonster -= elementalMaxDamagePerMonster * (Math.max(PDRate - Math.max(player.getStat().ignoreTargetDEF, 0) - Math.max(attackEffect == null ? 0 : attackEffect.getIgnoreMob(), 0), 0) / 100.0);

            // Calculate passive bonuses + Sharp Eye
            elementalMaxDamagePerMonster += ((double) elementalMaxDamagePerMonster / 100.0) * CritPercent;

//	    if (theSkill.isChargeSkill()) {
//	        elementalMaxDamagePerMonster = (double) (90 * (System.currentTimeMillis() - player.getKeyDownSkill_Time()) / 2000 + 10) * elementalMaxDamagePerMonster * 0.01;
//	    }
//          if (theSkill != null && theSkill.isChargeSkill() && player.getKeyDownSkill_Time() == 0) {
//              return 0;
//          }
            final MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.IMPRINT);
            if (imprint != null) {
                elementalMaxDamagePerMonster += (elementalMaxDamagePerMonster * imprint.getX() / 100.0);
            }

            elementalMaxDamagePerMonster += (elementalMaxDamagePerMonster * player.getDamageIncrease(monster.getObjectId()) / 100.0);
            elementalMaxDamagePerMonster *= (monster.getStats().isBoss() && attackEffect != null ? (player.getStat().bossdam_r + attackEffect.getBossDamage()) : player.getStat().dam_r) / 100.0;
        }
        return elementalMaxDamagePerMonster;
    }

    public static AttackInfo DivideAttack(final AttackInfo attack, final int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack; //lol
        }
        attack.allDamage.stream().filter((p) -> (p.attack != null)).forEachOrdered((p) -> {
            p.attack.forEach((eachd) -> {
                eachd.left /= rate; //too ex.
            });
        });
        return attack;
    }

    public static final AttackInfo Modify_AttackCrit(AttackInfo attack, MapleCharacter chr, int type, MapleStatEffect effect) {
        int CriticalRate;
        boolean shadow;
        List damages;
        List damage;
        if ((attack.skill != 4211006) && (attack.skill != 3211003) && (attack.skill != 4111004)) {
            CriticalRate = chr.getStat().passive_sharpeye_rate() + (effect == null ? 0 : effect.getCr());
            shadow = (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) && ((type == 1) || (type == 2));
            damages = new ArrayList();
            damage = new ArrayList();

            for (AttackPair p : attack.allDamage) {
                if (p.attack != null) {
                    int hit = 0;
                    int mid_att = shadow ? p.attack.size() / 2 : p.attack.size();

                    int toCrit = (attack.skill == 4221001) || (attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 4341005) || (attack.skill == 4331006) || (attack.skill == 21120005) ? mid_att : 0;
                    if (toCrit == 0) {
                        for (Pair eachd : p.attack) {
                            if ((!((Boolean) eachd.right)) && (hit < mid_att)) {
                                if ((((Integer) eachd.left) > 999999) || (Randomizer.nextInt(100) < CriticalRate)) {
                                    toCrit++;
                                }
                                damage.add(eachd.left);
                            }
                            hit++;
                        }
                        if (toCrit == 0) {
                            damage.clear();
                            continue;
                        }
                        Collections.sort(damage);
                        for (int i = damage.size(); i > damage.size() - toCrit; i--) {
                            damages.add(damage.get(i - 1));
                        }
                        damage.clear();
                    }
                    hit = 0;
                    for (Pair eachd : p.attack) {
                        if (!((Boolean) eachd.right)) {
                            if (attack.skill == 4221001) {
                                eachd.right = hit == 3;
                            } else if ((attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 21120005) || (attack.skill == 4341005) || (attack.skill == 4331006) || (((Integer) eachd.left) > 999999)) {
                                eachd.right = true;
                            } else if (hit >= mid_att) {
                                eachd.right = ((Pair) p.attack.get(hit - mid_att)).right;
                            } else {
                                eachd.right = damages.contains(eachd.left);
                            }
                        }
                        hit++;
                    }
                    damages.clear();
                }
            }
        }
        return attack;
    }

    public static final AttackInfo parseDmgMa(LittleEndianAccessor lea, MapleCharacter chr) {
        try {
            AttackInfo ret = new AttackInfo();

            lea.skip(1);
            ret.tbyte = lea.readByte();

            ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
            ret.hits = (byte) (ret.tbyte & 0xF);
            ret.skill = lea.readInt();
            if (ret.skill >= 91000000) {
                return null;
            }
            lea.skip(GameConstants.GMS ? 9 : 17);
            if (GameConstants.isMagicChargeSkill(ret.skill)) {
                ret.charge = lea.readInt();
            } else {
                ret.charge = -1;
            }
            ret.unk = lea.readByte();
            ret.display = lea.readUShort();

            lea.skip(4);
            lea.skip(1);
            ret.speed = lea.readByte();
            ret.lastAttackTickCount = lea.readInt();
            lea.skip(4);

            ret.allDamage = new ArrayList();

            for (int i = 0; i < ret.targets; i++) {
                int oid = lea.readInt();

                lea.skip(18);

                List allDamageNumbers = new ArrayList();

                for (int j = 0; j < ret.hits; j++) {
                    int damage = lea.readInt();
                    allDamageNumbers.add(new Pair(damage, false));
                }

                lea.skip(4);
                ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
            }
            if (lea.available() >= 4L) {
                ret.position = lea.readPos();
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final AttackInfo parseDmgM(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();

        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        if (ret.skill >= 91000000) {
            return null;
        }
        lea.skip(9);
        switch (ret.skill) {
            case 11101007: // Power Reflection
            case 11101006: // Dawn Warrior - Power Reflection
            case 21101003: // body pressure
            case 2111007:// tele mastery skills
            case 2211007:
            case 12111007:
            case 22161005:
            case 32111010:
            case 2311007: // bishop tele mastery
                lea.skip(1); // charge = 0
                ret.charge = 0;
                ret.display = lea.readUShort();
                lea.skip(4);// dunno
                ret.speed = (byte) lea.readShort();
                ret.lastAttackTickCount = lea.readInt();
                lea.skip(4);// looks like zeroes
                ret.allDamage = new ArrayList();
                for (int i = 0; i < ret.targets; i++) {
                    int oid = lea.readInt();
                    lea.skip(18);
                    List allDamageNumbers = new ArrayList();
                    for (int j = 0; j < ret.hits; j++) {
                        int damage = lea.readInt();
                        allDamageNumbers.add(new Pair(damage, false));
                    }
                    lea.skip(4);
                    ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
                }
                ret.position = lea.readPos();
                return ret;
            case 24121000:// mille
            // case 24121005://tempest
            //case 5101004: // Corkscrew
            //case 15101003: // Cygnus corkscrew
            case 5201002: // Gernard
            case 14111006: // Poison bomb
            case 4341002:
            case 4341003:
            case 5301001:
            case 5300007:
            case 31001000: // grim scythe
            case 31101000: // soul eater
            case 31111005: // carrion breath
                ret.charge = lea.readInt();
                break;
            default:
                ret.charge = 0;
                break;
        }

        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        lea.skip(4);
        lea.skip(1);
        if ((ret.skill == 5300007) || (ret.skill == 5101012) || (ret.skill == 5081001) || (ret.skill == 15101010)) {
            lea.readInt();
        }
        if (ret.skill == 24121005) {
            lea.readInt();
        }
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        if (ret.skill == 32121003) {
            lea.skip(4);
        } else {
            lea.skip(8);
        }

        ret.allDamage = new ArrayList();

        if (ret.skill == 4211006) {
            return parseMesoExplosion(lea, ret, chr);
        }

        //if (ret.skill == 24121000) {
        // lea.readInt();
        // }
        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();

            lea.skip(18);

            List allDamageNumbers = new ArrayList();

            for (int j = 0; j < ret.hits; j++) {
                int damage = lea.readInt();

                allDamageNumbers.add(new Pair(damage, false));
            }
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }

    public static final AttackInfo parseDmgR(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();

        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        if (ret.skill >= 91000000) {
            return null;
        }
        lea.skip(10);
        switch (ret.skill) {
            case 3121004: // Hurricane
            case 3221001: // Pierce
            case 5221004: // Rapidfire
            case 5721001: // Rapidfire
            case 13111002: // Cygnus Hurricane
            case 33121009:
            case 35001001:
            case 5711002:
            case 35101009:
            case 23121000:
            case 5311002:
            case 24121000:
                lea.skip(4); // extra 4 bytes
                break;
        }

        ret.charge = -1;
        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        lea.skip(4);
        lea.skip(1);
        if (ret.skill == 23111001) {
            lea.skip(4);
            lea.skip(4);

            lea.skip(4);
        }
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.slot = (byte) lea.readShort();
        ret.csstar = (byte) lea.readShort();
        ret.AOE = lea.readByte();

        ret.allDamage = new ArrayList();

        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();

            lea.skip(18);

            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                int damage = lea.readInt();
                List<Integer> OHKORanged = Collections.unmodifiableList(Arrays.asList(
                        3221007, 5721006));
                if (OHKORanged.contains(ret.skill) && damage > GameConstants.OHKODamage) {
                    damage = GameConstants.OHKODamage;
                }
                if (chr.getBuffSource(MapleBuffStat.WATK) == 5211009) { //cross cut blast
                    int attacksLeft = chr.getCData(chr, 5211009);
                    if (attacksLeft <= 0) {
                        chr.cancelEffectFromBuffStat(MapleBuffStat.WATK);
                    } else {
                        chr.setCData(5211009, -1);
                    }
                }
                allDamageNumbers.add(new Pair(damage, false));
            }

            lea.skip(4);

            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }
        lea.skip(4);
        ret.position = lea.readPos();

        return ret;
    }

    public static final AttackInfo parseMesoExplosion(final LittleEndianAccessor lea, final AttackInfo ret, final MapleCharacter chr) {
        //System.out.println(lea.toString(true));
        byte bullets;
        if (ret.hits == 0) {
            lea.skip(4);
            bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                ret.allDamage.add(new AttackPair(lea.readInt(), null));
                lea.skip(1);
            }
            lea.skip(2); // 8F 02
            return ret;
        }
        int oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
            //if (chr.getMap().isTown()) {
            //    final MapleMonster od = chr.getMap().getMonsterByOid(oid);
            //    if (od != null && od.getLinkCID() > 0) {
            //	    return null;
            //    }
            //}
            lea.skip(16);
            bullets = lea.readByte();
            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < bullets; j++) {
                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(lea.readInt()), false)); //m.e. never crits
            }
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
            lea.skip(4); // C3 8F 41 94, 51 04 5B 01
        }
        lea.skip(4);
        bullets = lea.readByte();

        for (int j = 0; j < bullets; j++) {
            ret.allDamage.add(new AttackPair(lea.readInt(), null));
            lea.skip(2);
        }
        // 8F 02/ 63 02

        return ret;
    }
}
