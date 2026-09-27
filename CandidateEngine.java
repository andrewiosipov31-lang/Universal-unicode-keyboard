package com.example.universalkbd;

import android.content.Context;import java.util.*;
public final class CandidateEngine {
    private CandidateEngine(){}
    private static final Map<String,String[]> CUNEIFORM=new LinkedHashMap<>();
    static{CUNEIFORM.put("a",new String[]{"𒀀"});CUNEIFORM.put("e",new String[]{"𒂊"});CUNEIFORM.put("i",new String[]{"𒄿"});CUNEIFORM.put("u",new String[]{"𒌋"});CUNEIFORM.put("an",new String[]{"𒀭"});CUNEIFORM.put("en",new String[]{"𒂗"});CUNEIFORM.put("ki",new String[]{"𒆠"});CUNEIFORM.put("lu",new String[]{"𒇽"});CUNEIFORM.put("šam",new String[]{"𒊭"});CUNEIFORM.put("šarru",new String[]{"𒈗"});CUNEIFORM.put("mātu",new String[]{"𒆳"});CUNEIFORM.put("awīlu",new String[]{"𒌉𒇽"});CUNEIFORM.put("bēlu",new String[]{"𒂗"});CUNEIFORM.put("ilum",new String[]{"𒀭"});}
    public static List<String> getCandidates(Context c,String language,String q){LinkedHashSet<String> out=new LinkedHashSet<>();if(q==null||q.isEmpty())return new ArrayList<>();String n=q.toLowerCase(Locale.ROOT);if(language.contains("клинопись")){for(Map.Entry<String,String[]>e:CUNEIFORM.entrySet())if(e.getKey().startsWith(n)||n.startsWith(e.getKey()))Collections.addAll(out,e.getValue());} else if(language.contains("пиньинь")){Map<String,String[]> p=Map.of("ni",new String[]{"你"},"hao",new String[]{"好"},"shi",new String[]{"是"},"wo",new String[]{"我"},"zhong",new String[]{"中"},"guo",new String[]{"国"});for(Map.Entry<String,String[]>e:p.entrySet())if(e.getKey().startsWith(n))Collections.addAll(out,e.getValue());} else if(language.contains("ромадзи")){out.add(q);}return new ArrayList<>(out);}
}
