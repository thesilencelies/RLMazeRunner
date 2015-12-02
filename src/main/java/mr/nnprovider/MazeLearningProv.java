package mr.nnprovider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.training.TrainingInputProviderImpl;

import mr.mazeImpl.StateExperience;

//this handles the batches for learning provided by the algorithm
public class MazeLearningProv extends TrainingInputProviderImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3247533855332357907L;
	private List<StateExperience> exp;
	private Iterator<StateExperience> inputit,targit;
	private Point maxc;
	
	public MazeLearningProv(Point _maxc){
		maxc = _maxc;
		exp = new ArrayList<StateExperience>();
		inputit = exp.iterator();
		targit = exp.iterator();
	}
	
	public void SetReplayBatch(List<StateExperience> bat){
		exp = bat;
		inputit = exp.iterator();
		targit = exp.iterator();
	}
    @Override
    public void reset() {
	currentInput = 0;
	inputit = exp.iterator();
	targit = exp.iterator();
    }
	@Override
	public int getInputSize() {
		return exp.size();
	}
	protected TrainingInputData getNextUnmodifiedInput() {
		if(!inputit.hasNext()) reset();
		StateExperience s = inputit.next();
		return new MazeData(s.startstate,maxc, s.target);
	}
	@Override
	public float[] getNextInput() {
		return getNextUnmodifiedInput().getInput().getElements();
	}

	@Override
	public float[] getNextTarget() {
		if(!targit.hasNext()) reset();
		return targit.next().target;
	}
	
}
