package com.example.universalkbd;

import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.view.*;
import android.view.inputmethod.InputConnection;
import android.widget.*;
import java.util.*;

public class KeyboardService extends InputMethodService {
    private static final int BG = Color.rgb(225,225,225);
    private static final int WHITE = Color.WHITE;
    private static final int BLACK = Color.BLACK;
    private static final int PRESSED = Color.rgb(150,150,150);
    private static final int PRESSED_TEXT = Color.rgb(225,225,225);
    private static final int GREEN = Color.rgb(46,155,87);
    private String language = "Русский";
    private LinearLayout root;
    private LinearLayout rowsBox;
    private TextView title;
    private final Handler handler = new Handler();

    @Override public View onCreateInputView() { return buildKeyboard(); }

    private View buildKeyboard() {
        language = getSharedPreferences("universal_keyboard", MODE_PRIVATE).getString("language", "Русский");
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(4),dp(4),dp(4),dp(4)); root.setBackgroundColor(BG);
        title = new TextView(this); title.setText(language); title.setTextColor(BLACK); title.setTextSize(12); title.setGravity(Gravity.CENTER); root.addView(title, new LinearLayout.LayoutParams(-1,dp(28)));
        LinearLayout candidate = new LinearLayout(this); candidate.setGravity(Gravity.CENTER_VERTICAL); candidate.setBackgroundColor(BG);
        addTop(candidate,"⌫",v -> deleteOne()); addTop(candidate,"🌐",v -> cycleLanguage()); addTop(candidate,"⚙",v -> showLanguagePopup()); addTop(candidate,"□",v -> setLayout(KeyboardLayouts.SYMBOLS)); addTop(candidate,"🙂",v -> setLayout(KeyboardLayouts.EMOJI));
        root.addView(candidate,new LinearLayout.LayoutParams(-1,dp(36)));
        rowsBox = new LinearLayout(this); rowsBox.setOrientation(LinearLayout.VERTICAL); root.addView(rowsBox,new LinearLayout.LayoutParams(-1,0,1));
        renderRows(KeyboardLayouts.get(this, language));
        LinearLayout bottom = new LinearLayout(this); bottom.setOrientation(LinearLayout.HORIZONTAL);
        addAction(bottom,"123",v -> setLayout(KeyboardLayouts.SYMBOLS),1); addAction(bottom,"😀",v -> setLayout(KeyboardLayouts.EMOJI),1); addAction(bottom,"␣",v -> commit(" "),3); addAction(bottom,"↵",v -> commit("\n"),1);
        root.addView(bottom,new LinearLayout.LayoutParams(-1,dp(48)));
        return root;
    }

    private void renderRows(List<List<KeyboardLayouts.KeySpec>> rows) {
        if (rowsBox == null) return;
        rowsBox.removeAllViews();
        for (List<KeyboardLayouts.KeySpec> row : rows) {
            LinearLayout line = new LinearLayout(this); line.setOrientation(LinearLayout.HORIZONTAL); line.setGravity(Gravity.CENTER); line.setPadding(0,dp(2),0,dp(2));
            for (KeyboardLayouts.KeySpec k : row) addKey(line,k,1f);
            rowsBox.addView(line,new LinearLayout.LayoutParams(-1,dp(46)));
        }
    }

    private void addKey(LinearLayout line, KeyboardLayouts.KeySpec spec,float weight) {
        TextView v = keyView(spec.label); line.addView(v,new LinearLayout.LayoutParams(0,-1,weight));
        v.setOnTouchListener((view,e)-> {
            if (e.getAction()==MotionEvent.ACTION_DOWN) setState(v,true,false);
            else if (e.getAction()==MotionEvent.ACTION_UP || e.getAction()==MotionEvent.ACTION_CANCEL) setState(v,false,false);
            return false;
        });
        v.setOnClickListener(x -> commit(spec.output));
        v.setOnLongClickListener(x -> { String[] p = spec.popup != null ? spec.popup : KeyboardLayouts.popups(language).get(spec.output); if (p==null && (spec.label.length()<=2)) p=KeyboardLayouts.punctuationPopups(); if (p==null) return false; showPopup(v,p); return true; });
    }

    private void addTop(LinearLayout bar,String s,View.OnClickListener l){ TextView v=keyView(s); v.setTextSize(15); v.setOnClickListener(l); bar.addView(v,new LinearLayout.LayoutParams(0,-1,1)); }
    private void addAction(LinearLayout bar,String s,View.OnClickListener l,float w){ TextView v=keyView(s); v.setOnClickListener(l); bar.addView(v,new LinearLayout.LayoutParams(0,-1,w)); }

    private TextView keyView(String text){
        TextView v=new TextView(this); v.setText(text); v.setTextColor(BLACK); v.setTextSize(19); v.setGravity(Gravity.CENTER); v.setPadding(dp(2),0,dp(2),0); v.setBackground(bg(WHITE)); return v;
    }
    private GradientDrawable bg(int c){ GradientDrawable d=new GradientDrawable(); d.setColor(c); d.setCornerRadius(dp(8)); return d; }
    private void setState(TextView v,boolean pressed,boolean popup){ v.setBackground(bg(pressed?(popup?GREEN:PRESSED):WHITE)); v.setTextColor(pressed?(popup?WHITE:PRESSED_TEXT):BLACK); }

    private void showPopup(View anchor,String[] choices){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.HORIZONTAL); box.setPadding(dp(6),dp(6),dp(6),dp(6)); box.setBackground(bg(WHITE));
        PopupWindow pw=new PopupWindow(box,dp(Math.min(330,70+choices.length*42)),dp(58),true); pw.setBackgroundDrawable(bg(WHITE)); pw.setOutsideTouchable(true); pw.setElevation(dp(10));
        for(String c:choices){ TextView b=keyView(c); b.setTextSize(18); box.addView(b,new LinearLayout.LayoutParams(dp(48),dp(46))); b.setOnTouchListener((vv,e)->{ if(e.getAction()==MotionEvent.ACTION_DOWN)setState(b,true,true); else if(e.getAction()==MotionEvent.ACTION_CANCEL)setState(b,false,true); return false;}); b.setOnClickListener(v->{setState(b,true,true); commit(c); handler.postDelayed(pw::dismiss,80);}); }
        pw.showAsDropDown(anchor,0,-anchor.getHeight()-dp(60));
    }

    private void showLanguagePopup(){
        List<String> langs=KeyboardLayouts.languages(this); ScrollView sv=new ScrollView(this); LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(8),dp(8),dp(8),dp(8)); sv.addView(box); PopupWindow pw=new PopupWindow(sv,dp(310),dp(460),true); pw.setBackgroundDrawable(bg(WHITE)); pw.setOutsideTouchable(true); pw.setElevation(dp(10));
        for(String l:langs){ TextView b=keyView(l); b.setTextSize(14); b.setGravity(Gravity.CENTER_VERTICAL); b.setPadding(dp(12),0,dp(12),0); box.addView(b,new LinearLayout.LayoutParams(-1,dp(44))); b.setOnClickListener(v->{language=l; getSharedPreferences("universal_keyboard",MODE_PRIVATE).edit().putString("language",l).apply(); title.setText(l); pw.dismiss(); renderRows(KeyboardLayouts.get(this,l));}); }
        pw.showAtLocation(root,Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,dp(34));
    }

    private void setLayout(String l){ language=l; title.setText(l); renderRows(KeyboardLayouts.get(this,l)); }
    private void cycleLanguage(){ List<String> ls=KeyboardLayouts.languages(this); int i=ls.indexOf(language); language=ls.get((i+1+ls.size())%ls.size()); title.setText(language); getSharedPreferences("universal_keyboard",MODE_PRIVATE).edit().putString("language",language).apply(); renderRows(KeyboardLayouts.get(this,language)); }
    private void deleteOne(){ InputConnection ic=getCurrentInputConnection(); if(ic!=null) ic.deleteSurroundingText(1,0); }
    private void commit(String s){ InputConnection ic=getCurrentInputConnection(); if(ic!=null) ic.commitText(s,1); }
    private int dp(int n){ return Math.round(n*getResources().getDisplayMetrics().density); }
}
