package mr.mazeImpl;

public enum Action {
	UP(0),DOWN(1),LEFT(2),RIGHT(3);
	public final int ind;
	Action(int i){
		ind = i;
	}
}
