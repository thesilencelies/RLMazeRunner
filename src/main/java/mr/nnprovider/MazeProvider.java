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
		//we'll have to redesign the iteration and similar later
	}
	
	Point maxc, mem;
	
	public void observe(Point p){
		mem = p;
	/*(	exp.add(p);
		if(exp.size() > 2){
			exp.remove(exp.listIterator().next());
		}*/
	}
	
	
	public void clearmem(){
		mem = new Point(0,0);
		/*exp.clear();
		target.clear();
		reset();*/
	}
	
	public int getInputSize() {
		return 1;//exp.size();
	}

	public void reset() {
		//memit = exp.listIterator();
		//targetit = target.listIterator();
	}
	public void setTarget(float[] t){
		targ = t;
		/*target.add(t);
		if(target.size() >40){
			target.remove(target.listIterator().next());
		}*/
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
