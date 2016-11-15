package io.muic.dcom.p2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
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
            return Long.compare(this.getTimeStamp(),a.getTimeStamp());
        }


        public String getParcelId() { return parcelId; }
        public String getStationId() { return stationId; }
        public long getTimeStamp() { return timeStamp; }
    }

    private ConcurrentHashMap<String, ConcurrentSkipListSet<ParcelObserved>> transactions_trail;
    //stationID, long or count
    private ConcurrentHashMap<String, Long> transactions_stop_count;
    DataModel() {
        transactions_trail = new ConcurrentHashMap<>();
        transactions_stop_count = new ConcurrentHashMap<>();
    }
    public void update_stop_count(String stationId){
        if (transactions_stop_count.containsKey(stationId)){
            Long oldCount=transactions_stop_count.get(stationId)+1L;
            transactions_stop_count.put(stationId, oldCount);
        }
        else{
            transactions_stop_count.put(stationId, 1L);
        }

    }

    public void postObserve(String parcelId, String stationId, long timestamp) {
        ParcelObserved parcelObserved = new ParcelObserved(parcelId, stationId, timestamp);
        //parcelId already exist add the object then add count
        if (transactions_trail.containsKey(parcelId)){
            transactions_trail.get(parcelId).add(parcelObserved);
            update_stop_count(stationId);
        }
        else {
            transactions_trail.put(parcelId, new ConcurrentSkipListSet<ParcelObserved>(){{
                add(parcelObserved);
            }});
            update_stop_count(stationId);
        }
    }

    public List<ParcelObserved> getParcelTrail(String parcelId) {
        return new ArrayList<>(transactions_trail.get(parcelId));
    }

    public long getStopCount(String stationId) {
        return transactions_stop_count.get(stationId);

    }
}
