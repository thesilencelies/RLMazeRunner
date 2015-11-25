package mr.nnprovider;

import com.github.neuralnetworks.training.TrainingInputDataImpl;
import com.github.neuralnetworks.tensor.Matrix;

import java.awt.Point;

public class MazeData extends TrainingInputDataImpl {
	private static final long serialVersionUID = 1L;
	
	
	public MazeData(Point loc){
		super(new Matrix(0,new float[]{(float) loc.getX(),(float) loc.getY()},new int[]{2,1},new int[][]{{0,0},{2,1}}));
	}
	
	public MazeData(Point loc, float[] targ){
		super(new Matrix(0,new float[]{(float) loc.getX(),(float) loc.getY()},new int[]{2,1},new int[][]{{0,0},{2,1}}),new Matrix(0, targ, new int[]{targ.length}, new int[][]{{0},{targ.length}}));
	}
}
