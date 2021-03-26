package com.exstas.kalaha.kalaha_move_chain;

import com.exstas.kalaha.model.Kalaha;

public abstract class AbstractKalahaMoveChain {

    AbstractKalahaMoveChain nextChain;

    public void setNextChain(AbstractKalahaMoveChain nextChain) {
        this.nextChain = nextChain;
    }

    public abstract Kalaha processMove(Kalaha kalaha, int stonesToSow, int pitIdToSow);
}
