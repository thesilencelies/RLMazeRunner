package mr.mazeImpl;
import java.awt.Point;

public class Position {

	public Position(){
		loc = new Point(0,0);
	}
	
	private Point loc;
	
	public Point getloc(){
		return loc;
	}
	
	public float go(Action a, Maze m){
		//asks the maze what happens if it takes the given action, and then finds out the reward
		loc = m.goresult(a,loc);
		return m.posreward(loc);
	}
}
