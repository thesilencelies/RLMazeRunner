package mr.maze;

import java.awt.Point;

import mr.mazeImpl.Action;
import mr.mazeImpl.Maze;
import mr.mazeImpl.Position;
import mr.tabularProvider.*;

public class TabularRLAgent extends RLMazeAgent{

	private TabularData data;
	private RLUpdater upd;
	private LearningParadigm learntype;
	
	public TabularRLAgent(Maze _m, float _alpha, RLType rtype, LearningParadigm lp, double _err, double _gamma){
		super(_m, _err, _gamma, _alpha);
		learntype = lp;
		data = new TabularData(m.getmaxc());
		//setup which learning type is used
		switch(rtype){
			case TD0:
				switch(lp){
				case Sarsa:
					upd = new TD0UpdaterSarsa(alpha, gamma);
					break;
				case Qlearning:
					upd = new TD0UpdaterQ(alpha, gamma);
					break;
				default:
					upd = new TD0UpdaterSarsa(alpha, gamma);
					learntype = LearningParadigm.Sarsa;
				}
				break;
			case TDlambda:
				switch(lp){
				case Sarsa:
					upd = new TDLambdaUpdaterSarsa(alpha, gamma,0.9);
					break;
				case Qlearning:
					upd = new TDLambdaUpdaterQ(alpha, gamma,0.9);
					break;
				default:
					upd = new TDLambdaUpdaterSarsa(alpha, gamma,0.9);
					learntype = LearningParadigm.Sarsa;
				}
				break;
			case MonteCarlo:
				upd = new MonteCarloUpdater(alpha, gamma);
				break;
			default:
				upd = new TD0UpdaterSarsa(alpha, gamma);
		}
	}
	
	@Override
	protected float peekchoice(Point loc) {
		//displays the final weights after an episode
		float rval =0;
		switch(learntype){
		//this will do something if we have anything that is value iteration or similar running at some point
		
		//for Q learning, the value of each state is defined as the maximum value of any action that could be taken at that state
		case Sarsa:
		case Qlearning:
		default:
			rval = data.Q[loc.x][loc.y][0];
			for (int i = 1; i < 4; i++){
				if(data.Q[loc.x][loc.y][i] > rval){
					rval = data.Q[loc.x][loc.y][i];
				}
			}
		}
		return rval;
	}

	private Action epsilonGreedyAction(){
		Action a = Action.UP;	//default value
		//choose greedily with probability 1-e
		if(Math.random()< err){
			//make a random choice
			if(Math.random() < 0.5){
				if(Math.random() < 0.5){
					a = Action.UP;
				}
				else{
					a = Action.DOWN;
				}
			}else{
				if(Math.random() < 0.5){
					a = Action.LEFT;
				}
				else{
					a = Action.RIGHT;
				}
			}
		}
		else{
			//choose the greedy action
			Point loc = mypos.getloc(); 
			float[] weights = (data.Q[loc.x][loc.y]);
			float maxval = weights[0];
			for (Action act : Action.values()){
				if(weights[act.ind] > maxval){
					maxval = weights[act.ind];
					a = act;
				}
			}
		}
		return a;
	}
	
	@Override
	public float runonce(boolean talkback) {
		switch(learntype){
		case Sarsa:
			return runonceSarsa(talkback);
		case Qlearning:
		default:
			return runonceQ(talkback);
		}
	}
	private float runonceQ(boolean talkback){
		mypos = new Position();
		float reward = 0;
		float totalReward = 0;
		int steps = 0;
		while (!m.isTermPos(mypos.getloc())){
			steps = steps +1;
			//store previous position for learning
			Position prevpos = new Position(mypos.getloc());
			//assess choices
				
			Action a = epsilonGreedyAction();
			//take the action
			if(talkback){
				 System.out.println ("at location " + mypos.getloc() + " action chosen was " + a);
			}
			
			reward = mypos.go(a,m);
			totalReward = totalReward + reward;
			if (talkback) {
				System.out.println("arriving to "+ mypos.getloc() + " with reward " + reward);
			}
			//assess new state and update previous
			data = upd.nextStepUpdate(data, a, prevpos,a, mypos, reward);
			if(steps >40000){
				//kill the episode if it's clearly stuck
				reward = -2;
				break;
			}
		}
		data = upd.episodeEndUpdate(data, totalReward);
		System.out.println("reward was " + totalReward + " after " + steps + " steps");
		return totalReward;
	}
	
		private float runonceSarsa(boolean talkback){
			mypos = new Position();
			float reward = 0;
			float totalReward = 0;
			int steps = 0;
			//preliminary action
			Action a = epsilonGreedyAction();
			
			while (!m.isTermPos(mypos.getloc())){
				steps = steps +1;
				//store previous position for learning
				Position prevpos = new Position(mypos.getloc());
				
				//take the action
				if(talkback){
					 System.out.println ("at location " + mypos.getloc() + " action chosen was " + a);
				}
				reward = mypos.go(a,m);
				totalReward = totalReward + reward;
				if (talkback) {
					System.out.println("arriving to "+ mypos.getloc() + " with reward " + reward);
				}
				//assess new state and update previous
				Action b = epsilonGreedyAction();
				
				data = upd.nextStepUpdate(data, a, prevpos,b, mypos, reward);
				a = b;
				if(steps >40000){
					//kill the episode if it's clearly stuck
					reward = -2;
					break;
				}
			}
		data = upd.episodeEndUpdate(data, totalReward);
		System.out.println("reward was " + totalReward + " after " + steps + " steps");
		return totalReward;
	}

}
