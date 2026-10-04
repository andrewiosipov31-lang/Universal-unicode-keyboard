package com.example.universalkbd;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ClipboardStore {

    private static final int MAX_UNPINNED = 10;
    private static final int MAX_PINNED = 500;

    private final ClipboardManager clipboardManager;
    private final List<String> unpinned = new ArrayList<>();
    private final List<String> pinned = new ArrayList<>();

    public ClipboardStore(Context context) {
        clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void add(String text) {
        if (text == null || text.isEmpty()) return;

        unpinned.remove(text);
        unpinned.add(0, text);

        while (unpinned.size() > MAX_UNPINNED) {
            unpinned.remove(unpinned.size() - 1);
        }
    }

    public List<String> getUnpinned() {
        return new ArrayList<>(unpinned);
    }

    public List<String> getPinned() {
        return new ArrayList<>(pinned);
    }

    public void pin(String text) {
        if (text == null || text.isEmpty()) return;
        unpinned.remove(text);

        if (!pinned.contains(text)) {
            pinned.add(0, text);
        }

        while (pinned.size() > MAX_PINNED) {
            pinned.remove(pinned.size() - 1);
        }
    }

    public void unpin(String text) {
        if (text == null) return;

        if (pinned.remove(text)) {
            add(text);
        }
    }

    public void delete(String text) {
        unpinned.remove(text);
        pinned.remove(text);
    }

    public void copyToClipboard(String text) {
        if (text == null) return;

        clipboardManager.setPrimaryClip(
                ClipData.newPlainText("Universal Keyboard", text)
        );
    }
}
