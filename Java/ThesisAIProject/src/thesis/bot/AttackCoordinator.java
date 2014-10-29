package thesis.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bwapi.*;

public class AttackCoordinator {

	// List<Unit> enemyUnitsInRange;
	boolean[][] attackTable;
	int[] ownUnitsInRange;
	ArrayList<Unit> myUnitsCopy;
	ArrayList<Unit> enemyUnitsCopy;
	HashMap<Unit, boolean[]> attackTableb;
	Game game;
	// HashMap<Unit, Integer> ownUnitsInRangeb;

	public AttackCoordinator(Game game) {
		this.game = game;
		// enemyUnitsInRange = new ArrayList<Unit>();
	}

	public void analyze(List<Unit> myUnits, List<Unit> enemyUnits) {
		ArrayList<Unit> myUnitsCopy = new ArrayList<Unit>(myUnits);
		ArrayList<Unit> enemyUnitsCopy = new ArrayList<Unit>(enemyUnits);
		attackTableb = new HashMap<Unit, boolean[]>();
		// ownUnitsInRangeb = new HashMap<Unit, Integer>();

		int j = 0;
		for (Unit eu : enemyUnitsCopy) {
			boolean[] attackers = new boolean[myUnits.size()];
			int ownUnitsInRangeTemp = 0;
			for (Unit mu : myUnitsCopy) {
				if (mu.isInWeaponRange(eu)) {
					attackers[j] = true;
					ownUnitsInRangeTemp++;
					// attackTable[i][j] = true;
					// ownUnitsInRange[i]++;
				} else
					attackers[j] = false;
				j++;
			}
			attackTableb.put(eu, attackers);
			// ownUnitsInRangeb.put(eu, ownUnitsInRangeTemp);
		}

		Collections.sort(enemyUnitsCopy, new HPComparator());

		for (Unit eu : enemyUnitsCopy) {
			while (getNumberOfOwnUnitsInRange(eu) > hitsToKill(eu)) {
				int mostOccurences = 0;
				int mostOccurencesIndex = 0;
				int currentOccurences;
				for (int i = 0; i < myUnits.size(); i++) {
					currentOccurences = 0;
					for (Unit eu2 : enemyUnitsCopy) {
						if (attackTableb.get(eu2)[i] == true)
							currentOccurences++;
					}
					if (currentOccurences > mostOccurences) {
						mostOccurences = currentOccurences;
						mostOccurencesIndex = i;
					}
				}
				attackTableb.get(eu)[mostOccurencesIndex] = false;
			}
			for (int i = 0; i < getNumberOfOwnUnitsInRange(eu); i++)
				for (j = 0; j < myUnits.size(); j++)
					if (attackTableb.get(eu)[j] == true)
						for (Unit eu2 : enemyUnitsCopy)
							attackTableb.get(eu2)[j] = false;
		}
		attackTable = new boolean[myUnits.size()][enemyUnits.size()];
		ownUnitsInRange = new int[enemyUnits.size()];

	}

	private int getNumberOfOwnUnitsInRange(Unit eu) {
		int number = 0;
		for (boolean canAttack : attackTableb.get(eu))
			if (canAttack)
				number++;
		return number;
	}

	public class HPComparator implements Comparator<Unit> {

		@Override
		public int compare(Unit o1, Unit o2) {
			if (o2.getHitPoints() + o2.getShields() == o1.getHitPoints()
					+ o2.getShields())
				return (getNumberOfOwnUnitsInRange(o1) - getNumberOfOwnUnitsInRange(o2));
			return (o2.getHitPoints() + o2.getShields())
					- (o1.getHitPoints() + o2.getShields());
		}
	}

	public int hitsToKill(Unit enemyUnit) {
			List<Bullet> bullets = game.getBullets();
			bullets.get(0).getSource().getType().airWeapon();
		return 0;
	}

//	public boolean[] canBeReassigned(Unit enemyUnit) {
//		
//	}
}
