package thesis.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bwapi.*;

public class AttackCoordinator {
	
	//List<Unit> enemyUnitsInRange;
	boolean[][] attackTable;
	int[] ownUnitsInRange;
	
	public AttackCoordinator() {
		//enemyUnitsInRange = new ArrayList<Unit>();
	}
	
	public void analyze(List<Unit> myUnits, List<Unit> enemyUnits) {
		List<Unit> myUnitsCopy = new ArrayList<Unit>(myUnits);
		List<Unit> enemyUnitsCopy = new ArrayList<Unit>(enemyUnits);
		Collections.sort(enemyUnitsCopy, new HPComparator());
		attackTable = new boolean[myUnits.size()][enemyUnits.size()];
		ownUnitsInRange = new int[enemyUnits.size()];
		int i = 0, j = 0;
		for(Unit eu : enemyUnits) {
			for(Unit mu : myUnits)
			{
				if(mu.isInWeaponRange(eu)) {
					attackTable[i][j] = true;
					ownUnitsInRange[i]++;
				}
				else
					attackTable[i][j] = false;
				j++;
			}
			i++;
		}		
	}
	
	public class HPComparator implements Comparator<Unit> {

		@Override
		public int compare(Unit o1, Unit o2) {
			return o2.getHitPoints() - o1.getHitPoints();
			
		}
		
	}
	
	
}
