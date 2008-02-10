package decaf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import decaf.resources.ResourceManagerFactory;

public class ExtendedListUtil {
	public enum ExtendedList {
		CENSOR, NOPLAY
	};

	private static final File EXTENDED_CENSOR_FILE = new File(
			ResourceManagerFactory.getManager().getDecafUserHome(),
			"extendedCensor.txt");

	private static final File EXTENDED_NOPLAY_FILE = new File(
			ResourceManagerFactory.getManager().getDecafUserHome(),
			"extendedNoPlay.txt");

	private static List<String> extendedCensorList = loadFile(EXTENDED_CENSOR_FILE);

	private static List<String> extendedNoplayList = loadFile(EXTENDED_NOPLAY_FILE);

	public static String getContents(ExtendedList list) {
		String result = null;
		List<String> listToUse = null;

		switch (list) {
		case CENSOR: {
			result = "Extended Censor List: ";
			listToUse = extendedCensorList;
			break;
		}
		case NOPLAY: {
			result = "Extended Noplay List: ";
			listToUse = extendedNoplayList;
			break;
		}
		default: {
			throw new IllegalArgumentException("Unknown list " + list);
		}
		}

		for (String entry : listToUse) {
			result += entry + " ";
		}
		return result;
	}

	public static boolean contains(ExtendedList list, String name) {
		switch (list) {
		case CENSOR: {
			return extendedCensorList.contains(name);
		}
		case NOPLAY: {
			return extendedNoplayList.contains(name);
		}
		default: {
			throw new IllegalArgumentException("Unknown list " + list);
		}
		}
	}

	public static void add(ExtendedList list, String name) {
		switch (list) {
		case CENSOR: {
			if (!extendedCensorList.contains(name)) {
				extendedCensorList.add(name);
				updateFile(extendedCensorList, EXTENDED_CENSOR_FILE);
			}
			break;
		}
		case NOPLAY: {
			if (!extendedNoplayList.contains(name)) {
				extendedNoplayList.add(name);
				updateFile(extendedNoplayList, EXTENDED_NOPLAY_FILE);
			}
			break;
		}
		default: {
			throw new IllegalArgumentException("Unknown list " + list);
		}

		}
	}

	public static void remove(ExtendedList list, String name) {
		switch (list) {
		case CENSOR: {
			extendedCensorList.remove(name);
			updateFile(extendedCensorList, EXTENDED_CENSOR_FILE);
			break;
		}
		case NOPLAY: {
			extendedNoplayList.remove(name);
			updateFile(extendedNoplayList, EXTENDED_NOPLAY_FILE);
			break;
		}
		default: {
			throw new IllegalArgumentException("Unknown list " + list);
		}

		}
	}

	public static List<String> loadFile(File file) {
		List<String> result = new LinkedList<String>();
		try {
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while (line != null) {
					result.add(line.trim());
					line = reader.readLine();
				}
				reader.close();
			}
			return result;

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}

	private static void updateFile(List<String> list, File file) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			for (String entry : list) {
				writer.println(entry);
			}
			writer.flush();
			writer.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
