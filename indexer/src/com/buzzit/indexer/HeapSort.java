package com.buzzit.indexer;

import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * Created by linwei on 12/6/15.
 */
public class HeapSort {
    public HeapSort() {}
    private List<Tuple2<String, Double>> min_heap = new ArrayList<>();

    private PriorityQueue<Tuple2<String, Double>> min_heap_ = new PriorityQueue<>();

    /*
    public Iterable<Tuple2<String, Double>> filter(Iterable<Tuple2<String, Double>> iter, int k){
        min_heap = new ArrayList<>();
        Iterator<Tuple2<String, Double>> iterator = iter.iterator();
        while(iterator.hasNext()){
            Tuple2<String, Double> tuple = iterator.next();
            if(min_heap.size()<k || larger(tuple, min_heap.get(0))){
                //if heap is full, remove smallest element
                if(min_heap.size() == k) deleteMin();
                insertHeap(tuple);
            }
        }
        return min_heap;
    }
    */

    public Iterable<Tuple2<String, Double>> filter(Iterable<Tuple2<String, Double>> iter, int k) {
        min_heap_ = new PriorityQueue<>(k, DocWeightComparator.INSTANCE);
        for (Tuple2<String, Double> docWeightPair : iter) {
            if (min_heap_.size() >= k) {
                if (min_heap_.peek()._2 < docWeightPair._2) {
                    min_heap_.poll();
                    min_heap_.add(docWeightPair);
                }
            } else {
                min_heap_.add(docWeightPair);
            }
        }
        return new ArrayList<>(min_heap_);
    }

    private static class DocWeightComparator implements Comparator<Tuple2<String, Double>>, Serializable {
        static DocWeightComparator INSTANCE = new DocWeightComparator();
        @Override
        public int compare(Tuple2<String, Double> o1, Tuple2<String, Double> o2) {
            return Double.compare(o1._2(), o2._2());
        }
    }

    private Tuple2<String, Double> deleteMin(){
        System.out.println("***before delete***");
        show();
        Tuple2<String, Double> min = min_heap.get(0);
        exch(1, min_heap.size());
        min_heap.remove(min_heap.size()-1);
        sink(0, min_heap.size());
        show();
        return min;
    }

    private void insertHeap(Tuple2<String, Double> tuple){
        min_heap.add(tuple);
        System.out.println("before swim");
        show();
        swim(min_heap.size());
        show();
    }
    /***************************************************************************
     * Helper functions to restore the heap invariant.
     ***************************************************************************/
    private void swim(int i){
        while(i>1 && !larger(i, i/2)){
            exch(i, i/2);
            i = i/2;
        }
    }

    private void sink (int k, int N){
        k++;
        while(2*k<=N){
            int j = 2*k;
            if(j<N && larger(j,j+1)) j++;
            if(!larger(k,j)) break;
            exch(k,j);
            k = j;
        }
    }

    /***************************************************************************
     * Helper functions for comparisons and swaps.
     * Indices are "off-by-one" to support 1-based indexing.
     ***************************************************************************/
    private boolean larger(int i, int j){
        if(min_heap.get(i-1)._2()>min_heap.get(j-1)._2()) return true;
        else return false;
    }
    private boolean larger(Tuple2<String, Double> t1, Tuple2<String, Double> t2){
        if(t1._2()>t2._2()) return true;
        else return false;
    }

    private void exch(int i, int j){
        Tuple2<String, Double> tmp = min_heap.get(i-1);
        min_heap.set(i-1,min_heap.get(j-1));
        min_heap.set(j-1,tmp);
    }

    /***************************************************************************
     *  print array to standard output
     ***************************************************************************/
    protected void show(){
        // DEBUG
        for(Tuple2<String, Double> t: min_heap_){
            System.out.println("doc: "+ t._1() + " factor: "+ t._2());
        }
        System.out.println("----");
    }

    public static void main(String[] args) {
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
