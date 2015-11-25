package mr.mazeImpl;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;


public class Maze {
	private boolean _warpwalls;
	private Point maxc;
	private Point goal;
	private List<Point> pits;
	private List<Point> walls;
	
	
	public Maze(int dimx, int dimy, int npits, int nwalls, boolean warpwalls){	//these must not be larger than 255
		_warpwalls = warpwalls;
		pits = new ArrayList<Point>();
		walls = new ArrayList<Point>();
		//randomly populate the maze
		maxc = new Point(dimx,dimy);
		for(int i = 0; i < npits; i++){
		pits.add(new Point(ThreadLocalRandom.current().nextInt(1, dimx + 1),ThreadLocalRandom.current().nextInt(1, dimy + 1)));
		}
		for(int i = 0; i < nwalls; i++){
		walls.add(new Point(ThreadLocalRandom.current().nextInt(1, dimx + 1),ThreadLocalRandom.current().nextInt(1, dimy + 1)));
		}
		goal = new Point(ThreadLocalRandom.current().nextInt(1, dimx + 1),ThreadLocalRandom.current().nextInt(1, dimy + 1));
		while (pits.equals(goal) || walls.equals(goal)){
			goal = new Point(ThreadLocalRandom.current().nextInt(1, dimx + 1),ThreadLocalRandom.current().nextInt(1, dimy + 1));
		}
	}
	public Maze(Path p){
		//loads the maze from a file
		byte[] fileArray;
		try{
		fileArray = Files.readAllBytes(p);
		int npits = fileArray[0];
		int nwalls = fileArray[1];
		maxc = new Point(fileArray[2],fileArray[3]);
		goal = new Point(fileArray[4],fileArray[5]);
		for(int i = 0; i < npits; i++){
			pits.add(new Point(fileArray[6+(2*i)],fileArray[7+(2*i)]));
		}
		for(int i = npits; i < nwalls+npits; i++){
			walls.add(new Point(fileArray[6+(2*i)],fileArray[7+(2*i)]));
		}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void save(Path p){
		byte[] buf = new byte [6 + 2*(pits.size() + walls.size())];
		buf[0] = (byte) pits.size();
		buf[1] = (byte) walls.size();
		buf[2] = (byte) maxc.getX();
		buf[3] = (byte) maxc.getY();
		buf[4] = (byte) goal.getX();
		buf[5] = (byte) goal.getY();
		Iterator<Point> it = pits.iterator();
		int i = 6;
		while(it.hasNext()){
			Point x = it.next();
			buf[i] = (byte) x.getX();
			i++;
			buf[i] = (byte) x.getY();
			i++;
		}
		it = walls.iterator();
		while(it.hasNext()){
			Point x = it.next();
			buf[i] = (byte) x.getX();
			i++;
			buf[i] = (byte) x.getY();
			i++;
		}
		try{
			Files.write(p, buf);
		
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public Point goresult(Action a, Point p){
		if (_warpwalls){
			return warpstep(a,p);
		}
		else{
			return solidgoresult(a,p);
		}
	}
	private Point warpstep(Action a, Point p){
		//find out where they would go
			Point newpos = new Point(p);
			switch(a){
				case UP:
					newpos.translate(0,1);
					break;
				case DOWN:
					newpos.translate(0,-1);
					break;
				case LEFT:
					newpos.translate(-1,0);
					break;
				case RIGHT:
					newpos.translate(1,0);
					break;
			}
			//walls
			if (walls.contains(newpos)){
				return warpstep(a, newpos);
			}
			//boundaries
			if( newpos.getX() < 0 || newpos.x > maxc.x || newpos.y < 0 || newpos.y > maxc.y){
				switch(a){
				case UP:
					newpos.move(newpos.x,-1);
					break;
				case DOWN:
					newpos.move(newpos.x,maxc.y+1);
					break;
				case LEFT:
					newpos.move(maxc.x+1,newpos.y);
					break;
				case RIGHT:
					newpos.move(-1,newpos.y);
					break;
			}
				return warpstep(a,newpos);
			}
		return newpos;
	}
	
	private Point solidgoresult(Action a, Point p){	
		//find out where they would go
		Point newpos = new Point(p);
		switch(a){
		case UP:
			newpos.translate(0,1);
			break;
		case DOWN:
			newpos.translate(0,-1);
			break;
		case LEFT:
			newpos.translate(-1,0);
			break;
		case RIGHT:
			newpos.translate(1,0);
			break;
		}
		
		//check that they can go there
		if(walls.contains(newpos) || newpos.x < 0 || newpos.x > maxc.x || newpos.y < 0 || newpos.y > maxc.y){
			return p;
		}
		else{
			return newpos;
		}
	}
	
	public float posreward(Point p){
		if(p.equals(goal)){
			return 1;
		}
		else{
			if(pits.contains(p)){
				return -1;
			}
			else{
				return -0.001f;
			}
		}
	}
	public boolean isTermPos(Point p){
		if (p.equals(goal)){
			return true;
		}
		else{
			return pits.contains(p);
		}
	}
	
	public Point getmaxc (){
		return new Point(maxc);
	}
}
