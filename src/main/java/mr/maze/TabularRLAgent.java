package mr.maze;

import java.awt.Point;
import mr.tabularProvider.*;

import com.github.neuralnetworks.tensor.Matrix;

public class TabularRLAgent extends RLMazeAgent{

	private TabularData data;
	
	public TabularRLAgent(float learningparam1, RLType rtype, LearningParadigm lp){
		super();
		data = new TabularData(m.getmaxc());
	}
	@Override
	protected Matrix peekchoice(Point loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float runonce(boolean talkback) {
		// TODO Auto-generated method stub
		return 0;
	}

}
