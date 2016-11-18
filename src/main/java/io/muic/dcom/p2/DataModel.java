package io.muic.dcom.p2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DataModel {
    public static class ParcelObserved implements Comparable<ParcelObserved> {
        private String parcelId;
        private String stationId;
        private long timeStamp;

        ParcelObserved(String parcelId_, String stationId_, long ts_) {
            this.parcelId = parcelId_;
            this.stationId = stationId_;
            this.timeStamp = ts_;
        }
        public int compareTo(ParcelObserved a){
            return Long.compare(this.getTimeStamp(),a.getTimeStamp()) != 0 ? Long.compare(this.getTimeStamp(),a.getTimeStamp()) : this.getStationId().compareTo(a.getStationId());
        }

        public String getParcelId() { return parcelId; }
        public String getStationId() { return stationId; }
        public long getTimeStamp() { return timeStamp; }
    }

    private ConcurrentHashMap<String, ConcurrentSkipListSet<ParcelObserved>> transactions_trail;
    //stationID, long or count
    private ConcurrentHashMap<String, AtomicLong> transactions_stop_count;
    DataModel() {
        transactions_trail = new ConcurrentHashMap<>();
        transactions_stop_count = new ConcurrentHashMap<>();
    }
    public void update_stop_count(String stationId){
        AtomicLong alvalue= transactions_stop_count.putIfAbsent(stationId, new AtomicLong(1));
        if (alvalue != null){
            alvalue.getAndIncrement();
        }
    }

    public void postObserve(String parcelId, String stationId, long timestamp) {
        ParcelObserved parcelObserved = new ParcelObserved(parcelId, stationId, timestamp);
        //parcelId already exist add the object then add count
        ConcurrentSkipListSet<ParcelObserved> value = transactions_trail.putIfAbsent(parcelId, new ConcurrentSkipListSet<ParcelObserved>(){{
            add(parcelObserved);
        }});
        update_stop_count(stationId);
        if (value!=null){
            value.add(parcelObserved);
        }
    }

    public List<ParcelObserved> getParcelTrail(String parcelId) {
        return new ArrayList<>(transactions_trail.get(parcelId));
    }

    public long getStopCount(String stationId) {
        return transactions_stop_count.get(stationId).longValue();

    }
}
