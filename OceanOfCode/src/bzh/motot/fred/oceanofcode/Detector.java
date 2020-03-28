package bzh.motot.fred.oceanofcode;

import java.util.ArrayList;
import java.util.Iterator;

import bzh.motot.fred.pathfinding.MatrixMap;
import bzh.motot.fred.pathfinding.Vertex;

import bzh.motot.fred.oceanofcode.*;

public class Detector {
	private final int ISLAND = -1;
	private final int WATER = 0;
	private final int ENEMY = 1;
	private final int[][] TORPEDO_FROM = { { 0, 0 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },
			{ 0, 1 }, { 0, -1 }, { 2, -2 }, { 2, -1 }, { 2, 0 }, { 2, 1 }, { 2, 2 }, { -2, -2 }, { -2, -1 }, { -2, 0 },
			{ -2, 1 }, { -2, 2 }, { 1, 2 }, { 0, 2 }, { -1, 2 }, { 1, -2 }, { 0, -2 }, { -1, -2 }, { 3, 1 }, { 3, 0 },
			{ 3, -1 }, { -3, 1 }, { -3, 0 }, { -3, -1 }, { 1, 3 }, { 0, 3 }, { -1, 3 }, { 1, -3 }, { 0, -3 },
			{ -1, -3 }, { 4, 0 }, { -4, 0 }, { 0, 4 }, { 0, -4 } };
	private final int[][] EXPLOSION_FROM = { { 0, 0 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },
			{ 0, 1 }, { 0, -1 } };

	private static MatrixMap map;
	private ArrayList<int[]> enemyPositions = new ArrayList<int[]>();
	private int[][] enemyPositionsMap = new int[15][15];

	public Detector(MatrixMap map) {
		this.map = map;

		for (int i = 0; i < enemyPositionsMap.length; i++) {
			for (int j = 0; j < enemyPositionsMap[0].length; j++) {
				this.map.getVertex(j, i);
				if (this.map.getVertex(j, i) == null) {
					enemyPositionsMap[j][i] = ISLAND;
				} else {
					enemyPositionsMap[j][i] = ENEMY;
					enemyPositions.add(new int[] { i, j });
				}
			}
		}
	}

	public void setEnemyAction(String actionsDetected) {
		ArrayList<String[]> actions = splitActions(actionsDetected);

		for (String[] action : actions) {

			switch (action[0]) {
			case "MOVE":
				this.setPositionFromMove(action[1]);
				break;
			case "TORPEDO":
				this.setPositionFromTorpedo(Integer.parseInt(action[1]), Integer.parseInt(action[2]), map);
				break;
			case "SURFACE":
				this.setPositionFromSurface(Integer.parseInt(action[1]));
				break;
			default:
				break;
			}
		}
	}

	private ArrayList<String[]> splitActions(String actionsDetected) {
		ArrayList<String[]> actions = new ArrayList<String[]>();

		for (String action : actionsDetected.split("\\|")) {
			actions.add(action.split(" "));
		}

		return actions;
	}

	public ArrayList<Vertex> getEnemyPositions() {
		ArrayList<Vertex> positions = new ArrayList<Vertex>();
		Vertex v;
		for (int[] pos : enemyPositions) {
			v = this.map.getVertex(pos[0], pos[1]);
			if (v != null) {
				positions.add(v);
			}
		}

		return positions;
	}

	private void setPositionFromSurface(int pos) {
		int minX = ((pos - 1) % 3) * 5;
		int minY = ((pos - 1) / 3) * 5;
		enemyPositions = new ArrayList<int[]>();

		for (int x = minX; x < minX + 5; x++) {
			for (int y = minY; y < minY + 5; y++) {
				if (enemyPositionsMap[x][y] == ENEMY) {
					enemyPositions.add(new int[] { x, y });
				}
			}
		}
		this.includePositionsToMap();
	}

	private void setPositionFromTorpedo(int x, int y, MatrixMap map) {
		ArrayList<Vertex> potentialpositions = new ArrayList<Vertex>();
		enemyPositions = new ArrayList<int[]>();

		for (int target = 0; target < TORPEDO_FROM.length; target++) {
			// si le path entre x/y et la cible < 6 alors c'est un cible potentiel
			int x2 = x + TORPEDO_FROM[target][0];
			int y2 = y + TORPEDO_FROM[target][1];
			if (x2 >= 0 && x2 < 15 && y2 >= 0 && y2 < 15) {
				ArrayList<Vertex> path = map.pathFinding(x, y, x2, y2);
				if (path != null) {
					if (path.size() < 6) {

						int[] coord = path.get(0).getCoord();
						if (enemyPositionsMap[coord[0]][coord[1]] == ENEMY) {
							enemyPositions.add(coord);
						}
					}
				}

			}
		}

		this.includePositionsToMap();
	}

	private void includePositionsToMap() {
		for (int i = 0; i < enemyPositionsMap.length; i++) {
			for (int j = 0; j < enemyPositionsMap[0].length; j++) {
				if (enemyPositionsMap[j][i] != ISLAND) {
					enemyPositionsMap[j][i] = WATER;
				}
			}
		}

		for (int[] pos : enemyPositions) {
			enemyPositionsMap[pos[0]][pos[1]] = ENEMY;
		}
		showMap();
	}

	private void excludePositionsToMap(ArrayList<int[]> positions) {
		
		for (int[] coord : positions) {
			System.err.print(coord[0] + " " + coord[1] + ", ");
			enemyPositionsMap[coord[0]][coord[1]] = WATER;
			System.err.print(enemyPositionsMap[coord[0]][coord[1]]);
		}
		System.err.println();

		for (int y = 0; y < enemyPositionsMap.length; y++) {
			for (int x = 0; x < enemyPositionsMap[0].length; x++) {
				if (enemyPositionsMap[x][y] == ENEMY) {
					enemyPositions.add(new int[] { x, y });
				}
			}
		}
	}

	public void showMap() {
		for (int y = 0; y < enemyPositionsMap.length; y++) {
			for (int x = 0; x < enemyPositionsMap[0].length; x++) {
				char c = 'A';
				switch (enemyPositionsMap[x][y]) {
				case -1:
					c = '█';
					break;
				case 0:
					c = ' ';
					break;
				case 1:
					c = '●';
				}
				System.err.print(c);
			}
			System.err.println();
		}
		
		// affiche la liste des positions ennemies potentiels si moins de 50
		if (enemyPositions.size() < 50) {
			for (int[] e : enemyPositions) {
				System.err.print(e[0] + " " + e[1] + ", ");
			}
			System.err.println();
		}
	}

	private void setPositionFromMove(String direction) {
		int directionX = 0;
		int directionY = 0;
		int fromX = 0;
		int fromY = 0;

		enemyPositions = new ArrayList<int[]>();

		switch (direction) {
		case "N":
			directionY += 1;
			break;
		case "S":
			directionY += -1;
			break;
		case "W":
			directionX += 1;
			break;
		case "E":
			directionX += -1;
			break;

		default:
			break;
		}

		for (int y = 0; y < enemyPositionsMap.length; y++) {
			for (int x = 0; x < enemyPositionsMap[0].length; x++) {

				if (enemyPositionsMap[x][y] != ISLAND) {

					fromX = x + directionX;
					fromY = y + directionY;
					if (fromX >= 0 && fromX < 15 && fromY >= 0 && fromY < 15) {

						if (enemyPositionsMap[fromX][fromY] == ENEMY) {
							enemyPositions.add(new int[] { x, y });
						}
					}
				}
			}
		}

		this.includePositionsToMap();
	}

	/**
	 * retourne les informations d'une action précisée en param
	 * 
	 * @param typeAction  l'action recherchée
	 * @param ListActions les actions réalisées
	 * @return l'action et ses informations ou null si cette action n'est pas
	 *         réalisée dans la liste
	 */
	private String[] getActionType(String typeAction, ArrayList<String[]> ListActions) {
		String[] actionType = null;
		for (String[] action : ListActions) {
			if (action[0].contentEquals(typeAction)) {
				actionType = action;
			}
		}

		return actionType;
	}

	public void setPositionFromExplosion(String myOrders, String oppOrders, int oppLifePrevious, int oppLife) {
		String[] oppTorpedo = null;
		String[] myTorpedo = null;

		ArrayList<String[]> oppActions = splitActions(oppOrders);
		ArrayList<String[]> myActions = splitActions(myOrders);

		oppTorpedo = this.getActionType("TORPEDO", oppActions);
		myTorpedo = this.getActionType("TORPEDO", myActions);

		if (oppTorpedo != null ^ myTorpedo != null) {
			
			String[] torpedo = oppTorpedo != null ? oppTorpedo : myTorpedo;
			int x = Integer.parseInt(torpedo[1]);
			int y = Integer.parseInt(torpedo[2]);

			if (oppLife < oppLifePrevious) {

			} else {
				ArrayList<int[]> positions = new ArrayList<int[]>();
				int coordX;
				int coordY;
				for (int[] coord : EXPLOSION_FROM) {
					coordX = x + coord[0];
					coordY = y + coord[1];
					if (coordX >= 0 && coordX < 15 && coordY >= 0 && coordY < 15) {
						System.err.print(coordX + " " + coordY + ", ");
						positions.add(new int[] { coordX, coordY });
					}
				}
				System.err.println();

				this.excludePositionsToMap(positions);
			}
		}

	}
}
