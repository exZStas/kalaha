package com.exstas.kalaha.model;

import com.exstas.kalaha.constants.Player;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

import static com.exstas.kalaha.constants.PitConstants.AMOUNT_OF_PITS;

@Data
public class Kalaha {

    private String id;
    private int stonesPerPit;
    private List<Pit> pits;
    private Player activePlayer;
    private Player winner;

    public int getAmountOfStonesInGame() {
        return stonesPerPit * AMOUNT_OF_PITS;
    }

    public Pit getPitHouseForPlayer(Player player) {
        return pits.stream().filter(pit -> pit.getId() == player.getPlayerPitHouseId()).findFirst().orElse(null);
    }

}
