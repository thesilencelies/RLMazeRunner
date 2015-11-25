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
	List<float[]> target;
	Iterator<float[]> targetit;
	
	public MazeProvider(Point p){
		mem = p;
		targ = new float[4];
		target = new ArrayList<float[]>();
		exp = new ArrayList<Point>();
		memit = exp.listIterator();
		targetit = target.listIterator();
	}
	
	Point mem;
	List<Point>exp;
	Iterator<Point> memit;
	
	public void observe(Point p){
		mem = p;
		exp.add(p);
		if(exp.size() > 40){
			exp.remove(exp.listIterator().next());
		}
	}
	
	
	public void clearmem(){
		//mem = new Point(0,0);
		exp.clear();
		target.clear();
		reset();
	}
	
	public int getInputSize() {
		return exp.size();
	}

	public void reset() {
		memit = exp.listIterator();
		targetit = target.listIterator();
	}
	public void setTarget(float[] t){
		targ = t;
		target.add(t);
		if(target.size() >40){
			target.remove(target.listIterator().next());
		}
	}
	protected TrainingInputData getNextUnmodifiedInput() {
		if(!memit.hasNext()){
			memit = exp.listIterator();
		}
		return new MazeData(memit.next(), targ);
	}
	public TrainingInputData getTIDInput(){
		return new MazeData(mem,targ);
	}

	@Override
	public float[] getNextInput() {
		return getNextUnmodifiedInput().getInput().getElements();
	}
	@Override
	public float[] getNextTarget() {
		if(!targetit.hasNext()){
			targetit = target.listIterator();
		}
		return targetit.next().clone();
	}

}
