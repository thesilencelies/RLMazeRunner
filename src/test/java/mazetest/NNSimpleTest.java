package mazetest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import mr.maze.*;
import mr.tabularProvider.*;
import mr.mazeImpl.Maze;
import mr.mazeImpl.SimpleMaze;

public class NNSimpleTest {
	
	public static void main(String [ ] args)
	{	
		RLMazeAgent agent;
		Maze m = new SimpleMaze();
		agent = new NNRLmazeAgent(m,new int[]{6,4},0.1,0.99,0.4);
	//	agent.setGroundTruth(new float[][][]{{{1.479f,1.463f,1.463f,-0.5f},{1.479f, 1.463f,1.479f,1.495f}},{{1.495f,-0.5f,1.463f,-0.5f},{1.495f,-0.5f,1.479f,1.495f}}}); //from empyrical data
	     agent.runonce(true);
	     //agent.displaynet();
	     float x =0;
	     if(args.length > 0){
	    	 double [] rot = new double [Integer.parseInt(args[0])];	
	    	 for(int i = 0; i < (int) Integer.parseInt(args[0]); i++){
	    		 rot[i] = agent.runonce(false);
	    		 x += rot[i];
	    			// agent.displaynet();
	    		 //simulated annealing
	    		 if(i > 80 ){
	    			 agent.setepsilon(Double.max(0.01,agent.getepsilon()*0.9));
	    		 }
	    	 }
	 	    System.out.println("agent scored " + x + " in " + args[0] + " runthroughs");
		    System.out.println("results over time were:");
		    double tot = 0;
		    for (int i = 0, len = rot.length; i < len; i++ ){
		    	System.out.printf(" %1.2f ", rot[i]);
		    	if(i > len -11){
		    		tot += rot[i];
		    	}
		    }
		    int div = (rot.length > 10)? 10 : rot.length;
		    System.out.printf("%n average result from the last %d run throughs", div);
		    System.out.printf(" %1.4f %n", tot/div);
		    
	    }
	     agent.displaynet();
	     agent.plotlearning();
	}

}
