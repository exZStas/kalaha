package com.exstas.kalaha.constants;

import java.util.Arrays;
import java.util.List;

import static com.exstas.kalaha.constants.PitConstants.*;

public enum Player {
    PLAYER_A(HOUSE_PLAYER_A, Arrays.asList(PIT_1_PLAYER_A, PIT_2_PLAYER_A, PIT_3_PLAYER_A, PIT_4_PLAYER_A, PIT_5_PLAYER_A, PIT_6_PLAYER_A)),
    PLAYER_B(HOUSE_PLAYER_B, Arrays.asList(PIT_1_PLAYER_B, PIT_2_PLAYER_B, PIT_3_PLAYER_B, PIT_4_PLAYER_B, PIT_5_PLAYER_B, PIT_6_PLAYER_B))
    ;

    private int pitHouseId;
    private List<Integer> playerPitIds;
    Player(int pitHouseId, List<Integer> playerPitIds) {
        this.pitHouseId = pitHouseId;
        this.playerPitIds = playerPitIds;
    }

    public int getPlayerPitHouseId() {
        return pitHouseId;
    }

    public List<Integer> getPlayerPitIds() {
        return playerPitIds;
    }
}
