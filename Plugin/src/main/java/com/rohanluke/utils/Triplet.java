package com.rohanluke.utils;

public class Triplet<K, V, S> {
    public K first;
    public V second;
    public S third;

    public Triplet(K first, V second, S third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}