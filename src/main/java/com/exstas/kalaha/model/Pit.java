package com.exstas.kalaha.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pit {
    private int id;
    private int stones;

    public int seizeStones() {
        int seizedStones = this.stones;
        this.stones = 0;

        return seizedStones;
    }

    public boolean isEmpty() {
        return stones == 0;
    }

    public void sow() {
        this.stones++;
    }

    @Override
    public String toString() {
        return id + ":" + stones;
    }
}
