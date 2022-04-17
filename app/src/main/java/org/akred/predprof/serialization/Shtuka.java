package org.akred.predprof.serialization;

public class Shtuka {

    public String sensor, anomaly;
    public double rank;

    public Shtuka() {
    }

    public Shtuka(String sensor, String anomaly, double rank) {
        this.sensor = sensor;
        this.anomaly = anomaly;
        this.rank = rank;
    }
}
