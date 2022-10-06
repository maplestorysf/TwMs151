
import java.util.LinkedList;
import java.util.List;

public class test {

    public static String UnicodeToChinese(String s) {
        String str = s.replace("|", ",");
        String[] s2 = str.split(",");
        String s1 = "";
        for (int i = 1; i < s2.length; i++) {
            s1 = s1 + (char) Integer.parseInt(s2[i], 16);
        }
        final List<String> lines = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            lines.add(s1);
        }
        for (int z = 0; z < 3; z++) {
            lines.add("");
        }
        return s1;
    }

    public static String ChineseToUnicode(String s) {
        String as[] = new String[s.length()];
        String unicode = "";
        for (int i = 0; i < s.length(); i++) {
            as[i] = Integer.toHexString(s.charAt(i) & 0xffff);
            unicode = unicode + "|" + as[i];
        }
        return unicode;
    }

    public static void main(String[] args) {
        System.out.println(ChineseToUnicode("測試"));
        System.out.println();
        System.out.println(UnicodeToChinese("|6e2c|8a66"));
    }
}
