/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package thesis.evolution.nodes;
import ec.*;
import ec.app.regression.*;
import ec.gp.*;
import ec.util.*;

import java.io.*;


/* 
 * RegERC.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class BigFloatERC extends FloatERC
    {
	 public String name() { return "KornsERC"; }

	    public void resetNode(final EvolutionState state, final int thread)
	    	{ value = state.random[thread].nextDouble() * 1000.0 - 500.0; }
    }



