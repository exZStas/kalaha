package com.exstas.kalaha.factory;

import com.exstas.kalaha.model.Pit;

import java.util.ArrayList;
import java.util.List;

import static com.exstas.kalaha.constants.PitConstants.*;
import static com.exstas.kalaha.constants.PitConstants.PIT_6_PLAYER_B;

public class PitsFactory {

    public List<Pit> createPits(int stonesPerPit) {
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(HOUSE_PLAYER_A, 0));
        pits.add(new Pit(PIT_1_PLAYER_A, stonesPerPit));
        pits.add(new Pit(PIT_2_PLAYER_A, stonesPerPit));
        pits.add(new Pit(PIT_3_PLAYER_A, stonesPerPit));
        pits.add(new Pit(PIT_4_PLAYER_A, stonesPerPit));
        pits.add(new Pit(PIT_5_PLAYER_A, stonesPerPit));
        pits.add(new Pit(PIT_6_PLAYER_A, stonesPerPit));

        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        pits.add(new Pit(PIT_1_PLAYER_B, stonesPerPit));
        pits.add(new Pit(PIT_2_PLAYER_B, stonesPerPit));
        pits.add(new Pit(PIT_3_PLAYER_B, stonesPerPit));
        pits.add(new Pit(PIT_4_PLAYER_B, stonesPerPit));
        pits.add(new Pit(PIT_5_PLAYER_B, stonesPerPit));
        pits.add(new Pit(PIT_6_PLAYER_B, stonesPerPit));

        return pits;
    }
}
