package mazetest;

import java.awt.Point;
import java.util.Set;

import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.calculation.memory.ValuesProvider;
import com.github.neuralnetworks.calculation.neuronfunctions.ConnectionCalculatorFullyConnected;
import com.github.neuralnetworks.calculation.neuronfunctions.ConstantConnectionCalculator;
import com.github.neuralnetworks.input.MeanInputFunction;
import com.github.neuralnetworks.input.MultipleNeuronsOutputError;
import com.github.neuralnetworks.input.ScalingInputFunction;
import com.github.neuralnetworks.input.SimpleInputProvider;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.TrainingInputData;
import com.github.neuralnetworks.training.TrainingInputDataImpl;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTanh;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.random.MersenneTwisterRandomInitializer;
import com.github.neuralnetworks.training.random.NNRandomInitializer;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.Properties;
import com.github.neuralnetworks.util.UniqueList;

public class ClassificationTest {

	public static void main(String[] args){
		NeuralNetworkImpl mlp = NNFactory.mlpSigmoid(new int[] { 4, 4, 4 }, true); //,new ConnectionCalculatorFullyConnected());
		SimpleInputProvider input = new SimpleInputProvider(new float[][] { {1,0,0, 0}, {0, 0,1, 0}, {0,1,0,0}, {0,0,0,1} }, new float[][] { {.5f*1.479f,.5f*1.463f,.5f*1.463f,.5f*-0.5f},{.5f*1.479f, .5f*1.463f,.5f*1.479f,.5f*1.495f},{.5f*1.495f,.5f*-0.5f,.5f*1.463f,.5f*-0.5f},{.5f*1.495f,.5f*-0.5f,.5f*1.479f,.5f*1.495f} });
		input.addInputModifier(new MeanInputFunction());
		// create backpropagation trainer for the network
		BackPropagationTrainer bpt = TrainerFactory.backPropagation(mlp, input, input, new MultipleNeuronsOutputError(), new NNRandomInitializer(new MersenneTwisterRandomInitializer(-0.01f, 0.01f)), 0.1f, 0.9f, 0f, 0f, 0f, 1, 1, 10000);
		
		//train it 
		bpt.train();
		
		for(int i = 0; i < input.getInputSize(); i += 1){
			Matrix M = runNN(mlp,input);
			float[] el = M.getElements();
			System.out.printf("elements for input %d were: ", i);
			for(int j= 0, len = el.length; j<len;j++){
				System.out.printf(" %1.4f ", el[j]);
			}
			System.out.printf("%n");
		}
		
	}
	static private Matrix runNN(NeuralNetworkImpl nnchoice, SimpleInputProvider testprov){
		
		MultipleNeuronsOutputError oe =  new MultipleNeuronsOutputError();
		ValuesProvider results = TensorFactory.tensorProvider(nnchoice,1, Environment.getInstance().getUseDataSharedMemory());
		Set<Layer> calculatedLayers= new UniqueList<Layer>();
		TrainingInputData input = new TrainingInputDataImpl(results.get(nnchoice.getInputLayer()), results.get(oe));
	    if (oe != null) {
		oe.reset();
		results.add(oe, results.get(nnchoice.getOutputLayer()).getDimensions());
	    }
	    //apply the new observation
	    testprov.populateNext(input);
		calculatedLayers.clear();
		calculatedLayers.add(nnchoice.getInputLayer());
		nnchoice.getLayerCalculator().calculate(nnchoice, nnchoice.getOutputLayer(), calculatedLayers, results);

		return  results.get(nnchoice.getOutputLayer());
	}
}
