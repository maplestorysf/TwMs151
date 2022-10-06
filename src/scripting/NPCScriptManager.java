package scripting;

import client.MapleClient;
import constants.WorldConstants;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static NPCScriptManager getInstance() {
        return instance;
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {

                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final ScriptException | NoSuchMethodException e) {
                if (c.getPlayer().isGM()) {
                    c.getPlayer().dropMessage(-1, "[系統提示] NPC " + cm.getNpc() + "腳本錯誤 ");
                }
                System.err.println("執行NPC腳本異常. NPC ID : " + cm.getNpc() + ":" + e);
                dispose(c);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行NPC腳本異常, NPC ID : " + cm.getNpc() + "." + e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 0, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc + " startquest " + quest);
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("執行任務腳本異常. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行任務腳本異常. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示]您已經建立與任務腳本:" + cm.getQuest() + "的往來。");
            }
            System.err.println("執行任務腳本異常. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行任務腳本異常. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 1, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc + " endquest " + quest);
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (ScriptException | NoSuchMethodException e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示]任務腳本:" + quest + "錯誤...NPC: " + npc);
            }
            System.err.println("執行任務腳本異常. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行任務腳本異常. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示]任務腳本:" + cm.getQuest() + "錯誤...NPC: " + cm.getNpc() + ":" + e);
            }
            System.err.println("執行任務腳本異常. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行任務腳本異常. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                if (npccm.getNpcMode() == 0) {
                    if (npccm.getScript() == null) {
                        c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                    } else {
                        c.removeScriptEngine("scripts/npc/" + npccm.getScript() + ".js");
                    }
                } else {
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + "_" + npccm.getNpcMode() + ".js");
                }

            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public void reloadScripts() {
        cms.clear();
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }

    public final void start(final MapleClient c, final int npc) {
        start(c, npc, 0);
    }

    public final void start(final MapleClient c, final int npc, final String script) {
        start(c, npc, 0, script);
    }

    public final void start(final MapleClient c, final int npc, final int npcMode) {
        start(c, npc, npcMode, null);
    }

    public final void start(final MapleClient c, final int npc, final int npcMode, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (cms.containsKey(c)) {
                dispose(c);
            }

            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示]您已經建立與NPC:" + npc + "的對話。");
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("npc/" + npc + ".js", c, true);
                if (npcMode == 0) {
                    if (script != null) {
                        iv = getInvocable("npc/" + script + ".js", c, true);
                    } else {
                        iv = getInvocable("npc/" + npc + ".js", c, true);
                    }
                } else {
                    iv = getInvocable("npc/" + npc + "_" + npcMode + ".js", c, true);
                }
                if (iv == null) {
                    if (npcMode == 0) {
                        iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + npc + ".js", c, true);
                    } else {
                        iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/" + npc + "_" + npcMode + ".js", c, true);
                    }
                    if (iv == null) {
                        if (npcMode == 0) {
                            iv = getInvocable("npc/default/" + npc + ".js", c, true);
                        } else {
                            iv = getInvocable("npc/default/" + npc + "_" + npcMode + ".js", c, true);
                        }
                        //safe disposal
                        if (iv == null) {
                            iv = getInvocable("npc/notcoded.js", c, true); //safe disposal
                            if (iv == null) {
                                iv = getInvocable("npc/world" + (c.getPlayer() != null ? c.getPlayer().getWorld() : WorldConstants.defaultserver) + "/notcoded.js", c, true); //safe disposal
                                if (iv == null) {
                                    dispose(c);
                                    return;
                                }
                            }
                        }
                    }
                }
                ScriptEngine scriptengine = (ScriptEngine) iv;
                NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte) -1, iv);
                cm = new NPCConversationManager(c, npc, -1, -1, (byte) -1, npcMode == 0 ? 0 : npcMode, script, iv);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("NPC 腳本錯誤, 它ID為 : " + npc + "." + e);
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(-1, "[系統提示] NPC " + npc + "腳本錯誤");
            }
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

}
