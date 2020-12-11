package utils;

import org.checkerframework.checker.units.qual.K;

import java.util.Comparator;
import java.util.Map;

public class MapEntryComparator implements Comparator<Map.Entry<String,Float>> {
    @Override
    public int compare(Map.Entry<String,Float> o, Map.Entry<String,Float> t1) {
        if(o.getValue() > t1.getValue())
            return 1;

        if(o.getValue() < t1.getValue())
            return -1;

        return 0;
    }
}
