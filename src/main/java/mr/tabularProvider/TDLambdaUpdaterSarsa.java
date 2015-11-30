package mr.tabularProvider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;

public class TDLambdaUpdaterSarsa implements RLUpdater {

	public TDLambdaUpdaterSarsa(double _alpha, double _gamma, double _lambda){
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
		Point loc = p.getloc();
		float delta = (float)((reward + gamma*(d.Q[p2.getloc().x][p2.getloc().y][a2.ind]) - d.Q[loc.x][loc.y][a.ind]));
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