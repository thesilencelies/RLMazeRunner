package mr.tabularProvider;

import java.awt.Point;
import java.util.Arrays;

public class TabularData {
	
	public float [][] V;
	public float [][][] Q;
	
	public TabularData(Point maxc){
		V = new float [maxc.x+1][maxc.y+1];
        for (int i = 0, len = V.length; i < len; i++){
        	Arrays.fill(V[i],0.5f);
        }
		Q = new float [maxc.x+1][maxc.y+1][4];
        for (int i = 0, len = Q.length; i < len; i++){
        	for(int j = 0, len2 = Q[0].length; j < len2;j++)
        	Arrays.fill(Q[i][j],0.5f);
        }
	}
}
