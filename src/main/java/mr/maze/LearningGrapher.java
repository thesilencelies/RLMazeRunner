package mr.maze;

import java.util.ArrayList;
import java.util.List;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.layout.StripeLayout;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

public class LearningGrapher {
	private List<float[][][]> Qvals;
	private List<Double> rewards;
	private float[][][] gt;
	public LearningGrapher(){
		Qvals = new ArrayList<float[][][]>();
		rewards = new ArrayList<Double>();
	}
	public void observe(float[][][] Q, double r){
		Qvals.add(Q.clone());
		rewards.add(r);
	}
	public void reset(){
		Qvals.clear();
	}
	public void setTrue(float[][][] truth){
		gt = truth.clone();
	}
	public void plotLearning(){
		float[][] plot;
		//set the target 
		if(gt == null){
			gt = Qvals.get(Qvals.size()-1);
		}
		//calculate the values
		plot = new float[Qvals.size()][2];
		for(int n =0, lenq = Qvals.size(); n < lenq; n++){
			float val = 0;
			float [][][] Qv = Qvals.get(n);
			for(int i = 0, lx = Qv.length;i < lx; i++){
				for(int j = 0, ly = Qv[i].length;j < ly; j++){
					for(int k = 0, lz = Qv[i][j].length; k < lz; k++){
						val += Math.abs(Qv[i][j][k] - gt[i][j][k]);
					}
				}
			}
			plot[n] = new float[]{n,val};
		}
		double[][] rplot = new double[rewards.size()][2];
		double totr =0;
		for(int n = 0, lr = rewards.size(); n < lr; n++){
		//average taken over the last 10 episodes
			totr += rewards.get(n);
			double div;
			if(n > 9){
				div = 10;
				totr -= rewards.get(n-10);
			}
			else{
				div = n;
			}
			rplot[n] = new double[]{n,totr/div};
		}
		
		JavaPlot p = new JavaPlot();
       // p.setTitle("Default Terminal Title");
        p.getAxis("x").setLabel("Steps", "Arial", 20);
        p.getAxis("y").setLabel("Average error");

        p.getAxis("x").setBoundaries(0, rewards.size());
        p.setKey(JavaPlot.Key.TOP_RIGHT);

        DataSetPlot s = new DataSetPlot(plot);
        p.addPlot(s);
        PlotStyle stl = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
        stl.setStyle(Style.LINES);
        p.newGraph();
        p.getAxis("y").setLabel("Average reward");
        DataSetPlot r = new DataSetPlot(rplot);
        p.addPlot(r);
        stl = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
        stl.setStyle(Style.LINES);

        p.setMultiTitle("Approach to true values");
        StripeLayout lo = new StripeLayout();
        lo.setColumns(1);
        p.getPage().setLayout(lo);
        p.plot();
	}
}
