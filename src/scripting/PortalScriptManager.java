package scripting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import server.MaplePortal;
import tools.FileoutputUtil;

public class PortalScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();
    private final Map<String, PortalScript> scripts = new HashMap<String, PortalScript>();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();

    public final static PortalScriptManager getInstance() {
        return instance;
    }

    private final PortalScript getPortalScript(final String scriptName) {
        if (scripts.containsKey(scriptName)) {
            return scripts.get(scriptName);
        }

        final File scriptFile = new File("scripts/portal/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            return null;
        }

        FileReader fr = null;
        final ScriptEngine portal = sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (final Exception e) {
            System.err.println("請檢查Portal為:(" + scriptName + ".js)的文件.\r\n " + e);
            //    System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Portal script. (" + scriptName + ") " + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (final IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        final PortalScript script = ((Invocable) portal).getInterface(PortalScript.class);
        scripts.put(scriptName, script);
        return script;
    }

    public final void executePortalScript(final MaplePortal portal, final MapleClient c) {
        final PortalScript script = getPortalScript(portal.getScriptName());

        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(-1, "執行傳送腳本: " + portal.getScriptName() + "。");
                }
            } catch (Exception e) {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(-1, "執行傳送腳本過程中發生錯誤.請檢查Portal為:( " + portal.getScriptName() + ".js)的文件");
                }
                System.err.println("執行傳送腳本過程中發生錯誤.請檢查Portal為:( " + portal.getScriptName() + ".js)的文件 \r\n " + e);
            }
        } else {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage(-1, "執行地圖腳本過程中發生錯誤.未找到Portal為:( " + portal.getScriptName() + ".js)的文件");
            }
            System.out.println("執行傳送腳本過程中發生錯誤.未找到Portal為 " + portal.getScriptName() + " 在地圖代碼為: " + c.getPlayer().getMapId());
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
            //    try {
            //        createPortalScript(c,portal.getScriptName(), c.getPlayer().getLastMap(), c.getPlayer().getLastPortal());
            //    } catch (IOException ex) {
            //        ex.printStackTrace();
            //    }
        }
    }

    public final void clearScripts() {
        scripts.clear();
    }
}
