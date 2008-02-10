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
package decaf.dialog;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ProfileDialog extends JDialog {

	private JLabel heapm, heap1, heap2, heap3, heap4, stackm, stack1, stack2,
			stack3, stack4, threadsm, threads1, threads2, threads3;

	private ProfileDialog thisDialog = this;

	public ProfileDialog() {
		super();
		setTitle("Resource Profile");
		setModal(false);
		final MemoryUsage heap = ManagementFactory.getMemoryMXBean()
				.getHeapMemoryUsage();
		final MemoryUsage stack = ManagementFactory.getMemoryMXBean()
				.getNonHeapMemoryUsage();
		final ThreadMXBean threads = ManagementFactory.getThreadMXBean();

		getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		getContentPane().add(heapm = new JLabel("Heap: "));
		getContentPane()
				.add(
						heap1 = new JLabel("   Initial: " + heap.getInit()
								/ 1024 + "K"));
		getContentPane().add(
				heap2 = new JLabel("   Used: " + heap.getUsed() / 1024 + "K"));
		getContentPane().add(
				heap3 = new JLabel("   Committed: " + heap.getMax() / 1024
						+ "K"));
		getContentPane().add(
				heap4 = new JLabel("   Max: " + heap.getMax() / 1024 + "K"));
		getContentPane().add(Box.createHorizontalStrut(15));
		getContentPane().add(stackm = new JLabel("Non Heap:"));
		getContentPane().add(
				stack1 = new JLabel("   Initial: " + stack.getInit() / 1024
						+ "K"));
		getContentPane()
				.add(
						stack2 = new JLabel("   Used: " + stack.getUsed()
								/ 1024 + "K"));
		getContentPane().add(
				stack3 = new JLabel("   Committed: " + stack.getMax() / 1024
						+ "K"));
		getContentPane().add(
				stack4 = new JLabel("   Max: " + stack.getMax() / 1024 + "K"));
		getContentPane().add(Box.createHorizontalStrut(15));
		getContentPane().add(threadsm = new JLabel("Threads:"));
		getContentPane()
				.add(
						threads1 = new JLabel("   Threads: "
								+ threads.getThreadCount()));
		getContentPane().add(
				threads2 = new JLabel("   Peak Threads: "
						+ threads.getPeakThreadCount()));
		getContentPane().add(
				threads3 = new JLabel("   Total Started Threads: "
						+ threads.getTotalStartedThreadCount()));
		getContentPane().add(Box.createHorizontalStrut(15));

		add(new JButton(new AbstractAction(
				"Dump Thread Traces to threaddump.txt") {
			public void actionPerformed(ActionEvent e) {
				final ThreadMXBean threads = ManagementFactory
						.getThreadMXBean();
				long[] threadIds = threads.getAllThreadIds();
				PrintWriter printWriter = null;
				try {
					printWriter = new PrintWriter(new FileWriter(
							"threaddump.txt", false));
					printWriter.println("Decaf thread dump initiated on "
							+ new Date());
					for (int i = 0; i < threadIds.length; i++) {
						ThreadInfo threadInfo = threads.getThreadInfo(
								threadIds[i], 10);
						printWriter.println("Thread "
								+ threadInfo.getThreadName() + " Block time:"
								+ threadInfo.getBlockedTime() + " Block count:"
								+ threadInfo.getBlockedCount() + " Lock name:"
								+ threadInfo.getLockName() + " Waited Count:"
								+ threadInfo.getWaitedCount() + " Waited Time:"
								+ threadInfo.getWaitedTime() + " Is Suspended:"
								+ threadInfo.isSuspended());
						StackTraceElement[] stackTrace = threadInfo
								.getStackTrace();
						for (int j = 0; j < stackTrace.length; j++) {
							printWriter.println(stackTrace[j]);
						}

					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (printWriter != null) {
						try {
							printWriter.flush();
							printWriter.close();
						} catch (Exception e2) {
						}
					}
				}
			}
		}));

		add(new JButton(new AbstractAction("Suggest Garbage Collection") {
			public void actionPerformed(ActionEvent e) {
				System.gc();
			}
		}));

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (!thisDialog.isVisible()) {
					timer.cancel();
					return;
				}

				MemoryUsage curHeap = ManagementFactory.getMemoryMXBean()
						.getHeapMemoryUsage();
				MemoryUsage curStack = ManagementFactory.getMemoryMXBean()
						.getNonHeapMemoryUsage();
				ThreadMXBean curThreads = ManagementFactory.getThreadMXBean();

				heap1.setText("   Initial: " + curHeap.getInit() / 1024 + "K");
				heap2.setText("   Used: " + curHeap.getUsed() / 1024 + "K");
				heap3.setText("   Committed: " + curHeap.getMax() / 1024 + "K");
				heap4.setText("   Max: " + curHeap.getMax() / 1024 + "K");

				stack1
						.setText("   Initial: " + curStack.getInit() / 1024
								+ "K");
				stack2.setText("   Used: " + curStack.getUsed() / 1024 + "K");
				stack3.setText("   Committed: " + curStack.getMax() / 1024
						+ "K");
				stack4.setText("   Max: " + curStack.getMax() / 1024 + "K");

				threads1.setText("   Threads: " + curThreads.getThreadCount());
				threads2.setText("   Peak Threads: "
						+ curThreads.getPeakThreadCount());
				threads3.setText("   Total Started Threads: "
						+ curThreads.getTotalStartedThreadCount());
			}

		}, 2000, 2000);

		pack();
		setVisible(true);
	}
}
