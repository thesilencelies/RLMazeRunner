package mr.tabularProvider;

import mr.mazeImpl.*;

public interface RLUpdater {
	public TabularData nextStepUpdate(TabularData d, Action a, Position p,Action a2, Position p2, double reward);
	public TabularData episodeEndUpdate(TabularData d, double totalreward);
	
}
