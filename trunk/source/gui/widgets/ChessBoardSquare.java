/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2007  Carson Day (carsonday@gmail.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package decaf.gui.widgets;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

import decaf.gui.Disposable;
import decaf.gui.GUIUtil;
import decaf.gui.event.UserActionEvent;
import decaf.gui.event.UserActionListener;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.BorderUtil;
import decaf.gui.util.CoordinatesUtil;
import decaf.gui.util.PieceUtil;
import decaf.gui.util.XmlUtil;
import decaf.moveengine.Piece;

public class ChessBoardSquare extends JPanel implements MouseListener,
		Preferenceable, Disposable {

	private Stroke[] SELECT_ANIMATION_STROKE = new Stroke[] {
			new BasicStroke(16.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL),
			new BasicStroke(10.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL),
			new BasicStroke(6.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL),
			new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL) };

	private static final long SELECT_ANIMATION_DELAY_MILLIS = 250;

	private static ChessBoardSquare startSquare;

	private static ChessBoardSquare currentSquare;

	private static String currentDragBoardId;

	private static Cursor storedCursor;

	private String boardId;

	private ChessPiece chessPiece;

	private boolean isLightSquare;

	private Preferences preferences;

	private Vector guiMoveListeners;

	private boolean isIgnoringPieceMoves;

	private int rank = -1;

	private int file = -1;

	private int dropPiece;

	private boolean isSelected = false;

	private int selectAnimationStage = 0;

	private Dimension sizeConstraint;

	public ChessBoardSquare(Preferences preferences, String boardID,
			boolean isWhiteSquare, int rank, int file) {
		isIgnoringPieceMoves = false;
		this.boardId = boardID;
		isLightSquare = isWhiteSquare;
		setLayout(new BorderLayout());
		setPreferences(preferences);
		addMouseListener(this);
		guiMoveListeners = new Vector(10);
		CoordinatesUtil.assertValidFile(file);
		CoordinatesUtil.assertValidRank(rank);
		this.rank = rank;
		this.file = file;
	}

	public ChessBoardSquare(Preferences preferences, String boardID,
			boolean isWhiteSquare, int dropPiece) {
		isIgnoringPieceMoves = false;
		this.boardId = boardID;
		isLightSquare = isWhiteSquare;
		setLayout(new BorderLayout());
		setPreferences(preferences);
		addMouseListener(this);
		guiMoveListeners = new Vector(10);
		PieceUtil.assertValid(dropPiece);
		setBorder(null);
		if (dropPiece == Piece.EMPTY) {
			throw new IllegalArgumentException("Drop piece cant be empty.");
		}
		this.dropPiece = dropPiece;
	}

	/**
	 * Sets the size constraint for pieces in this square.
	 */
	public void setSizeConstraint(Dimension sizeConstraint) {
		setMaximumSize(sizeConstraint);
		setMinimumSize(sizeConstraint);
		setPreferredSize(sizeConstraint);

	}

	public Dimension getSizeConstraint() {
		return sizeConstraint;
	}

	public boolean isDropSquare() {
		return rank == -1 || file == -1;
	}

	public void dispose() {
		startSquare = null;
		currentDragBoardId = null;
		storedCursor = null;
		boardId = null;
		chessPiece = null;
		preferences = null;
		if (guiMoveListeners != null) {
			guiMoveListeners.removeAllElements();
			guiMoveListeners = null;
		}
	}

	public ChessBoardSquare(Preferences preferences, boolean isWhiteSquare) {
		isIgnoringPieceMoves = true;
		isLightSquare = isWhiteSquare;
		setLayout(new BorderLayout());
		setPreferences(preferences);
		guiMoveListeners = new Vector(10);
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String s) {
		synchronized (this) {
			boardId = s;
		}
	}

	public Preferences getPreferences() {
		return preferences.getDeepCopy();
	}

	public void addGUIMoveListener(UserActionListener guimovelistener) {
		guiMoveListeners.addElement(guimovelistener);
	}

	public void removeGUIMoveListener(UserActionListener guimovelistener) {
		guiMoveListeners.removeElement(guimovelistener);
	}

	private void sendMoveEvent(UserActionEvent guimoveevent) {
		for (Enumeration enumeration = guiMoveListeners.elements(); enumeration
				.hasMoreElements(); ((UserActionListener) enumeration
				.nextElement()).moveOccured(guimoveevent))
			;
	}

	private void sendIncompleteMove() {
		for (Enumeration enumeration = guiMoveListeners.elements(); enumeration
				.hasMoreElements(); ((UserActionListener) enumeration
				.nextElement()).incompleteMoveOccured())
			;
		;
	}

	private boolean isValidMoveStartSquare() {
		boolean result = true;
		for (Enumeration enumeration = guiMoveListeners.elements(); result
				&& enumeration.hasMoreElements();) {
			result = ((UserActionListener) enumeration.nextElement())
					.isValidMoveStartSquare(getCoordinates());
		}
		return result;
	}

	private void sendRightClickEvent() {
		for (Enumeration enumeration = guiMoveListeners.elements(); enumeration
				.hasMoreElements();) {
			((UserActionListener) enumeration.nextElement()).rightClickOccured(
					this, getChessPieceType() == Piece.EMPTY);
		}
	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			this.preferences = preferences;

			if (preferences != null) {
				if (isLightSquare)
					setBackground(preferences.getBoardPreferences()
							.getLightSquareBackgroundColor());
				else
					setBackground(preferences.getBoardPreferences()
							.getDarkSquareBackgroundColor());
				setBorder(BorderUtil.intToBorder(preferences
						.getBoardPreferences().getSquareBorder()));
				if (chessPiece != null) {
					setPiece(preferences.getBoardPreferences().getSet()
							.createChessPiece(chessPiece.getType()));
				}
			}
		}
	}

	public void addMouseLisetener(MouseListener mouselistener) {
		super.addMouseListener(mouselistener);
		if (getChessPieceType() != 0)
			chessPiece.addMouseListener(mouselistener);
	}

	public void removeMouseLisetener(MouseListener mouselistener) {
		super.removeMouseListener(mouselistener);
		if (getChessPieceType() != 0)
			chessPiece.removeMouseListener(mouselistener);
	}

	public void setPiece(ChessPiece chesspiece) {
		synchronized (this) {
			makeEmpty();
			chessPiece = chesspiece;
			MouseListener amouselistener[] = (MouseListener[]) getListeners(java.awt.event.MouseListener.class);
			for (int i = 0; i < amouselistener.length; i++)
				chesspiece.addMouseListener(amouselistener[i]);
			chessPiece.setSizeConstraint(sizeConstraint);
			add(chesspiece, "Center");
			forceLayout();
		}
	}

	public void changePiece(int piece) {
		synchronized (this) {
			if ((chessPiece != null && piece == Piece.EMPTY)
					|| (chessPiece == null && piece != Piece.EMPTY)
					|| (chessPiece != null && piece != chessPiece.getType())) {
				if (!PieceUtil.isEmpty(piece)) {
					setPiece(preferences.getBoardPreferences().getSet()
							.createChessPiece(piece));

				} else {
					makeEmpty();
					forceLayout();
				}
			}
		}
	}

	public void makeEmpty() {
		synchronized (this) {
			removeAll();
			chessPiece = null;
			repaint();
		}
	}

	public int getChessPieceType() {
		return chessPiece == null ? Piece.EMPTY : chessPiece.getType();
	}

	public ChessPiece getChessPiece() {
		return chessPiece;
	}

	public boolean isLightSquare() {
		return isLightSquare;
	}

	public void forceLayout() {
		getLayout().layoutContainer(this);
	}

	public String toString() {
		return XmlUtil.toXml(this);
	}

	public void mouseClicked(MouseEvent mouseEvent) {
	}

	public void mouseEntered(MouseEvent mouseevent) {
		if (startSquare != null) {
			currentSquare = this;
		}
	}

	public void mouseExited(MouseEvent mouseevent) {
		if (startSquare != null) {
			currentSquare = null;
		}
	}

	public void mousePressed(MouseEvent mouseevent) {
		if (mouseevent.getButton() == MouseEvent.BUTTON1) {
			drag();
		} else if (mouseevent.getButton() == MouseEvent.BUTTON3) {
			sendRightClickEvent();
		}
	}

	public void mouseReleased(MouseEvent mouseevent) {
		if (mouseevent.getButton() == MouseEvent.BUTTON1) {
			drop();
		}

	}

	public int[] getCoordinates() {
		return new int[] { getRank(), getFile() };
	}

	/**
	 * @return Returns the file.
	 */
	public int getFile() {
		return file;
	}

	/**
	 * @return Returns the rank.
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @return Returns the dropPiece.
	 */
	public int getDropPiece() {
		return dropPiece;
	}

	public void select() {
		if (!isSelected) {
			isSelected = true;

			if (getPreferences().getBoardPreferences().getHighlightMode() == BoardPreferences.NO_HIGHLIGHT) {
				isSelected = false;
			} else if (getPreferences().getBoardPreferences()
					.getHighlightMode() == BoardPreferences.HIGHLIGHT_UNTIL_NEXT_MOVE) {
				selectAnimationStage = 2;
				repaint();
			} else {
				startSelectAnimation();
			}
		}
	}

	public void unselect() {
		if (isSelected) {
			isSelected = false;
			selectAnimationStage = 0;
			repaint();
		}
	}

	private void startSelectAnimation() {
		selectAnimationStage = 0;
		Thread myThread = new Thread(new Runnable() {
			public void run() {
				while (isSelected && selectAnimationStage <= 3) {
					repaint();
					selectAnimationStage++;

					if (selectAnimationStage <= 3) {
						try {
							Thread.sleep(SELECT_ANIMATION_DELAY_MILLIS);
						} catch (InterruptedException ie) {
						}
					} else {
						unselect();
						break;
					}
				}
			}
		});
		myThread.start();
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		if (isSelected && selectAnimationStage <= 3) {
			Graphics2D graphics2D = (Graphics2D) graphics;
			int width = this.getWidth();
			int height = getHeight();

			graphics2D.setColor(preferences.getBoardPreferences()
					.getMoveHighlightColor());
			Rectangle shape = new Rectangle(getWidth(), getHeight());
			graphics2D.setStroke(SELECT_ANIMATION_STROKE[selectAnimationStage]);
			graphics2D.draw(shape);
		}

	}

	protected void markAsMoveStart() {
		// select();
	}

	protected void unmarkAsMoveStart() {
		// unselect();
	}

	private void drag() {
		synchronized (this) {
			if (startSquare != null) {
				startSquare.unmarkAsMoveStart();
			}

			if (getChessPiece() != null && !getChessPiece().isTransparent()) {
				if (isValidMoveStartSquare()) {
					startSquare = this;

					currentSquare = startSquare;

					if (storedCursor == null) {
						storedCursor = getCursor();
					}
					GUIUtil.getGreatestParent(this).setCursor(
							GUIUtil.getCursor(chessPiece));
					chessPiece.setTransparent(true);
					chessPiece.repaint();

					markAsMoveStart();
				}
			}
		}
	}

	private void drop() {
		synchronized (this) {

			if (startSquare != null && currentSquare != null
					&& startSquare != currentSquare) {
				if (currentSquare.getRank() != -1) {
					sendMoveEvent(new UserActionEvent(startSquare,
							currentSquare, boardId));
				}
			}

			if (currentSquare == null || (startSquare == currentSquare)) {
				sendIncompleteMove();
			}

			if (startSquare != null) {
				if (startSquare.getChessPiece() != null)
					startSquare.getChessPiece().setTransparent(false);
				startSquare.unmarkAsMoveStart();
			}
			GUIUtil.getGreatestParent(this).setCursor(storedCursor);

			if (getChessPiece() != null) {
				chessPiece.repaint();
			}

			currentSquare = null;
			startSquare = null;
		}

	}
}