package handling.world;

import client.MapleCharacter;
// import client.inventory.MaplePet;
// import java.util.List;
import java.util.Map;

public interface MapleCharacterLook {

    public byte getGender();

    public byte getSkinColor();

    public int getFace();

    public int getHair();

    public int getDemonMarking();

    public short getJob();

    //public boolean isElf();
    public Map<Byte, Integer> getEquips();

    public boolean isElf(MapleCharacter player);

    // public List<MaplePet> getPets();
    public Map<Byte, Integer> getTotems();
}
