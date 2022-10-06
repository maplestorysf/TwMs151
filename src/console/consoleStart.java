package console;

import java.sql.SQLException;

public class consoleStart {

    public void run() throws SQLException {
        consoleScan.run();
        System.out.println("服務端控制台已啟動");
    }
}
