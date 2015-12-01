package mazetest;

import java.awt.Point;
import java.util.Set;

import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.calculation.memory.ValuesProvider;
import com.github.neuralnetworks.input.MultipleNeuronsOutputError;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.training.TrainingInputDataImpl;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.random.MersenneTwisterRandomInitializer;
import com.github.neuralnetworks.training.random.NNRandomInitializer;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.UniqueList;

import mr.nnprovider.MazeData;
import mr.nnprovider.MazeProvider;

public class NNprovtest {
	//test to make sure that the output of the input provider behaves as expected 
	public static void testuse(){
		//create the objects
	Point testp = new Point(1,1);
	Point maxp = new Point(20,20);
	MazeProvider mprov = new MazeProvider(maxp);
	MazeProvider mtest = new MazeProvider(maxp);
	NeuralNetworkImpl nnchoice = NNFactory.mlpSigmoid(new int []{400, 4},true);
	ValuesProvider results = TensorFactory.tensorProvider(nnchoice,1, Environment.getInstance().getUseDataSharedMemory());
	MultipleNeuronsOutputError oe = new MultipleNeuronsOutputError();
	BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nnchoice, mprov, mtest, oe, new NNRandomInitializer(new MersenneTwisterRandomInitializer(0,0.1f)), 0.1f, 0.1f, 0.1f, 0.1f, 0, 1, 1, 2);
	Set<Layer> calculatedLayers  = new UniqueList<Layer>();
	
	//produce a result for a test
	 if (oe != null) {
			oe.reset();
			results.add(oe, results.get(nnchoice.getOutputLayer()).getDimensions());
		    }
		    //apply the new observation
	 		TrainingInputData input = new TrainingInputDataImpl(results.get(nnchoice.getInputLayer()), results.get(oe));
			mprov.observe(testp);
			mprov.populateNext(input);
			calculatedLayers.clear();
			calculatedLayers.add(nnchoice.getInputLayer());
			nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);
			
			//Produce next result
			Matrix mat = results.get(nnchoice.getOutputLayer());
			System.out.printf("layer output for 1,1 was %f %f %f %f %n", mat.getElements()[0],mat.getElements()[1],mat.getElements()[2],mat.getElements()[3]);
			testp.setLocation(5, 3);
	 		//mprov.observe(testp);
			mprov.populateNext(input);
			calculatedLayers.clear();
			calculatedLayers.add(nnchoice.getInputLayer());
			nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

			mat = results.get(nnchoice.getOutputLayer());
			System.out.printf("new layer output for 5,3 was %f %f %f %f %n", mat.getElements()[0],mat.getElements()[1],mat.getElements()[2],mat.getElements()[3]);
			//train once to see changes
			
			mprov.setTarget(new float[]{0.2f,0.5f,0,1});
			bpt.train();
			//get new values for those locations
			testp.setLocation(1, 1);
			//mprov.observe(testp);
			mprov.populateNext(input);
			calculatedLayers.clear();
			calculatedLayers.add(nnchoice.getInputLayer());
			nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

			mat = results.get(nnchoice.getOutputLayer());
			System.out.printf("new layer output for 1,1 was %f %f %f %f %n", mat.getElements()[0],mat.getElements()[1],mat.getElements()[2],mat.getElements()[3]);
			testp.setLocation(5, 3);
			//mprov.observe(testp);
			mprov.populateNext(input);
			calculatedLayers.clear();
			calculatedLayers.add(nnchoice.getInputLayer());
			nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

			mat = results.get(nnchoice.getOutputLayer());
			System.out.printf("layer output for 5,3 was %f %f %f %f", mat.getElements()[0],mat.getElements()[1],mat.getElements()[2],mat.getElements()[3]);
			
			
	}
	
	public static void testgetinput(){
		Point testp = new Point(1,1);
		Point maxp = new Point(5,3);
		MazeProvider mprov = new MazeProvider(maxp);
		mprov.observe(testp);
		float[] vals = mprov.getNextInput();
		System.out.println("get next input for 1,1 gave:");
		for(int i = 0, len = vals.length; i < len; i++){
			System.out.printf(" %f ", vals[i]);
		}
		testp.translate(2, 1);
		//mprov.observe(testp);
		vals = mprov.getNextInput();
		System.out.printf("%n get next input for 3, 2 gave: %n");
		for(int i = 0, len = vals.length; i < len; i++){
			System.out.printf(" %f ", vals[i]);
		}
		System.out.printf("%n");
	}
	
	public static void  main(String [] args){
		testgetinput();
		testuse();
	}
}
