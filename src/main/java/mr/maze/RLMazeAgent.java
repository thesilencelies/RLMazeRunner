package mr.maze;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Path;

import com.github.neuralnetworks.tensor.Matrix;

import mr.mazeImpl.Maze;
import mr.mazeImpl.Position;

public abstract class RLMazeAgent {
	protected Maze m;
	protected Position mypos;
	protected double err;
	protected double gamma;
	protected LearningGrapher plotter;
	
	public double alpha;
	
	public RLMazeAgent(int x, int y, int npits, int nwalls, boolean warpwalls, double _err, double _gamma, double _alpha){
		mypos = new Position();
	    plotter = new LearningGrapher();
		m = new Maze(x,y,npits,nwalls, warpwalls, true);
		err = _err;
		gamma = _gamma;
		alpha = _alpha;
	}
	public RLMazeAgent(Maze _m, double _err, double _gamma, double _alpha){
		mypos = new Position();
	    plotter = new LearningGrapher();
		m = _m;
		err = _err;
		gamma = _gamma;
		alpha = _alpha;
	}
	
	public void load(Path mazep, Path datapath) throws IOException{
		m = new Maze(mazep);
	}
	public void save(Path mazep, Path datapath){
		m.save(mazep);
	}
	//function to allow simulated annealing to be implemented
	public void setepsilon(double _eps){
		err = _eps;
	}
	public double getepsilon(){
		return err;
	}
	protected abstract float peekchoice(Point loc);
	
	public abstract float runonce(boolean talkback);
	public void plotlearning(){
		plotter.plotLearning();
	}
	public void setGroundTruth(float[][][] gt){
		plotter.setTrue(gt);
	}
	
	public void displaynet(){
		//method to visualise the current state of the neural network.
		int x = m.getmaxc().x+1;
		int y = m.getmaxc().y+1; 
		double [][] netvals = new double[x][y];
		for (int i =0; i < x; i ++){
			for (int j =0; j < y; j++){
				netvals[i][j] = peekchoice(new Point(i,j));
			}
		}
		for (int i =0; i < x; i++){
			for(int j = 0; j < y; j++){
				System.out.printf(" %1.4f ",netvals[i][j]);
			}
			System.out.printf("%n");
		}
	}
}
