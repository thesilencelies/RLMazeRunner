package mr.maze;

import java.awt.Point;
import java.nio.file.Path;
import java.util.Set;

import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.calculation.memory.ValuesProvider;
import com.github.neuralnetworks.calculation.neuronfunctions.ConnectionCalculatorFullyConnected;
import com.github.neuralnetworks.input.MultipleNeuronsOutputError;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.training.TrainingInputDataImpl;
import com.github.neuralnetworks.training.TrainingInputProvider;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.random.MersenneTwisterRandomInitializer;
import com.github.neuralnetworks.training.random.NNRandomInitializer;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.UniqueList;

import mr.mazeImpl.Action;
import mr.mazeImpl.Maze;
import mr.mazeImpl.Position;
import mr.nnprovider.MazeData;
import mr.nnprovider.MazeProvider;


//there are a lot of definitional magic numbers here right now
//how would loading from a file be handled?

public class NNRLmazeAgent extends RLMazeAgent{
	//private NeuralNetworkImpl nnmem;
	private NeuralNetworkImpl nnchoice;
	private MazeProvider mycsprov;
	private TrainingInputData input;
	
	
	private BackPropagationTrainer<?> bpt;
	private MultipleNeuronsOutputError oe;
	private 		ValuesProvider results;
	private 	    Set<Layer> calculatedLayers;
	
	private float gamma;
	
	public NNRLmazeAgent (Maze _m, double epsilon, double gamma, double alpha){
		super(_m,epsilon,gamma,alpha);
		//multilayer perceptron for the final decision for now
		//base and final layers are fixed by the size of the maze and number of outputs
		nnchoice = NNFactory.mlpSigmoid(new int []{40, 41, 4},true);//,new ConnectionCalculatorFullyConnected());
		//nnmem = NNFactory.mlpSigmoid(new int []{41, 4},true);
		mycsprov = new MazeProvider(mypos.getloc());
		oe = new MultipleNeuronsOutputError();
		results = TensorFactory.tensorProvider(nnchoice,1, Environment.getInstance().getUseDataSharedMemory());
		calculatedLayers  = new UniqueList();
		bpt = TrainerFactory.backPropagation(nnchoice, mycsprov, mycsprov, oe, new NNRandomInitializer(new MersenneTwisterRandomInitializer(0.1f,0.1f)), 0.1f, 0.1f, 0.1f, 0.1f, 0, 1, 1, 2);
	}
	@Override
	public void load(Path mazep, Path nnp){
		m = new Maze(mazep);
		//functions to load the neural net itself as well
		//TODO
	}
	@Override
	public void save(Path mazep, Path nnp){
		m.save(mazep);
		//functions to save the neural net itself as well
		//TODO
	}
	
	private Matrix rankchoice(){
	    if (oe != null) {
		oe.reset();
		results.add(oe, results.get(nnchoice.getOutputLayer()).getDimensions());
	    }
	    input = new MazeData(mypos.getloc());
	    //apply the new observation
		mycsprov.observe(mypos.getloc());
		calculatedLayers.clear();
		calculatedLayers.add(nnchoice.getInputLayer());
		nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

		return  results.get(nnchoice.getOutputLayer());

	}
	
	protected float peekchoice(Point loc){
		
		input = new MazeData(loc);
		//create the necessary machinery (is there a more efficient place to create these objects?
		calculatedLayers.clear();
		calculatedLayers.add(nnchoice.getInputLayer());
		//produce the results 
		nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);
		Matrix peek = results.get(nnchoice.getOutputLayer());
		float val = peek.get(0,0);
		for (float v : peek.getElements()){
			if(v > val){
				val = v;
			}
		}
		return val;
	}

	public float runonce(boolean talkback){
		//nnmem =
		mypos = new Position();
		float reward = 0;
		float totalReward = 0;
		int steps = 0;
		while (!m.isTermPos(mypos.getloc())){
			steps = steps +1;
			//assess choices
			Matrix mat = rankchoice();
			Action a = Action.UP;	//default value
			//choose greedily with probability 1-e
			if(Math.random()< err){
				//make a random choice
				if(Math.random() < 0.5){
					if(Math.random() < 0.5){
						a = Action.UP;
					}
					else{
						a = Action.DOWN;
					}
				}else{
					if(Math.random() < 0.5){
						a = Action.LEFT;
					}
					else{
						a = Action.RIGHT;
					}
				}
			}
			else{
				float[] weights = mat.getElements();
				float maxval = weights[0];
				for (Action act : Action.values()){
					if(weights[act.ind] > maxval){
						maxval = weights[act.ind];
						a = act;
					}
				}
			}
			//take the action
			if(talkback){
				 System.out.println ("at location " + mypos.getloc() + " action chosen was " + a);
			}
			
			reward = mypos.go(a,m);
			totalReward = totalReward + reward;
			if (talkback) {
				System.out.println("arriving to "+ mypos.getloc() + " with reward " + reward);
			}
			//assess new state and update previous
			float [] target = mat.getElements();
			float val = 0;
			//clamp terminal states to their reward
			if(!m.isTermPos(mypos.getloc())){
				val = peekchoice(mypos.getloc());
				
				if(talkback){
					System.out.println("the new max Q value is " + val);
				}
			}
			target[a.ind] = target[a.ind] + (float)(alpha*(reward + gamma*val) - target[a.ind]);
			mycsprov.setTarget(target);
			bpt.train();
		}
		
			System.out.println("reward was " + totalReward + " after " + steps + " steps");
		return totalReward;
	}

}
