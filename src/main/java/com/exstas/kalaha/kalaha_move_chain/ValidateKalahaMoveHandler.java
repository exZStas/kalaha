package com.exstas.kalaha.kalaha_move_chain;

import com.exstas.kalaha.PitService;
import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.exstas.kalaha.constants.PitConstants.HOUSE_PLAYER_A;
import static com.exstas.kalaha.constants.PitConstants.HOUSE_PLAYER_B;

@Slf4j
@Component
public class ValidateKalahaMoveHandler extends AbstractKalahaMoveChain {

    @Autowired
    private PitService pitService;

    @Override
    public Kalaha processMove(Kalaha kalaha, int stonesToSow, int requestedPitId) {
        Player activePlayer = kalaha.getActivePlayer();

        //check player can't request house pit
        if(requestedPitId == HOUSE_PLAYER_A || requestedPitId == HOUSE_PLAYER_B) {
            return kalaha;
        }

        //check player can request only pits which belongs to they
        boolean isPitBelongsToPlayer = pitService.isPitBelongsToPlayer(activePlayer, requestedPitId);
        if(!isPitBelongsToPlayer) {
            return kalaha;
        }

        Pit requestedPit = pitService.getPitById(kalaha, requestedPitId);

        if(Objects.isNull(requestedPit)) {
            //return special object with error code and status?
            log.warn("Couldn't find requested pit. Pit id={}, kalaha id={}", requestedPitId, kalaha.getId());
            return kalaha;
        }

        if(stonesToSow == 0) {
            log.info("Requested pit is empty. Nothing to do. Pit id={}, kalaha id={}", requestedPitId, kalaha.getId());
            return kalaha;
        }

        //int stonesToSow = requestedPit.seizeStones();
        int pitIdToSow = requestedPitId + 1;
        nextChain.processMove(kalaha, stonesToSow, pitIdToSow);

        return kalaha;
    }
}
