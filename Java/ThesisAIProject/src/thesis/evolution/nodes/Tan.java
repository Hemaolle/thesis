/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package thesis.evolution.nodes;

import thesis.evolution.DoubleData;
import ec.*;
import ec.gp.*;
import ec.util.*;

/**
 * A node representing tangent function in a program tree generated by genetic
 * programming.
 * 
 * @author Sean Luke
 * 
 */
public class Tan extends GPNode {

	public String toString() {
		return "tan";
	}
	
	public int expectedChildren() {
		return 1;
	}

	public void eval(final EvolutionState state, final int thread,
			final GPData input, final ADFStack stack,
			final GPIndividual individual, final Problem problem) {
		double result;
		DoubleData rd = ((DoubleData) (input));

		children[0].eval(state, thread, input, stack, individual, problem);
		result = Math.tan(rd.x);
		if (result == Double.NaN || result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY )
			result = 0;
		rd.x = result;
	}
}
