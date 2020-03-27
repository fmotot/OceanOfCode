package bzh.motot.fred.pathfinding;

import java.util.ArrayList;

public class Vertex {

	private final int X;
	private final int Y;
	private ArrayList<Vertex> listVertex;
	
	
	private Object o;
	private boolean isVisited = false;
	private Vertex parent = null;
	
	
	
	
	
//	public Vertex(Object o){
//		this.o = o;
//		X=Y=0;
//	}
	
	/**
	 * les coordonnées du Vertex dans la carte
	 * @param x
	 * @param y
	 */
	public Vertex (int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public Object getObject() {
		return this.o;
	}
	
	public boolean isVisited() {
//		System.err.print(o);
//		System.err.print(" visited : ");
//		System.err.println(this.isVisited);
		return this.isVisited;
	}
	
	public void setVisited() {
		this.isVisited = true;
//		System.err.print(o);
//		System.err.println(" visited !");
	}
	
	public void clearVisited() {
//		System.err.print(o);
//		System.err.print(" cleared!\nVisited : ");
		
		this.isVisited = false;
//		System.err.println(this.isVisited);
	}
	
	public void setParent(Vertex v) {
		this.parent = v;
	}
	
	public void clearParent() {
		this.parent = null;
	}
	
	public Vertex getParent() {
		return this.parent;
	}
	
	public String toString(){
        return this.X + " " + this.Y;   
    }
	
	public int[] getCoord(){
    	int[] coord = {X,Y};
    	
    	return coord ;
    }
}
