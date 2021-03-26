package com.exstas.kalaha.factory;

import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class KalahaFactory {

    @Autowired
    private PitsFactory pitsFactory;

    public Kalaha createKalaha(String id, int stonesPerPit, Player player) {
        Kalaha kalaha = new Kalaha();
        kalaha.setId(id);
        kalaha.setActivePlayer(player);
        kalaha.setStonesPerPit(stonesPerPit);

        List<Pit> pits = pitsFactory.createPits(stonesPerPit);
        kalaha.setPits(pits);

        return kalaha;
    }
}
