package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

class TicTacToeMainTest {

    private Stone[] board;

    @BeforeEach
    void setup() {
        board = new Stone[9];
    }

    private void set(Stone stone, int... positions) {
        for (int position : positions) {
            board[position] = stone;
        }
    }

    @Test
    void givenBesetztesFeld_whenAusgewaehlt_thenFehler() {
        TicTacToePlayer player1 = (board, color) -> 0;
        TicTacToePlayer player2 = (board, color) -> 0;

        assertThatThrownBy(() -> TicTacToeMain.play(player1, player2))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void givenXVertikal_whenSpielfeldGeprueft_thenXGewinnt() {
        set(Stone.CROSS, 0, 3, 6);

        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        assertThat(result).isTrue();
    }

    @Test
    void givenXHorizontal_whenSpielfeldGeprueft_thenXGewinnt() {
        set(Stone.CROSS, 0, 1, 2);

        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        assertThat(result).isTrue();
    }

    @Test
    void givenXDiagonal_whenSpielfeldGeprueft_thenXGewinnt() {
        set(Stone.CROSS, 0, 4, 8);

        boolean result = TicTacToeMain.isWin(board, Stone.CROSS);

        assertThat(result).isTrue();
    }

    @Test
    void givenKeinGewinner_whenSpielfeldGeprueft_thenNiemandGewinnt() {
        set(Stone.CROSS, 0, 2, 3, 7);
        set(Stone.CIRCLE, 1, 4, 5, 6);

        boolean resultX = TicTacToeMain.isWin(board, Stone.CROSS);
        boolean resultO = TicTacToeMain.isWin(board, Stone.CIRCLE);

        assertThat(resultX).isFalse();
        assertThat(resultO).isFalse();
    }
}