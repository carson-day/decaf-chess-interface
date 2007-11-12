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

public class PerformanceWidget extends JComponent {
	/*
	 * private static final String NO_RATING_ADJUSTMENT = "No ratings adjustment
	 * done.";
	 * 
	 * private static final User user = User.getInstance();
	 * 
	 * private static final EventService eventService =
	 * EventService.getInstance();
	 * 
	 * private PerformanceSubscriber subscriber;
	 * 
	 * private boolean userIsWhite;
	 * 
	 * private GameStartEvent userGameStartEvent;
	 * 
	 * private GInfoEvent partnerInfoEvent;
	 * 
	 * private RatingStats standardRatingStats;
	 * 
	 * private RatingStats blitzRatingStats;
	 * 
	 * private RatingStats crazyhouseRatingStats;
	 * 
	 * private RatingStats lightningRatingStats;
	 * 
	 * private RatingStats wildRatingStats;
	 * 
	 * private RatingStats bughouseRatingStats;
	 * 
	 * private RatingStats suicideRatingStats;
	 * 
	 * private RatingStats atomicRatingStats;
	 * 
	 * private RatingStats losersRatingStats;
	 * 
	 * private class RatingStats { public int numberOfGamesPlayed;
	 * 
	 * public int performanceTotal;
	 * 
	 * public int performanceRating; }
	 * 
	 * public PerformanceWidget() { standardRatingStats = new RatingStats();
	 * blitzRatingStats = new RatingStats(); crazyhouseRatingStats = new
	 * RatingStats(); lightningRatingStats = new RatingStats(); wildRatingStats =
	 * new RatingStats(); bughouseRatingStats = new RatingStats();
	 * suicideRatingStats = new RatingStats(); atomicRatingStats = new
	 * RatingStats(); losersRatingStats = new RatingStats();
	 * 
	 * subscriber = new PerformanceSubscriber();
	 * 
	 * eventService.subscribe(new Subscription(GInfoEvent.class, null,
	 * subscriber)); eventService.subscribe(new
	 * Subscription(GameStartEvent.class, null, subscriber));
	 * eventService.subscribe(new Subscription(GameEndEvent.class, null,
	 * subscriber));
	 * 
	 * setToolTipText("Performance ratings"); }
	 * 
	 * 
	 * 
	 * private String getPerformanceString() { String result = ""; if
	 * (bughouseRatingStats.performanceRating != 0) { result += " Bughouse(" +
	 * bughouseRatingStats.performanceRating + ")"; } if
	 * (crazyhouseRatingStats.performanceRating != 0) { result += " Crazyhouse(" +
	 * crazyhouseRatingStats.performanceRating + ")"; } if
	 * (lightningRatingStats.performanceRating != 0) { result += " Lightning(" +
	 * lightningRatingStats.performanceRating + ")"; } if
	 * (blitzRatingStats.performanceRating != 0) { result += " Blitz(" +
	 * blitzRatingStats.performanceRating + ")"; } if
	 * (suicideRatingStats.performanceRating != 0) { result += " Suicide(" +
	 * suicideRatingStats.performanceRating + ")"; } if
	 * (wildRatingStats.performanceRating != 0) { result += " Wild(" +
	 * wildRatingStats.performanceRating + ")"; } if
	 * (standardRatingStats.performanceRating != 0) { result += " Standard(" +
	 * standardRatingStats.performanceRating + ")"; } if
	 * (losersRatingStats.performanceRating != 0) { result += " Losers(" +
	 * losersRatingStats.performanceRating + ")"; } if
	 * (atomicRatingStats.performanceRating != 0) { result += " Atomic(" +
	 * atomicRatingStats.performanceRating + ")"; }
	 * 
	 * return result; }
	 * 
	 * protected void paintComponent(Graphics g) {
	 * 
	 * int height = getSize().height; int width = getSize().width;
	 * 
	 * g.setColor(Color.gray); g.drawRect(0, 0, width - 1, height - 1);
	 * 
	 * g.setColor(Color.black); g.setFont(getFont());
	 * 
	 * FontMetrics fontMetrics = g.getFontMetrics();
	 * 
	 * String text = getPerformanceString();
	 * 
	 * setToolTipText(text); int textWidth = fontMetrics.stringWidth(text); int
	 * ascent = g.getFontMetrics().getAscent();
	 * 
	 * g.drawString(text, 10, (height - ascent) / 2 + ascent); }
	 * 
	 * private void updateGameStats(String opponentRating, RatingStats stats,
	 * double score) { int opponentRatingInt = 0; try { opponentRatingInt =
	 * Integer.parseInt(opponentRating); } catch (NumberFormatException nfe) {
	 * opponentRatingInt = 1600; }
	 * 
	 * if (score == 0.0) { opponentRatingInt -= 200; } else if (score == 1.0) {
	 * opponentRatingInt += 200; }
	 * 
	 * stats.numberOfGamesPlayed++; stats.performanceTotal += opponentRatingInt;
	 * stats.performanceRating = stats.performanceTotal /
	 * stats.numberOfGamesPlayed; }
	 * 
	 * private void updateBughouseGameStats(String opponent1Rating, String
	 * opponent2Rating, double score) { int opponentRating1Int = 0; int
	 * opponentRating2Int = 0;
	 * 
	 * try { opponentRating1Int = Integer.parseInt(opponent1Rating); } catch
	 * (NumberFormatException nfe) { opponentRating1Int = 1600; }
	 * 
	 * try { opponentRating2Int = Integer.parseInt(opponent2Rating); } catch
	 * (NumberFormatException nfe) { opponentRating2Int = 1600; } int
	 * commbinedOppRating = (opponentRating1Int + opponentRating2Int) / 2;
	 * 
	 * if (score == 0.0) { commbinedOppRating -= 200; } else if (score == 1.0) {
	 * commbinedOppRating += 200; }
	 * 
	 * bughouseRatingStats.numberOfGamesPlayed++;
	 * bughouseRatingStats.performanceTotal += commbinedOppRating;
	 * bughouseRatingStats.performanceRating =
	 * bughouseRatingStats.performanceTotal /
	 * bughouseRatingStats.numberOfGamesPlayed; }
	 * 
	 * public class PerformanceSubscriber implements Subscriber { public void
	 * inform(GInfoEvent event) { if (user.getBughousePartner() != null &&
	 * event.getGameType() == GInfoEvent.BUGHOUSE &&
	 * (event.getWhitesName().equalsIgnoreCase( user.getBughousePartner()) ||
	 * event.getBlacksName() .equalsIgnoreCase(user.getBughousePartner()))) {
	 * partnerInfoEvent = event; } }
	 * 
	 * public void inform(GameStartEvent event) { if
	 * (event.getWhiteName().equalsIgnoreCase(user.getHandle()) ||
	 * event.getBlackName().equalsIgnoreCase(user.getHandle())) {
	 * userGameStartEvent = event; userIsWhite =
	 * event.getWhiteName().equalsIgnoreCase( user.getHandle()); } }
	 * 
	 * public void inform(GameEndEvent event) { if (userGameStartEvent != null) {
	 * if (isHandlingEvent(event)) { String opponentsRating = userIsWhite ?
	 * userGameStartEvent .getBlackRating() : userGameStartEvent
	 * .getWhiteRating();
	 * 
	 * double score = userIsWhite && event.getScore() == GameEndEvent.WHITE_WON ?
	 * 1.0 : userIsWhite && event.getScore() == GameEndEvent.BLACK_WON ? 0.0 :
	 * !userIsWhite && event.getScore() == GameEndEvent.BLACK_WON ? 1.0 :
	 * !userIsWhite && event.getScore() == GameEndEvent.WHITE_WON ? 0.0 : .5;
	 * 
	 * switch (userGameStartEvent.getGameType()) { case GameStartEvent.STANDARD: {
	 * updateGameStats(opponentsRating, standardRatingStats, score); break; }
	 * case GameStartEvent.BLITZ: { updateGameStats(opponentsRating,
	 * blitzRatingStats, score); break; } case GameStartEvent.LIGHTNING: {
	 * updateGameStats(opponentsRating, lightningRatingStats, score); break; }
	 * case GameStartEvent.WILD: { updateGameStats(opponentsRating,
	 * lightningRatingStats, score); break; } case GameStartEvent.CRAZYHOUSE: {
	 * updateGameStats(opponentsRating, crazyhouseRatingStats, score); break; }
	 * case GameStartEvent.SUICIDE: { updateGameStats(opponentsRating,
	 * suicideRatingStats, score); break; } case GameStartEvent.LOSERS: {
	 * updateGameStats(opponentsRating, losersRatingStats, score); break; } case
	 * GameStartEvent.ATOMIC: { updateGameStats(opponentsRating,
	 * atomicRatingStats, score); break; } case GameStartEvent.BUGHOUSE: { if
	 * (partnerInfoEvent != null) { String otherBoardOpponent = userIsWhite ?
	 * partnerInfoEvent .getWhitesRating() : partnerInfoEvent.getBlacksRating();
	 * updateBughouseGameStats(opponentsRating, otherBoardOpponent, score); }
	 * break; } default: { throw new IllegalArgumentException("Unknown game
	 * type:" + userGameStartEvent.getGameType()); } } repaint();
	 * userGameStartEvent = null; partnerInfoEvent = null; } } else { } }
	 * 
	 * private boolean isHandlingEvent(GameEndEvent event) { return
	 * userGameStartEvent != null && (event.getScore() == GameEndEvent.WHITE_WON ||
	 * event.getScore() == GameEndEvent.DRAW || event .getScore() ==
	 * GameEndEvent.BLACK_WON) && event.getText().indexOf(NO_RATING_ADJUSTMENT) ==
	 * -1 && (event.getGameId() == userGameStartEvent.getGameId() ||
	 * (userGameStartEvent .getGameType() == GInfoEvent.BUGHOUSE &&
	 * partnerInfoEvent != null && partnerInfoEvent .getGameId() ==
	 * event.getGameId())); } }
	 */
}