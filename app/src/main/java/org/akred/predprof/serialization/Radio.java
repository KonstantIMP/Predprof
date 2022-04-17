package org.akred.predprof.serialization;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

import java.util.ArrayList;
import java.util.List;

public class Radio {
    public int id = 0;

    public List<Double> coords = new ArrayList<>();
    public List<Swan> swans = new ArrayList<>();

    public Radio() {}
}
