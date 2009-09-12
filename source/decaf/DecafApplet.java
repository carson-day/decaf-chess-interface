package decaf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import decaf.dialog.LoginPanel;
import decaf.event.EventService;
import decaf.gui.GUIManager;
import decaf.gui.User;
import decaf.gui.pref.Preferences;
import decaf.messaging.ics.ICSCommunicationsDriver;
import decaf.resources.AppletResourceManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;

public class DecafApplet extends JApplet {
	private static final Logger LOGGER = Logger.getLogger(Decaf.class);

	private Preferences preferences = null;

	public DecafApplet() {
	}

	@Override
	public void init() {
		LOGGER.error("init applet " + this);
	}

	@Override
	public void start() {
		LOGGER.error("start applet " + this);
		ResourceManagerFactory.init(new AppletResourceManager());
		User user = User.getInstance();
		EventService.getInstance();
		preferences = ResourceManagerFactory.getManager().loadPreferences();

		preferences.getLoginPreferences().setDefaultTimesealEnabled(false);
		preferences.getLoginPreferences().setAutoLogin(false);
		final LoginPanel panel = new LoginPanel(preferences);

		panel.disableTimeseal();
		panel.disableAutoLogin();
		panel.disableServer();
		panel.disablePort();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				getContentPane().add(
						new JLabel("Loading ...", SwingConstants.CENTER),
						BorderLayout.CENTER);
				getContentPane().getLayout().layoutContainer(getContentPane());
				String userName, password, server;
				int port;
				boolean isTimesealEnabled, isGuest;

				userName = panel.getUserName();
				password = panel.getPassword();
				server = panel.getServer();
				port = panel.getPort();
				isGuest = panel.isLoggingInAsGuest();
				isTimesealEnabled = panel.isTimesealEnabled();

				GUIManager.getInstance().init(preferences);

				final String finalUserName = userName;
				final String finalPassowrd = password;
				final String finalServer = server;
				final int finalPort = port;
				final boolean finalIsGuest = isGuest;
				final boolean finalIsTimesealEnabled = isTimesealEnabled;

				// Ugly but break off a new thread to give the swing event
				// thread a
				// chance to update.
				ThreadManager.execute(new Runnable() {
					public void run() {
						ICSCommunicationsDriver driver = new ICSCommunicationsDriver(
								preferences);
						GUIManager.getInstance().setDriver(driver);

						try {
							if (finalIsGuest) {
								driver.connect(finalServer, finalPort,
										finalUserName, null, true,
										finalIsTimesealEnabled);
							} else {
								driver.connect(finalServer, finalPort,
										finalUserName, finalPassowrd, false,
										finalIsTimesealEnabled);
							}
						} catch (Exception e) {
							throw new RuntimeException(
									"Communications dirver error:", e);
						}
					}
				});
				getContentPane().removeAll();
				getContentPane().add(
						new JLabel("Loaded", SwingConstants.CENTER),
						BorderLayout.CENTER);
				getContentPane().getLayout().layoutContainer(getContentPane());
			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("started applet " + this);
		}
	}

	@Override
	public void stop() {
		LOGGER.error("stopping applet " + this);
		User.reset();
		try {
			GUIManager.getInstance().dispose();
		} catch (Exception e) {
		}

		try {
			EventService.getInstance().dispose();
		} catch (Exception e) {
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Stopped applet " + this);
		}
	}
}
