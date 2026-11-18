package ch.bbw.m450.tictactoe.players;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIn;
import org.junitpioneer.jupiter.StdIo;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

/**
 * Tests for HumanPlayer using the JUnit Pioneer extension to capture and simulate console input (StdIo).
 */
class HumanPlayerPioneerTest {

	@Test
	@StdIo("4\n")
	void givenKonsoleneingabeVier_whenHumanPlayerZieht_dannWaehltFeldVier(StdIn in) {
		var humanPlayer = new HumanPlayer();
		var board = new Stone[TicTacToeMain.BOARD_SIZE];

		int move = humanPlayer.play(board, Stone.CROSS);

		assertThat(move).isEqualTo(4);
	}

	@Test
	@StdIo("0\n")
	void givenKonsoleneingabeNull_whenHumanPlayerZieht_dannWaehltFeldNull(StdIn in) {
		var humanPlayer = new HumanPlayer();
		var board = new Stone[TicTacToeMain.BOARD_SIZE];

		int move = humanPlayer.play(board, Stone.CIRCLE);

		assertThat(move).isEqualTo(0);
	}
}

