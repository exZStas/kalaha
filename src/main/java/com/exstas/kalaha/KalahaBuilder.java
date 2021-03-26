package com.exstas.kalaha;

import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.exstas.kalaha.constants.PitConstants.*;

@Component
public class KalahaBuilder {

    private int stones;
    private List<Pit> pits;
    private Player activePlayer;
    private String id;

    public KalahaBuilder withStones(int stones) {
        this.stones = stones;
        return this;
    }

    public KalahaBuilder withDefaultPits() {
        this.pits = new ArrayList<>();

        pits.add(new Pit(HOUSE_PLAYER_A, 0));
        pits.add(new Pit(PIT_1_PLAYER_A, stones));
        pits.add(new Pit(PIT_2_PLAYER_A, stones));
        pits.add(new Pit(PIT_3_PLAYER_A, stones));
        pits.add(new Pit(PIT_4_PLAYER_A, stones));
        pits.add(new Pit(PIT_5_PLAYER_A, stones));
        pits.add(new Pit(PIT_6_PLAYER_A, stones));

        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        pits.add(new Pit(PIT_1_PLAYER_B, stones));
        pits.add(new Pit(PIT_2_PLAYER_B, stones));
        pits.add(new Pit(PIT_3_PLAYER_B, stones));
        pits.add(new Pit(PIT_4_PLAYER_B, stones));
        pits.add(new Pit(PIT_5_PLAYER_B, stones));
        pits.add(new Pit(PIT_6_PLAYER_B, stones));

        return this;
    }

    public KalahaBuilder withStartingPlayer(Player player) {
        this.activePlayer = player;

        return this;
    }

    public KalahaBuilder withId(String id) {
        this.id = id;

        return this;
    }

    public Kalaha build() {
        Kalaha kalaha = new Kalaha();
        kalaha.setId(id);
        kalaha.setStonesPerPit(this.stones);
        kalaha.setPits(this.pits);
        kalaha.setActivePlayer(this.activePlayer);

        return kalaha;
    }

}
