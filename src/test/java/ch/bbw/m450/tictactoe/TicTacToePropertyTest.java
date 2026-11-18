package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import ch.bbw.m450.tictactoe.players.PerfectPlayer;

/**
 * Property-Based Testing with jqwik (additional testing framework).
 * Verifies invariant properties across randomized generated Tic-Tac-Toe boards.
 */
class TicTacToePropertyTest {

	@Property
	void givenAnyBoard_whenToStringCalled_thenContainsThreeRows(@ForAll("anyBoard") Stone[] board) {
		String representation = TicTacToeMain.toString(board);
		assertThat(representation).isNotNull().isNotEmpty();
		assertThat(representation.split("\n")).hasSize(3);
	}

	@Property
	void givenPartiallyFilledBoard_whenGreedyPlays_thenAlwaysSelectsFreePosition(@ForAll("partiallyFilledBoard") Stone[] board) {
		GreedyPlayer player = new GreedyPlayer();
		int move = player.play(board, Stone.CROSS);

		assertThat(move).isBetween(0, 8);
		assertThat(board[move]).isNull();
	}

	@Property
	void givenPartiallyFilledBoard_whenPerfectPlayerPlays_thenAlwaysSelectsFreePosition(@ForAll("partiallyFilledBoard") Stone[] board) {
		PerfectPlayer player = new PerfectPlayer();
		int move = player.play(board, Stone.CIRCLE);

		assertThat(move).isBetween(0, 8);
		assertThat(board[move]).isNull();
	}

	@Property
	void givenWinningRow_whenIsWinEvaluated_thenReturnsTrue(
			@ForAll("stone") Stone stone,
			@ForAll("rowIndex") int row) {
		Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];
		board[row * 3] = stone;
		board[row * 3 + 1] = stone;
		board[row * 3 + 2] = stone;

		assertThat(TicTacToeMain.isWin(board, stone)).isTrue();
	}

	@Provide
	Arbitrary<Stone[]> anyBoard() {
		return Arbitraries.of(Stone.CROSS, Stone.CIRCLE, null)
				.array(Stone[].class)
				.ofSize(TicTacToeMain.BOARD_SIZE);
	}

	@Provide
	Arbitrary<Stone[]> partiallyFilledBoard() {
		return anyBoard().filter(board -> {
			for (Stone s : board) {
				if (s == null) {
					return true;
				}
			}
			return false;
		});
	}

	@Provide
	Arbitrary<Stone> stone() {
		return Arbitraries.of(Stone.CROSS, Stone.CIRCLE);
	}

	@Provide
	Arbitrary<Integer> rowIndex() {
		return Arbitraries.integers().between(0, 2);
	}
}
