package handling.channel.handler;

import java.util.List;
import java.awt.Point;

import client.Skill;
import constants.GameConstants;
import client.MapleCharacter;
import client.SkillFactory;
import server.MapleStatEffect;
import tools.AttackPair;

public class AttackInfo {

    public int skill, charge, lastAttackTickCount;
    public List<AttackPair> allDamage;
    public Point position;
    public int display;
    public byte hits, targets, tbyte, speed, csstar, AOE, slot, unk;
    public boolean real = true;

    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final Skill skill_) {
        if (GameConstants.isMulungSkill(skill) || GameConstants.isPyramidSkill(skill) || GameConstants.isInflationSkill(skill)) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            return null;
        }
        if (GameConstants.isLinkedAranSkill(skill)) {
            final Skill skillLink = SkillFactory.getSkill(skill);
            return skillLink.getEffect(skillLevel);
        }
        return skill_.getEffect(skillLevel);
    }
}
