> # Change Log #

  * Whats new/fixed in v1.1.1 (2-10-2008)
    1. Fixed wrong square selected when black castles. (cant remember but someone)
    1. Enhanced click click move to support clicking on your piece then another piece of yours and starting a new move. (mhill)
    1. Also fixed selection problems with click click move mode.
    1. Selected both squares on a premove.
    1. Fixed keystrokes not being forwarded to console in various cases from the chess area window.(PCM)
    1. Made the default clock sizes a bit bigger.
    1. Changed smart move to not use drop moves as candidates when randomly picking a move.(mastertan)
    1. Fixed whisper (PCM)
    1. Added some more seek graph configuration options. (Kozyr)
    1. Changed windows timeseal to the timeseal.exe in thief. The one that I was using had issues on some peoples pcs.

  * Whats new/fixed in v1.1 (1-2-2008)
    1. kozyr added a nice seek graph
    1. Redid the way the move list was laid out. It is now part of the layout instead of being in a split pane. Its orientation changes depending on where you have the holdings panel setup. You can also show and hide the move list form a button in the boards toolbar.
    1. Added an option to show a piece jail in the holdings panel.
    1. Selected both the start/end square for a move instead of just the end square.
    1. Fixed the bug where the second char was eaten when you typed two chars quickly in something other than the input field in the console.
    1. Added a fill background option for square selection mode.
    1. Added an option to store games in one pgn file instead of seperate ones.
    1. Fixed a bug where you could not change the foreground color of fonts in the Chat Text tab.
    1. Fixed some move list parsing bugs.
    1. Redid the default console toolbar. Removed the sought button, changed bugwho to bugwho gp and changed the order of the buttons a bit.
    1. Added a Right 3x2 option for a holdings panel mode.
    1. Fixed some resize bugs and layout problems with the different holdings panel modes.
    1. Changed the wording of the checkbox preferences to be easier to understand.
    1. Added options to show/hide move list by default for bug/chess observing and playing.
    1. Added Winboard and right oriented options in the ChessGui tab.
    1. Made up time text configurable.
    1. Changed the way telling to a channel/person tab works. Now the text is automatically prepended to the input panel. You can hit escape to erase it and type a normal fics command (like you would enter on the console).
    1. Added game result to pgn
    1. Added a diagonal line square selection mode.
    1. Added a granite, and some paper background images.
    1. Added a way to support cropping of a background image, or reusing it for all squares.
    1. Fixed a bug in OS X Jaguar where when a game started or a keystroke was forward to the input field in the console all text was selected and the next keystroke would delete everything.
    1. Fixed a bug where captions would not always show up on the right side in a game.
    1. Fixed a bug where temp tab keystrokes were not being forwarded to the input field.
    1. Added preferences for removing bug open,seek graph, and bug seek from the console toolbar.
    1. Fixed a bug where images were being generated with -1 width.
    1. Added a bug seek frame to easily match available bug teams and partners.
    1. cached seek graph position/dimensions.
    1. Fixed a bug where sought was not being parsed properly in some cases from the server for the seek graph resulting in games not opening.
    1. Fixed premove drop in bughouse.
    1. Hopefully fixed and DND bug that would not let you move a piece in rare circumstances, the only way to fix it was to reboot.
    1. Added announce check speech option.
    1. Added Games In Progress tab to Bug Seek.
    1. Added a match winner button to observed games (bug and regular).
    1. Added sub menu support to PersonPopup.properties (see file for more details).
    1. Added RightClickPopup.properties for a dry right click popup.
    1. Fixed some layout issues in Thief layout (for some holdings locations the move list is now on the bottom).
    1. Fixed some layout issues in Right Oriented layout.
    1. Commands are no longer eaten, you can type sought,moves 34, etc and see the output.


  * Whats new/fixed in v1.03 (12-20-2007)
    1. Added storing games in pgn, enable it in the Preferences/General tab. (many have suggested this).
    1. Fixed lag showing up reversed. (mastertan)
    1. Added result to the end of the move list. (Ludens)
    1. Added time up on for bug by default,configurable for other. (Ludens)
    1. Fixed some initial bug layout problems. If you were playing bugear was behind your board. (sgs)
    1. Added drop rook in bsetup mode. It was missing. (Tekken)
    1. Fixed comming spelling error in bugear. (mastertan)
    1. Fixed pawn spelling error in betsetup mode. (Tekken)
    1. Made move list optional if you are playing bug. Default is off. (Ludens)

  * Whats new/fixed in v1.02 (12-19-2007)
    1. Added code to reuse the current frame if it is available instead of creating a new one.
    1. Added smart move.
    1. Added click move drag and drop mode.
    1. Changed premove drop in bughouse to wait until you have the piece in hand before the premove is issued, even if it is your turn.
    1. Fixed a bughouse bug where the screen would flash to an old position at the start of a game.
    1. Fixed a bug where rarely a style 12 messages would be sent to the server console if the message containing the style 12 was greater than 600 characters (changed this to 900).
    1. Fixed the bug where a box would be displayed in invisible move mode on macs and some windows installs.


  * Whats new/fixed in v1.01 (12-16-2007)
    1. Added bughouse move lists.
    1. Added right click drop pieces/clear for bsetup in examine mode
    1. Added the ability to switch interface into ex mode when a user is made examiner of an obs game.
    1. Added Fill Screen option in the window menu.
    1. Added logging support in general tab.Console logging/channel logging/individual tell logging are all supported.
    1. Changed default window layouts to fill the screen.
    1. Refactored code/added s few missing copyright statements.
    1. preferences.obj, logs, and in widnows only proeprties are now located in ${user.home}/.Decaf (in windows this is c:/Documents And Settings/USER/.Decaf).
    1. Added a way to set all of the fonts to a specific font in chat text tab.
    1. Added a remove tab button.
    1. Added  around tabs with tells the user hasnt seen.
    1. Fixed bug where the background text on the dialog that pops up when you select a font is changed if the background color is changed.
    1. Changed default clock colors.

  * Whats new/fixed in v1.0 (12-10-2007)
    1. Removed cursor caching.
    1. Add chess set image caching and chess board square image caching.
    1. Added different sound implementations, windows gets javax sound, generic gets applet sound,osx gets applet sound.
    1. Added a move list. Currently it does not support bughouse, ex mode, or adjusting holdings in zh.
    1. Fixed clock not stopping/starting bugs.
    1. Changed drag and drop implementation, switched over to java DND. This should fix the drag and drop bugs.
    1. Added sound for tell/ptell/challenge that plays only if speech is disabled or disabled for the particular function needed (tell,ptell,notification,etc).
    1. Refactored package names to be more intuitive.
    1. Added a new invisible move cursor for OSX. The crosshairs were previously being used.
    1. Changed obsMove sound to a clicking sound. The sound would not play well on speakers that didnt have midrange/base.
    1. Added checkboxes for open,sl,bugopen instead of buttons. The buttons were misleading.
    1. Added a preference to control x when calculating chess piece size: square - x = chess piece size. This allows for smaller pieces and easier drag n drop if desired.
    1. Added a preferences for move list location.
    1. Refactored some thread changes made earlier to attempt to utilize less threads in osx.
    1. Added some performance enhancing code when changing chess pieces on a chess board for efficency.
    1. Fixed a promote capture bug where the move would fail if it was a capture.
    1. Fixed bug where keyboard input was not being forwarded if playing a game.
    1. Added forward/backward buttons to the move list.
    1. Fixed some zh algebraic parsing problems with move list.
    1. Added a preferences for controlling if an unfollow is issued when starting a game you are playing.


  * Whats new/fixed in v.96 (12-2-2007)
    1. Added cursor caching to fix some issues in windows.
    1. Added an applet version.
    1. Changed layouts to be more intuitive.

  * Whats new/fixed in v.94 (11-27-2007)
    1. Added right click Temportary channel tabs/Direct tell tabs. You can use \ to send a message that does not pertain to the tab in a tab.
    1. Fixed alert sound not being played.
    1. Fixed clocks not stopping sometimes.
    1. Fixed board not snapping exactly into place.
    1. Changed windows/linux default font/lnf/window positions to be more appealing.
    1. Fixed bug where you couldnt premove right click drop on an existing piece.
    1. Fixed bug where your possible drops were not properly displayed when you right clicked.
    1. Added ability to connect without timeseal.
    1. Fixed lag not being calculated correctly if you are playing.
    1. Fixed ptells not showing up in captions
    1. Fixed promotion bug, when you click on knight it was making a bishop
    1. Added invisible move drag and drop mode in the chess tab
    1. Fixed bug where shout text was not being adjusted to what was selected in preferences
    1. Added function keys and a mappings file, KeyMappings.properties. f9 now works like thief by setting tell x where x was the last person who told you, f3 is accept f4 is decline f6 is abort f7 is rematch.
    1. Fixed bug where a piece was labeled as transparent sometimes at the end of a game.
    1. Fixed sought parsing bug
    1. Fixed chess clock not ticking accurately. Set time to 0 and stopped decrementing when time runs out.
    1. Added about dialog.
    1. Ludens added a windows installer! (Thanks ludens!!).
    1. Removed speech.properties and added auto detection for speech.
    1. Fixed a bug where multiple line urls were not being parsed correctly.
    1. Moved threads over to Timers.

  * Whats new/fixed in v.93 (11-23-2007)
    1. Fixed bug where playing a game while observing sent style 12 messages to console. Now if you play a game you unfollow and unobserve all games.
    1. Fixed multiple preferences showing up if you used different window menus in OSX.
    1. Fixed bug where preferences was not mvoed to front if it was already visible and selected.

  * Whats new/fixed in v.92 (11-22-2007)
    1. Added runwindows rungeneric to build.xml.
    1. Fixed extraneous events published to the EventService
    1. Added right click on http:// option to launch in default browser.
    1. Added preference to control if observed games are moved to front or not.
    1. Fixed bug where your lag was not being updated.
    1. Lag is shown in 10ths of a second.
    1. 10ths are now shown from the server they werent previously.
    1. Fixed bug where games couldnt be unobserved.

  * Whats new/fixed in v.91 (11-21-2007)
    1. Added a way to launch a command via command line to play a sound. See the Install Guide for generic/linux for more details.
    1. Fixed a bug where the game end message was not being shown.
    1. Set logging back to ERROR it was accidentally on DEBUG in .90
    1. Added an option to set when 10th of a second are shown in the Chess tab.
    1. Changed default clock fonts to be 16 point instead of 14 point.
    1. Fixed a bug where sounds would not play after the app was running for a while

  * Whats new/fixed in v.90 (11-20-2007)
    1. Fixed all kinds of windows/linux message parsing issues that resulted in strange behavior and crashes.
    1. Fixed bug where preferences menu would pop up several times.
    1. Fixed bug where if you reconnect under a different name you couldnt play.
    1. Fixed games popping up in ex mode.
    1. Fixed the application going into an unstable state if a game popups up in ex mode when it shouldnt.
    1. Added 2 new square backgrounds, replaced marble with marble1.
    1. Slightly redid input text pane added some indention.
    1. Fixed unamed guest/guest not being able to obs games in win/linux.
    1. Fixed bug where game wouldnt stop if a move was received during the end event.
    1. Added kib/says/ptell captions
    1. Rewrote message parsing code.

  * Whats new/fixed in Beta3.9: (11-18-2007)
    1. Added tabbed chat.
    1. Added hover over someones name to see past tells to you.
    1. Added copy/paste menus.
    1. Fixed games not displaying inc properly in window title
    1. Hopefully fixed unamed guests not being able to play games in windows/linux
    1. Hopefully fixed bug boards rotating after a game is starting if you had rotated a previous bughouse game.
    1. Redid preferences dialog, added general and tabbed chat tabs.
    1. Added look and feel setting in general preferences.

  * Whats new/fixed in Beta3.8: (11-16-2007)
    1. Added square background images.
    1. Added code to just ignore a sound if one is already playing.
    1. Fixed Top/Bottom Holdings panel layout it was not displaying properly.
    1. Added some code to preload images before they are displayed in a seperate thread.

  * Whats new/fixed in Beta3.7: (11-16-2007)
    1. Fixed a bunch of linux issues.
    1. Added in coordinates (you can disable in preferences)
    1. Fixed chat panel shaking when typing text.
    1. Fixed toolbar not updating after finishing a game.
    1. Added Resources Profile under File Menu to aid in detecting memory leaks.
    1. Made game window titles more verbose.
    1. Changed Prefrences dialog to fit on smaller resolutions.
    1. Changed default window sizes to fit on smaller resolutions.
    1. Changed partner suggestions to use short algebraic instead of long algebraic.
    1. Changed partner suggestions to n@ instead of Knight At.

  * Whats new/fixed in Beta3.6: (11-14-2007)
    1. Changed sounds to be not as frightening
    1. Added colored lag
    1. Added sending text to console if other window has focus (Ludens)
    1. Added osx showing main menu in any window.
    1. Fixed lag not getting reset each game.
    1. Changed default set to WCN.


  * Whats new/fixed in Beta3.5: (11-12-2007)
    1. Fixed invalid move sound playing after you play 3 games and the double spoken countdown,
    1. Added auto login
    1. follow /B works now
    1. Redid game start events
    1. Fixed bug games not observing sometimes

  * Whats new/fixed in Beta3.4: (11-11-2007)
    1. Added in safeguards to check recycled frames to make sure they are stable before reusing them.
    1. Fixed bug that was not cleaning up a frame properly after it was recycled.
    1. Fixed some more bugs dealing with event handling.
    1. Added more synchonization for thread safety.
    1. Fixed bug where status bar wasnt being cleared in the second game.
    1. Moved log4j.properties into properties so it can be configured to debug if needed (default is error).

Unknown end tag for &lt;/li&gt;


    1. Compiled release optimized without debug for efficency.


  * Whats new/fixed in Beta3.3: (11-10-2007)
    1. Cleaned up some event subscribers/listeners that were not being removed when a frame was recycled.
    1. Fixed a bug where scroll lock was being turned on if it was off and a snap to layout was being issued. (Maras)
    1. Further reducted the default chat speed buttons so they all show up on lower resolutions. (mrundersun)
    1. Added back not letting you close a window you are playing.
    1. Fixed invalid move sound playing every move when you play more than 1 games.
    1. Turned off validation for atomic,wild/fr,suicide,losers. (You can edit Decaf.properties and add any other  game type you want to turn off). (mrundersun)
    1. Added a left/right holdings mode to view holdings in zh and bug.
    1. Fixed some failfast list bugs.