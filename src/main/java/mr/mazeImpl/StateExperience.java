package mr.mazeImpl;

import java.awt.Point;

public class StateExperience {
	public Point startstate,endstate;
	public double reward;
	public Action act;
	public float[] target;
	public StateExperience(Point st, Point stt,double r, Action a){
		startstate = new Point(st);
		endstate = new Point(stt);
		reward = r;
		act = a;
		target = new float[4];
	}
	public boolean equals(Object obj){
		if (obj instanceof StateExperience) {
            StateExperience s = (StateExperience)obj;
            if(s.act == act){
            	if(s.reward == reward){
            		if(s.startstate.equals(startstate)){
            			if(s.endstate.equals(endstate)){
            				return true;
            			}
            		}
            	}
            }
            return false;

        }
        return super.equals(obj);
		
	}
}
