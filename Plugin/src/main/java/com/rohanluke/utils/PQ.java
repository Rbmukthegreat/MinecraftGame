package com.rohanluke.utils;

import org.bukkit.entity.Player;

import java.util.PriorityQueue;

public class PQ extends PriorityQueue<Pair<Player, Integer>> {
    public PQ(int initialCapacity, PairComparator pairComparator) {
        super(initialCapacity, pairComparator);
    }
    public PQ(PQ pq) {
        super(pq);
    }

    public Integer get(Player p) {
        for (Pair<Player, Integer> entry : this) {
            if (entry.first == p) {
                return entry.second;
            }
        }
        throw new NullPointerException();
    }
}
