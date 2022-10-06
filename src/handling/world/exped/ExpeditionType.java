package handling.world.exped;

public enum ExpeditionType {
    // CWKPQ(30, 2007, 90, 255),
    Arkarium(18, 2009, 120, 255),
    Ranmaru(18, 2092, 120, 255),
    Chaos_Ranmaru(18, 2093, 180, 255),
    BloodyMary(30, 2092, 120, 255),
    Ch_BloodyMary(30, 2092, 150, 255),
    Normal_Balrog(15, 2001, 50, 255),
    Horntail(30, 2003, 80, 255),
    Zakum(30, 2002, 50, 255),
    Chaos_Zakum(30, 2005, 100, 255),
    ChaosHT(30, 2006, 110, 255),
    Pink_Bean(30, 2004, 140, 255),
    CWKPQ(30, 2007, 90, 255),
    Von_Leon(30, 2008, 120, 255),
    Cygnus(18, 2009, 170, 255),
    Hilla(30, 2010, 70, 255);

    public int maxMembers, maxParty, exped, minLevel, maxLevel;

    private ExpeditionType(int maxMembers, int exped, int minLevel, int maxLevel) {
        this.maxMembers = maxMembers;
        this.exped = exped;
        this.maxParty = (maxMembers / 2) + (maxMembers % 2 > 0 ? 1 : 0);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public static ExpeditionType getById(int id) {
        for (ExpeditionType pst : ExpeditionType.values()) {
            if (pst.exped == id) {
                return pst;
            }
        }
        return null;
    }
}
