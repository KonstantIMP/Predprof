package org.akred.predprof.serialization;

import java.util.ArrayList;

public class Anomaly {
    public String id = "";
    public ArrayList<AnomalyData> ranks = new ArrayList<>();

    public Anomaly(String i) {
        id = i;
    }

    public static class AnomalyData {
        public Double x, y, rank;
    }
}
