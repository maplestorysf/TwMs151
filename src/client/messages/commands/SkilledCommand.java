package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;

public class SkilledCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.老實習生;
    }

    public static class Level extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int level = c.getPlayer().getLevel();
            try {
                level = Short.parseShort(splitted[1]);
            } catch (Exception ex) {

            }
            c.getPlayer().setLevel((short) level);
            c.getPlayer().updateSingleStat(MapleStat.LEVEL, level);
            c.getPlayer().setExp(0);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Level <等級> - 改變等級").toString();
        }
    }

    public static class 黑單 extends FakeReport {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!黑單 <玩家名稱> - 將玩家設定為無法回報的黑名單").toString();
        }
    }

    public static class FakeReport extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = splitted[1];
            int ch = World.Find.findChannel(input);
            if (ch <= 0) {
                c.getPlayer().dropMessage("玩家[" + input + "]不在線上");
                return true;
            }
            MapleCharacter target = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(input);
            if (target.isGM()) {
                c.getPlayer().dropMessage(1, "無法黑單GM唷");
                return true;
            }
            int accID = target.getAccountID();
            ServerConstants.setBlackList(accID, input);
            String msg = "[GM 密語] GM " + c.getPlayer().getName() + " 在回報系統黑單了 " + input;
            World.Broadcast.broadcastGMMessage(CWvsContext.getItemNotice(msg));
            FileoutputUtil.logToFile("Logs/Data/玩家回報黑單.txt", "\r\n  " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " 在回報系統黑單了 " + input);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!FakeReport <玩家名稱> - 將玩家設定為無法回報的黑名單").toString();
        }
    }

    public static class Heal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp(), c.getPlayer());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer().getJob()), c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer().getJob()));
            c.getPlayer().dispelDebuffs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!heal - 補滿血魔").toString();
        }
    }

    public static class HealMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            player.getMap().getCharacters().stream().filter((mch) -> (mch != null)).map((mch) -> {
                mch.getStat().setHp(mch.getStat().getCurrentMaxHp(), mch);
                return mch;
            }).map((mch) -> {
                mch.updateSingleStat(MapleStat.HP, mch.getStat().getCurrentMaxHp());
                return mch;
            }).map((mch) -> {
                mch.getStat().setMp(mch.getStat().getCurrentMaxMp(mch.getJob()), mch);
                return mch;
            }).map((mch) -> {
                mch.updateSingleStat(MapleStat.MP, mch.getStat().getCurrentMaxMp(mch.getJob()));
                return mch;
            }).forEachOrdered((mch) -> {
                mch.dispelDebuffs();
            });
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!healmap  - 治癒地圖上所有的人").toString();
        }
    }
}
