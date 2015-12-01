package mr.tabularProvider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;
import mr.mazeImpl.StateActionPair;

public class TDLambdaUpdaterQ implements RLUpdater{
	public TDLambdaUpdaterQ(double _alpha, double _gamma, double _lambda){
		alpha = _alpha;
		gamma = _gamma;
		lambda = _lambda;
		trace = new ArrayList<StateActionPair>();
		}
		private double alpha;
		private double gamma;
		private double lambda;
		private ArrayList<StateActionPair> trace;
		
		@Override
		public TabularData nextStepUpdate(TabularData d, Action a, Position p, Action a2, Position p2, double reward) {
			//this will function like the super naieve combination of peng's and watkin's Q(l) suggested in reinforcement learning: an introduction
			//it's basically just watkins, but without cutting off learning after a non-greedy action
			//if the non-greedy actions are sufficiently annealed, this should work
			//it also doesn't work
			Point loc = p2.getloc();
			float val = d.Q[loc.x][loc.y][0];
			for (int i = 1; i < 4; i++){
				if(d.Q[loc.x][loc.y][i] > val){
					val = d.Q[loc.x][loc.y][i];
				}
			}
			float delta = (float)(reward + gamma*val - d.Q[p.getloc().x][p.getloc().y][a.ind]);
			//check for the SA pair in the trace
			boolean notthere = true;
			StateActionPair nsa = new StateActionPair(p.getloc(),a);
			Iterator<StateActionPair> it = trace.iterator();
			while(it.hasNext()){
				StateActionPair testp = it.next();
				if(testp.equals(nsa)){
					testp.et = 1;	//replacing traces
					notthere = false;
					break;
				}
			}
			//if not, add it
			if(notthere){
				trace.add(nsa);
			}
			//update the values according to TDlambda
			it = trace.iterator();
			while(it.hasNext()){
				StateActionPair testp = it.next();
				d.Q[testp.a][testp.b][testp.c] = (float) (d.Q[testp.a][testp.b][testp.c] + alpha*(delta*testp.et));
				testp.et = gamma*lambda*testp.et;
			}
			//remove the values that are too small to worry about
			trace.removeIf(s -> s.et < 0.05);
			
			return d;

		}
		@Override
		public TabularData episodeEndUpdate(TabularData d, double totalreward) {
			trace.clear();
			return d;
		}
}