package com.buzzit.indexer;

import org.junit.Test;
import scala.Tuple2;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 */
public class HeapSortTest {
    @Test
    public void testHeapSort() {
        Map<String, Double> map = new HashMap<>();
        map.put("a", 3.2);
        map.put("b", 2.2);
        map.put("c", 3.6);
        map.put("d", 3.8);
        map.put("e", 3.1);
        map.put("f", 3.9);
        map.put("g", 3.21);
        map.put("h", 3.5);
        List<Tuple2<String, Double>> l = new ArrayList<>();
        for(String s: map.keySet()){
            Tuple2<String, Double> t = new Tuple2<>(s,map.get(s));
            l.add(t);
        }
        HeapSort sort = new HeapSort();
        sort.filter(l,3);
        sort.show();
    }
}