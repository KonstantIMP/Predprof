package org.akred.predprof.serialization;

import java.util.ArrayList;
import java.util.List;

public class Anomaly {
    public String id = "";
    public ArrayList<AnomalyData> ranks = new ArrayList<>();

    public static class AnomalyData{
        public Double x, y, rank;
    }

    public Anomaly(String i) { id = i; }
}
