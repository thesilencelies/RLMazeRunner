package mr.tabularProvider;

import java.awt.Point;

import mr.mazeImpl.Action;

public class StateActionPair {
	public int a,b,c;
	public double et;
	public StateActionPair(Point state, Action act){
		a = state.x;
		b = state.y;
		c = act.ind;
		et = 1;
	}
	public boolean isequal(Point p, Action act){
		if(act.ind == c && p.x ==a && p.y == b) return true;
		else return false;
	}

	public boolean equals(StateActionPair s){
		if(s.c == c && s.a ==a && s.b == b) return true;
		else return false;
	}
}
