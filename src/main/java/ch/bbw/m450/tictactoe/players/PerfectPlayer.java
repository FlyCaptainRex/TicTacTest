package ch.bbw.m450.tictactoe.players;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer;

/**
 * An optimal Tic-Tac-Toe AI player implementing the Minimax algorithm.
 * A PerfectPlayer will never lose a game; it will either win or draw.
 */
public class PerfectPlayer implements TicTacToePlayer {

	@Override
	public int play(Stone[] board, Stone colorToPlay) {
		int bestMove = -1;
		int bestScore = Integer.MIN_VALUE;

		for (int i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
			if (board[i] == null) {
				board[i] = colorToPlay;
				int score = minimax(board, 0, false, colorToPlay);
				board[i] = null;

				if (score > bestScore) {
					bestScore = score;
					bestMove = i;
				}
			}
		}

		if (bestMove == -1) {
			throw new IllegalStateException("cannot play at all");
		}
		return bestMove;
	}

	private int minimax(Stone[] board, int depth, boolean isMaximizing, Stone myColor) {
		Stone opponentColor = myColor.opponent();

		if (TicTacToeMain.isWin(board, myColor)) {
			return 10 - depth;
		}
		if (TicTacToeMain.isWin(board, opponentColor)) {
			return depth - 10;
		}
		if (isBoardFull(board)) {
			return 0;
		}

		if (isMaximizing) {
			int maxEval = Integer.MIN_VALUE;
			for (int i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
				if (board[i] == null) {
					board[i] = myColor;
					int eval = minimax(board, depth + 1, false, myColor);
					board[i] = null;
					maxEval = Math.max(maxEval, eval);
				}
			}
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			for (int i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
				if (board[i] == null) {
					board[i] = opponentColor;
					int eval = minimax(board, depth + 1, true, myColor);
					board[i] = null;
					minEval = Math.min(minEval, eval);
				}
			}
			return minEval;
		}
	}

	private boolean isBoardFull(Stone[] board) {
		for (Stone s : board) {
			if (s == null) {
				return false;
			}
		}
		return true;
	}
}

