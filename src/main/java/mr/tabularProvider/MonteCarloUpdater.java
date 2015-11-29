package mr.tabularProvider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;

public class MonteCarloUpdater implements RLUpdater {

	public MonteCarloUpdater(double _alpha, double _gamma){
		alpha = _alpha;
		gamma = _gamma;
		trace = new ArrayList<StateActionPair>();
		}
		private double alpha;
		private double gamma;
		private ArrayList<StateActionPair> trace;
		
		@Override
		public TabularData nextStepUpdate(TabularData d, Action a, Position p, Action a2, Position p2, double reward) {
			//check for the SA pair in the trace
			boolean notthere = true;
			StateActionPair nsa = new StateActionPair(p.getloc(),a);
			nsa.et = reward;	//use et to store the reward experienced after that action.
			Iterator<StateActionPair> it = trace.iterator();
			while(it.hasNext()){
				StateActionPair testp = it.next();
				if(testp.equals(nsa)){
					notthere = false;
					break;
				}
			}
			//if not, add it
			if(notthere){
				trace.add(nsa);
			}
			return d;
		}
		@Override
		public TabularData episodeEndUpdate(TabularData d, double totalreward) {
			StateActionPair[] arr = trace.toArray(new StateActionPair[trace.size()]);
			double R = 0;
			for (int i = 1, len = arr.length ; i < len +1; i++){
				R += arr[len - i].et;
				d.Q[arr[len-i].a][arr[len-i].b][arr[len-i].c] = (float)R/(i);
			}
			return d;
		}

}
