package handling.channel.handler;

import client.MapleClient;
import scripting.NPCScriptManager;
import tools.data.LittleEndianAccessor;

public class UserInterfaceHandler {

    public static void CygnusSummon_NPCRequest(final MapleClient c) {
        if (c.getPlayer().getJob() == 2000) {
            NPCScriptManager.getInstance().start(c, 1202000);
        } else if (c.getPlayer().getJob() == 1000) {
            NPCScriptManager.getInstance().start(c, 1101008);
        }
    }

    public static void InGame_Poll(final LittleEndianAccessor slea, final MapleClient c) {

    }

    public static void ShipObjectRequest(final int mapid, final MapleClient c) {
        // BB 00 6C 24 05 06 00 - Ellinia
        // BB 00 6E 1C 4E 0E 00 - Leafre

    }
}
