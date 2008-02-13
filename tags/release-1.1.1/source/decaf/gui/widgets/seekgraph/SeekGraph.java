/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Sergei Kozyrenko (kozyr82@gmail.com)
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
package decaf.gui.widgets.seekgraph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * Seek Graph
 * 
 * @author Sergei Kozyrenko
 * 
 */
public class SeekGraph extends JComponent implements MouseMotionListener {

	private static final Logger logger = Logger.getLogger(SeekGraph.class);

	private static final int SEEK_SIZE = 10;

	private static final float MAX_SECONDS = 1200.00f;

	private Map<Point, List<Seek>> seeks;

	private Map<Point, Point> screen;

	private JPopupMenu menu;

	private Rectangle _lastPopupRect;

	private int inset;

	private Color MANY = Color.green;

	private Color RATED = Color.red;

	private Color UNRATED = Color.cyan;

	private Color COMPUTER = Color.gray;

	private BufferedImage legendImage;

	private int hstart = 1000;

	// Allows more space where needed
	private int[][] hscale = { { 1300, 1 }, { 1500, 2 }, { 1700, 2 },
			{ 1900, 2 }, { 2100, 1 }, { 2500, 1 } };

	// this should be the sum of second column
	int hfactor = 9;

	// Same as for hscale
	private int vstart = 0;

	int[][] vscale = { { 1, 1 }, { 3, 2 }, { 5, 2 }, { 10, 1 }, { 15, 1 },
			{ 20, 1 } };

	private int vfactor = 8;

	private List<AcceptSeekListener> acceptListeners;
	
	private boolean showComputer = true;
	private boolean showUnrated = true;

	public SeekGraph() {
		seeks = new HashMap<Point, List<Seek>>();
		screen = new HashMap<Point, Point>();
		inset = 20;
		menu = new JPopupMenu();
		acceptListeners = new LinkedList<AcceptSeekListener>();

		addMouseMotionListener(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Point relative = e.getPoint();
					Point real = new Point(relative);
					SwingUtilities.convertPointToScreen(real, e.getComponent());
					acceptGameAt(relative, real);
				}
			}
		});
	}
	
	public void setVStart(int start) {
		vstart = start;
	}
	
	public void setHStart(int start) {
		hstart = start;
	}
	
	public void setVScale(int [][] scale) {
		vscale = scale;
		
		vfactor = 0;
		for (int [] range : vscale) {
			vfactor += range[1];
		}
	}
	
	public void setHScale(int [][] scale) {
		hscale = scale;
		
		hfactor = 0;
		for (int [] range : hscale) {
			hfactor += range[1];
		}
	}
	
	public void setShowComputerSeeks(boolean value) {
		showComputer = value;
	}
	
	public void setShowUnratedSeeks(boolean value) {
		showUnrated = value;
	}
	
	public void setComputerColor(Color c) {
		COMPUTER = c;
	}
	
	public void setRatedColor(Color c) {
		RATED = c;
	}
	
	public void setUnratedColor(Color c) {
		UNRATED= c;
	}
	
	public void setManyColor(Color c) {
		MANY = c;
	}
	
	public void redoLegend() {
		legendImage = null;
	}

	protected void acceptGameAt(Point where, Point realWhere) {
		for (Point loc : screen.keySet()) {

			Rectangle rect = new Rectangle(loc.x, loc.y, SEEK_SIZE, SEEK_SIZE);

			if (rect.contains(where)) {
				List<Seek> existing = seeks.get(screen.get(loc));
				if (existing.size() > 1) {
					showAcceptPopup(realWhere, loc, rect);
				} else {
					notifyAcceptListeners(existing.get(0).getAdNumber());
				}

				break;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		// who cares
	}

	public void mouseMoved(MouseEvent e) {
		Point where = e.getPoint();
		boolean showing = false;

		for (Point loc : screen.keySet()) {

			Rectangle rect = new Rectangle(loc.x, loc.y, SEEK_SIZE, SEEK_SIZE);

			if (rect.contains(where)) {
				SwingUtilities.convertPointToScreen(where, e.getComponent());
				showAcceptPopup(where, loc, rect);
				showing = true;
				break;
			}
		}

		if (!showing) {
			menu.setVisible(showing);
			// we're not pointing at anything, so reset _lastPopupRect
			_lastPopupRect = null;
		}
	}

	private void showAcceptPopup(Point realLoc, Point loc, Rectangle rect) {
		// are we're already showing for this?
		if (_lastPopupRect == null || !rect.equals(_lastPopupRect)) {
			if (menu.getComponentCount() > 0)
				menu.removeAll();
			List<Seek> existing = seeks.get(screen.get(loc));
			menu.add(new JLabel("Accept:"));
			for (Seek seek : existing) {
				String rating = (seek.getRating() == -1) ? " (Guest) " : " ("
						+ seek.getRating() + ") ";
				String rated = seek.isRated() ? "r" : "ur";
				Action accept = new AcceptAction(
						seek.getName()
								+ rating
								+ seek.getMins()
								+ " "
								+ seek.getIncr()
								+ " "
								+ rated
								+ (seek.isInterestingType() ? " "
										+ seek.getType() : ""), seek
								.getAdNumber());
				menu.add(new JMenuItem(accept));
			}
			_lastPopupRect = rect;
			menu.setLocation(realLoc);
			menu.setVisible(true);
		}
	}

	public void addSeek(final int gameNumber, final int rating,
			final String name, final int mins, final int incr,
			final boolean rated) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addSeek(new Seek(gameNumber, rating, name, mins, incr, rated),
						false);
			}
		});
	}

	private void addSeek(Seek seek, boolean fullRepaint) {
		
		if (seek.isComputer() && !showComputer) return;
		
		if (!seek.isRated() && !showUnrated) return; 
		
		Point loc = new Point(seek.getX(), seek.getY());
		List<Seek> existing = seeks.get(loc);
		if (existing == null) {
			existing = new LinkedList<Seek>();
			seeks.put(loc, existing);
		}

		boolean already = false;
		for (Seek s : existing) {
			if (s.getAdNumber() == seek.getAdNumber()) {
				already = true;
				break;
			}
		}

		if (!already) {
			existing.add(seek);

			if (!fullRepaint && isDisplayable()) {

				int width = getWidth();
				int height = getHeight();
				Point where = scale(loc, width - 2 * inset, height - 2 * inset);
				where.y = height - inset - where.y;
				where.x = where.x + inset;
				repaint(where.x - SEEK_SIZE / 2, where.y - SEEK_SIZE / 2,
						SEEK_SIZE, SEEK_SIZE);
			}
		}
	}

	/**
	 * This is empirically faster then replace by one, as it just does one
	 * repaint call
	 * 
	 * @param incoming
	 */
	public void replaceBy(final List<Seek> incoming) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				long before = System.nanoTime();
				screen.clear();
				seeks.clear();
				_lastPopupRect = null;

				for (Seek seek : incoming) {
					addSeek(seek, true);
				}
				repaint();
				long after = System.nanoTime();

				if (logger.isDebugEnabled()) {
					logger.debug("Time to reload: " + (after - before));
				}
			}
		});
	}

	public void replaceByOne(final List<Seek> incoming) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				long before = System.nanoTime();
				Set<Integer> current = new HashSet<Integer>();
				for (Seek seek : incoming) {
					addSeek(seek, false);
					current.add(seek.getRating());
				}

				// now, check whether there are things that are not in current
				// and remove them
				for (Point scp : screen.keySet()) {
					Point loc = screen.get(scp);
					List<Seek> existing = seeks.get(loc);
					for (Seek seek : existing) {
						if (current.contains(seek.getAdNumber())) {
							existing.remove(seek);
						}
					}
					if (existing.size() == 0) {
						screen.remove(scp);
						seeks.remove(loc);
						repaint(scp.x, scp.y, SEEK_SIZE, SEEK_SIZE);
					}
				}

				long after = System.nanoTime();
				if (logger.isDebugEnabled()) {
					logger.debug("Time to reload: " + (after - before));
				}

				// System.err.println("Time to reload: " + (after-before));
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		Rectangle clip = g2.getClipBounds();

		// fix resize problem
		if (clip.getWidth() == width && clip.getHeight() == height) {
			// we're probably resizing, this will invalidate screen map
			screen.clear();
			_lastPopupRect = null;
		}

		g2.setColor(Color.white);
		g2.fillRect(inset, inset, width - 2 * inset, height - 2 * inset);

		drawHorizontalLines(g2, height, height - 2 * inset, width, width - 2
				* inset);
		drawVerticalLines(g2, height, height - 2 * inset, width, width - 2
				* inset);
		drawLegend(g2, height, width);

		for (Point sp : seeks.keySet()) {
			Point p = scale(sp, width - 2 * inset, height - 2 * inset);
			p.y = height - inset - p.y - SEEK_SIZE / 2;
			p.x = p.x + inset - SEEK_SIZE / 2;
			if (clip.contains(p)) { // we will honor the clip!
				paintSeeks(g2, p, seeks.get(sp));
				screen.put(p, sp);
			}
		}

		g2.dispose();
	}

	private void drawLegend(Graphics2D g2, int height, int width) {
		
		int y = inset / 4;

		if (legendImage == null) {
			BufferedImage computer = createSingleLegend("Computer", COMPUTER);
			BufferedImage rated = createSingleLegend("Rated", RATED);
			BufferedImage unrated = createSingleLegend("Unrated", UNRATED);
			BufferedImage many = createSingleLegend("Many", MANY);

			legendImage = new BufferedImage(computer.getWidth()
					+ rated.getWidth() + unrated.getWidth() + many.getWidth(),
					SEEK_SIZE + 5, BufferedImage.TYPE_INT_ARGB);

			Graphics2D lg = legendImage.createGraphics();
			int cx = 0;
			lg.drawImage(computer, 0, 0, null);
			cx += computer.getWidth();
			lg.drawImage(rated, cx, 0, null);
			cx += rated.getWidth();
			lg.drawImage(unrated, cx, 0, null);
			cx += unrated.getWidth();
			lg.drawImage(many, cx, 0, null);
			lg.dispose();
		}

		int x = width - legendImage.getWidth() - inset; 
		g2.drawImage(legendImage, x, y, null);
	}

	private BufferedImage createSingleLegend(String text, Color color) {
		LegendLabel legend = new LegendLabel(text, color, SEEK_SIZE);
		legend.setSize(80, SEEK_SIZE + 5);
		BufferedImage ci = new BufferedImage(legend.getSize().width, legend
				.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D cig = ci.createGraphics();
		legend.paint(cig);
		cig.dispose();

		return ci;
	}

	private void drawHorizontalLines(Graphics2D g2, int height, int plotH,
			int width, int plotW) {

		int one = plotH / hfactor;

		g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1f, new float[] { 10f, 5f }, 0f));

		int factor = 0;
		for (int[] pair : hscale) {
			factor += pair[1];
			int h = height - one * factor - inset;
			g2.setColor(Color.blue);
			g2.drawLine(inset, h, inset + plotW, h);
			g2.setColor(Color.black);
			g2.drawString(String.valueOf(pair[0]), 0, h);
		}
	}

	private void drawVerticalLines(Graphics2D g2, int height, int plotH,
			int width, int plotW) {

		int one = plotW / vfactor;

		g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1f, new float[] { 10f, 5f }, 0f));

		int factor = 0;
		for (int[] pair : vscale) {
			factor += pair[1];
			int w = one * factor + inset;
			g2.setColor(Color.blue);
			g2.drawLine(w, height - inset, w, inset);
			g2.setColor(Color.black);
			g2.drawString(String.valueOf(pair[0]), w, height - inset / 2);
		}

		// for (int scale : vscale) {
		// int x = (int) (inset + (plotW / MAX_SECONDS * scale*60));
		// g2.setColor (Color.blue);
		// g2.drawLine(x, height - inset, x, inset);
		// g2.setColor (Color.black);
		// g2.drawString(String.valueOf(scale), x, height - inset/2 );
		// }
	}

	private void paintSeeks(Graphics2D g2, Point p, List<Seek> here) {
		Color color = UNRATED;

		if (here.size() == 1) {
			Seek s = here.get(0);
			if (s.isComputer()) {
				if (!showComputer) return;
				color = COMPUTER;
			} else if (s.isRated()) {
				color = RATED;
			} else if (!s.isRated() && !showUnrated) return;
		} else {
			color = MANY;
		}

		g2.setColor(color);
		g2.fillOval(p.x, p.y, SEEK_SIZE, SEEK_SIZE);
	}

	protected Point scale(Point p, int width, int height) {
		Point result = new Point(-1, -1);

		float one = ((float) height) / hfactor;

		// scale appropriately
		if (p.y < hstart) {
			result.y = 1;
		} else {
			int factor = 0;
			int prev = hstart;
			for (int[] pair : hscale) {
				if (p.y <= pair[0]) {
					result.y = (int) (factor * one + pair[1] * one
							/ (pair[0] - prev) * (p.y - prev));
					break;
				}
				factor += pair[1];
				prev = pair[0];
			}
		}

		if (result.y == -1) {
			result.y = height;
		}

		float oneW = ((float) width) / vfactor;

		// scale appropriately
		if (p.x < vstart) {
			result.x = 1;
		} else {
			int factor = 0;
			int prev = vstart;
			float scaled_x = p.x;
			for (int[] pair : vscale) {
				if (scaled_x <= pair[0] * 60) {
					result.x = (int) (factor * oneW + pair[1] * oneW
							/ (pair[0] * 60 - prev) * (scaled_x - prev));
					break;
				}
				factor += pair[1];
				prev = pair[0] * 60;
			}
		}

		if (result.x == -1) {
			result.x = width;
		}

		// result.x = (int) (((float) width) / MAX_SECONDS * p.x);
		// if (result.x > width) {
		// result.x = width;
		// }

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Seek Test");

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setSize(640, 480);
				frame.setLayout(new BorderLayout());

				final SeekGraph graph = new SeekGraph();
				graph.addSeek(24, 1500, "Sergei", 5, 0, true);
				graph.addSeek(48, 1500, "Someone", 5, 0, true);
				graph.addSeek(48, 1500, "Someone", 3, 0, true);
				graph.addSeek(48, 1500, "Someone", 5, 2, true);
				graph.addSeek(48, 1500, "Someone", 3, 1, true);
				graph.addSeek(48, 1500, "Someone", 1, 0, true);
				Seek suicide = new Seek(34, 1300, "Suicide", 2, 3, false);
				suicide.setType("suicide");
				graph.addSeek(suicide, true);

				frame.add(graph, BorderLayout.CENTER);

				JButton button = new JButton("Add seek");
				button.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						Random random = new Random();
						int rating = random.nextInt(1000) + 1000;
						int mins = random.nextInt(3);
						int incr = random.nextInt(10);
						String[] names = new String[] { "Hi", "Bye", "My",
								"Try" };
						int gameNumber = random.nextInt(1000);

						System.err.println("Adding seek ( " + rating + ", "
								+ mins + ", " + incr + " )");
						graph.addSeek(gameNumber, rating, names[random
								.nextInt(names.length)], mins, incr, random
								.nextBoolean());
					}

				});
				frame.add(button, BorderLayout.SOUTH);
				frame.setVisible(true);
			}
		});
	}

	class AcceptAction extends AbstractAction {
		public AcceptAction(String name, int gameNumber) {
			super();
			putValue(Action.ACTION_COMMAND_KEY, "play " + gameNumber);
			putValue(Action.NAME, name);
		}

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			hideSelectMenu();
			notifyAcceptListeners(Integer.parseInt(command.substring(5)));
		}
	}

	public void addAcceptSeekListener(AcceptSeekListener listener) {
		acceptListeners.add(listener);
	}

	public void hideSelectMenu() {
		if (menu != null && menu.isVisible()) {
			menu.setVisible(false);
			_lastPopupRect = null;
		}
	}

	public void notifyAcceptListeners(int adNumber) {
		for (AcceptSeekListener l : acceptListeners) {
			l.acceptedSeek(adNumber);
		}
	}

	public void removeAcceptSeekListener(AcceptSeekListener listener) {
		acceptListeners.remove(listener);
	}
}