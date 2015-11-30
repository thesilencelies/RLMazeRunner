package mr.nnprovider;

import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.TensorFactory;

import java.awt.Point;

public class MazeData implements TrainingInputData {
	private static final long serialVersionUID = 1L;
		private Tensor input;
	    private Tensor target;
	
	public MazeData(Point loc, Point maxc){
		super();
		float [] elem = new float[(maxc.x+1)*(maxc.y+1)];
		elem[loc.x + loc.y*maxc.x] = 1;
		this.input = TensorFactory.matrix(elem,1);
		
	}
	
	public MazeData(Point loc, Point maxc, float[] targ){
		super();
		float [] elem = new float[(maxc.x+1)*(maxc.y+1)];
		elem[loc.x + loc.y*maxc.x] = 1;
		this.input = TensorFactory.matrix(elem,1);
		this.target = TensorFactory.matrix(targ,1);
	}

	    @Override
	    public Tensor getInput() {
		return input;
	    }

	    public void setInput(Tensor input) {
	        this.input = input;
	    }

	    @Override
	    public Tensor getTarget() {
		return target;
	    }

	    public void setTarget(Matrix target) {
	        this.target = target;
	    }
	}

