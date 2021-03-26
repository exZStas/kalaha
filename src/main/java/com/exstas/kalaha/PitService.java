package com.exstas.kalaha;

import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import org.springframework.stereotype.Service;

import static com.exstas.kalaha.constants.PitConstants.*;
import static com.exstas.kalaha.constants.PitConstants.HOUSE_PLAYER_B;
import static com.exstas.kalaha.constants.Player.PLAYER_A;
import static com.exstas.kalaha.constants.Player.PLAYER_B;

@Service
public class PitService {

    public boolean isPitBelongsToPlayer(Player player, int pitId) {
        if(player == PLAYER_A && (pitId >= PIT_1_PLAYER_A && pitId <= HOUSE_PLAYER_A)) {
            return true;
        } else {
            return player == PLAYER_B && (pitId >= PIT_1_PLAYER_B && pitId <= HOUSE_PLAYER_B);
        }
    }

    public Pit getPitById(Kalaha kalaha, int id) {
        return kalaha.getPits().stream().
                filter(pit -> pit.getId() == id).
                findFirst().
                orElse(null);
    }

    public Pit getOppositePitForPit(Kalaha kalaha, Pit pit) {
        int oppositePitId = AMOUNT_OF_PITS_AND_HOUSES - pit.getId();
        return getPitById(kalaha, oppositePitId);
    }

    public boolean isEligibleToSowPit(Player activePlayer, int pitIdToSow) {
        if(pitIdToSow == HOUSE_PLAYER_A && activePlayer == PLAYER_B ||
                pitIdToSow == HOUSE_PLAYER_B && activePlayer == PLAYER_A) {
            return false;
        }

        return true;
    }
}
