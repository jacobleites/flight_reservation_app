package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlightItinerary {
    private final List<FlightInstance> segments;

    public FlightItinerary(List<FlightInstance> segments) {
        this.segments = Collections.unmodifiableList(new ArrayList<>(segments));
    }

    public List<FlightInstance> getSegments() {
        return segments;
    }

    public int getStopsCount() {
        return Math.max(0, segments.size() - 1);
    }

    public FlightInstance getFirstSegment() {
        return segments.get(0);
    }

    public FlightInstance getLastSegment() {
        return segments.get(segments.size() - 1);
    }

    public double getTotalPrice() {
        double total = 0;
        for (FlightInstance segment : segments) {
            total += segment.getPrice();
        }
        return total;
    }
}
