/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Carson Day (carsonday@gmail.com)
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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import decaf.gui.ChessAreaFrame;
import decaf.gui.event.UserIncompleteMoveEvent;
import decaf.gui.event.UserMoveEvent;
import decaf.gui.event.UserMoveInputListener;
import decaf.gui.event.UserRightClickSquareEvent;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.moveengine.Piece;
import decaf.util.BorderUtil;
import decaf.util.CoordinatesUtil;
import decaf.util.GUIUtil;
import decaf.util.PieceUtil;

public class ChessBoardSquare extends JPanel // implements MouseListener,
		implements Preferenceable, Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(ChessBoardSquare.class);

	private Stroke[] SELECT_ANIMATION_STROKE = new Stroke[] {
			new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL),
			new BasicStroke(6.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL),
			new BasicStroke(10.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL),
			new BasicStroke(12.0F, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL) };

	private static final long SELECT_ANIMATION_DELAY_MILLIS = 250;

	private static final DataFlavor STRING_FLAVOR = DataFlavor.stringFlavor;

	private static ChessPieceCache chessPieceCache = new ChessPieceCache();

	private static SquareImageBackgroundCache squareImageBackgroundCache = new SquareImageBackgroundCache();

	private DragGestureRecognizer recognizer = DragSource
			.getDefaultDragSource().createDefaultDragGestureRecognizer(this,
					DnDConstants.ACTION_MOVE, new MyDragGestureListener());

	private static Cursor INVIS_MOVE_CURSOR = null;

	private static Image TRANSPARENT_IMAGE = null;

	static {
		// Cache the INVIS_MOVE_CURSOR for speed
		try {
			LOGGER.debug("Creating invis move cursor image");
			Image invisMoveCursorImage = ChessSet
					.getChessPieceImage("InvisMoveCursor.BMP");

			TRANSPARENT_IMAGE = ChessSet.getChessPieceImage("Transparent.BMP");

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension dimension = toolkit.getBestCursorSize(
					invisMoveCursorImage.getWidth(null), invisMoveCursorImage
							.getHeight(null));
			BufferedImage bufferedimage = new BufferedImage(dimension.width,
					dimension.height, 2);

			Graphics graphics = bufferedimage.getGraphics();
			((Graphics2D) graphics).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			((Graphics2D) graphics).setRenderingHint(
					RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			((Graphics2D) graphics).drawImage(invisMoveCursorImage, 0, 0,
					dimension.width, dimension.height, null);

			INVIS_MOVE_CURSOR = toolkit.createCustomCursor(bufferedimage,
					new Point(dimension.width / 2, dimension.height / 2),
					"Invisible Move Cursor");
		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	protected static ChessBoardSquare dragStartSquare;

	protected static ChessBoardSquare dragEndSquare;

	private UserMoveInputListener userMoveInputListener;

	private String boardId;

	private int chessPieceType;

	private boolean isChessPieceTransparent;

	private boolean isLightSquare;

	private Preferences preferences;

	private boolean isMoveable;

	private int rank = -1;

	private int file = -1;

	private int dropPiece;

	private boolean isSelected = false;

	private int selectAnimationStage = 0;

	private ChessBoardSquare thisSquare = this;

	public ChessBoardSquare(Preferences preferences, String boardID,
			boolean isWhiteSquare, int rank, int file) {
		isMoveable = false;
		this.boardId = boardID;
		isLightSquare = isWhiteSquare;
		setLayout(new BorderLayout());
		setPreferences(preferences);
		new DropTarget(this, new DropTargetListener());

		// Hopefully fixes the awt + swing DND bug.
		setTransferHandler(null);

		ChessBoardSquareMouseMotionListener mouseHandler = new ChessBoardSquareMouseMotionListener();
		addMouseListener(mouseHandler);

		CoordinatesUtil.assertValidFile(file);
		CoordinatesUtil.assertValidRank(rank);
		setOpaque(true);
		this.rank = rank;
		this.file = file;
	}

	public ChessBoardSquare(Preferences preferences, String boardID,
			boolean isWhiteSquare, int dropPiece) {
		isMoveable = false;
		this.boardId = boardID;
		isLightSquare = isWhiteSquare;
		setLayout(new BorderLayout());
		setPreferences(preferences);
		new DropTarget(this, new DropTargetListener());
		setOpaque(true);

		// Hopefully fixes the awt + swing DND bug.
		setTransferHandler(null);

		ChessBoardSquareMouseMotionListener mouseHandler = new ChessBoardSquareMouseMotionListener();
		addMouseListener(mouseHandler);

		PieceUtil.assertValid(dropPiece);
		if (dropPiece == Piece.EMPTY) {
			throw new IllegalArgumentException("Drop piece cant be empty.");
		}
		this.dropPiece = dropPiece;
	}

	public void setDropPiece(int piece) {
		this.dropPiece = piece;
	}

	public UserMoveInputListener getUserMoveInputListener() {
		return userMoveInputListener;
	}

	public void setUserMoveInputListener(
			UserMoveInputListener userMoveInputListener) {
		this.userMoveInputListener = userMoveInputListener;
	}

	public boolean isMoveable() {
		return isMoveable;
	}

	public void setMoveable(boolean isMoveable) {
		this.isMoveable = isMoveable;
	}

	public boolean isDropSquare() {
		return rank == -1 || file == -1;
	}

	public void dispose() {
		boardId = null;
		preferences = null;
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

	public boolean isValidMoveStartSquare() {
		return getChessPiece() != Piece.EMPTY && isMoveable();
	}

	private void sendClickEvent() {
		if (userMoveInputListener != null) {
			userMoveInputListener.userClicked(this);
		}
	}

	private void sendRightClickEvent() {
		if (userMoveInputListener != null) {
			userMoveInputListener
					.userRightClicked(new UserRightClickSquareEvent(this,
							getChessPiece() == Piece.EMPTY, getBoardId()));
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
			}
		}
	}

	public void setPiece(int chessPiece) {
		if (!PieceUtil.isEmpty(chessPiece)) {
			if (chessPieceType != chessPiece) {
				this.chessPieceType = chessPiece;
				repaint();
			}

		} else if (chessPieceType != Piece.EMPTY) {
			clear();
		}
	}

	public void clear() {
		synchronized (this) {
			// removeAll();
			chessPieceType = Piece.EMPTY;
			repaint();
		}
	}

	public int getChessPiece() {
		return chessPieceType;
	}

	public boolean isChessPieceTransparent() {
		return isChessPieceTransparent;
	}

	public void setChessPieceTransparent(boolean isTransparent) {
		this.isChessPieceTransparent = isTransparent;
	}

	public boolean isLightSquare() {
		return isLightSquare;
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

			if (getPreferences().getBoardPreferences().getSquareSelectionMode() == BoardPreferences.NONE_SQUARE_SELECTION_MODE) {
				isSelected = false;
			} else if (getPreferences().getBoardPreferences()
					.getSquareSelectionMode() == BoardPreferences.BORDER_SQUARE_SELECTION_MODE) {
				selectAnimationStage = 2;
				repaint();
			} else if (getPreferences().getBoardPreferences()
					.getSquareSelectionMode() == BoardPreferences.FADE_SQUARE_SELECTION_MODE) {
				selectAnimationStage = 3;
				playAnimationState();
			} else if (getPreferences().getBoardPreferences()
					.getSquareSelectionMode() == BoardPreferences.FILL_BACKGROUND_SQUARE_SELECTION_MODE
					|| getPreferences().getBoardPreferences()
							.getSquareSelectionMode() == BoardPreferences.DIAGONAL_LINE_BACKGROUND_SQUARE_SELECTION_MODE) {
				repaint();
			}
		}
	}

	public void unselect() {
		if (isSelected) {
			isSelected = false;
			selectAnimationStage = -1;
			repaint();
		}
	}

	private void playAnimationState() {
		if (selectAnimationStage >= 0) {
			repaint();
			selectAnimationStage--;
			if (selectAnimationStage >= 0) {
				java.util.Timer timer = new java.util.Timer();
				timer.schedule(new java.util.TimerTask() {
					public void run() {
						playAnimationState();
					}
				}, SELECT_ANIMATION_DELAY_MILLIS);
			}
		}
	}

	public void paintComponent(Graphics graphics) {

		if (preferences.getBoardPreferences().getSquareImageBackground() != null
				&& getRank() != -1) {
			BufferedImage image = squareImageBackgroundCache
					.getSquareBackgroundImage(preferences.getBoardPreferences()
							.getSquareImageBackground(), getWidth(),
							getHeight(), getRank(), getFile());
			graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		} else {
			super.paintComponent(graphics);
		}

		if (isSelected
				&& getPreferences().getBoardPreferences()
						.getSquareSelectionMode() == BoardPreferences.FILL_BACKGROUND_SQUARE_SELECTION_MODE) {
			Graphics2D graphics2D = (Graphics2D) graphics;

			graphics2D.setColor(preferences.getBoardPreferences()
					.getMoveHighlightColor());
			graphics2D.fillRect(0, 0, getWidth(), getHeight());

		} else if (isSelected
				&& getPreferences().getBoardPreferences()
						.getSquareSelectionMode() == BoardPreferences.DIAGONAL_LINE_BACKGROUND_SQUARE_SELECTION_MODE) {
			Graphics2D graphics2D = (Graphics2D) graphics;

			graphics2D.setColor(preferences.getBoardPreferences()
					.getMoveHighlightColor());

			graphics2D.drawLine(getWidth() / 3, 0, 0, getHeight() / 3);
			graphics2D.drawLine(2 * getWidth() / 3, 0, 0, 2 * getHeight() / 3);
			graphics2D.drawLine(0, getHeight(), getWidth(), 0);
			graphics2D.drawLine(getWidth() / 3, getHeight(), getWidth(),
					getHeight() / 3);
			graphics2D.drawLine(2 * getWidth() / 3, getHeight(), getWidth(),
					2 * getHeight() / 3);
		} else if (isSelected && selectAnimationStage <= 3
				&& selectAnimationStage > -1) {
			Graphics2D graphics2D = (Graphics2D) graphics;

			graphics2D.setColor(preferences.getBoardPreferences()
					.getMoveHighlightColor());
			Rectangle shape = new Rectangle(getWidth(), getHeight());
			graphics2D.setStroke(SELECT_ANIMATION_STROKE[selectAnimationStage]);
			graphics2D.draw(shape);
		}

		// Draw the piece last.
		if (chessPieceType != Piece.EMPTY && !isChessPieceTransparent) {
			int pieceSizeDelta = preferences.getBoardPreferences()
					.getPieceSizeDelta();

			BufferedImage image = chessPieceCache.getChessPiece(
					getPreferences().getBoardPreferences().getSet(),
					chessPieceType, getWidth() - pieceSizeDelta, getHeight()
							- pieceSizeDelta);
			graphics.drawImage(image, pieceSizeDelta / 2, pieceSizeDelta / 2,
					image.getWidth(), image.getHeight(), null);
		}
	}

	protected void onDragStart() {
		try {
			setChessPieceTransparent(true);
			dragStartSquare.repaint();
		} catch (Exception e) {
			LOGGER.error("Unexpected exception occured:", e);
		}
	}

	protected void onSuccessfulDropEnd() {
		if (dragStartSquare != null) {
			dragStartSquare.setChessPieceTransparent(false);
			dragStartSquare.repaint();
			GUIUtil.getGreatestParent(this).setCursor(null);
		}
		dragStartSquare = null;
		dragEndSquare = null;
	}

	protected void onUnsuccessfulDropEnd() {
		if (dragStartSquare != null) {
			dragStartSquare.setChessPieceTransparent(false);
			dragStartSquare.repaint();
			GUIUtil.getGreatestParent(this).setCursor(null);
		}
		dragStartSquare = null;
		dragEndSquare = null;
	}

	private class DropTargetListener extends DropTargetAdapter {
		private boolean sendMoveEvent(ChessBoardSquare sourceSquare,
				UserMoveEvent userMoveEvent) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sending move event " + hashCode() + " "
						+ sourceSquare.getBoardId());
			}
			if (userMoveInputListener != null) {
				return userMoveInputListener.userMoved(userMoveEvent);
			} else {
				return true;
			}
		}

		private void sendIncompleteMove() {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sending incomplete event " + hashCode() + " "
						+ dragStartSquare.getBoardId());
			}

			if (userMoveInputListener != null) {
				userMoveInputListener
						.userMadeIncompleteMove(new UserIncompleteMoveEvent(
								dragStartSquare.getBoardId()));
			}

		}

		public void drop(DropTargetDropEvent arg0) {
			synchronized (ChessBoardSquare.class) {
				arg0.acceptDrop(DnDConstants.ACTION_MOVE);
				arg0.dropComplete(true);

				dragEndSquare = thisSquare;
			}
			LOGGER.debug("dropping piece data startSquare=" + dragStartSquare
					+ " endSquare=" + dragEndSquare);

			if (!dragStartSquare.getBoardId()
					.equals(dragEndSquare.getBoardId())) {
				sendIncompleteMove();
				dragStartSquare.onUnsuccessfulDropEnd();
			} else if (dragStartSquare == dragEndSquare) {
				sendIncompleteMove();
				dragStartSquare.onUnsuccessfulDropEnd();
			} else if (dragEndSquare.getRank() != -1) {
				boolean result = sendMoveEvent(dragStartSquare,
						new UserMoveEvent(dragStartSquare, dragEndSquare,
								dragEndSquare.boardId));

				if (result) {
					dragStartSquare.onSuccessfulDropEnd();
				} else {
					dragStartSquare.onUnsuccessfulDropEnd();
				}
			} else {
				sendIncompleteMove();
				dragStartSquare.onUnsuccessfulDropEnd();
			}

		}
	}

	private static class ChessPieceTransferable implements Transferable {
		private int chessPiece;

		ChessPieceTransferable(int chessPieceType) {
			this.chessPiece = chessPieceType;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return "" + chessPiece;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { STRING_FLAVOR };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return STRING_FLAVOR.equals(flavor);
		}

		public int getPiece() {
			return chessPiece;
		}
	}

	private class ChessBoardSquareMouseMotionListener extends MouseAdapter {
		public void mouseClicked(MouseEvent arg0) {
			if (isMoveable && SwingUtilities.isRightMouseButton(arg0)) {

				sendRightClickEvent();
				arg0.consume();
			} else if (isMoveable && SwingUtilities.isLeftMouseButton(arg0)) {
				sendClickEvent();
				arg0.consume();
			}
		}
	}

	public class MyDragSourceListener implements DragSourceListener {

		ChessAreaFrame chessAreaFrame;

		private BufferedImage dragImage;

		private Point offset;

		private Cursor cursor;

		private Rectangle lastBounds;

		private LinkedList<Point> pointDrawList = new LinkedList<Point>();

		private Timer timer;

		/**
		 * Works but flashes and sometimes doesnt clean up properly.
		 */
		private ActionListener pieceDrawer = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point point = null;
				synchronized (pointDrawList) {
					point = pointDrawList.size() == 0 ? null : pointDrawList
							.getLast();
					pointDrawList.clear();
				}

				synchronized (this) {
					if (point != null) {
						JComponent glassPane = (JComponent) chessAreaFrame
								.getGlassPane();
						JComponent contentPane = ((JComponent) chessAreaFrame
								.getContentPane());
						SwingUtilities.convertPointFromScreen(point, glassPane);
						point.translate((int) (-1 * offset.getX()),
								(int) (-1 * offset.getY()));

						LOGGER.error("Painting image at " + point);

						// Erase the last ghostimage and cue line
						if (lastBounds != null) {
							contentPane.paintImmediately(lastBounds);
						}
						// Store off the ghost.
						lastBounds = new Rectangle(point, new Dimension(
								dragImage.getWidth(), dragImage.getHeight()));

						glassPane.getGraphics().drawImage(dragImage,
								(int) point.getX(), (int) point.getY(),
								dragImage.getWidth(), dragImage.getHeight(),
								null);

					}
				}
			}
		};

		public MyDragSourceListener(Point offset, BufferedImage dragImage) {
			chessAreaFrame = (ChessAreaFrame) GUIUtil
					.getGreatestParent(thisSquare);
			// LOGGER.debug("Set chess area frame.");
			this.dragImage = dragImage;
			this.offset = offset;
		}

		public MyDragSourceListener(Cursor cursor, Point offset,
				BufferedImage dragImage) {
			chessAreaFrame = (ChessAreaFrame) GUIUtil
					.getGreatestParent(thisSquare);
			this.dragImage = dragImage;
			this.offset = offset;
			this.cursor = cursor;
			timer = new Timer(100, pieceDrawer);
			timer.start();
		}

		public MyDragSourceListener(Cursor cursor) {
			chessAreaFrame = (ChessAreaFrame) GUIUtil
					.getGreatestParent(thisSquare);
			this.cursor = cursor;
		}

		public void dragDropEnd(DragSourceDropEvent arg0) {
			try {
				// LOGGER.debug("Entered dragDropEnd " + arg0.getDropSuccess() +
				// " "
				// + arg0);
				// If was a drop but not in the drop target, dragStartSquare
				// will
				// not
				// be null.
				if (dragStartSquare != null) {
					dragStartSquare.onUnsuccessfulDropEnd();
				}
				if (timer != null) {
					timer.stop();
				}

				// Erase the last ghostimage and cue line
				if (lastBounds != null) {
					((JComponent) chessAreaFrame.getContentPane())
							.paintImmediately(lastBounds);
				}

			} catch (Exception e) {
				LOGGER.error("Unexpected exception:", e);
			}
		}

		public void dragEnter(DragSourceDragEvent arg0) {
			try {
				// LOGGER.debug("Entered dragEnter " + " " + arg0);
				if (cursor != null) {
					chessAreaFrame.setCursor(cursor);
				}

			} catch (Exception e) {
				LOGGER.error("Unexpected exception:", e);
			}

		}

		public void dragExit(DragSourceEvent arg0) {
			try {
				// LOGGER.debug("Entered dragExit " + " " + arg0);
				if (cursor != null) {
					chessAreaFrame.setCursor(cursor);
				}
			} catch (Exception e) {
				LOGGER.error("Unexpected exception:", e);
			}
		}

		public void dragOver(DragSourceDragEvent arg0) {

			// TO DO instead of setting the cursor if DragImage isnt supported
			// redraw the
			// piece on the glass pane.
			if (// !DragSource.isDragImageSupported() &&
			dragImage != null) {
				synchronized (pointDrawList) {
					LOGGER.debug("Entered dragOver " + " " + arg0);
					pointDrawList.addLast(new Point(arg0.getX(), arg0.getY()));
				}
			}
		}

		public void dropActionChanged(DragSourceDragEvent arg0) {
			// LOGGER.debug("Entered dropActionChanged " + " " + arg0);
		}
	}

	public class MyDragGestureListener implements DragGestureListener {

		public void dragGestureRecognized(DragGestureEvent arg0) {
			synchronized (ChessBoardSquare.class) {
				dragStartSquare = thisSquare;

				if (isValidMoveStartSquare()) {
					if (preferences.getBoardPreferences().getDragAndDropMode() == BoardPreferences.STANDARD_DRAG_AND_DROP
							|| preferences.getBoardPreferences()
									.getDragAndDropMode() == BoardPreferences.CLICK_CLICK_DRAG_AND_DROP) {

						if (DragSource.isDragImageSupported()) {
							// OSX seemslike the only os that supports this:
							int pieceDelta = preferences.getBoardPreferences()
									.getPieceSizeDelta();

							BufferedImage image = chessPieceCache
									.getChessPiece((ChessSet) getPreferences()
											.getBoardPreferences().getSet(),
											dragStartSquare.getChessPiece(),
											getWidth() - pieceDelta,
											getHeight() - pieceDelta);
							DragSource.getDefaultDragSource().startDrag(
									arg0,
									null,
									image,
									new Point(-image.getWidth() / 2, -image
											.getHeight() / 2),
									new ChessPieceTransferable(chessPieceType),
									new MyDragSourceListener(arg0
											.getDragOrigin(), image));
						} else {
							int pieceDelta = preferences.getBoardPreferences()
									.getPieceSizeDelta();
							Cursor cursor = getPreferences()
									.getBoardPreferences().getSet().getCursor(
											getChessPiece(),
											getWidth() - pieceDelta,
											getHeight() - pieceDelta);

							// BufferedImage image = chessPieceCache
							// .getChessPiece((ChessSet) getPreferences()
							// .getBoardPreferences().getSet(),
							// dragStartSquare.getChessPiece(),
							// getWidth() - pieceDelta,
							// getHeight() - pieceDelta);

							DragSource.getDefaultDragSource().startDrag(
									arg0,
									cursor,
									TRANSPARENT_IMAGE,
									new Point((getWidth() - pieceDelta) / 2,
											(getHeight() - pieceDelta) / 2),
									new ChessPieceTransferable(chessPieceType),
									new MyDragSourceListener(cursor));
							// new MyDragSourceListener(cursor, arg0
							// .getDragOrigin(), image));
						}
						onDragStart();
					} else {
						// invisible move mode
						DragSource.getDefaultDragSource().startDrag(arg0,
								INVIS_MOVE_CURSOR, TRANSPARENT_IMAGE,
								new Point((getWidth()) / 2, (getHeight()) / 2),
								new ChessPieceTransferable(chessPieceType),
								new MyDragSourceListener(INVIS_MOVE_CURSOR));
					}
				}
			}
		}
	}

}