package tools.wztosql;

import java.io.FileOutputStream;
import java.io.IOException;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

public class ItemIdCreator {

    public static void main(String args[]) throws IOException {
        if (System.getProperty("net.sf.odinms.wzpath") == null) {
            System.setProperty("net.sf.odinms.wzpath", "wz");
        }
        MapleDataProvider weaponSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Weapon"));
        MapleDataProvider capSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Cap"));
        MapleDataProvider accessorySource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Accessory"));
        MapleDataProvider capeSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Cape"));
        MapleDataProvider coatSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Coat"));
        MapleDataProvider dragonSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Dragon"));
        MapleDataProvider gloveSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Glove"));
        MapleDataProvider longcoatSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Longcoat"));
        MapleDataProvider mechanicSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Mechanic"));
        MapleDataProvider pantsSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Pants"));
        MapleDataProvider petequipSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/PetEquip"));
        MapleDataProvider ringSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Ring"));
        MapleDataProvider shieldSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Shield"));
        MapleDataProvider shoesSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Shoes"));
        MapleDataProvider tamingmobSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/TamingMob"));
        // MapleDataProvider andriodSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Andriod"));
        MapleDataProvider andriodSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Andriod"));
        MapleDataProvider familiarSource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Character.wz/Familiar"));

        final MapleDataDirectoryEntry root = weaponSource.getRoot();
        StringBuilder sb = new StringBuilder();
        FileOutputStream out = new FileOutputStream("ItemID-Windyboy.txt", false);
        System.out.println("???????????? !");
        sb.append("??????:\r\n");
        root.getFiles().stream().map((topDir) -> Integer.parseInt(topDir.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        System.out.println("??????????????????!");
        System.out.println("??????????????????!");
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root2 = capSource.getRoot();
        root2.getFiles().stream().map((topDir2) -> Integer.parseInt(topDir2.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root3 = accessorySource.getRoot();
        root3.getFiles().stream().map((topDir3) -> Integer.parseInt(topDir3.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("????????????:\r\n");
        final MapleDataDirectoryEntry root4 = capeSource.getRoot();
        root4.getFiles().stream().map((topDir4) -> Integer.parseInt(topDir4.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("???????????????!");
        sb.append("?????????:\r\n");
        final MapleDataDirectoryEntry root16 = andriodSource.getRoot();
        root16.getFiles().stream().map((topDir16) -> Integer.parseInt(topDir16.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root5 = coatSource.getRoot();
        root5.getFiles().stream().map((topDir5) -> Integer.parseInt(topDir5.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("?????????!");
        sb.append("???:\r\n");
        final MapleDataDirectoryEntry root6 = dragonSource.getRoot();
        root6.getFiles().stream().map((topDir6) -> Integer.parseInt(topDir6.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root7 = gloveSource.getRoot();
        root7.getFiles().stream().map((topDir7) -> Integer.parseInt(topDir7.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root8 = longcoatSource.getRoot();
        root8.getFiles().stream().map((topDir8) -> Integer.parseInt(topDir8.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("???????????? !");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root9 = mechanicSource.getRoot();
        root9.getFiles().stream().map((topDir9) -> Integer.parseInt(topDir9.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root10 = pantsSource.getRoot();
        root10.getFiles().stream().map((topDir10) -> Integer.parseInt(topDir10.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("?????? Familiar!");
        sb.append("Familiar:\r\n");
        final MapleDataDirectoryEntry root17 = familiarSource.getRoot();
        root17.getFiles().stream().map((topDir17) -> Integer.parseInt(topDir17.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("??????????????????!");
        sb.append("????????????:\r\n");
        final MapleDataDirectoryEntry root11 = petequipSource.getRoot();
        root11.getFiles().stream().map((topDir11) -> Integer.parseInt(topDir11.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root12 = ringSource.getRoot();
        root12.getFiles().stream().map((topDir12) -> Integer.parseInt(topDir12.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root13 = shieldSource.getRoot();
        root13.getFiles().stream().map((topDir13) -> Integer.parseInt(topDir13.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("????????????!");
        sb.append("??????:\r\n");
        final MapleDataDirectoryEntry root14 = shoesSource.getRoot();
        root14.getFiles().stream().map((topDir14) -> Integer.parseInt(topDir14.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        System.out.println("?????? Taming Mobs!");
        sb.append("Taming Mobs:\r\n");
        final MapleDataDirectoryEntry root15 = tamingmobSource.getRoot();
        root15.getFiles().stream().map((topDir15) -> Integer.parseInt(topDir15.getName().substring(0, 8))).forEachOrdered((id) -> {
            sb.append(id).append(", ");
        });
        sb.append("\r\n\r\n");
        sb.append("????????????????????? ID?????????????????????BAT??????????????????.");
        out.write(sb.toString().getBytes());
    }
}
