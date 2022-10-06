package constants;

public enum Occupations {

    None(0), // fixes the possibility of returning null by default 0
    Pioneer(1),
    Sniper(100),
    Leprechaun(200),
    NX_Addict(300),
    Hacker(400),
    Eric_IdoL(500),
    The_Transformers_AutoBots(600),
    Smega_Whore(700),
    Terrorist(800),
    TrollMaster(9001);

    final int jobid;

    private Occupations(int id) {
        jobid = id;
    }

    public int getId() {
        return jobid;
    }

    public boolean is(Occupations job) {
        return getId() >= job.getId() && getId() / 10 == job.getId() / 10;
    }

    public static Occupations getById(int id) {
        for (Occupations i : Occupations.values()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public static String getNameById(int id) {
        switch (id) {
            case 0:
                return "無";
            case 1:
                return "先驅者";
            case 100:
                return "狙擊手";//Sniper
            case 200:
                return "小精靈";//Leprechaun
            case 300:
                return "冰火師";//NX Addict
            case 400:
                return "高手"; //Hacker
            case 500:
                return "Eric IdoL"; // 不可能
            case 600:
                return "變型金剛";//The Transformers AutoBots
            case 700:
                return "大姊頭"; //Smega Whore
            case 800:
                return "恐怖分子";//Terorrist
            case 9001:
                return "Troll Master"; // 不可能
        }
        return "{{職業為 -1}}"; // -1, cause 0 = none and 1 = default as Wizer
    }

    public static final String toString(final String occName) {
        StringBuilder builder = new StringBuilder(occName.length() + 1);
        for (String word : occName.split("_")) {
            if (word.length() <= 2) {
                builder.append(word); // assume that it's an abbrevation
            } else {
                builder.append(word.charAt(0));
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        return builder.substring(0, occName.length());
    }
}
