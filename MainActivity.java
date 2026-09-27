package com.example.universalkbd;

import android.app.*;import android.content.*;import android.graphics.Color;import android.os.Bundle;import android.provider.Settings;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root; private TextView current; private final int GREEN=Color.rgb(46,155,87);
    @Override protected void onCreate(Bundle b){super.onCreate(b); build();}
    private void build(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(14),dp(14),dp(14),dp(14));root.setBackgroundColor(Color.rgb(241,255,244));
        TextView h=t("Universal Keyboard",24,true); root.addView(h,new LinearLayout.LayoutParams(-1,dp(58)));
        TextView d=t("Универсальная Android-клавиатура с языками, диакритиками, символами, эмодзи, пользовательскими раскладками и шумерско-аккадской клинописью.",14,false); d.setPadding(0,0,0,dp(8)); root.addView(d,new LinearLayout.LayoutParams(-1,dp(74)));
        addButton("1. Открыть настройки клавиатур",v->startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        addButton("2. Выбрать Universal Keyboard",v->{InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);if(imm!=null)imm.showInputMethodPicker();});
        current=t("",14,false); root.addView(current,new LinearLayout.LayoutParams(-1,dp(44))); refresh();
        addButton("➕ Создать пользовательскую клавиатуру",v->createCustom());
        ListView list=new ListView(this); ArrayAdapter<String> a=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1){public int getCount(){return KeyboardLayouts.languages(MainActivity.this).size();} public String getItem(int p){return KeyboardLayouts.languages(MainActivity.this).get(p);} public View getView(int p,View c,android.view.ViewGroup parent){TextView x=(TextView)super.getView(p,c,parent);x.setTextColor(Color.BLACK);x.setTextSize(16);x.setPadding(dp(10),dp(8),dp(10),dp(8));x.setBackgroundColor(Color.rgb(241,255,244));return x;}};
        list.setAdapter(a); list.setOnItemClickListener((p,v,pos,id)->{String l=a.getItem(pos);getSharedPreferences("universal_keyboard",MODE_PRIVATE).edit().putString("language",l).apply();refresh();}); root.addView(list,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }
    private void refresh(){String l=getSharedPreferences("universal_keyboard",MODE_PRIVATE).getString("language","Русский");current.setText("Выбрано: "+l);}
    private void addButton(String s,View.OnClickListener l){TextView b=t(s,15,false);b.setGravity(Gravity.CENTER);b.setBackgroundColor(Color.WHITE);b.setOnClickListener(l);b.setPadding(dp(8),0,dp(8),0);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.setMargins(0,dp(4),0,dp(4));root.addView(b,p);}
    private void createCustom(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(12),0,dp(12),0);
        EditText n=new EditText(this);n.setHint("Название");box.addView(n,new LinearLayout.LayoutParams(-1,dp(56)));
        EditText rows=new EditText(this);rows.setHint("Строки, например:\nq w e r t y u i o p\na s d f g h j k l\nz x c v b n m");rows.setMinLines(4);box.addView(rows,new LinearLayout.LayoutParams(-1,dp(130)));
        new AlertDialog.Builder(this).setTitle("Новая пользовательская клавиатура").setView(box).setNegativeButton("Отмена",null).setPositiveButton("Сохранить",(di,w)->{String name=n.getText().toString().trim();String data=rows.getText().toString().trim();if(name.isEmpty()||data.isEmpty())return;getSharedPreferences("uk_custom",MODE_PRIVATE).edit().putString("names",getSharedPreferences("uk_custom",MODE_PRIVATE).getString("names","")+"\n"+name).putString("layout_"+name,data).apply();build();}).show();
    }
    private TextView t(String s,float z,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextColor(Color.BLACK);v.setTextSize(z);if(bold)v.setTypeface(null,1);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
}
