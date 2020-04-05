package bzh.motot.fred.pathfinding;

import java.util.ArrayList;

public class Vertex implements Cloneable{

	private final int X;
	private final int Y;
	
	

	private static ArrayList<Vertex> listVertex;
	
	private boolean isWater;
	private boolean isMine = false;
	private boolean isEnemy = true;
	private boolean isEnemyVisited = false;
	private boolean isVisited = false;
	private boolean isPathVisited = false;
	private Vertex parent = null;
	
	
	/**
	 * les coordonnées du Vertex dans la carte
	 * @param x
	 * @param y
	 */
	public Vertex (int x, int y, boolean isWater) {
		this.X = x;
		this.Y = y;
		this.isWater = isWater;
		if (!isWater)
			this.setEnemy(false);
	}
	
	@Override
	protected Vertex clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (Vertex) super.clone();
	}
	
	
	/**
	 * 
	 * @return true si c'est de l'eau, false si une île
	 */
	public boolean isWater() {
		return isWater;
	}
	/*
	 * Pour les besoins en Pathfinding
	 * 
	 */
	
	


	/**
	 * PATH_FINDING
	 * @return true si la case a déjà été utilisé pour le pathfinding
	 */
	public boolean isPathVisited() {
		return this.isPathVisited;
	}
	
	/**
	 * PATH_FINDING
	 * @param isPathVisited
	 */
	public void setPathVisited(boolean isPathVisited) {
		this.isPathVisited = isPathVisited;
	}
	
	/**
	 * PATH_FINDING
	 * @param v
	 */
	public void setParent(Vertex v) {
		this.parent = v;
	}
	
	/**
	 * PATH_FINDING
	 */
	public void clearParent() {
		this.parent = null;
	}
	
	/**
	 * PATH_FINDING
	 * @return
	 */
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
	
	/**
	 * Chemin en cours pris par le sous-marin
	 * @return true si déjà pris
	 */
	public boolean isVisited() {
		return this.isVisited;
	}
	
	/**
	 * Chemin en cours pris par le sous-marin
	 * @param isVisited
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
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
