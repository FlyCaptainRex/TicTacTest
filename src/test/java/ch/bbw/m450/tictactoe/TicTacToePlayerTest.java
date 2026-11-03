package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;

class TicTacToePlayerTest {

    @ParameterizedTest
    @MethodSource("opponentColors")
    void givenSpielstein_whenGegnerAbgefragt_dannGegnerfarbe(Stone stone, Stone opponent) {
        assertThat(stone.opponent()).isEqualTo(opponent);
    }

    static Stream<Arguments> opponentColors() {
        return Stream.of(
                Arguments.of(Stone.CROSS, Stone.CIRCLE),
                Arguments.of(Stone.CIRCLE, Stone.CROSS));
    }

    @Test
    void givenLeeresFeld_whenGreedySpielerZieht_dannErstesFeld() {
        assertThat(new GreedyPlayer().play(new Stone[TicTacToeMain.BOARD_SIZE], Stone.CROSS))
                .isZero();
    }

    @Test
    void givenNurFeldVierFrei_whenGreedySpielerZieht_dannFeldVier() {
        var board = new Stone[TicTacToeMain.BOARD_SIZE];
        for (var position = 0; position < board.length; position++) {
            if (position != 4) {
                board[position] = Stone.CROSS;
            }
        }

        assertThat(new GreedyPlayer().play(board, Stone.CIRCLE)).isEqualTo(4);
    }

    @Test
    void givenVollstaendigesBrett_whenGreedySpielerZieht_dannFehler() {
        var board = new Stone[TicTacToeMain.BOARD_SIZE];
        java.util.Arrays.fill(board, Stone.CROSS);

        assertThatThrownBy(() -> new GreedyPlayer().play(board, Stone.CIRCLE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot play at all");
    }
}
