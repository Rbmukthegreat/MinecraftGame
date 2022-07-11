package com.rohanluke.utils;

import org.bukkit.entity.Player;

import java.util.Comparator;

public class PairComparator implements Comparator<Pair<Player, Integer>> {

    @Override
    public int compare(Pair<Player, Integer> o1, Pair<Player, Integer> o2) {
        return o2.second.compareTo(o1.second);
    }
}