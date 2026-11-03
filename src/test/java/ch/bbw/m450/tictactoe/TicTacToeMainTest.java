package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    void givenGleicherSpieler_whenSpielGestartet_thenFehler() {
        TicTacToePlayer player = (currentBoard, currentColor) -> 0;

        assertThatThrownBy(() -> TicTacToeMain.play(player, player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("players must differ");
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, TicTacToeMain.BOARD_SIZE })
    void givenUngueltigePosition_whenAusgewaehlt_thenFehler(int position) {
        TicTacToePlayer player1 = (currentBoard, currentColor) -> position;
        TicTacToePlayer player2 = (currentBoard, currentColor) -> 0;

        assertThatThrownBy(() -> TicTacToeMain.play(player1, player2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot play to position " + position);
    }

    @Test
    void givenXErfuelltEineLinie_whenSpielGespielt_thenXGewinnt() {
        var xPlayer = scriptedPlayer(0, 1, 2);
        var oPlayer = scriptedPlayer(3, 4);

        assertThat(TicTacToeMain.play(xPlayer, oPlayer)).isEqualTo(Stone.CROSS);
    }

    @Test
    void givenOErfuelltEineLinie_whenSpielGespielt_thenOGewinnt() {
        var xPlayer = scriptedPlayer(0, 1, 8);
        var oPlayer = scriptedPlayer(3, 4, 5);

        assertThat(TicTacToeMain.play(xPlayer, oPlayer)).isEqualTo(Stone.CIRCLE);
    }

    @Test
    void givenKeinSpielerErfuelltEineLinie_whenSpielGespielt_thenUnentschieden() {
        var xPlayer = scriptedPlayer(0, 2, 7, 5, 6);
        var oPlayer = scriptedPlayer(1, 3, 4, 8);

        assertThat(TicTacToeMain.play(xPlayer, oPlayer)).isNull();
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
                Arguments.of(new int[] { 3, 4, 5 }, Stone.CROSS),
                Arguments.of(new int[] { 6, 7, 8 }, Stone.CROSS),
                Arguments.of(new int[] { 1, 4, 7 }, Stone.CROSS),
                Arguments.of(new int[] { 2, 5, 8 }, Stone.CROSS),
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

    private TicTacToePlayer scriptedPlayer(int... moves) {
        var nextMove = new AtomicInteger();
        return (currentBoard, currentColor) -> moves[nextMove.getAndIncrement()];
    }
}