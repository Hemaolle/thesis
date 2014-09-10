/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package thesis.evolution;
import ec.*;
import ec.gp.*;

/**
 * A node representing division in a program tree generated by genetic
 * programming.
 * 
 * @author Sean Luke
 * 
 */
public class Div extends GPNode
    {

	private static final long serialVersionUID = 8528119815001531877L;

	public String toString() { return "/"; }

/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=2)
  state.output.error("Incorrect number of children for node " + 
  toStringForError() + " at " +
  individualBase);
  }
*/
    public int expectedChildren() { return 2; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
        double result;
        DoubleData rd = ((DoubleData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        result = rd.x;

        children[1].eval(state,thread,input,stack,individual,problem);
        if (-0.001 < rd.x && rd.x < 0.001)
        	rd.x = 1.0;
        else
        	rd.x = result / rd.x;
        }
    }

