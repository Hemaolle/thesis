package thesis.bot;

import java.util.HashMap;
import java.util.List;

import bwapi.*;
import bwta.Region;
import bwta.*;

/**
 * Handles the visualizations on screen during the gameplay for the AI client.
 * 
 * @author Oskari Leppäaho
 * 
 */
public class Visualizer {

	final Game game;
	final Controller bot;
	final boolean isVisualizationOn;

	HashMap<Unit, Unit> attackTargets = new HashMap<Unit,Unit>(); // Key is the attacker, value is the
										// target.

	public Visualizer(Game game, Controller bot, boolean isVisualizationOn) {
		this.game = game;
		this.bot = bot;
		this.isVisualizationOn = isVisualizationOn;		
	}

	/**
	 * Draw a circle around both own and enemy units to highlight them.
	 */
	void highlightUnits() {
		if (isVisualizationOn) {
			int enemyCircleRadius = 128; // Maximum shooting distance for a
											// dragoon.
			int ownCircleRadius = 2;
			// Position enemyPosition = null;
			// Position myPosition = null;
			for (Unit u : bot.getEnemyUnitsNoRevealers()) {
				game.drawCircleMap(u.getPosition().getX(), u.getPosition()
						.getY(), enemyCircleRadius, Color.Orange, false);
				// bwapi.drawCircle(u.getPosition(), 120, BWColor.Orange, false,
				// false);
				// bwapi.drawCircle(u.getPosition(), 136, BWColor.Orange, false,
				// false);
				// System.out.println("Enemy position: " + u.getPosition());
				// enemyPosition = u.getPosition();
			}
			for (Unit u : bot.getMyUnitsNoRevealers()) {
				game.drawCircleMap(u.getPosition().getX(), u.getPosition()
						.getY(), ownCircleRadius, Color.Blue, false);
				// bwapi.drawCircle(u.getPosition(), 136, BWColor.Orange, false,
				// false);
				// System.out.println("Enemy position: " + u.getPosition());
				// myPosition = u.getPosition();
			}

			// System.out.println("Distance between units: " +
			// enemyPosition.getPDistance(myPosition));
		}
	}	

	/**
	 * Visualize the destination/target of the unit. If the unit is attacking,
	 * draw a red line to its target. If it is moving, draw a green line to its
	 * destination.
	 * 
	 * @param u
	 *            The unit to visualize.
	 * @param moveTo
	 */
	void visualizeDestination(Unit u, Position moveTo, boolean isAttacking) {
		if (isVisualizationOn) {
			if (isAttacking) {
				Unit attackTarget = attackTargets.get(u);
				game.drawLineMap(u.getPosition().getX(),
						u.getPosition().getY(), attackTarget.getPosition()
								.getX(), attackTarget.getPosition().getY(),
						Color.Red);
			} else if (moveTo != null) {
				game.drawLineMap(u.getPosition().getX(),
						u.getPosition().getY(), moveTo.getX(), moveTo.getY(),
						Color.Green);
			}
		}
	}

	/**
	 * Draws the potential values around an unit on the screen for
	 * visualization/debugging purposes.
	 * 
	 * @param potentials
	 *            Array of the potential values.
	 */
	void drawPotentialValues(double[] potentials) {
		if (isVisualizationOn) {
			String text;
			for (int i = 0; i < potentials.length; i++) {
				text = String.format("%.5f", potentials[i]);
				game.drawTextScreen(100 * (i % 3), 40 + 10 * ((i) / 3), text);
			}
			int highestIndex = Controller.findHighestDefaultTo4(potentials);
			text = String.format("%c%.5f", 0x06, potentials[highestIndex]);
			game.drawTextScreen(100 * (highestIndex % 3),
					40 + 10 * ((highestIndex) / 3), text);
		}
	}

	/**
	 * Sets the attack target for a unit.
	 * 
	 * @param attacker
	 *            The attacker.
	 * @param target
	 *            The target.
	 */
	public void setAttackTarget(Unit attacker, Unit target) {
		attackTargets.put(attacker, target);
	}
}
