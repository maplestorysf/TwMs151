package scripting;

import client.MapleClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tools.EncodingDetect;
import tools.FileoutputUtil;

public abstract class AbstractScriptManager {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        InputStream in = null;//   FileReader fr = null;
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("javascript");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                in = new FileInputStream(scriptFile);
                BufferedReader bf = new BufferedReader(new InputStreamReader(in, EncodingDetect.codeString(scriptFile)));
                engine.eval(bf);
                //      fr = new FileReader(scriptFile);
                //       engine.eval(fr);
            } else if (c != null && npc) {
                c.getPlayer().dropMessage(-1, "您當前已經和1個NPC對話了. 如果不是請輸入 @ea 命令進行解卡。");
            }
            return (Invocable) engine;
        } catch (FileNotFoundException | ScriptException e) {
            System.err.println("執行腳本錯誤. 路徑: " + path + "\n例外 " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "執行腳本錯誤. 路徑: " + path + "\n例外 " + e);
            return null;
        } catch (UnsupportedEncodingException ex) {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage("腳本錯誤 :" + ex);
            }
            System.err.println("讀取腳本出錯 - " + ex);
            return null;
        } finally {
            try {
                //  if (fr != null) {
                //     fr.close();
                //  }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
