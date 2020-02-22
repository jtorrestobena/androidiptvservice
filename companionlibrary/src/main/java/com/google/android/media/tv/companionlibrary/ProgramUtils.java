package com.google.android.media.tv.companionlibrary;

import com.google.android.media.tv.companionlibrary.model.Program;

import java.util.List;

public class ProgramUtils {
    public static Program getPlayingNow(List<Program> programs) {
        if (programs != null) {
            long timeNow = System.currentTimeMillis();

            for (Program p : programs) {
                if (timeNow >= p.getStartTimeUtcMillis() && timeNow <= p.getEndTimeUtcMillis()) {
                    return p;
                }
            }
        }

        return null;
    }

    public static List<Program> getUpcomingPrograms(List<Program> programs) {
        final Program playingNow = getPlayingNow(programs);

        if (playingNow != null) {
            return programs.subList(programs.indexOf(playingNow), programs.size());
        }

        return programs;
    }
}
