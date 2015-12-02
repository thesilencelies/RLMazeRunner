package mazetest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import mr.maze.*;
import mr.tabularProvider.*;
import mr.mazeImpl.Maze;

public class SimpleTest {
	
	public static void main(String [ ] args)
	{	
		RLMazeAgent agent;
		Maze m;
		Path mpath = Paths.get("/home/stephen/Documents/maze.mz");
		boolean printmap = false;
		try{
		m = new Maze(mpath);
		}catch(IOException e){
			m = new Maze(10,10,3,1,true,false);
			m.save(mpath);
		}
		if (args.length == 0){
		agent = new NNRLmazeAgent(m,0.1,0.99,0.8);
		printmap = true;
		}
		else {
			switch(args[0]){
				case "NN":
					agent = new NNRLmazeAgent(m,0.1,0.99, 0.8);
					printmap = true;
					break;
				case "TD":
					agent = new TabularRLAgent(m, 0.6f,RLType.TD0, LearningParadigm.Sarsa, 0.1,0.99);
					break;
				case "TDL":
					agent = new TabularRLAgent(m, 0.6f,RLType.TDlambda, LearningParadigm.Sarsa, 0.1,0.99);
					break;
				case "TDLQ":
					agent = new TabularRLAgent(m, 0.6f,RLType.TDlambda, LearningParadigm.Qlearning, 0.1,0.99);
					break;
				case "Q":
					agent = new TabularRLAgent(m, 0.6f,RLType.TD0, LearningParadigm.Qlearning, 0.1,0.99);
					break;
				case "MC":
					agent = new TabularRLAgent(m,0.6f,RLType.MonteCarlo,LearningParadigm.Qlearning,0.1,.99);
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
	    		 if(printmap)
	    			 agent.displaynet();
	    		 //simulated annealing
	    		 if(i > 80 ){
	    			 agent.setepsilon(Double.max(0.01,agent.getepsilon()*0.9));
	    		 }
	    		 
	    	 }
	 	    System.out.println("agent scored " + x + " in " + args[1] + " runthroughs");
		    System.out.println("results over time were:");
		    double tot = 0;
		    for (int i = 0, len = rot.length; i < len; i++ ){
		    	System.out.printf(" %1.2f ", rot[i]);
		    	if(i > len -11){
		    		tot += rot[i];
		    	}
		    }
		    System.out.printf("%n average result from the last 10 run throughs");
		    int div = (rot.length > 10)? 10 : rot.length;
		    System.out.printf(" %1.4f ", tot/div);
	    }

	}

}
