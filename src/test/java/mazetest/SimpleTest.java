package mazetest;

import mr.maze.*;
import mr.tabularProvider.*;
import mr.mazeimpl.Maze;

public class SimpleTest {
	
	public static void main(String [ ] args)
	{	
		? extends RLmazeAgent agent;
		Maze m = new Maze(20,20,5,1,true);
		int[]l = new int[4];
		if (args.length == 0){
		agent = new NNRLmazeAgent(m,0.1,0.99);
		}
		else {
			switch(args[0]){
				case "NN":
					agent = new NNRLmazeAgent(m,0.1,0.99);
					break;
				case "TD":
					agent = new TabularRLAgent(m, 1,RLType.TD, LearningParadigm.QLearning);
					break;
				default:
					System.out.println("unrecognised input - accepted types are NN or TD followed by the number of iterations");
					throw(IOError());
			}	
		}
	     agent.runonce(true);
	     float x =0;
	     if(args.length > 1){
	    	 for(int i = 0; i < (int) Integer.parseInt(args[1]); i++){
	    		 x += agent.runonce(false);
	    		 agent.displaynet();
	    	 }
	    }
	    System.out.println("agent scored " + x + " in " + args[0] + " runthroughs");
	}

}
