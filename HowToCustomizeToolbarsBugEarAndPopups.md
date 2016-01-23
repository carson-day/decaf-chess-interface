## How to customize toolbars and popups and BugEar ##

In the Decaf/properties directory are several properties files controlling toolbars and popups.

In windows check in c:/Documents And Settings/USER/.Decaf/properties

## Toolbars ##
AfterPlayingGameToolbar.properties
PlayingGameToolbar.properties
PlayingBugToolbar.properties
ExamineGameToolbar.properties
ChatToolbar.properties

Each properties file should have comments at the top of the file with instructions.
Typically you number each button you want starting with 1, and you can use seperators.
Make sure your entires are sequential.

### Example: Adding a resign button: ###
Lets add a seperator before draw and a resign button after draw.

PlayingGameToolbar.properties (before)
```
#Reserved Buttons $CLOSE as a command will close the game window.
#                 $FLIP as a command will flip the board.
#                 All other commands are sent to fics.
#                 $GAME_ID can be used to send the games id to the server e.g. (allobs $GAME_ID)
#                 $CLEAR_PREMOVE can be used to clear premove

1.text=Clear Premove
1.command=$CLEAR_PREMOVE
2.text=All Observers
2.command=allobs $GAME_ID
3.text=Draw
3.command=draw
```


PlayingGameToolbar.properties (after):
```
#Reserved Buttons $CLOSE as a command will close the game window.
#                 $FLIP as a command will flip the board.
#                 All other commands are sent to fics.
#                 $GAME_ID can be used to send the games id to the server e.g. (allobs $GAME_ID)
#                 $CLEAR_PREMOVE can be used to clear premove

1.text=Clear Premove
1.command=$CLEAR_PREMOVE
2.text=All Observers
2.command=allobs $GAME_ID
3.isSeperator=true
4.text=Draw
4.command=draw
5.text=Resign
5.command=resign
```


## BugEar ##
BugBottons.properties

You can customize the buttons that show up on bugear and what is sent to your partner. Instructions should appear at the top of the file.
Make sure your entries are sequential

## PersonPopup ##
PersonPopup.properties RightClickPopup.properties

You can customize what is displayed when you click on a persons name in the console or do a dry click. Popups support submenus and are quite similar to the toolbars mentioned above. Each file has commented instructions at the top on how to configure it.

Instructions should appear at the top of the file.

Make sure your entries are sequential.