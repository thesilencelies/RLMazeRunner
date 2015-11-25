package mazetest;

import mr.maze.RLmazeAgent;

public class SimpleTest {
	
	public static void main(String [ ] args)
	{
		int[]l = new int[4];		
		RLmazeAgent agent = new RLmazeAgent(0.1,0.99);
	     agent.runonce(true);
	     float x =0;
	     if(args.length > 0){
	    	 for(int i = 0; i < (int) Integer.parseInt(args[0]); i++){
	    		 x += agent.runonce(false);
	    		 agent.displaynet();
	    	 }
	    }
	    System.out.println("agent scored " + x + " in " + args[0] + " runthroughs");
	}

}
