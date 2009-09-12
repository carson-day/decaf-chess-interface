package decaf.gui.event;

import decaf.gui.widgets.ChessBoardSquare;

public interface UserMoveInputListener {
	public void userClicked(ChessBoardSquare square);

	public void userMadeIncompleteMove(UserIncompleteMoveEvent event);

	/**
	 * Should return true if the move was successful, false otherwise.
	 * 
	 * @param event
	 * @return
	 */
	public boolean userMoved(UserMoveEvent event);

	public void userRightClicked(UserRightClickSquareEvent event);
}
