package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

class TicTacToeMainTest {

    @Test
    void besetztesFeldKannNichtAusgewaehltWerden() {

        // GIVEN
        TicTacToePlayer player1 = (board, color) -> 0;
        TicTacToePlayer player2 = (board, color) -> 0;

        // WHEN, THEN
        assertThatThrownBy(() -> TicTacToeMain.play(player1, player2))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void xGewinntVertikal() {

        // GIVEN
        Stone[] board = {
                Stone.CROSS, null, null,
                Stone.CROSS, null, null,
                Stone.CROSS, null, null
        };

        // WHEN
        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void xGewinntHorizontal() {

        // GIVEN
        Stone[] board = {
                Stone.CROSS, Stone.CROSS, Stone.CROSS,
                null, null, null,
                null, null, null
        };

        // WHEN
        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void xGewinntDiagonal() {

        // GIVEN
        Stone[] board = {
                Stone.CROSS, null, null,
                null, Stone.CROSS, null,
                null, null, Stone.CROSS
        };

        // WHEN
        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void keinerGewinnt() {

        // GIVEN
        Stone[] board = {
                Stone.CROSS, Stone.CIRCLE, Stone.CROSS,
                Stone.CROSS, Stone.CIRCLE, Stone.CIRCLE,
                Stone.CIRCLE, Stone.CROSS, null
        };

        // WHEN
        boolean resultX = TicTacToeMain.isWin(board, Stone.CROSS);
        boolean resultO = TicTacToeMain.isWin(board, Stone.CIRCLE);

        // THEN
        assertThat(resultX).isFalse();
        assertThat(resultO).isFalse();
    }
}



