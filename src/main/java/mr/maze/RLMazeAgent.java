package mr.maze;

import java.awt.Point;
import java.nio.file.Path;

import com.github.neuralnetworks.tensor.Matrix;

import mr.mazeImpl.Maze;
import mr.mazeImpl.Position;

public abstract class RLMazeAgent {
	protected Maze m;
	protected Position mypos;
	
	public RLMazeAgent(){
		mypos = new Position();
		m = new Maze(20,20,1,5, true);
	}
	
	public void load(Path mazep, Path datapath){
		m = new Maze(mazep);
	}
	public void save(Path mazep, Path datapath){
		m.save(mazep);
	}
	
	protected abstract Matrix peekchoice(Point loc);
	
	public abstract float runonce(boolean talkback);
	
	public void displaynet(){
		//method to visualise the current state of the neural network.
		double [][] netvals = new double[21][21];
		for (int i =0; i < 21; i ++){
			for (int j =0; j < 21; j++){
				Matrix peek = peekchoice(new Point(i,j));
				double val = peek.get(0,0);
				for (float v : peek.getElements()){
					if(v > val){
						val = v;
					}
				}
				netvals[i][j] = val;
			}
		}
		for (int i =0; i < 21; i++){
			for(int j = 0; j < 21; j++){
				System.out.printf(" %1.2f ",netvals[i][j]);
			}
			System.out.printf("%n");
		}
	}
}
