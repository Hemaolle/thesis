package thesis.evolution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import thesis.rmi.PotentialFunctionProvider;
import thesis.rmi.ThesisProblemRMI;
import thesis.rmi.RemoteBotInterface;

/**
 * The evolution problem of the master's thesis.
 * 
 * The object is to evolve a potential function that results in the best
 * possible performance in a small scale combat scenario in StarCraft Broodwar
 * RTS game.
 * 
 * Different trees in the individuals are ment to calculate different potential
 * fields: Tree 0: Enemy units Tree 1: Map edges
 * 
 * @author Oskari Leppäaho
 * 
 */
public class ThesisProblem extends GPProblem implements SimpleProblemForm,
		PotentialFunctionProvider {
	private static final long serialVersionUID = 1;

	/**
	 * Parameter name for how many times the evaluation of a single individual
	 * should be repeated
	 */
	public static final String P_REPEATEDEVALUATIONS = "repeated-evaluations";

	public double currentX;
	public double currentY;
	public double currentZ;
	public double currentW;
	public double currentU;
	/** The broodwar clients used in evaluation */
	ArrayList<RemoteBotInterface> bwClients;

	// Information regarding the current individual
	EvolutionState state;
	int threadnum;
	Individual ind;
	DoubleData localInput;

	int repeatedEvaluations; // How many times a single individual should be
								// evaluated?

	int currentGen = 0;
	int individualssEvaluatedThisGen = 0;

	/**
	 * Setup the evolution. Read how many times an individual should be
	 * evaluated. Connect to the remote BroodWar clients.
	 */
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();

		repeatedEvaluations = state.parameters.getIntWithDefault(
				base.push(P_REPEATEDEVALUATIONS),
				def.push(P_REPEATEDEVALUATIONS), 1);

		System.out.println("Repeated evaluations: " + repeatedEvaluations);

		// verify our input is the right class (or subclasses from it)
		if (!(input instanceof DoubleData))
			state.output.fatal("GPData class must subclass from "
					+ DoubleData.class, base.push(P_DATA), null);
		bwClients = new ArrayList<RemoteBotInterface>();
		System.out.println(state.evalthreads + " evalthreads");

		ThesisProblemRMI starter = new ThesisProblemRMI();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				System.in));
		for (int i = 0; i < state.evalthreads; i++) {
			System.out.println("Start client " + i);
			System.out
					.println("Press enter when the client has been started >");
			try {
				buffer.readLine();
				bwClients.add(starter.connectClient(i + ""));
				System.out.println("Connected to remote bot " + i);
			} catch (Exception e) {
				System.err.println("Something went wrong when waiting for \n"
						+ "the user to start the client\n" + "(for stepping).");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Evaluate the fitness of an individual. The fitness is the average of
	 * round scores of BroodWar matches run using the current individual as the
	 * potential function.
	 */
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (!ind.evaluated) // don't bother reevaluating
		{
			this.ind = ind;
			this.state = state;
			this.threadnum = threadnum;

			localInput = (DoubleData) (this.input);

			int hits = 0;

			double fitness = 0;
			double fitnessSum = 0;

			try {
				for (int i = 0; i < repeatedEvaluations; i++) {
					fitnessSum += bwClients.get(threadnum).getRoundScore(this);
				}
				fitness = fitnessSum / repeatedEvaluations;
			} catch (RemoteException e) {
				System.err.println("A remote exception while evaluating: ");
				e.printStackTrace();
				throw new Error();
			}

			if (fitness < 0.001)
				hits++;

			// the fitness better be KozaFitness!
			KozaFitness f = ((KozaFitness) ind.fitness);
			f.setStandardizedFitness(state, fitness);
			f.hits = hits;
			ind.evaluated = true;

			printProgressInfo(subpopulation, threadnum);
		}
	}

	/**
	 * Print information about the evolution progress. Prints the current thread
	 * number and the progress of the current evolution.
	 * 
	 * @param subpopulation
	 *            Index of the current subpopulation
	 * @param threadnum
	 *            Index of the current thread.
	 */
	private void printProgressInfo(int subpopulation, int threadnum) {
		if (state.generation != currentGen) {
			currentGen = state.generation;
			individualssEvaluatedThisGen = 0;
		}
		individualssEvaluatedThisGen++;
		System.out.println("Evaluated in thread " + threadnum);
		System.out.println("Generation progress: "
				+ ((float) individualssEvaluatedThisGen)
				/ state.population.subpops[subpopulation].individuals.length
				* 100 + " %");
	}

	/**
	 * Returns the potential for an enemy unit. This part of the potential
	 * function is evolved in tree 0.
	 * 
	 * @param distanceFromEnemy
	 *            Player unit's distance from the enemy unit
	 * @param ownMaximumShootDistance
	 *            The own unit's maximum shooting distance.
	 * @param relativeHP
	 * @param enemyPositionVector
	 * @return The potential for the enemy.
	 */
	private double getEnemyPotential(double distanceFromEnemy,
			double ownMaximumShootDistance, double relativeHP,
			double[] enemyPositionVector) {
		currentX = distanceFromEnemy;
		currentY = ownMaximumShootDistance;
		currentZ = relativeHP;
		currentU = enemyPositionVector[0];
		currentW = enemyPositionVector[1];
		((GPIndividual) ind).trees[0].child.eval(state, threadnum, localInput,
				stack, ((GPIndividual) ind), this);
		return localInput.x;
	}

	/**
	 * Returns the potential for an enemy unit when the own unit is on cooldown.
	 * This part of the potential function is evolved in tree 3.
	 * 
	 * @param distanceFromEnemy
	 *            Player unit's distance from the enemy unit
	 * @param ownMaximumShootDistance
	 *            The own unit's maximum shooting distance.
	 * @param relativeHP
	 * @param enemyPositionVectors 
	 * @return The potential for the enemy.
	 */
	private double getenemyPotentialWhenOnCooldown(double distanceFromEnemy,
			double ownMaximumShootDistance, double relativeHP,
			double[] enemyPositionVector) {
		currentX = distanceFromEnemy;
		currentY = ownMaximumShootDistance;
		currentZ = relativeHP;
		currentU = enemyPositionVector[0];
		currentW = enemyPositionVector[1];
		((GPIndividual) ind).trees[3].child.eval(state, threadnum, localInput,
				stack, ((GPIndividual) ind), this);
		return localInput.x;
	}

	/**
	 * Returns the potential for an own unit. This part of the potential
	 * function is evolved in tree 2.
	 * 
	 * @param distance
	 *            Player unit's distance from an own unit
	 * @return The potential for the enemy.
	 */
	private double getOwnUnitPotential(double distance) {
		currentX = distance;
		((GPIndividual) ind).trees[2].child.eval(state, threadnum, localInput,
				stack, ((GPIndividual) ind), this);
		return localInput.x;
	}

	/**
	 * Returns the potential for the map edges. This part of the potential
	 * function is evolved in tree 1.
	 * 
	 * @param distancesFromEdges
	 *            Distances from the 4 map edges.
	 * @return The potential for the map edges.
	 */
	private double getMapEdgePotential(double distanceFromEdge) {
		double potential = 0;
		currentX = distanceFromEdge;
		((GPIndividual) ind).trees[1].child.eval(state, threadnum, localInput,
				stack, ((GPIndividual) ind), this);
		potential += localInput.x;

		return potential;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Using the function from the individual currently being evaluated.
	 * 
	 */
	public double getPotential(double[] distancesFromEnemies,
			double[] distancesFromOwnUnits, double ownMaximumShootDistance,
			double[] distancesFromEdges, boolean onCooldown, double relativeHP,
			double[][] enemyPositionVectors) {
		double potential = 0;
		double maxPotential = -Double.MAX_VALUE;
		double currentPotential;
		if (!onCooldown)
			for (int i = 0; i < distancesFromEnemies.length; i++) {
				currentPotential = getEnemyPotential(distancesFromEnemies[i],
						ownMaximumShootDistance, relativeHP,
						enemyPositionVectors[i]);
				if (maxPotential < currentPotential)
					maxPotential = currentPotential;
			}
		else {
			for (int i = 0; i < distancesFromEnemies.length; i++) {
				currentPotential = getenemyPotentialWhenOnCooldown(
						distancesFromEnemies[i], ownMaximumShootDistance,
						relativeHP, enemyPositionVectors[i]);
				if (maxPotential < currentPotential)
					maxPotential = currentPotential;
			}
		}
		potential += maxPotential;
		for (int i = 0; i < distancesFromOwnUnits.length; i++) {
			potential += getOwnUnitPotential(distancesFromOwnUnits[i]);
		}
		for (int i = 0; i < distancesFromEdges.length; i++) {
			potential += getMapEdgePotential(distancesFromEdges[i]);
		}
		return potential;
	}
}
