package client.inventory;

import java.io.Serializable;
import server.MapleItemInformationProvider;

public class Item implements Comparable<Item>, Serializable {

    private int id;
    private short position;
    private short quantity;
    private short flag;
    private long expiration = -1, inventoryitemid = 0;
    private long equiponlyId = -1;
    private MaplePet pet = null;
    private int uniqueid;

    private String owner = "";
    private String GameMaster_log = "";
    private String giftFrom = "";

    public Item(int id, short position, short quantity, short flag, int uniqueid) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
        this.equiponlyId = -1;
    }

    public Item(int id, short position, short quantity, short flag) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = -1;
        this.equiponlyId = -1;
    }

    public Item(int id, byte position, short quantity) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.uniqueid = -1;
        this.equiponlyId = -1;
    }

    public Item copy() {
        Item ret = new Item(id, position, quantity, flag, uniqueid);
        ret.pet = pet;
        ret.owner = owner;
        ret.GameMaster_log = GameMaster_log;
        ret.expiration = expiration;
        ret.giftFrom = giftFrom;
        ret.equiponlyId = equiponlyId;
        return ret;
    }

    public Item copyWithQuantity(short qq) {
        Item ret = new Item(id, position, qq, flag, uniqueid);
        ret.pet = pet;
        ret.owner = owner;
        ret.GameMaster_log = GameMaster_log;
        ret.expiration = expiration;
        ret.giftFrom = giftFrom;
        ret.equiponlyId = equiponlyId;
        return ret;
    }

    public void setPosition(short position) {
        this.position = position;

        if (pet != null) {
            pet.setInventoryPosition(position);
        }
    }

    public boolean hasSetOnlyId() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((this.uniqueid > 0) || (ii.isCash(this.id)) || (this.id / 1000000 != 1)) {
            return false;
        }
        return this.equiponlyId <= 0;
    }

    public void setEquipOnlyId(long id) {
        equiponlyId = id;
    }

    public long getEquipOnlyId() {
        return equiponlyId;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return id;
    }

    public short getPosition() {
        return position;
    }

    public short getFlag() {
        return flag;
    }

    public short getQuantity() {
        return quantity;
    }

    public byte getType() {
        return 2; // An Item
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expire) {
        this.expiration = expire;
    }

    public String getGMLog() {
        return GameMaster_log;
    }

    public void setGMLog(String GameMaster_log) {
        this.GameMaster_log = GameMaster_log;
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public void setUniqueId(int ui) {
        this.uniqueid = ui;
    }

    public long getInventoryId() { //this doesn't need to be 100% accurate, just different
        return inventoryitemid;
    }

    public void setInventoryId(long ui) {
        this.inventoryitemid = ui;
    }

    public MaplePet getPet() {
        return pet;
    }

    public void setPet(MaplePet pet) {
        this.pet = pet;
        if (pet != null) {
            this.uniqueid = pet.getUniqueId();
        }
    }

    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    public String getGiftFrom() {
        return giftFrom;
    }

    @Override
    public int compareTo(Item other) {
        if (Math.abs(position) < Math.abs(other.getPosition())) {
            return -1;
        } else if (Math.abs(position) == Math.abs(other.getPosition())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item ite = (Item) obj;
        return uniqueid == ite.getUniqueId() && id == ite.getItemId() && quantity == ite.getQuantity() && Math.abs(position) == Math.abs(ite.getPosition());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.id;
        hash = 79 * hash + this.position;
        hash = 79 * hash + this.quantity;
        hash = 79 * hash + this.uniqueid;
        return hash;
    }

    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }
}
