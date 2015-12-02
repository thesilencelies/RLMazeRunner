package mr.maze;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import mr.mazeImpl.StateExperience;
import mr.nnprovider.MazeData;
import mr.nnprovider.MazeLearningProv;
import mr.nnprovider.MazeProvider;


//there are a lot of definitional magic numbers here right now
//how would loading from a file be handled?

public class NNRLmazeAgent extends RLMazeAgent{
	//private NeuralNetworkImpl nnmem;
	private NeuralNetworkImpl nnchoice;
	private MazeLearningProv mycsprov;
	private MazeProvider testprov;
	private TrainingInputData input;
	
	private Point maxc;
	private BackPropagationTrainer<?> bpt;
	private MultipleNeuronsOutputError oe;
	private 		ValuesProvider results;
	private 	    Set<Layer> calculatedLayers;
	
	private List<StateExperience> expreplay, replaybatch;
	
	private int maxexplen, batchlen;
	
	public NNRLmazeAgent (Maze _m, double epsilon, double gamma, double alpha){
		super(_m,epsilon,gamma,alpha);
		maxc = m.getmaxc();
		
		maxexplen = 10000;
	    batchlen = 15;
		
		//multilayer perceptron for the final decision for now
		//base and final layers are fixed by the size of the maze and number of outputs
		nnchoice = NNFactory.mlpTanh(new int []{401, 4},true,new ConnectionCalculatorFullyConnected());
		//nnmem = NNFactory.mlpSigmoid(new int []{41, 4},true);
		mycsprov = new MazeLearningProv(maxc);
		testprov = new MazeProvider(maxc);
		oe = new MultipleNeuronsOutputError();
		results = TensorFactory.tensorProvider(nnchoice,1, Environment.getInstance().getUseDataSharedMemory());
		calculatedLayers  = new UniqueList();				//float learningRate, float momentum, float l1weightDecay, float l2weightDecay, float dropoutRate, int trainingBatchSize, int testBatchSize, int epochs
		bpt = TrainerFactory.backPropagation(nnchoice, mycsprov, testprov, oe, new NNRandomInitializer(new MersenneTwisterRandomInitializer(0,0.1f)), 0.3f, 0.3f, 0f, 0f, 0, batchlen, 1, 5);
		//connect the input to the neural network
	    input = new TrainingInputDataImpl(results.get(nnchoice.getInputLayer()), results.get(oe));
	    expreplay = new ArrayList<StateExperience>();
	    replaybatch = new ArrayList<StateExperience>();
	    maxexplen = 10000;
	    batchlen = 15;
	}
	@Override
	public void load(Path mazep, Path nnp) throws IOException{
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
		return runNN(mypos.getloc());
	}
	private Matrix runNN(Point loc){
	    if (oe != null) {
		oe.reset();
		results.add(oe, results.get(nnchoice.getOutputLayer()).getDimensions());
	    }
	    //apply the new observation
	    testprov.observe(loc);
	    testprov.populateNext(input);
		calculatedLayers.clear();
		calculatedLayers.add(nnchoice.getInputLayer());
		nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

		return  results.get(nnchoice.getOutputLayer());
	}
	
	protected float peekchoice(Point loc){
		Matrix peek = runNN(loc);
		float val = peek.get(0,0);
		for (float v : peek.getElements()){
			if(v > val){
				val = v;
			}
		}
		return val;
	}
	protected float experience(Action choice){
		//moves the position, then stores the results in expreplay, then prepares a random batch from experience
		//create the random batch
		if(expreplay.size() < batchlen){
			replaybatch = new ArrayList<StateExperience>(expreplay);
		}
		else{
			replaybatch.clear();
		//select batchlen members from expreplay with equal probability
			double batchleft = batchlen;
			for(int i = 0, len = expreplay.size(); i < len;i++){
				double prob = batchleft/(len-i);
				StateExperience se = expreplay.get(i);
				if(Math.abs(se.reward) > 0.5) prob = 10*prob;
				if(Math.random() < prob){
					replaybatch.add(se);
					batchleft--;
					if(batchleft < 1){
						break;
					}
				}
			}
		}
		//add the new experience
		Point spos = new Point(mypos.getloc());
		float reward = mypos.go(choice,m);
		Point epos = new Point(mypos.getloc());
		StateExperience s = new StateExperience(spos,epos,reward,choice);
		expreplay.add(s);
		if(expreplay.size() > maxexplen){
			expreplay.remove(0);
		}
		//always include the most recent experience in the batch.
		replaybatch.add(s);
		return reward;
	}
	protected void settargets(){
		//sets the target values for each of the points in replaybatch
		Iterator<StateExperience> it = replaybatch.iterator();
		while(it.hasNext()){
			StateExperience i = it.next();
			float [] target = runNN(i.startstate).getElements();
			float val = 0;
			//clamp terminal states to their reward
			if(!m.isTermPos(i.endstate)){
				val = peekchoice(i.endstate);
			}
			target[i.act.ind] = target[i.act.ind] + (float)(alpha*((i.reward + gamma*val) - target[i.act.ind]));
			i.target = target;
		}
		//apply this list to the provider
		mycsprov.SetReplayBatch(replaybatch);
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
				if(talkback){
					 System.out.println ("at location " + mypos.getloc() + " action chosen was " + a + " randomly");
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
				if(talkback){
					 System.out.println ("at location " + mypos.getloc() + " action chosen was " + a + " with value " + maxval);
				}
			}
			//take the action

			
			reward = experience(a);
			totalReward = totalReward + reward;
			if (talkback) {
				System.out.println("arriving to "+ mypos.getloc() + " with reward " + reward);
			}
			//assess new state and update previous
			settargets();
			bpt.train();
		}
		//experiences are retained between episodes
			System.out.println("reward was " + totalReward + " after " + steps + " steps");
		return totalReward;
	}

}
