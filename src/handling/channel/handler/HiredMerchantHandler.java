package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MerchItemPackage;
import tools.Pair;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {

    public static boolean UseHiredMerchant(final MapleClient c, final boolean packet) {
        if (c.getPlayer().getMap() != null && c.getPlayer().getMap().allowPersonalShop()) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());

            switch (state) {
                case 1:
                    c.getPlayer().dropMessage(1, "請先從富蘭德里取回被保管的道具.");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    if (!merch) {
                        if (packet) {
                            c.getSession().write(PlayerShopPacket.sendTitleBox());
                        }
                        return true;
                    } else {
                        c.getPlayer().dropMessage(1, "伺服器即將關機");
                    }
                    break;
                default:
                    c.getPlayer().dropMessage(1, "請先關閉商店後再來找我.");
                    break;
            }
        } else {
            c.getSession().close(true);
        }
        return false;
    }

    private static byte checkExistance(final int accid, final int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ps.close();
                        rs.close();
                        return 1;
                    }
                }
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }

    public static void displayMerch(MapleClient c) {
        MapleCharacter ch = c.getPlayer();
        final int conv = ch.getConversation();
        boolean merch = World.hasMerchant(ch.getAccountID(), ch.getId());
        if (merch) {
            ch.dropNPC_HM("請先關閉商店後再來找我");
            ch.setConversation(0);
        } else if (c.getChannelServer().isShutdown()) {
            ch.dropNPC_HM("伺服器即將關機 ...");
            ch.setConversation(0);
        } else if (conv == 3) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(ch.getAccountID());

            if (pack == null) {
                ch.dropNPC_HM("你沒有任何保管中的道具");
                //    ch.dropNPC_HM("You do not have any item(s) with Fredrick.");
                ch.setConversation(0);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(ch, pack)) {
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 33));
                    ch.getClient().getSession().write(CWvsContext.enableActions());
                    return;
                }
                if (deletePackage(ch.getAccountID(), pack.getPackageid(), ch.getId())) {
                    if (ServerConstants.MerchantsUseCurrency) {
                        ch.gainCurrency(pack.getMesos(), false);
                    } else {
                        ch.gainMeso(pack.getMesos(), false);
                    }
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 32));
                    ch.dropNPC_HM("成功領取" + (ServerConstants.MerchantsUseCurrency ? "Munny" : "楓幣"));
                } else {
                    ch.dropNPC_HM("發生未知的錯誤");
                }
                ch.setConversation(0);
            } else {
                c.getSession().write(PlayerShopPacket.merchItemStore_ItemData(pack));
                MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (ch.getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.removeClickedNPC();
                        ch.dropNPC_HM("請先確認是否有足夠欄未領取被保管的道具");
                        ch.setConversation(0);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(ch.getAccountID(), pack.getPackageid(), ch.getId());
                    c.removeClickedNPC();
                    //   ch.dropNPC_HM("我幫你保管了一些道具，下次也要記得來領取，祝你遊戲愉快");
                    ch.setConversation(0);
                }
                ch.dropNPC(9030000, "我幫你保管了一些道具，下次也要記得來領取，祝你遊戲愉快!");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void MerchantItemStore(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = slea.readByte();
        if (operation == 27 || operation == 28) { // Request, Take out
            requestItems(c, operation == 27);
        } else if (operation == 30) { // Exit
            c.getPlayer().setConversation(0);
        }
    }

    private static void requestItems(final MapleClient c, final boolean request) {
        MapleCharacter ch = c.getPlayer();

        if (ch.getConversation() != 3) {
            return;
        }
        boolean merch = World.hasMerchant(ch.getAccountID(), ch.getId());
        if (merch) {
            ch.dropNPC_HM("請先關閉商店後再來找我.");
            ch.setConversation(0);
            return;
        }
        final MerchItemPackage pack = loadItemFrom_Database(ch.getAccountID());
        if (pack == null) {
            ch.dropNPC_HM("發生未知的錯誤.");
            return;
        }
        final int days = StringUtil.getDaysAmount(pack.getSavedTime(), System.currentTimeMillis()); // max 100%
        final double percentage = days / 100.0;
        final int fee = (int) Math.ceil(percentage * pack.getMesos()); // if no mesos = no tax
        if (request && days > 0 && percentage > 0 && pack.getMesos() > 0 && fee > 0) {
            c.getSession().write(PlayerShopPacket.merchItemStore((byte) 38, days, fee));
            return;
        }
        if (fee < 0) { // impossible
            c.getSession().write(PlayerShopPacket.merchItem_Message(33));
            return;
        }
        if (ch.getMeso() < fee) {
            c.getSession().write(PlayerShopPacket.merchItem_Message(35));
            return;
        }
        if (!check(c.getPlayer(), pack)) {
            c.getSession().write(PlayerShopPacket.merchItem_Message(36));
            return;
        }
        if (deletePackage(ch.getAccountID(), pack.getPackageid(), ch.getId())) {
            if (fee > 0) {
                ch.gainMeso(-fee, true);
            }
            ch.gainMeso(pack.getMesos(), false);
            pack.getItems().forEach((item) -> {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            });
            c.getSession().write(PlayerShopPacket.merchItem_Message(32));
        } else {
            ch.dropNPC_HM("發生未知的錯誤.");
        }
    }

    private static boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : pack.getItems()) {
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (null != invtype) switch (invtype) {
                case EQUIP:
                    eq++;
                    break;
//            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
            //              return false;
            //        }
                case USE:
                    use++;
                    break;
                case SETUP:
                    setup++;
                    break;
                case ETC:
                    etc++;
                    break;
                case CASH:
                    cash++;
                    break;
                default:
                    break;
            }
        }
        return !(chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash);
    }

    private static boolean deletePackage(final int accid, final int packageid, final int chrId) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, packageid);
                ps.setInt(3, chrId);
                ps.executeUpdate();
            }
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static MerchItemPackage loadItemFrom_Database(final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            ResultSet rs;
            final int packageid;
            final MerchItemPackage pack;
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ?")) {
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    rs.close();
                    return null;
                }
                packageid = rs.getInt("PackageId");
                pack = new MerchItemPackage();
                pack.setPackageid(packageid);
                pack.setMesos(rs.getInt("Mesos"));
                pack.setSavedTime(rs.getLong("time"));
            }
            rs.close();

            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid);
            if (items != null) {
                List<Item> iters = new ArrayList<>();
                items.values().forEach((z) -> {
                    iters.add(z.left);
                });
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            return null;
        }
    }
}
