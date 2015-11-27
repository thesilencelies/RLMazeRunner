package mr.tabularProvider;

import java.awt.Point;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;

public class TD0UpdaterQ implements RLUpdater {
	public TD0UpdaterQ(double _alpha, double _gamma){
		alpha = _alpha;
		gamma = _gamma;
		}
		private double alpha;
		private double gamma;

	@Override
	public TabularData nextStepUpdate(TabularData data, Action a, Position p, Action a2, Position p2, double reward) {
		Point loc = p2.getloc();
		float val = data.Q[loc.x][loc.y][0];
		for (int i = 1; i < 4; i++){
			if(data.Q[loc.x][loc.y][i] > val){
				val = data.Q[loc.x][loc.y][i];
			}
		}
		data.Q[p.getloc().x][p.getloc().y][a.ind] = (float) (data.Q[p.getloc().x][p.getloc().y][a.ind] + alpha*(reward + gamma*val - data.Q[p.getloc().x][p.getloc().y][a.ind]));		
		return data;
	}

	@Override
	public TabularData episodeEndUpdate(TabularData d, double totalreward) {
		return d;
	}
	

}