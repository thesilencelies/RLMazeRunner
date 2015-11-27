package mazetest;

import mr.maze.*;
import mr.tabularProvider.*;
import mr.mazeImpl.Maze;

public class SimpleTest {
	
	public static void main(String [ ] args)
	{	
		RLMazeAgent agent;
		Maze m = new Maze(20,20,8,2,false, true);
		if (args.length == 0){
		agent = new NNRLmazeAgent(m,0.1,0.99,0.5);
		}
		else {
			switch(args[0]){
				case "NN":
					agent = new NNRLmazeAgent(m,0.1,0.99, 0.5);
					break;
				case "TD":
					agent = new TabularRLAgent(m, 0.6f,RLType.TD0, LearningParadigm.Sarsa, 0.1,0.99);
					break;
				case "TDL":
					agent = new TabularRLAgent(m, 0.6f,RLType.TDlambda, LearningParadigm.Sarsa, 0.1,0.99);
					break;
				case "Q":
					agent = new TabularRLAgent(m, 0.6f,RLType.TD0, LearningParadigm.Qlearning, 0.1,0.99);
					break;
				default:
					System.out.println("unrecognised input - accepted types are NN or TD followed by the number of iterations");
					agent = new TabularRLAgent(m, 0.6f,RLType.TD0, LearningParadigm.Qlearning,0.1,0.99);
			}
		}
	     agent.runonce(true);
	     float x =0;
	     if(args.length > 1){
	    	 double [] rot = new double [Integer.parseInt(args[1])];	
	    	 for(int i = 0; i < (int) Integer.parseInt(args[1]); i++){
	    		 rot[i] = agent.runonce(false);
	    		 x += rot[i];
	    		 agent.displaynet();
	    		 //simulated annealing
	    		 if(i > 80){
	    			 agent.setepsilon(agent.getepsilon()*0.9);
	    		 }
	    		 
	    	 }
	 	    System.out.println("agent scored " + x + " in " + args[1] + " runthroughs");
		    System.out.println("results over time were:");
		    double tot = 0;
		    for (int i = 0, len = rot.length; i < len; i++ ){
		    	System.out.printf(" %1.2f ", rot[i]);
		    	if(i > len -10){
		    		tot += rot[i];
		    	}
		    }
		    System.out.printf("%n average result from the last 10 run throughs");
		    int div = (rot.length > 10)? 10 : rot.length;
		    System.out.printf(" %1.4f ", tot/div);
	    }

	}

}
