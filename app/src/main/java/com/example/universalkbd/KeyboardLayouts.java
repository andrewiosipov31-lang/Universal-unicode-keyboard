package com.example.universalkbd;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.*;

public final class KeyboardLayouts {
    private KeyboardLayouts() {}

    public static final String SYMBOLS = "Символы";
    public static final String EMOJI = "Эмодзи";
    public static final String CUNEIFORM = "Шумерско-аккадская клинопись";

    public static final String[] LANGUAGES = {
        "Русский", "Украинский", "Белорусский", "Болгарский",
        "Казахский (кириллица)", "Казахский (латиница)",
        "Сербский (кириллица)", "Сербский (латиница)",
        "Узбекский (кириллица)", "Узбекский (латиница)",
        "Польский", "Испанский", "Немецкий", "Греческий",
        "Универсальная латиница", "English (US)",
        CUNEIFORM, SYMBOLS, EMOJI
    };

    public static List<String> languages(Context context) {
        ArrayList<String> out = new ArrayList<>(Arrays.asList(LANGUAGES));
        SharedPreferences p = context.getSharedPreferences("uk_custom", Context.MODE_PRIVATE);
        String names = p.getString("names", "");
        if (!names.isEmpty()) for (String n : names.split("\\n")) if (!n.trim().isEmpty()) out.add("Пользовательская: " + n.trim());
        return out;
    }

    public static List<List<KeySpec>> get(Context context, String language) {
        if (language.equals(SYMBOLS)) return symbolRows();
        if (language.equals(EMOJI)) return emojiRows();
        if (language.equals(CUNEIFORM)) return cuneiformRows();
        if (language.startsWith("Пользовательская: ")) return customRows(context, language.substring("Пользовательская: ".length()));
        if (language.equals("Русский")) return rows("й ц у к е н г ш щ з х ъ", "ф ы в а п р о л д ж э", "я ч с м и т ь б ю");
        if (language.equals("Украинский")) return rows("й ц у к е н г ш щ з х ї ґ", "ф і в а п р о л д ж є", "я ч с м и т ь б ю");
        if (language.equals("Белорусский")) return rows("й ц у к е н г ш ў з х", "ф ы в а п р о л д ж э", "я ч с м і т ь б ю");
        if (language.equals("Болгарский")) return rows("у е и ш щ к с з ь ц", "ф ы в а п р о л д ж т", "я ч й н м б ю г х");
        if (language.equals("Казахский (кириллица)")) return rows("й ц у к е н г ш щ з х һ і", "ф ы в а п р о л д ж э ө", "я ч с м и т ь б ю қ ң ә ғ ү ұ");
        if (language.equals("Казахский (латиница)")) return rows("q w e r t y u i o p ǵ ń", "a s d f g h j k l ó", "z x c v b n m ú ü á");
        if (language.equals("Сербский (кириллица)")) return rows("љ њ е р т з у и о п ш ђ", "а с д ф г х ј к л ч ћ", "џ ц в б н м");
        if (language.equals("Сербский (латиница)")) return rows("q w e r t z u i o p š đ", "a s d f g h j k l č ć", "y x c v b n m ž");
        if (language.equals("Узбекский (кириллица)")) return rows("й у к е н г қ ғ ҳ", "ф з х о в а п р л д", "ж б с т м и қ ч ш");
        if (language.equals("Узбекский (латиница)")) return rows("q w e r t y u i o p o‘ g‘", "a s d f g h j k l sh ch", "z x c v b n m");
        if (language.equals("Польский")) return rows("q w e r t y u i o p ó", "a s d f g h j k l ł", "z x c v b n m ą ę ś ć");
        if (language.equals("Испанский")) return rows("q w e r t y u i o p", "a s d f g h j k l ñ", "z x c v b n m");
        if (language.equals("Немецкий")) return rows("q w e r t z u i o p ü", "a s d f g h j k l ö ä", "y x c v b n m ß");
        if (language.equals("Греческий")) return rows("ς ε ρ τ υ θ ι ο π", "α σ δ φ γ η ξ κ λ", "ζ χ ψ ω β ν μ");
        return latinRows();
    }

    private static List<List<KeySpec>> customRows(Context c, String name) {
        SharedPreferences p = c.getSharedPreferences("uk_custom", Context.MODE_PRIVATE);
        String data = p.getString("layout_" + name, "q w e r t y u i o p\\na s d f g h j k l\\nz x c v b n m");
        ArrayList<List<KeySpec>> out = new ArrayList<>();
        for (String row : data.split("\\n")) {
            if (!row.trim().isEmpty()) {
                ArrayList<KeySpec> r = new ArrayList<>();
                for (String s : row.trim().split("\\s+")) r.add(new KeySpec(s, s));
                out.add(r);
            }
        }
        return out;
    }

    private static List<List<KeySpec>> latinRows() { return rows("q w e r t y u i o p", "a s d f g h j k l", "z x c v b n m"); }

    private static List<List<KeySpec>> rows(String... textRows) {
        ArrayList<List<KeySpec>> result = new ArrayList<>();
        for (String row : textRows) {
            ArrayList<KeySpec> line = new ArrayList<>();
            for (String s : row.split(" ")) line.add(new KeySpec(s, s));
            result.add(line);
        }
        return result;
    }

    private static List<List<KeySpec>> symbolRows() {
        return rows(
            "1 2 3 4 5 6 7 8 9 0 - + = _ / *",
            "@ # % & ; : . , ? ! ' \" ;  ₽ € $ £ ¥",
            "( ) [ ] { } < > « » “ ” „ ‘ ’ ` ~ • °",
            "✓ ® © ™ § × ÷ ± ≠ ≤ ≥ √ ∞ ∑ ∆ §"
        );
    }

    private static List<List<KeySpec>> emojiRows() {
        return rows(
            "🙂 🙃 😄 🤩 🙁 ☹️ 😢 😭",
            "😟 😨 😱 😠 😡 🤬 😕 🤢",
            "🤮 😯 😮 😲 😐 😳 🤨 🤓",
            "👍 👎 🙏 ❤️ ⭐ ✅ ❌ 🔥 ✨"
        );
    }

    private static List<List<KeySpec>> cuneiformRows() {
        return rows(
            "𒀸 𒁹 𒀺 𒀹 𒌋 𒀀 𒂊 𒄿 𒌋 𒀭",
            "𒂗 𒆠 𒇽 𒀊 𒈗 𒆳 𒊭 𒋗 𒌣 𒀴",
            "𒀵 𒀼 𒁀 𒁍 𒁺 𒂊 𒄑 𒅗 𒅗 𒆠",
            "a e i u an en ki lu ab šam šarru mātu bēlu ilum"
        );
    }

    public static Map<String,String[]> popups(String language) {
        Map<String,String[]> m = new HashMap<>();
        put(m,"a","á","à","â","ä","ã","å","ā","ă","ą","ǎ");
        put(m,"e","é","è","ê","ë","ē","ė","ę","ě");
        put(m,"i","í","ì","î","ï","ī","į","ǐ");
        put(m,"o","ó","ò","ô","ö","õ","ø","ō","ő","ǒ");
        put(m,"u","ú","ù","û","ü","ū","ů","ǔ","ǖ","ǘ","ǚ","ǜ");
        put(m,"c","ç","ć","č","ĉ"); put(m,"n","ñ","ń","ň","ņ");
        put(m,"s","ś","š","ş","ß"); put(m,"z","ž","ź","ż");
        put(m,"l","ł","ľ"); put(m,"d","ð","ď"); put(m,"g","ğ","ģ");
        put(m,"r","ř"); put(m,"t","ť","þ"); put(m,"h","ħ"); put(m,"y","ý","ÿ","ŷ");
        if (language.equals("Арабский")) put(m,"ا","َ","ً","ُ","ٌ","ِ","ٍ","ْ","ّ","ٰ");
        return m;
    }

    public static String[] punctuationPopups() {
        return new String[]{".",",","!","?","…",";",":","-","–","—","/","\\","(",")","[","]","{","}","'","\"","«","»","“","”","„","`","~","•","°","+","=","_","%","&","@","₽","€","$","£","¥","✓","®","©","™"};
    }

    private static void put(Map<String,String[]> m,String k,String... v){m.put(k,v);}

    public static class KeySpec {
        public final String label; public final String output; public final String[] popup;
        public KeySpec(String l,String o){this(l,o,null);} public KeySpec(String l,String o,String... p){label=l;output=o;popup=p;}
    }
}
