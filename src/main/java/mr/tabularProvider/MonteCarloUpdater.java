package mr.tabularProvider;

import mr.mazeImpl.Action;
import mr.mazeImpl.Position;

public class MonteCarloUpdater implements RLUpdater {

	public MonteCarloUpdater(double _alpha, double _gamma){
		alpha = _alpha;
		gamma = _gamma;
		}
		private double alpha;
		private double gamma;
		@Override
		public TabularData nextStepUpdate(TabularData d, Action a, Position p, Action a2, Position p2, double reward) {
			// TODO Auto-generated method stub
			//puts the choice into local memory
			return d;
		}
		@Override
		public TabularData episodeEndUpdate(TabularData d, double totalreward) {
			// TODO Auto-generated method stub
			//updates the values
			return d;
		}

}
