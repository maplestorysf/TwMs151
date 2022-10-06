package client;

import handling.channel.handler.InventoryHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;

public class AutoLooter extends Thread {

    private ConcurrentLinkedQueue<MapleMapObject> autoloots;
    private MapleMapItem item;
    private MapleMapObject object;
    private MapleCharacter chr;
    private MapleClient c;

    public AutoLooter(MapleCharacter chr) {
        this.chr = chr;
        this.autoloots = new ConcurrentLinkedQueue<MapleMapObject>();
        this.c = chr.getClient();
    }

    public synchronized void addObject(MapleMapObject ob) {
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp() || mapitem.getQuest() > 0 && this.chr.getQuestStatus(mapitem.getQuest()) != 1 || mapitem.getOwner() != this.chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && this.chr.getMap().getEverlast())) || !mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != this.chr.getId()) {
                return;
            }
            if (mapitem.getMeso() > 0) {
                this.autoloots.add(ob);
            } else {
                if (!MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) && !(this.c.getPlayer().inPVP() && Integer.parseInt(this.c.getPlayer().getEventInstance().getProperty("ice")) == this.c.getPlayer().getId()) && !(InventoryHandler.useItem(c, mapitem.getItemId())) && mapitem.getItemId() / 10000 != 291) {
                    this.autoloots.add(ob);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void run() {
        try {
            while (!interrupted()) {
                this.wait(7000);
                List<MapleMapItem> items = chr.getMap().getAllItemsThreadsafe();
                items.forEach((i) -> {
                    this.addObject(i);
                });
                while (this.autoloots.peek() != null) {
                    this.object = this.autoloots.poll();
                    this.item = (MapleMapItem) this.object;
                    final Lock lock = this.item.getLock();
                    lock.lock();
                    try {
                        if (this.item.getMeso() > 0) {
                            if (this.chr.getParty() != null && this.item.getOwner() != this.chr.getId()) {
                                final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                                //  final int splitMeso = this.item.getMeso() * 40 / 100;
                                this.chr.getParty().getMembers().stream().map((z) -> this.chr.getMap().getCharacterById(z.getId())).filter((m) -> (m != null && m.getId() != this.chr.getId())).forEachOrdered((m) -> {
                                    toGive.add(m);
                                });
                            } else {
                            }
                            if (!interrupted()) {
                                InventoryHandler.removeItem(this.chr, this.item, this.object);
                                this.wait(50);
                            }
                        } else {
                            if (MapleInventoryManipulator.checkSpace(c, this.item.getItemId(), this.item.getItem().getQuantity(), this.item.getItem().getOwner()) && !interrupted() && (item.getItem().getItemId() / 10000 == 287)) {
                                MapleInventoryManipulator.addFromDrop(this.chr.getClient(), this.item.getItem(), true, this.item.getDropper() instanceof MapleMonster);
                                InventoryHandler.removeItem(this.chr, this.item, this.object);
                                this.wait(50);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
        }
    }
}
