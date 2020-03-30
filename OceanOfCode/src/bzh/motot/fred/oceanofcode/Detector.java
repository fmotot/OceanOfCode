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
	private static Vertex[][] mappy;
//	private ArrayList<int[]> enemyPositions = new ArrayList<int[]>();
//	private int[][] enemyPositionsMap = new int[15][15];
	private int enemyLife;

	public Detector(MatrixMap map) {
		this.map = map;
		this.mappy = map.getMappy();
//		for (int i = 0; i < enemyPositionsMap.length; i++) {
//			for (int j = 0; j < enemyPositionsMap[0].length; j++) {
//				this.map.getVertex(j, i);
//				if (this.map.getVertex(j, i) == null) {
//					enemyPositionsMap[j][i] = ISLAND;
//				} else {
//					enemyPositionsMap[j][i] = ENEMY;
//					enemyPositions.add(new int[] { i, j });
//				}
//			}
//		}
		
		this.enemyLife = 6;
	}

	public void setEnemyAction(String actionsDetected) {
		ArrayList<String[]> actions = splitActions(actionsDetected);

		for (String[] action : actions) {

			switch (action[0]) {
			case "MOVE":
				this.setPositionFromMove(action[1]);
				System.err.println("mouvement pris en compte");
				break;
			case "TORPEDO":
				this.setPositionFromTorpedo(Integer.parseInt(action[1]), Integer.parseInt(action[2]));
				System.err.println("tir de torpille pris en compte");
				break;
			case "SURFACE":
				this.setPositionFromSurface(Integer.parseInt(action[1]));
				this.enemyLife--;
				break;
			case "SILENCE":
				// à modifier bien sûr
				
				for (Vertex[] vertexs : mappy) {
					for(Vertex v : vertexs) {
						if (v.isWater())
							v.setEnemy(true);
					}
				}
				break;
			default:
				break;
			}
		}
		
		showMap();
	}

	private ArrayList<String[]> splitActions(String actionsDetected) {
		ArrayList<String[]> actions = new ArrayList<String[]>();

		for (String action : actionsDetected.split("\\|")) {
			actions.add(action.split(" "));
		}

		return actions;
	}

	private void setPositionFromSurface(int pos) {
		int minX = ((pos - 1) % 3) * 5;
		int minY = ((pos - 1) / 3) * 5;
		ArrayList<Vertex> positions = new ArrayList<Vertex>();

		for (int x = minX; x < minX + 5; x++) {
			for (int y = minY; y < minY + 5; y++) {
				positions.add(mappy[x][y]);
			}
		}
		this.positionsToMapCross(positions);
	}

	private void setPositionFromTorpedo(int x, int y) {
		ArrayList<Vertex> positions = new ArrayList<Vertex>();

		for (int target = 0; target < TORPEDO_FROM.length; target++) {
			// si le path entre x/y et la cible < 6 alors c'est un cible potentiel
			int x2 = x + TORPEDO_FROM[target][0];
			int y2 = y + TORPEDO_FROM[target][1];
			if (x2 >= 0 && x2 < 15 && y2 >= 0 && y2 < 15) {
				ArrayList<Vertex> path = map.pathFinding(x, y, x2, y2);
				if (path != null) {
					if (path.size() < 6) {

						positions.add(path.get(0));
					}
				}

			}
		}

		this.positionsToMapCross(positions);
	}

	/**
	 * réalise l'intersection des positions ennemies sur la carte avec la lsite des positions fournies
	 * (parmi les positions déjà connues, ne garde que celles fournies)
	 * @param positions
	 */
	private void positionsToMapCross(ArrayList<Vertex> positions) {
		for (int y = 0; y < mappy.length; y++) {
			for (int x = 0; x < mappy.length; x++) {
				Vertex v = mappy[x][y];
				
				if (v.isEnemy()) {
					if (positions.contains(v)) {
						v.setEnemy(true);
					}
					else {
						v.setEnemy(false);
					}
				}
			}
		}
		showMap();
	}

	/**
	 * Exclus des positions ennemies déjà connues la liste des positions fournies
	 * @param positions
	 */
	private void positionsToMapExclude(ArrayList<Vertex> positions) {
		
		for (Vertex v : positions) {
			if (v.isWater()) {
				System.err.print(v.getCoord()[0] + " " + v.getCoord()[1] + ", ");
				mappy[v.getCoord()[0]][v.getCoord()[1]].setEnemy(false);
				System.err.print(v);
			}
		}
		System.err.println();

	}

	public void showMap() {
		Vertex[][] carte = map.getMappy();
		for (int y = 0; y < carte.length; y++) {
			for (int x = 0; x < carte.length; x++) {
				Vertex v = carte[x][y];
				 if (v.isEnemy()) {
					System.err.print('●');
				}
				else if (v.isWater()) {
					System.err.print(' ');
				}
				else {
					System.err.print('█');
				}
			}
			System.err.println();
		}
		
		// affiche la liste des positions ennemies potentielles
		System.err.println("liste des positions ennemies potentielles :");
		System.err.println(map.getEnemyPositions());
	}

	/**
	 * Déplace les positions ennemies connues dans la direction fournie
	 * 
	 * @param direction
	 */
	private void setPositionFromMove(String direction) {
		int directionX = 0;
		int directionY = 0;
		int fromX = 0;
		int fromY = 0;
		ArrayList<Vertex> positions = map.getEnemyPositions();
		

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

		for (int y = 0; y < mappy.length; y++) {
			for (int x = 0; x < mappy[0].length; x++) {
				
				if (mappy[x][y].isWater()) {
					
					fromX = x + directionX;
					fromY = y + directionY;
					if (fromX >= 0 && fromX < 15 && fromY >= 0 && fromY < 15) {
						
						if (positions.contains(mappy[fromX][fromY])) {
							mappy[x][y].setEnemy(true);
						}
						else {
							mappy[x][y].setEnemy(false);
						}
					} 
					else {
						mappy[x][y].setEnemy(false);
					}
				}
			}
		}
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

	public void setPositionFromExplosion(String myOrders, String oppOrders, int enemyLife) {
		String[] oppTorpedo = null;
		String[] myTorpedo = null;

		ArrayList<String[]> oppActions = splitActions(oppOrders);
		ArrayList<String[]> myActions = splitActions(myOrders);

		oppTorpedo = this.getActionType("TORPEDO", oppActions);
		myTorpedo = this.getActionType("TORPEDO", myActions);

		if (oppTorpedo != null ^ myTorpedo != null) {
			ArrayList<Vertex> positions = new ArrayList<Vertex>();
			
			String[] torpedo = oppTorpedo != null ? oppTorpedo : myTorpedo;
			int x = Integer.parseInt(torpedo[1]);
			int y = Integer.parseInt(torpedo[2]);
			int coordX;
			int coordY;
			
			System.err.println("un seul tir de torpille détecté");
			if (enemyLife < this.enemyLife) {
				System.err.println("adv touché");
				
				if (enemyLife == this.enemyLife - 1 ) {
					
					System.err.println("adv perd 1 pv");
					for (int[] coord : EXPLOSION_FROM) {
						coordX = x + coord[0];
						coordY = y + coord[1];
						if (coordX >= 0 && coordX < 15 && coordY >= 0 && coordY < 15 && (coordX != x || coordY != y)) {
							System.err.print(coordX + " " + coordY + ", ");
							positions.add(mappy[coordX][coordY]);
						}
					}
					System.err.println();
				}
				else {
					System.err.println("adv perd 2 pv");
					positions.add(mappy[x][y]);
					System.err.println("liste des positions à croisées : ");
					System.err.println(positions);
				}
				positionsToMapCross(positions);
				this.enemyLife = enemyLife;
			} else {
				System.err.println("listes des positions potentielles à supprimer");
				for (int[] coord : EXPLOSION_FROM) {
					coordX = x + coord[0];
					coordY = y + coord[1];
					if (coordX >= 0 && coordX < 15 && coordY >= 0 && coordY < 15) {
						System.err.print(coordX + " " + coordY + ", ");
						positions.add(mappy[coordX][coordY]);
					}
				}
				System.err.println();
				positionsToMapExclude(positions);
			}
		}

	}
}
