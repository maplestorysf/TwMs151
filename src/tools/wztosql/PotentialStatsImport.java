package tools.wztosql;

import java.io.FileOutputStream;
import java.io.IOException;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

public class PotentialStatsImport {

    public static void main(String args[]) throws IOException {
        MapleDataProvider potStatSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Item.wz"));
        final MapleDataDirectoryEntry root = potStatSource.getRoot();
        StringBuilder sb = new StringBuilder();
        FileOutputStream out = new FileOutputStream("Potential Stats Import.txt", false);
        System.out.println("載入淺能數值!");
        sb.append("淺能數值 :\r\n");
        root.getFiles().stream().map((topDir) -> Integer.parseInt(topDir.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        System.out.println("淺能數值載入完成!");
        sb.append("\r\n\r\n");
        sb.append("載入完所有物品 ID列表將在您啟動BAT檔案的目錄下.");
        // sb.append("The ID's will be located at the same folder where is your .bat launcher.");
        out.write(sb.toString().getBytes());
    }
}
