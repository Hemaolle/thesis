/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package thesis.evolution.nodes;
import thesis.evolution.DoubleData;
import thesis.evolution.ThesisProblem;
import ec.*;
import ec.gp.*;

/**
 * A node representing an input parameter in a program tree generated by genetic
 * programming.
 * 
 * @author Sean Luke
 * 
 */
public class U extends GPNode
    {    

	public String toString() { return "z"; }

/*
  public void checkConstraints(final EvolutionState state,
  final int tree,
  final GPIndividual typicalIndividual,
  final Parameter individualBase)
  {
  super.checkConstraints(state,tree,typicalIndividual,individualBase);
  if (children.length!=0)
  state.output.error("Incorrect number of children for node " + 
  toStringForError() + " at " +
  individualBase);
  }
*/
    public int expectedChildren() { return 0; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem)
        {
        DoubleData rd = ((DoubleData)(input));
        rd.x = ((ThesisProblem)problem).currentU;
        }
    }

