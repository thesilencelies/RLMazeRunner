package mr.mazeImpl;

import java.awt.Point;

public class SimpleMaze extends Maze {
	//a class to produce a consistent maze with known parameters
	public SimpleMaze() {
		super(1,1,1,0,false,true);	//2x2 with one pit, solid walls, penalty on each step
		pits.clear();
		pits.add(new Point(1,0));
		goal.move(1,1);
	}

}
