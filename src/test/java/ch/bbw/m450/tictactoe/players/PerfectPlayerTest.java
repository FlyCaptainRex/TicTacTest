package ch.bbw.m450.tictactoe.players;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

class PerfectPlayerTest {

	private PerfectPlayer perfectPlayer;
	private Stone[] board;

	@BeforeEach
	void setup() {
		perfectPlayer = new PerfectPlayer();
		board = new Stone[TicTacToeMain.BOARD_SIZE];
	}

	@Test
	void givenVollstaendigesBrett_whenPerfectPlayerZieht_dannFehler() {
		Arrays.fill(board, Stone.CROSS);

		assertThatThrownBy(() -> perfectPlayer.play(board, Stone.CIRCLE))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play at all");
	}

	@Test
	void givenGewinnmoeglichkeit_whenPerfectPlayerZieht_dannNimmtGewinnzug() {
		// Board: X | X | .
		//        O | O | .
		//        . | . | .
		board[0] = Stone.CROSS;
		board[1] = Stone.CROSS;
		board[3] = Stone.CIRCLE;
		board[4] = Stone.CIRCLE;

		int move = perfectPlayer.play(board, Stone.CROSS);

		// Must complete the top row to win immediately on position 2
		assertThat(move).isEqualTo(2);
	}

	@Test
	void givenDrohendeNiederlage_whenPerfectPlayerZieht_dannBlockiertGegner() {
		// Board: O | O | .
		//        X | . | .
		//        . | . | .
		board[0] = Stone.CIRCLE;
		board[1] = Stone.CIRCLE;
		board[3] = Stone.CROSS;

		int move = perfectPlayer.play(board, Stone.CROSS);

		// Must block position 2 to prevent CIRCLE from winning
		assertThat(move).isEqualTo(2);
	}

	@Test
	void givenSpielGegenGreedyPlayerAlsStartspieler_thenPerfectPlayerGewinntImmer() {
		var greedy = new GreedyPlayer();
		Stone winner = TicTacToeMain.play(perfectPlayer, greedy);

		assertThat(winner).isEqualTo(Stone.CROSS);
	}

	@Test
	void givenSpielGegenGreedyPlayerAlsZweiter_thenPerfectPlayerGewinntImmer() {
		var greedy = new GreedyPlayer();
		Stone winner = TicTacToeMain.play(greedy, perfectPlayer);

		assertThat(winner).isEqualTo(Stone.CIRCLE);
	}

	@Test
	void givenSpielZweierPerfectPlayers_thenImmerUnentschieden() {
		var opponent = new PerfectPlayer();
		Stone winner = TicTacToeMain.play(perfectPlayer, opponent);

		assertThat(winner).isNull();
	}
}

