package decaf.resources;

import java.awt.Image;
import java.io.File;
import java.net.URL;

import decaf.gui.pref.Preferences;

public interface ResourceManager {
	public String[] getBackgroundNames();

	public String[] getChessSetNames();

	public File getDecafUserHome();

	public Image getImage(String imageName);

	public int getInt(String bundleName, String resourceKey);

	public String getString(String bundleName, String resourceKey);

	public URL getUrl(String file);

	public boolean isApplet();

	public Preferences loadDefaultPreferences();

	public Preferences loadPreferences();

	public void savePerferences(Preferences preferences);
}
