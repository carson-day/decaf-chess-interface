package decaf.resources;

import java.awt.Image;
import java.io.File;
import java.net.URL;

import decaf.gui.pref.Preferences;

public interface ResourceManager {
	public Image getImage(String imageName);

	public URL getUrl(String file);

	public String getString(String bundleName, String resourceKey);

	public int getInt(String bundleName, String resourceKey);

	public void savePerferences(Preferences preferences);

	public Preferences loadPreferences();

	public Preferences loadDefaultPreferences();

	public String[] getChessSetNames();

	public String[] getBackgroundNames();

	public File getDecafUserHome();

	public boolean isApplet();
}
