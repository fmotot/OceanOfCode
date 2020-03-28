package bzh.motot.fred.pathfinding;

import java.util.ArrayList;

public class Vertex {

	private final int X;
	private final int Y;
	
	
	private static ArrayList<Vertex> listVertex;
	
	
	private boolean isMine = false;
	private boolean isEnemy = true;
	private boolean isVisited = false;
	private boolean isPathVisited = false;
	private Vertex parent = null;
	
	
	/**
	 * les coordonnées du Vertex dans la carte
	 * @param x
	 * @param y
	 */
	public Vertex (int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	
	/*
	 * Pour les besoins en Pathfinding
	 * 
	 */
	
	
	public boolean isPathVisited() {
		return this.isPathVisited;
	}
	
	public void setPathVisited(boolean isPathVisited) {
		this.isPathVisited = isPathVisited;
	}
	
//	public void clearPathVisited() {
//		this.isVisited = false;
//	}
	
	public void setParent(Vertex v) {
		this.parent = v;
	}
	
	public void clearParent() {
		this.parent = null;
	}
	
	public Vertex getParent() {
		return this.parent;
	}
	
	/*
	 * Pour savoir si une mine est présente
	 * nécessiter de savoir si une mine a pu être poser plusieurs fois ici pour connaitre l'éventuel emplacement des autres après explosion ?
	 * 
	 */
	
	
	public boolean isMine() {
		return isMine;
	}


	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}


	/*
	 * Pour connaitre la présence ennemie
	 * 
	 */
	
	
	public boolean isEnemy() {
		return isEnemy;
	}


	public void setEnemy(boolean isEnemy) {
		this.isEnemy = isEnemy;
	}
	
	/*
	 * Pour savoir si on peut retourner sur la case
	 * 
	 */
	
	
	public boolean isVisited() {
		return this.isVisited;
	}
	
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
//	
//	public void clearVisited() {
//		this.isVisited = false;
//	}
	
	
	/*
	 * Récupérer les coordonnées d'un vertex 
	 * 
	 */
	
	
	public String toString(){
        return this.X + " " + this.Y;   
    }
	
	public int[] getCoord(){
    	int[] coord = {X,Y};
    	
    	return coord ;
    }
}
