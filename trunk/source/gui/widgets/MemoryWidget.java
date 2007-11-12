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

import javax.swing.JComponent;

public class MemoryWidget extends JComponent {
	/*
	 * private static final long SLEEP_TIME = 1000L;
	 * 
	 * private long currentMemory;
	 * 
	 * private long totalMemory;
	 * 
	 * private Thread memoryUpdaterThread;
	 * 
	 * private Runnable memoryUpdater;
	 * 
	 * public MemoryWidget() { memoryUpdater = new MemoryUpdater();
	 * memoryUpdaterThread = new Thread(memoryUpdater);
	 * memoryUpdaterThread.start(); setBackground(Color.lightGray);
	 * setOpaque(false); addMouseListener(new ClickMouseAdapter()); }
	 * 
	 * private class ClickMouseAdapter extends MouseAdapter { public void
	 * mouseReleased(MouseEvent event) { Runtime.getRuntime().gc(); } }
	 * 
	 * 
	 * protected void paintComponent(Graphics g) { float percentageToFill =
	 * (float) currentMemory / (float) totalMemory;
	 * 
	 * int height = getSize().height; int width = getSize().width;
	 * 
	 * int marker = (int) (percentageToFill * (float) width);
	 * 
	 * g.setColor(Color.yellow); g.fillRect(0, 0, marker, height);
	 * 
	 * g.setColor(Color.gray); g.drawRect(0, 0, width - 1, height - 1);
	 * 
	 * g.setColor(Color.gray); g.drawLine(marker, 0, marker, height);
	 * 
	 * g.setColor(Color.black); g.setFont(getFont());
	 * 
	 * String text = "" + currentMemory + "/" + totalMemory;
	 * 
	 * FontMetrics fontMetrics = g.getFontMetrics(); int textWidth =
	 * fontMetrics.stringWidth(text); int ascent =
	 * g.getFontMetrics().getAscent();
	 * 
	 * g.drawString(text, (width - textWidth) / 2, (height - ascent) / 2 +
	 * ascent); }
	 * 
	 * private class MemoryUpdater implements Runnable { public void run() {
	 * while (true) { currentMemory = Runtime.getRuntime().totalMemory();
	 * totalMemory = Runtime.getRuntime().freeMemory() + currentMemory;
	 * 
	 * setToolTipText("Memory usage: " + currentMemory + " out of " +
	 * totalMemory + " in use. Double click to suggest garbage collection.");
	 * 
	 * currentMemory = currentMemory / 1000000; totalMemory = totalMemory /
	 * 1000000;
	 * 
	 * repaint(); try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException
	 * ie) { } } } }
	 * 
	 * public static void main(String args[]) { JFrame frame = new JFrame();
	 * frame.getContentPane().setLayout(new BorderLayout());
	 * frame.getContentPane().add(new MemoryWidget()); frame.pack();
	 * frame.setVisible(true); }
	 */
}