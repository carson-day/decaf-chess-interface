Creating personalized greeting and salutation buttons
# Add a button to greet opponents by handle #
First we need to make an alias.

In the FICS console, enter:

`alias greet say Hello $o!`

($o is a FICS variable that is replaced by your latest opponent's handle)

Next, open _Decaf/properties/PlayingGameToolbar.properties_ in a text editor or word processor.

You will see pairs of numbered text lines. The lines making-up a pair share the same number.

The pairs themselves are numbered sequentially and each pair creates a button on the chessboard toolbar.

The last pair in the file (in Version 1.3, at least) are:
```
6.text=Draw
6.command=draw
```
which renders the **Draw** button on the chessboard toolbar.

Now add this pair of lines to create a **Greet** button:
```
7.text=Greet
7.command=greet
```
Save the file. If Decaf is open, restart it so the changes will take effect.

When the chessboard opens for a new game, just click on the **Greet**
button to send a personalized greeting to your opponent.

## A button to thank opponents by handle at games' end is just as easy: ##

Make an alias, such as:

`alias salute say Thank you for the game, $o!`

**Important:** the button configuration must to added to a different file,
namely:

_Decaf/properties/AfterPlayingGameToolbar.properties_

Open it in your editor and add the following:
```
4.text=Salute
4.command=salute
```
At games end the chessboard toolbar buttons are replaced by end-of-game buttons.
**Note:** sometimes you must refresh the window for this to happen.

Just click **Salute** to send a personalized message to your opponent.