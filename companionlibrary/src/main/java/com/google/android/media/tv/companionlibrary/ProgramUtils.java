package com.google.android.media.tv.companionlibrary;

import com.google.android.media.tv.companionlibrary.model.Program;

import java.util.List;

public class ProgramUtils {
    public static Program getPlayingNow(List<Program> programs) {
        long timeNow = System.currentTimeMillis();

        for (Program p: programs) {
            if (timeNow >= p.getStartTimeUtcMillis() && timeNow <= p.getEndTimeUtcMillis()) {
                return p;
            }
        }
        return null;
    }
}
