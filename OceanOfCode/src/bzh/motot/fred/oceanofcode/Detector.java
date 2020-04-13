package bzh.motot.fred.oceanofcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bzh.motot.fred.pathfinding.MatrixMap;
import bzh.motot.fred.pathfinding.Vertex;
import bzh.motot.fred.Util;
import bzh.motot.fred.oceanofcode.*;

public class Detector {
	private static final int ISLAND = -1;
	private static final int WATER = 0;
	private static final int ENEMY = 1;
	private static final int[][] TORPEDO_FROM = { { 0, 0 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },
			{ 0, 1 }, { 0, -1 }, { 2, -2 }, { 2, -1 }, { 2, 0 }, { 2, 1 }, { 2, 2 }, { -2, -2 }, { -2, -1 }, { -2, 0 },
			{ -2, 1 }, { -2, 2 }, { 1, 2 }, { 0, 2 }, { -1, 2 }, { 1, -2 }, { 0, -2 }, { -1, -2 }, { 3, 1 }, { 3, 0 },
			{ 3, -1 }, { -3, 1 }, { -3, 0 }, { -3, -1 }, { 1, 3 }, { 0, 3 }, { -1, 3 }, { 1, -3 }, { 0, -3 },
			{ -1, -3 }, { 4, 0 }, { -4, 0 }, { 0, 4 }, { 0, -4 } };
	private static final int[][] EXPLOSION_FROM = { { 0, 0 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },
			{ 0, 1 }, { 0, -1 } };

	private MatrixMap map;
	private Vertex[][] mappy;
	private int enemyLife;
	private ArrayList<ArrayList<int[]>> enemyMovements = new ArrayList<ArrayList<int[]>>();
	private int enemySilence = 0;

	public Detector(MatrixMap map) {
		this.map = map;
		this.mappy = map.getMappy();
		this.enemyLife = 6;
		enemyMovements.add(new ArrayList<int[]>());
	}

	// TODO Reste à gérer le cas de 2 tir de torpille
	public void setEnemyActions(String actionsDetected, String myOrders, int oppLife) {
		ArrayList<String[]> actions = splitActions(actionsDetected);
		ArrayList<String[]> myActions = splitActions(myOrders);

		for (String[] action : myActions) {
			switch (action[0]) {
			case "TORPEDO":
				System.err.printf("tir de torpille de ma part pris en compte : %s %s %n", action[1], action[2]);
				setPositionFromExplosion(myOrders, actionsDetected, oppLife);
				break;
			default:
				break;
			}
		}

		for (String[] action : actions) {
			switch (action[0]) {
			case "MOVE":
				this.setPositionFromMove(action[1]);
				System.err.println("mouvement pris en compte");
				break;
			case "TORPEDO":
				this.setPositionFromTorpedo(Integer.parseInt(action[1]), Integer.parseInt(action[2]));
				setPositionFromExplosion(myOrders, actionsDetected, oppLife);
				System.err.printf("tir de torpille ennemi pris en compte : %s %s %n", action[1], action[2]);
				break;
			case "SURFACE":
				this.setPositionFromSurface(Integer.parseInt(action[1]));
				this.enemyLife--;
				break;
			case "SILENCE":
				// à modifier bien sûr
				this.setPositionFromSilence();
//				for (Vertex[] vertexs : mappy) {
//					for(Vertex v : vertexs) {
//						if (v.isWater())
//							v.setEnemy(true);
//					}
//				}
				break;
			default:
				break;
			}
		}
		this.enemyLife = oppLife;
		showMap();
	}

	private ArrayList<String[]> splitActions(String actionsDetected) {
		ArrayList<String[]> actions = new ArrayList<String[]>();

		for (String action : actionsDetected.split("\\|")) {
			actions.add(action.split(" "));
		}

		return actions;
	}

	private void setPositionFromSilence() {
		

		// TODO après un appel à listPossibleMovesFromPreviousOnes() empêcher le test
		// d'une position plus loin dans la même direction si la position testée n'est
		// pas possible

		List<int[]> possiblePositions = new ArrayList<int[]>();
		int[] coordTest;
		boolean isPositionPossible;
		int index;
		// Pour chaque direction N S E W
		for (int[] direction : map.DIRECTIONS) {
			isPositionPossible = true;
			index = 1;
			// Tant que l'ennemi peut se déplacer sur la case tester la position suivante jusqu'à 4 cases
			while(isPositionPossible && index <= 4) {
				coordTest = Util.arrayMultiply(direction, index);
				
				isPositionPossible = isPositionPossiblesFromPreviousOnes(coordTest);
				if (isPositionPossible) {
					possiblePositions.add(coordTest);
				}
				index++;
			}
		}
		
		System.err.println("L'ennemi peut être sur les positions relatives suivantes :");
		for (int[] pos : possiblePositions) {
			System.err.println(pos[0] + " " + pos[1]);
		}
		
		// TODO ajouter les positions du silence
		// pour toutes les positions dans la croix NS/EW
		// tant qu'on ne rencontre pas une limite de direction
		// pour toutes les positions potentielles de l'ennemi sur la carte
		// je crée une liste de positions potentiels supplémentaires à ajouter à la
		// liste des positions potentiels
		
		
		this.positionsToMapAddFromSilence(possiblePositions);
		
	}

	/**
	 * Depuis les coordonnées des chemins précédents Vérifie si le déplacement vers
	 * la position relative fournie est possible
	 * 
	 * @param  coordTest
	 * @return boolean true si la position est disponible, false s'il ne peut pas se
	 *         trouver sur cette position
	 */
	private boolean isPositionPossiblesFromPreviousOnes(int[] coordTest) {
		boolean result = true;
		int[] coord = new int[2];
		int index;

		// TODO Revoir le système après le 1er silence probablement
		
		// pour chaque list des mouvements ennemis entre 2 silences 
		// à voir si ça ne bloque pas plus tard
		for (ArrayList<int[]> listMovements : enemyMovements) {
			coord[0] = 0;
			coord[1] = 0;
			index = listMovements.size() - 1;
			// tant que je ne trouve pas de position relative identique à la position testée dans les mouvements
			while (result && index > 0) {
				coord[0] += listMovements.get(index)[0];
				coord[1] += listMovements.get(index)[1];
				
				if (Arrays.equals(coord, coordTest)) {
					result = false;
				}
				index--;
			}
		}

		return result;
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
				System.err.println("appel pathFinding pour setPositionFromTorpedo : " + x + " " + y +" "+ x2 + " " + y2);
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
	 * réalise l'intersection des positions ennemies sur la carte avec la lsite des
	 * positions fournies (parmi les positions déjà connues, ne garde que celles
	 * fournies)
	 * 
	 * @param positions
	 */
	private void positionsToMapCross(ArrayList<Vertex> positions) {
		for (int y = 0; y < mappy.length; y++) {
			for (int x = 0; x < mappy.length; x++) {
				Vertex v = mappy[x][y];

				if (v.isEnemy()) {
					if (positions.contains(v)) {
						v.setEnemy(true);
					} else {
						v.setEnemy(false);
					}
				}
			}
		}
		showMap();
	}

	/**
	 * Exclus des positions ennemies déjà connues la liste des positions fournies
	 * 
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
	
	/**
	 * Ajoute les positions relatives fournies à toutes les positions potentielles 
	 * puis les ajoute aux positions potentielles
	 * @param positions une liste de positions relatives
	 */
	private void positionsToMapAddFromSilence(List<int[]> positions) {
		List listEnemyPositions = map.getEnemyPositions();
		HashSet<Vertex> positionsToAdd = new HashSet<Vertex>(listEnemyPositions);
		int coordX;
		int coordY;
		
		
		// TODO empêcher le passage par une île
		for (Vertex vertex : map.getEnemyPositions()) {
			for (int[] pos : positions) {
				coordX = vertex.getCoord()[0] + pos[0];
				coordY = vertex.getCoord()[1] + pos[1];
				try {
					if (mappy[coordX][coordY].isWater())
						positionsToAdd.add(mappy[coordX][coordY]);
				}catch(ArrayIndexOutOfBoundsException e){}
			}
		}
		
//		System.err.println("Liste des nouvelles positions potentielles");
//		
//		for (Vertex vertex : positionsToAdd) {
//			System.err.println(vertex.getCoord()[0] + " " + vertex.getCoord()[1]);
//		}
		
		this.showMap();
		
		for (Vertex vertex : positionsToAdd) {
			vertex.setEnemy(true);
		}
	}

	public void showMap() {
		Vertex[][] carte = map.getMappy();
		System.err.println("  012345678901234");
		for (int y = 0; y < carte.length; y++) {
			System.err.printf("%02d", y);
			for (int x = 0; x < carte.length; x++) {
				Vertex v = carte[x][y];
				if (v.isEnemy()) {
					System.err.print('x');
				} else if (v.isWater()) {
					System.err.print('.');
				} else {
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
		int[] movement = new int[2];
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

		movement[0] = directionX;
		movement[1] = directionY;
		enemyMovements.get(enemySilence).add(movement);

		for (int y = 0; y < mappy.length; y++) {
			for (int x = 0; x < mappy[0].length; x++) {

				if (mappy[x][y].isWater()) {

					fromX = x + directionX;
					fromY = y + directionY;
					if (fromX >= 0 && fromX < 15 && fromY >= 0 && fromY < 15) {

						if (positions.contains(mappy[fromX][fromY])) {
							mappy[x][y].setEnemy(true);
						} else {
							mappy[x][y].setEnemy(false);
						}
					} else {
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

	private void setPositionFromExplosion(String myOrders, String oppOrders, int enemyLife) {
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

				if (enemyLife == this.enemyLife - 1) {
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
				} else {
					System.err.println("adv perd 2 pv");
					positions.add(mappy[x][y]);
					System.err.println("liste des positions à croisées : ");
					System.err.println(positions);
				}
				positionsToMapCross(positions);
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
				System.err.println("explosion prise en compte");
			}
		}

	}
}
