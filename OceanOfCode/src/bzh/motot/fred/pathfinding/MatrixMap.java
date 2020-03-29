package bzh.motot.fred.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bzh.motot.fred.oceanofcode.*;

public class MatrixMap {
	public static final int[] NORTH = { 0, -1 };
	public static final int[] SOUTH = { 0, 1 };
	public static final int[] EAST = { 1, 0 };
	public static final int[] WEST = { -1, 0 };
	public static final int[][] DIRECTIONS = { NORTH, SOUTH, EAST, WEST };

//	private Map<Vertex, Object> map;
	private static final Vertex[][] MAPPY = new Vertex[15][15];

	/**
	 * Constructor
	 */
	public MatrixMap() {
//		map = new HashMap<Vertex, Object>();
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
			if (lineMap[x] == '.') {
				MAPPY[x][y] = new Vertex(x, y);

//				if (x > 0) {
//					if (lineMap[x - 1] == '.') {
//						this.addEdge(mappy[x - 1][y], mappy[x][y]);
//					}
//				}
//				if (y > 0) {
//					if (mappy[x][y - 1] != null) {
//						this.addEdge(mappy[x][y], mappy[x][y - 1]);
//					}
//				}
			}
		}
	}

	public void showMappy() {
		for (int i = 0; i < MAPPY.length; i++) {
			for (int j = 0; j < MAPPY[0].length; j++) {
				if (MAPPY[j][i] != null) {
					System.out.println(MAPPY[j][i]);
					System.out.println(this.getLinkedVertex(MAPPY[j][i]));
				} else {
					System.out.println("TERRE !");
				}
			}
		}
	}

	public void showVertex() {
//		for (Map.Entry<Vertex, Object> e : this.map.entrySet()) {
//			System.out.println(e.getKey());
//			System.out.println(this.getLinkedVertex(e.getKey()));
//		}
		for (int i = 0; i < MAPPY.length; i++) {
			Vertex[] vertexs = MAPPY[i];
			for (Vertex vertex : vertexs) {
				System.out.println(vertex);
				System.out.println(this.getLinkedVertex(vertex));
			}
		}
	}

//	public Vertex addVertex(Vertex v) {
//		map.put(v, new HashMap<Vertex, Object>());
//		return v;
//	}

	public Vertex getVertex(int x, int y) {
		return MAPPY[x][y];

	}

//	public void addEdge(Vertex v, Vertex v2, int weight) {
//
//		((HashMap) map.get(v)).put(v2, weight);
//		((HashMap) map.get(v2)).put(v, weight);
//	}
//
//
//	public void addEdge(Vertex v, Vertex v2) {
//		if (!this.map.containsKey(v))
//			this.addVertex(v);
//		if (!this.map.containsKey(v2))
//			this.addVertex(v2);
//		this.addEdge(v, v2, 1);
//	}

	/**
	 * Renvoie la liste des sommets liés au vertex "v"
	 * 
	 * @param v
	 * @return la liste des objets liés
	 */
	public ArrayList<Vertex> getLinkedVertex(Vertex v) {
		ArrayList<Vertex> list = new ArrayList<Vertex>();

//		for (Map.Entry<Vertex, Object> entry : ((Map<Vertex, Object>) map.get(v)).entrySet()) {
//			list.add(entry.getKey());
//		}
		for (int[] direction : DIRECTIONS) {
			int coordX = direction[0] + v.getCoord()[0];
			int coordY = direction[1] + v.getCoord()[1];

			if (coordX >= 0 && coordX < 15 && coordY >= 0 && coordY < 15) {
				if (MAPPY[coordX][coordY] != null) {
					list.add(MAPPY[coordX][coordY]);
				}
			}
		}

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

//		for (Map.Entry<Vertex, Object> entry : map.entrySet()) {
//
//			path = this.pathFindingLongest(entry.getKey());
//			this.clearVertex();
//
//			longestPath = longestPath.size() > path.size() ? longestPath : path;
//		}

		for (Vertex[] line : MAPPY) {
			for (Vertex vertex : line) {
				if (vertex != null) {
					path = this.pathFindingLongest(vertex);
					this.clearVertex();
					
					longestPath = longestPath.size() > path.size() ? longestPath : path;
				}
			}
		}

		return longestPath;
	}

	/**
	 * remets à false le boolean isVisited de tous les Vertex de la map
	 */
	public void clearVertex() {

		for (Vertex[] line : MAPPY) {
			for (Vertex vertex : line) {
				if (vertex != null)
					vertex.setPathVisited(false);
			}
		}
	}

	/**
	 * supprime les parents d'un sommet
	 */
	public void clearParent() {
//		for (Map.Entry<Vertex, Object> entry : map.entrySet()) {
//			entry.getKey().clearParent();
//		}

		for (Vertex[] line : MAPPY) {
			for (Vertex vertex : line) {
				if (vertex != null)
					vertex.clearParent();
			}
		}
	}

	/**
	 * Retourne un des plus longs chemins depuis le Vertex fourni
	 * 
	 * @param v le Vertex de départ
	 * @return ArrayList<Vertex> un chemin des plus longs
	 */
	public ArrayList<Vertex> pathFindingLongest(Vertex v) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		v.setPathVisited(true);
		for (Vertex vertex : this.getLinkedVertex(v)) {
			if (!vertex.isPathVisited()) {
				ArrayList<Vertex> newPath = pathFindingLongest(vertex);

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
		Vertex v1 = this.MAPPY[x1][y1];
		Vertex v2 = this.MAPPY[x2][y2];

		if (v1 == null || v2 == null)
			return null;

		return pathFinding(v1, v2);
	}

	public ArrayList<Vertex> pathFinding(Vertex v1, Vertex v2) {
		ArrayList<Vertex> queue = new ArrayList<Vertex>();
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		Vertex goal = null;

		v1.setPathVisited(true);

		queue.add(v1);

		while (queue.size() > 0 && goal == null) {
			Vertex vertex = queue.remove(0);
			if (vertex != v2) {
				for (Vertex v : this.getLinkedVertex(vertex)) {
					if (!v.isPathVisited()) {
						v.setPathVisited(true);
						v.setParent(vertex);
						queue.add(v);
					}
				}
			} else {
				goal = vertex;
			}
		}

		do {
			path.add(goal);
			goal = goal.getParent();
		} while (goal != null);

		this.clearVertex();
		this.clearParent();
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
