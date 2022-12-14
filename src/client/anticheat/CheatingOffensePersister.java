package client.anticheat;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.Timer.CheatTimer;

public class CheatingOffensePersister {

    private final static CheatingOffensePersister instance = new CheatingOffensePersister();
    private final Set<CheatingOffenseEntry> toPersist = new LinkedHashSet<CheatingOffenseEntry>();
    private final Lock mutex = new ReentrantLock();

    private CheatingOffensePersister() {
        CheatTimer.getInstance().register(new PersistingTask(), 61000);
    }

    public static CheatingOffensePersister getInstance() {
        return instance;
    }

    public void persistEntry(CheatingOffenseEntry coe) {
        mutex.lock();
        try {
            toPersist.remove(coe); //equal/hashCode h4x
            toPersist.add(coe);
        } finally {
            mutex.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            //CheatingOffenseEntry[] offenses;

            mutex.lock();
            try {
                //offenses = toPersist.toArray(new CheatingOffenseEntry[toPersist.size()]);
                toPersist.clear();
            } finally {
                mutex.unlock();
            }

        }
    }
}
