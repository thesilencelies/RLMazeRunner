package mr.tabularProvider;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;

public class TD0UpdaterSarsa implements RLUpdater {

	public TD0UpdaterSarsa(double _alpha, double _gamma){
	alpha = _alpha;
	gamma = _gamma;
	}
	private double alpha;
	private double gamma;

	@Override
	public TabularData nextStepUpdate(TabularData d, Action a, Position p, Action a2, Position p2, double reward) {
		d.Q[p.getloc().x][p.getloc().y][a.ind] = (float) (d.Q[p.getloc().x][p.getloc().y][a.ind] + alpha*(reward + gamma*(d.Q[p2.getloc().x][p2.getloc().y][a2.ind]) - d.Q[p.getloc().x][p.getloc().y][a.ind]));
		return d;
	}

	@Override
	public TabularData episodeEndUpdate(TabularData d, double totalreward) {
		System.out.println("this method should not be called for TDOSarsa");
		return d;
	}


}