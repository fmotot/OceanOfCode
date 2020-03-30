package bzh.motot.fred.oceanofcode;

import java.util.ArrayList;
import java.util.Random;

import bzh.motot.fred.pathfinding.MatrixMap;
import bzh.motot.fred.pathfinding.Vertex;

public class Submarine {
	private static final int[][] AUTHORIZED_TARGET = { { 2, -2 }, { 2, -1 }, { 2, 0 }, { 2, 1 }, { 2, 2 },

			{ -2, -2 }, { -2, -1 }, { -2, 0 }, { -2, 1 }, { -2, 2 },

			{ 1, 2 }, { 0, 2 }, { -1, 2 },

			{ 1, -2 }, { 0, -2 }, { -1, -2 },

			{ 3, 1 }, { 3, 0 }, { 3, -1 },

			{ -3, 1 }, { -3, 0 }, { -3, -1 },

			{ 1, 3 }, { 0, 3 }, { -1, 3 },

			{ 1, -3 }, { 0, -3 }, { -1, -3 },

			{ 4, 0 }, { -4, 0 }, { 0, 4 }, { 0, -4 } };

	private static MatrixMap map;
	private static Random random = new Random();
	private static Detector enemyDetector;
	private static ArrayList<Vertex> path;
	private static Vertex position;
	private static int myLife;
	private static int oppLife;
	private static String myOrders = "NA";
	private static String oppOrders = "NA";

	public Submarine(MatrixMap map) {
		this.map = map;
		this.path = map.getLongestPath();
		System.err.println("longueur du chemin trouvé : " + path.size());
		System.err.println(path);
		this.enemyDetector = new Detector(this.map);
		this.myLife = 6;
		this.oppLife = 6;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param myLife
	 * @param oppLife
	 * @param torpedoCooldown
	 * @param sonarCooldown
	 * @param silenceCooldown
	 * @param mineCooldown
	 * @param sonarResult
	 * @param opponentOrders
	 * @return
	 */
	public String nextAction(int x, int y, int myLife, int oppLife, int torpedoCooldown, int sonarCooldown,
			int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders) {
		// System.err.println("x : " + x);
        // System.err.println("y : " + y);
        System.err.println("myLife : " + myLife);
        System.err.println("oppLife : " + oppLife);
        // System.err.println("torpedoCooldown : " + torpedoCooldown);
        // System.err.println("sonarCooldown : " + sonarCooldown);
        // System.err.println("silenceCooldown : " + silenceCooldown);
        // System.err.println("mineCooldown : " + mineCooldown);
        System.err.println("Opponent order : " + opponentOrders);
        
		// on vérifie si les points de vie ont été modifiés suite à un tir de torpille,
		// une explosion de mine

     // TODO tester s'il faut mettre this.oppOrders ou oppOrders
     		enemyDetector.setPositionFromExplosion(this.myOrders, opponentOrders, oppLife);

		// on modifie les positions potentiels de l'ennemi en fonction des ordres qu'il
		// a donnés
		enemyDetector.setEnemyAction(opponentOrders);
		System.err.println("ordres pris en compte");
		
		
		System.err.println("explosion prise en compte");

		String action = "";

		if (torpedoCooldown == 0) {
			Vertex target = this.launchTorpedoFrom(x, y, map.getEnemyPositions());

			if (target != null) {

				action += "TORPEDO " + target + "|";
			}
		}

		action += this.move(x, y);

		this.myOrders = action;
		this.oppOrders = opponentOrders;
		this.myLife = myLife;

		return action;
	}

	/**
	 * Sélectionne une coordonnée parmi la la liste des emplacements potentiels de l'ennemi à portée
	 * @param x		Coordonnées x du sous-marin
	 * @param y		Coordonnées y du sous-marin
	 * @param enemyPositions	Coordonnées potentielles du sous-marin ennemi
	 * @return		Coordonnées de la cible du tir
	 */
	private Vertex launchTorpedoFrom(int x, int y, ArrayList<Vertex> enemyPositions) {
		ArrayList<Vertex> potentialTargets = new ArrayList<Vertex>();
		ArrayList<Vertex> listTargets = new ArrayList<Vertex>();
		
		for (int target = 0; target < AUTHORIZED_TARGET.length; target++) {
			// si le path entre x/y et la cible < 6 alors c'est un cible potentiel
			int x2 = x + AUTHORIZED_TARGET[target][0];
			int y2 = y + AUTHORIZED_TARGET[target][1];
			if (x2 >= 0 && x2 < 15) {
				if (y2 >= 0 && y2 < 15) {
					ArrayList<Vertex> path = map.pathFinding(x, y, x2, y2);
					if (path != null) {
						if (path.size() < 6) {
							potentialTargets.add(path.get(0));
						}
					}
				}
			}
		}

		// tir ciblé
//		System.err.println("liste des emplacements potentiels :");
//		System.err.println(potentialTargets);
		for (Vertex vertex : potentialTargets) {
			if (enemyPositions.contains(vertex)) {
				listTargets.add(vertex);
			}
		}
		Vertex coord = listTargets.size() != 0 ? listTargets.get(random.nextInt(listTargets.size())) : null;
		System.err.println("liste des emplacements potentiels à portée :");
		System.err.println(listTargets);

		return coord;
	}

	private String move(int x, int y) {
		String direction = null;

		if (path.size() > 0) {
			position = path.remove(path.size() - 1);
			int[] nextCoord = position.getCoord();
			int nextX = nextCoord[0];
			int nextY = nextCoord[1];

			if (nextX != x) {
				if (nextX > x) {
					direction = "E";
				} else {
					direction = "W";
				}
			}

			if (nextY != y) {
				if (nextY > y) {
					direction = "S";
				} else {
					direction = "N";
				}
			}
			return "MOVE " + direction + " TORPEDO";
		} else {
			path = map.pathFindingLongest(position);
			path.remove(path.size() - 1);
			map.clearVertex();
			return "SURFACE";
		}

	}
	
	
	/**
	 * TEST
	 * permet de remplacer le path déjà calculé pour forcer le cheminement
	 * @param path
	 */
	public void setPath(ArrayList<Vertex> path) {
		this.path = path;
	}

	/**
	 * retour la chaine de caractère des coordonnées de départ
	 * @return
	 */
	public String firstCoord() {
//		System.err.print("cheminement calculé : ");
//		System.err.println(path);
		return this.path.remove(path.size() - 1).toString();
	}
}
