package server;

import client.MapleClient;
import client.inventory.*;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.MTSCSPacket;

public class CashShop implements Serializable {

    private static final long serialVersionUID = 231541893513373579L;
    private int accountId, characterId;
    private ItemLoader factory = ItemLoader.CASHSHOP;
    private List<Item> inventory = new ArrayList<>();
    private List<Integer> uniqueids = new ArrayList<>();

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;
        for (Pair<Item, MapleInventoryType> item : factory.loadItems(false, accountId).values()) {
            inventory.add(item.getLeft());
        }
    }

    public int getItemsSize() {
        return inventory.size();
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public Item findByCashId(int cashId) {
        for (Item item : inventory) {
            if (item.getUniqueId() == cashId) {
                return item;
            }
        }

        return null;
    }

    public void checkExpire(MapleClient c) {
        List<Item> toberemove = new ArrayList<>();
        inventory.stream().filter((item) -> (item != null && !GameConstants.isPet(item.getItemId()) && item.getExpiration() > 0 && item.getExpiration() < System.currentTimeMillis())).forEachOrdered((item) -> {
            toberemove.add(item);
        });
        if (toberemove.size() > 0) {
            toberemove.stream().map((item) -> {
                removeFromInventory(item);
                return item;
            }).forEachOrdered((item) -> {
                c.getSession().write(MTSCSPacket.cashItemExpired(item.getUniqueId()));
            });
            toberemove.clear();
        }
    }

    public Item toItem(CashItemInfo cItem) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }

    public Item toItem(CashItemInfo cItem, String gift) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }

    public Item toItem(CashItemInfo cItem, int uniqueid) {
        return toItem(cItem, uniqueid, "");
    }

    public Item toItem(CashItemInfo cItem, int uniqueid, String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        if ((period <= 0 && GameConstants.getInventoryType(cItem.getId()) != MapleInventoryType.EQUIP) || GameConstants.isPet(cItem.getId())) {
            period = GameConstants.GMS ? 90 : 45;
        }
        if (cItem.getId() >= 5000100 && cItem.getId() < 5000200) { //permanent pet
            period = 20000; //permanent time millis
        }
        Item ret;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(cItem.getId(), uniqueid);
            if (period > 0) {
                eq.setExpiration(2475606994921L);
            }
            eq.setGMLog("Cash Shop: " + cItem.getSN() + " on " + FileoutputUtil.CurrentReadable_Date());
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        } else {
            Item item = new Item(cItem.getId(), (byte) 0, (short) cItem.getCount(), (byte) 0, uniqueid);
            if (period > 0) {
                item.setExpiration(2475606994921L);
            }
            item.setGMLog("Cash Shop: " + cItem.getSN() + " on " + FileoutputUtil.CurrentReadable_Date());
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                final MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        inventory.remove(item);
    }

    public void gift(int recipient, String from, String message, int sn) {
        gift(recipient, from, message, sn, 0);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, recipient);
                ps.setString(2, from);
                ps.setString(3, message);
                ps.setInt(4, sn);
                ps.setInt(5, uniqueid);
                ps.executeUpdate();
            }
        } catch (SQLException sqle) {
        }
    }

    public List<Pair<Item, String>> loadGifts() {
        List<Pair<Item, String>> gifts = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"));
                if (cItem == null) {
                    continue;
                }
                Item item = toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
                gifts.add(new Pair<>(item, rs.getString("message")));
                uniqueids.add(item.getUniqueId());
                List<Integer> packages = CashItemFactory.getInstance().getPackageItems(cItem.getId());
                if (packages != null && packages.size() > 0) {
                    for (int packageItem : packages) {
                        CashItemInfo pack = CashItemFactory.getInstance().getSimpleItem(packageItem);
                        if (pack != null) {
                            addToInventory(toItem(pack, rs.getString("from")));
                        }
                    }
                } else {
                    addToInventory(item);
                }
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, characterId);
            ps.executeUpdate();
            ps.close();
            save();
        } catch (SQLException sqle) {
        }
        return gifts;
    }

    public boolean canSendNote(int uniqueid) {
        return uniqueids.contains(uniqueid);
    }

    public void sendedNote(int uniqueid) {
        for (int i = 0; i < uniqueids.size(); i++) {
            if (uniqueids.get(i) == uniqueid) {
                uniqueids.remove(i);
            }
        }
    }

    public void save() throws SQLException {
        List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

        inventory.forEach((item) -> {
            itemsWithType.add(new Pair<>(item, GameConstants.getInventoryType(item.getItemId())));
        });

        factory.saveItems(itemsWithType, accountId);
    }
}
