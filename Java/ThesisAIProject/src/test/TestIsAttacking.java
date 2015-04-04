package test;

import java.util.HashMap;

import bwapi.*;
import bwta.*;

public class TestIsAttacking{

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    boolean isAttacking;
    
    /**
	 * Indicates if the isStartingAttack has already changed to true after a
	 * unit has been issued an attack command. The key is the unit ID.
	 */
	HashMap<Integer, Boolean> isAttackInProgress = new HashMap<Integer, Boolean>();
	
	/**
	 * Used to avoid giving a move order before the attack of a unit has been
	 * finished. The key is the unit ID.
	 */
	HashMap<Integer, Boolean> hasAttackOrderBeenGiven = new HashMap<Integer, Boolean>();

    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {
            @Override
            public void onUnitCreate(Unit unit) {
                System.out.println("New unit " + unit.getType());
            }

            @Override
            public void onStart() {
                game = mirror.getGame();
                self = game.self();
                
                final int ENABLECODE_USER_INPUT = 1;
                game.enableFlag(ENABLECODE_USER_INPUT);

                //Use BWTA to analyze map
                //This may take a few minutes if the map is processed first time!
                System.out.println("Analyzing map...");
                BWTA.readMap();
                BWTA.analyze();
                System.out.println("Map data ready");
                
            }

            @Override
            public void onFrame() {

        		populateUnitHashMaps();
                game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());

                StringBuilder units = new StringBuilder("My units:\n");

                //iterate through my units
                for (Unit myUnit : self.getUnits()) {
                	if(myUnit.getOrder() == Order.AttackMove || myUnit.getOrder() == Order.AttackTile || myUnit.getOrder() == Order.AttackUnit)
                		hasAttackOrderBeenGiven.put(myUnit.getID(), true);
                	if (isAttacking && !isAttacking(myUnit)) {
                		isAttacking = false;
                		myUnit.move(myUnit.getPosition());
                		
                	}                		
                	if (isAttacking(myUnit))
                		isAttacking = true;
                    units.append(myUnit.getType()).append(" ").append(
                    		myUnit.getTilePosition() + ", attacking: ").append(
                    				isAttacking(myUnit)).append("\n");

//                    //if there's enough minerals, train an SCV
//                    if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50) {
//                        myUnit.train(UnitType.Terran_SCV);
//                    }
//
//                    //if it's a drone and it's idle, send it to the closest mineral patch
//                    if (myUnit.getType().isWorker() && myUnit.isIdle()) {
//                        Unit closestMineral = null;
//
//                        //find the closest mineral
//                        for (Unit neutralUnit : game.neutral().getUnits()) {
//                            if (neutralUnit.getType().isMineralField()) {
//                                if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
//                                    closestMineral = neutralUnit;
//                                }
//                            }
//                        }
//
//                        //if a mineral patch was found, send the drone to gather it
//                        if (closestMineral != null) {
//                            myUnit.gather(closestMineral, false);
//                        }
//                    }
                }

                //draw my units on screen
                game.drawTextScreen(10, 25, units.toString());
            }
        });

        mirror.startGame();
    }

    /**
	 * Indicates if a unit has been issued an attack command and hasn't finished
	 * attacking.
	 * 
	 * @param u
	 *            Unit to check.
	 * @return True if the unit has been issued an attack command and it hasn't
	 *         finished attacking.
	 */
	private boolean isAttacking(Unit u) {
		/*
		 * After the unit has been issued an attack order it takes a few frames
		 * before its state is isStartingAttack. The order of the unit will
		 * however change immediately. But the order won't change after the
		 * attack has ended. attackInProgress tells if the isStartingAttack has
		 * already changed to true. The attack ends when isAttackFrame and
		 * isStartingAttack are false but attackInProgress is yet true so then
		 * attackInProgress will be set to false.
		 */

		 System.out.println("AttackCheck: "
		 + (u.isAttackFrame() || u.isStartingAttack()
					|| (!getIsAttackInProgress(u) && getHasAttackOrderBeenGiven(u)))
		 + " isAttackFrame: " + u.isAttackFrame() + " isStartingAttack: "
		 + u.isStartingAttack() + " attackInProgress: " + getIsAttackInProgress(u)
		 + " attackOrderBeenGiven: " + getHasAttackOrderBeenGiven(u)
		 + " order: " + u.getOrder().c_str());
		 
//		 + " orderIsAttack: " + (u.getOrder() ==
//		 OrderType.OrderTypes.AttackUnit));
		// if( u.isAttackFrame() || u.isStartingAttack() || (!attackInProgress
		// && u.getOrder() == OrderType.OrderTypes.AttackUnit)) {

		if (u.isAttackFrame() || u.isStartingAttack()
				|| (!getIsAttackInProgress(u) && getHasAttackOrderBeenGiven(u))) {
			// Attack order has been given and attack hasn't finished yet.
			if (!getIsAttackInProgress(u) && (u.isStartingAttack())) {
				// First frame of the actual attack.
				isAttackInProgress.put(u.getID(), true);
				hasAttackOrderBeenGiven.put(u.getID(), false);
			}
			return true;
		} else if (getIsAttackInProgress(u)) {
			// First frame after the attack.
			isAttackInProgress.put(u.getID(), false);
		}

		// No attacking in progress.
		return false;
	}
	
	/**
	 * Checks if the HashMaps containing information about the units' attack
	 * states have been filled for the AI player's units and fills them if they
	 * are empty.
	 */
	private void populateUnitHashMaps() {
		if (hasAttackOrderBeenGiven.isEmpty())
			for (Unit u : self.getUnits())
				hasAttackOrderBeenGiven.put(u.getID(), false);

		if (isAttackInProgress.isEmpty())
			for (Unit u : self.getUnits())
				isAttackInProgress.put(u.getID(), false);
	}
	
	/**
	 * Indicates if the unit has an attack in progress.
	 * 
	 * @param u
	 *            The unit to check.
	 * @return True if the unit has an attack in progress.
	 */
	private boolean getIsAttackInProgress(Unit u) {
		return isAttackInProgress.get(u.getID()).booleanValue();
	}

	/**
	 * Indicates if the unit has been given an attack order.
	 * 
	 * @param u
	 *            The unit to check.
	 * @return True if the unit has been given an attack order.
	 */
	private boolean getHasAttackOrderBeenGiven(Unit u) {
		return hasAttackOrderBeenGiven.get(u.getID()).booleanValue();
	}
    
    public static void main(String... args) {
        new TestIsAttacking().run();
    }
}
