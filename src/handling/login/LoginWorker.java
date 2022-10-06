package handling.login;

import client.MapleClient;
import server.Timer.PingTimer;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.getSession().write(CWvsContext.serverNotice(1, "伺服器正在維修中.\r\n請稍候在試"));
            c.getSession().write(LoginPacket.getLoginFailed(7));
            return;
        }

        if (c.finishLogin() == 0) {
            if (c.getSecondPassword() == null || c.getSecondPassword().isEmpty()) {
                c.sendPacket(LoginPacket.getGenderNeeded(c));
                c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
            } else {
                c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
                //handling.login.handler.CharLoginHandler.ServerListRequest(c);
            }

            //CharLoginHandler.ServerStatusRequest(c);
            //c.getSession().write(LoginPacket.getCharList(c.getSecondPassword() != null, c.loadCharacters(0), c.getCharacterSlots()));
            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    c.getSession().close(true);
                }
            }, 10 * 60 * 20000));
        } else {
            c.getSession().write(LoginPacket.getLoginFailed(7));
        }
    }
}
