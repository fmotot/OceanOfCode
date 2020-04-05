package bzh.motot.fred.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bzh.motot.fred.oceanofcode.*;

public class MatrixMap implements Cloneable {
	public static final int[] NORTH = { 0, -1 };
	public static final int[] SOUTH = { 0, 1 };
	public static final int[] EAST = { 1, 0 };
	public static final int[] WEST = { -1, 0 };
	public static final int[][] DIRECTIONS = { NORTH, SOUTH, EAST, WEST };

	public static int clone = 0; 
	private final Vertex[][] mappy = new Vertex[15][15];

	public Vertex[][] getMappy() {
		return mappy;
	}

	/**
	 * Constructor
	 */
	public MatrixMap() {
	}

	public MatrixMap clone() throws CloneNotSupportedException {
		
		MatrixMap m = (MatrixMap) super.clone();
		
		for (Vertex[] vertexs : m.mappy) {
			for (Vertex vertex : vertexs) {
				vertex = vertex.clone();
			}
		}
		clone++;
		return m;
	}
	
	public ArrayList<Vertex> getEnemyPositions() {
		ArrayList<Vertex> positions = new ArrayList<Vertex>();
		
		for (Vertex[] vertexs : mappy) {
			for (Vertex v : vertexs) {
				if (v.isEnemy()) {
					positions.add(v);
				}
			}
		}

		return positions;
	}
	
	/**
	 * create the vertex and edge between each map's cell
	 * 
	 * @param line String la chaine représentant les cellules de la ligne
	 * @param y    int le numéro de la ligne (coordonnées y)
	 */
	public void setLine(String line, int y) {
		char[] lineMap = line.toCharArray();

		for (int x = 0; x < line.length(); x++) {
			mappy[x][y] = new Vertex(x, y, lineMap[x] == '.');
		}
	}

	public void showMappy() {
		for (int i = 0; i < mappy.length; i++) {
			for (int j = 0; j < mappy[0].length; j++) {
				if (mappy[j][i].isWater()) {
					System.out.println(mappy[j][i]);
					System.out.println(this.getLinkedVertex(mappy[j][i]));
				} else {
					System.out.println("TERRE !");
				}
			}
		}
	}

	public void showVertex() {
		for (int i = 0; i < mappy.length; i++) {
			Vertex[] vertexs = mappy[i];
			for (Vertex vertex : vertexs) {
				System.out.println(vertex);
				System.out.println(this.getLinkedVertex(vertex));
			}
		}
	}


	public Vertex getVertex(int x, int y) {
		return mappy[x][y];

	}


	/**
	 * Renvoie la liste des sommets liés au vertex "v"
	 * 
	 * @param v
	 * @return la liste des objets liés
	 */
	public ArrayList<Vertex> getLinkedVertex(Vertex v) {
		ArrayList<Vertex> list = new ArrayList<Vertex>();

		for (int[] direction : DIRECTIONS) {
			int coordX = direction[0] + v.getCoord()[0];
			int coordY = direction[1] + v.getCoord()[1];

			// TODO voir s'il ne faut pas enlever la condition isWater maintenant que les vertex d'ile sont aussi dans la map (erreur NullPointer dans pathFinding)
			if (coordX >= 0 && coordX < 15 && coordY >= 0 && coordY < 15) {
				if (mappy[coordX][coordY].isWater()) {
					list.add(mappy[coordX][coordY]);
				}
			}
		}

		// TODO voir pour mélanger l'ordre dans la liste
		Collections.shuffle(list);
		return list;
	}

	/**
	 * recherche UN des chemins les plus long dans la map (non pondéré)
	 * 
	 * @return ArrayList<Vertex>
	 */
	public ArrayList<Vertex> getLongestPath() {
		ArrayList<Vertex> longestPath = new ArrayList<Vertex>();
		ArrayList<Vertex> path;


		for (Vertex[] line : mappy) {
			for (Vertex vertex : line) {
				if (vertex.isWater()) {
					path = this.findLongestPathFrom(vertex);
					this.clearVertex();
					
					longestPath = longestPath.size() > path.size() ? longestPath : path;
				}
			}
		}

		return longestPath;
	}

	/**
	 * remets à false le boolean isVisited de tous les Vertex de la map
	 * réinitialise le chemin pris par le sous-marin sur la carte
	 */
	public void clearVertex() {

		for (Vertex[] line : mappy) {
			for (Vertex vertex : line) {
				if (vertex.isWater())
					vertex.setVisited(false);
			}
		}
	}
	
	
	/**
	 * remets à false le boolean isPathVisited de tous les Vertex de la map
	 * réinitialise le chemin pris par le sous-marin pour le pathFinding
	 */
	public void clearPathVertex() {

		for (Vertex[] line : mappy) {
			for (Vertex vertex : line) {
				vertex.setPathVisited(false);
			}
		}
	}

	/**
	 * supprime les parents d'un sommet
	 */
	public void clearParent() {

		for (Vertex[] line : mappy) {
			for (Vertex vertex : line) {
				if (vertex.isWater())
					vertex.clearParent();
			}
		}
	}

	/**
	 * Retourne un des plus longs chemins depuis le Vertex fourni
	 * nécessite de clear les isPathVisited après
	 * 
	 * @param v le Vertex de départ
	 * @return ArrayList<Vertex> un chemin des plus longs
	 */
	public ArrayList<Vertex> findLongestPathFrom(Vertex v) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		v.setPathVisited(true);
		for (Vertex vertex : this.getLinkedVertex(v)) {
			if (!vertex.isPathVisited() && !vertex.isVisited()) {
				ArrayList<Vertex> newPath = findLongestPathFrom(vertex);

				if (newPath.size() > path.size() - 1) {
					path = newPath;
					path.add(v);
				}
			}
		}

		if (path.size() == 0) {
			path.add(v);
		}

		return path;
	}
	
	public ArrayList<Vertex> getLongestPathFrom(Vertex v) {
		this.clearPathVertex();
		ArrayList<Vertex> path = findLongestPathFrom( v);
		

		return path;
	}
	

	/**
	 * fonction spécifique pour Ocean of Code
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public ArrayList<Vertex> pathFinding(int x1, int y1, int x2, int y2) {
		Vertex v1 = this.mappy[x1][y1];
		Vertex v2 = this.mappy[x2][y2];

		if (!v1.isWater() || !v2.isWater())
			return null;

		return pathFinding(v1, v2);
	}

	public ArrayList<Vertex> pathFinding(Vertex v1, Vertex v2) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		this.clearPathVertex();
		this.clearParent();
		if(v2.toString().equalsIgnoreCase("4 0")) {
			System.err.println(this.mappy[6][1]);
			System.err.println(this.mappy[6][1].isPathVisited());
		}
		
		
		if (v2.isWater()) {
			ArrayList<Vertex> queue = new ArrayList<Vertex>();
			Vertex goal = null;
			
			v1.setPathVisited(true);
			
			queue.add(v1);
			if(v2.toString().equalsIgnoreCase("4 0")) {
				System.err.println("queue : ");
				System.err.println(queue);
			}
			
			while (queue.size() > 0 && goal == null) {
				Vertex vertex = queue.remove(0);
				if (vertex != v2) {
					if(v2.toString().equalsIgnoreCase("4 0")) {
						System.err.println("vertex : " + vertex);
					}
					for (Vertex v : this.getLinkedVertex(vertex)) {
						if(v2.toString().equalsIgnoreCase("4 0")) {
							System.err.print(v + " ");
							System.err.println(v.isPathVisited());
						}
						if (!v.isPathVisited()) {
							v.setPathVisited(true);
							v.setParent(vertex);
							queue.add(v);
						}
						if(v2.toString().equalsIgnoreCase("4 0")) {
							System.err.println("queue : ");
							System.err.println(queue);
						}
					}
				} else {
					goal = vertex;
				}
			}
			
			do {
				path.add(goal);
				System.err.println(goal);
				// TODO voir pour les problèmes de nullPointerException lors de recherche pour tir de torpille, voir les appels de la fonction
				goal = goal.getParent();
			} while (goal != null);
			System.err.println("remise à 0 pathVisited");
		}

		return path;
	}

	/**
	 * Retourne une liste de chemin depuis le Vertex fourni
	 * 
	 * @param v le Vertex de départ
	 * @return ArrayList<ArrayList<Vertex>>
	 */
	public ArrayList<ArrayList<Vertex>> getAllPaths(Vertex v) {
		ArrayList<ArrayList<Vertex>> newPath = new ArrayList<ArrayList<Vertex>>();

		v.setPathVisited(true);

		for (Vertex vertex : this.getLinkedVertex(v)) {
			if (!vertex.isPathVisited()) {

				for (ArrayList<Vertex> list : getAllPaths(vertex)) {
					newPath.add(list);
				}
			}
		}

		for (ArrayList<Vertex> list : newPath) {
			list.add(v);
		}

		if (newPath.size() == 0) {
			ArrayList path = new ArrayList<Vertex>();
			path.add(v);
			newPath.add(path);
		}

		return newPath;
	}
}
