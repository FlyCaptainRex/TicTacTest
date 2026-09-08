package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

@SuppressWarnings("unused")
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
        TicTacToePlayer player1 = (currentBoard, currentColor) -> 0;
        TicTacToePlayer player2 = (currentBoard, currentColor) -> 0;

        assertThatThrownBy(() -> TicTacToeMain.play(player1, player2))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "Konstellation {index}")
    @MethodSource("winningBoards")
    void givenGewinnendeKonstellation_whenSpielfeldGeprueft_thenSpielerGewinnt(
            int[] positions, Stone color) {
        set(color, positions);

        boolean result = TicTacToeMain.isWin(board, color);

        assertThat(result).isTrue();
    }

    static Stream<Arguments> winningBoards() {
        return Stream.of(
                Arguments.of(new int[] { 0, 3, 6 }, Stone.CROSS),
                Arguments.of(new int[] { 0, 1, 2 }, Stone.CROSS),
                Arguments.of(new int[] { 0, 4, 8 }, Stone.CROSS),
                Arguments.of(new int[] { 2, 4, 6 }, Stone.CIRCLE));
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