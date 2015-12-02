package mr.nnprovider;

import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.training.TrainingInputProviderImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.awt.Point;

//for initial workthrough this will only handle one point at a time
public class MazeProvider extends TrainingInputProviderImpl {

	private static final long serialVersionUID = 1L;
	float [] targ;
	
	public MazeProvider(Point _maxc){
		maxc = _maxc;
		targ = new float[4];
		mem = new Point(0,0);
	}
	
	Point maxc, mem;
	
	public void observe(Point p){
		mem = p;
	}
	
	
	public void clearmem(){
		mem = new Point(0,0);
	}
	
	public int getInputSize() {
		return 1;
	}

	public void reset() {
	}
	public void setTarget(float[] t){
		targ = t;
	}
	protected TrainingInputData getNextUnmodifiedInput() {
		
		return new MazeData(mem,maxc, targ);
	}
	public TrainingInputData getTIDInput(){
		return new MazeData(mem,maxc,targ);
	}

	@Override
	public float[] getNextInput() {
		return getNextUnmodifiedInput().getInput().getElements();
	}
	@Override
	public float[] getNextTarget() {
		
		return targ.clone();
	}

}
