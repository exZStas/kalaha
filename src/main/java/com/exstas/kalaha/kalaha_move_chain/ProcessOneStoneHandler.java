package com.exstas.kalaha.kalaha_move_chain;

import com.exstas.kalaha.PitService;
import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.exstas.kalaha.constants.PitConstants.HOUSE_PLAYER_A;
import static com.exstas.kalaha.constants.PitConstants.HOUSE_PLAYER_B;
import static com.exstas.kalaha.constants.Player.PLAYER_A;
import static com.exstas.kalaha.constants.Player.PLAYER_B;

@Component
public class ProcessOneStoneHandler extends AbstractKalahaMoveChain {

    @Autowired
    private PitService pitService;

    @Override
    public Kalaha processMove(Kalaha kalaha, int stonesToSow, int pitIdToSow) {
        if(stonesToSow == 1) {
            Player activePlayer = kalaha.getActivePlayer();
            Pit pitToSow = pitService.getPitById(kalaha, pitIdToSow);
            boolean isPitBelongsToActivePlayer = pitService.isPitBelongsToPlayer(activePlayer, pitIdToSow);
            Player nextActivePlayer = activePlayer == PLAYER_A ? PLAYER_B : PLAYER_A;

            if(pitIdToSow == HOUSE_PLAYER_A || pitIdToSow == HOUSE_PLAYER_B) {
                pitToSow.sow();
                return nextChain.processMove(kalaha, stonesToSow, pitIdToSow);
            } else if(isPitBelongsToActivePlayer && pitToSow.isEmpty()) {
                //get stones from opposite pit along with stone from current pit and put it to active player house
                Pit oppositePit = pitService.getOppositePitForPit(kalaha, pitToSow);
                if(!oppositePit.isEmpty()) {
                    int seizedStones = oppositePit.seizeStones() + stonesToSow;

                    Pit pitHouse = kalaha.getPitHouseForPlayer(activePlayer);
                    int stonesOfPitHouse = pitHouse.seizeStones() + seizedStones;

                    pitHouse.setStones(stonesOfPitHouse);
                    kalaha.setActivePlayer(nextActivePlayer);

                    return nextChain.processMove(kalaha, stonesToSow, pitIdToSow);
                }
            }

            kalaha.setActivePlayer(nextActivePlayer);

            stonesToSow -= 1;
            pitToSow.sow();
        }
        return nextChain.processMove(kalaha, stonesToSow, pitIdToSow);
    }
}
