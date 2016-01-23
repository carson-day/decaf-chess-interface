# How to customize/add Chess Sets and Image Square Backgrounds #

## Chess Sets ##
All of the chess sets in decaf have been copied from thief.
Thief uses yellow for the transparent color
red=255 blue=255 green=0. Decaf does also; however, you can also make
the background of the images transparent.

Naming Convention
  * SET.NAME.WPAWN.SUFFIX
  * SET.NAME.BPAWN.SUFFIX
  * SET.NAME.WKNIGHT.SUFFIX
  * SET.NAME.BKNIGHT.SUFFIX
  * SET.NAME.WBISHOP.SUFFIX
  * SET.NAME.BBISHOP.SUFFIX
  * SET.NAME.WQUEEN.SUFFIX
  * SET.NAME.BQUEEN.SUFFIX
  * SET.NAME.WROOK.SUFFIX
  * SET.NAME.BROOK.SUFFIX
  * SET.NAME.WKING.SUFFIX
  * SET.NAME.BKING.SUFFIX

To add a set simply follow the naming convention above replacing NAME with your unique name and copy the images to ./Decaf/Resources directory. They dont have to be BMP but it is preferred since images seem to be processed quicker as BMP in java. After restarting Decaf the new backgrounds should appear in Configure->Preferences.

If you come up with a new chess set and would like to contribute it please post in in group discussions or you can email it to me.

## Chess Square Backgrounds ##
Two images need to be provided to do square backgrounds. If you append use SQUARE.CROP.NAME instead of SQUARE.NAME Decaf clips each image into 64 squares, otherwise it uses the entire image scaled to the size of the square.

Naming Convention
  * SQUARE.NAME.LIGHT.BMP
  * SQUARE.CROP.NAME.LIGHT.BMP
  * SQUARE.NAME.DARK.BMP
  * SQUARE.CROP.NAME.DARK.BMP

To add square backgrounds simply follow the naming convention above replacing NAME with your unique name and copy the images to ./Decaf/Resources. They dont have to be BMP but it is preferred since images seem to be processed quicker as BMP in java. After restarting Decaf the new backgrounds should appear in Configure->Preferences.

If you come up with a new square background and would like to contribute it please post in in group discussions or you can email it to me.