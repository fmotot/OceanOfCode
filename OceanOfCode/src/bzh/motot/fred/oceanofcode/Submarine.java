package bzh.motot.fred.oceanofcode;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import bzh.motot.fred.Util;
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
	private static Random random = new Random();

	private MatrixMap map;
	private Detector enemyDetector;
	private Detector myDetector;
	private List<Vertex> path;
	private Vertex position;
	private String myOrders = "NA";
	private String oppOrders = "NA";
	
	private int myLife;
	private int oppLife;
	private int torpedoCooldown;
	private int sonarCooldown;
	private int silenceCooldown;
	private int mineCooldown;

	public Submarine(MatrixMap map) {
		try {
			this.map = map.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
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
		
		this.torpedoCooldown = torpedoCooldown;
		this.sonarCooldown = sonarCooldown;
		this.silenceCooldown = silenceCooldown;
		this.mineCooldown = mineCooldown;
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

        
        
        /*
         * Pour l'ennemi
         * gérer ses actions dans l'ordre, 
         * tester l'explosion en même temps (ou presque) que l'endroit d'où il a pu tirer
         * 
         * tester l'explosion de ma torpille à ce moment-là si tir simultané ou avant(?) si tir unique de ma part 
         */
        
         
        // TODO modifier le système de détection lors de tir de torpille
        
//        enemyDetector.setPositionFromExplosion(this.myOrders, opponentOrders, oppLife);

		// on modifie les positions potentiels de l'ennemi en fonction des ordres qu'il
		// a donnés
		enemyDetector.setEnemyActions(opponentOrders, this.myOrders,oppLife);
		System.err.println("ordres pris en compte");
		
		
		

		String action = "";

		// TODO vérifier si tir de torpille possible après déplacement
		// TODO tester à partir de combien de tir de torpille on doit pouvoir tirer
		if (torpedoCooldown == 0) {
			ArrayList<Vertex> enemyPositions = map.getEnemyPositions();
			if (enemyPositions.size() < 30) {
				
				Vertex target = this.launchTorpedoFrom(x, y, enemyPositions);
				
				if (target != null) {
					
					action += "TORPEDO " + target + "|";
				}
			}
		}

		action += this.getMovement(x, y);
		this.map.getVertex(x, y).setVisited(true);
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
					System.err.println("appel pathFinding pour launchTorpedoFrom : " + x + " " + y +" "+ x2 + " " + y2);
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
	
	private String getSilenceMove(int x, int y, Vertex v) {
		String move = "SILENCE " + this.getDirectionFromNextCoord(x, y, v.getCoord()) + " ";
		
		int nextX = v.getCoord()[0];
		int nextY = v.getCoord()[1];

		if (nextX != x) {
			move += Math.abs(nextX - x);
		}
		else if (nextY != y) {
			move += Math.abs(nextY - y);
		}
		else {
			move += "0";
		}
		
		
		return move;
	}

	/**
	 * donne le prochain mouvement en fonction du chemin prévu
	 * @param x
	 * @param y
	 * @return
	 */
	private String getMovement(int x, int y) {
		String direction = null;
		String move = null;

		if (this.path.size() > 0) {
			if (this.torpedoCooldown > 1 && this.silenceCooldown == 0) {
				move = getSilence(x, y);
			}
			else {
				move = getMove(x, y);
			}
		} else {
			this.map.clearVertex();	
			this.map.clearPathVertex();
			// TODO a faire avant this.path = this.map.findLongestPathFrom(position); ? 
			this.path = this.map.getLongestPathFrom(position);
			System.err.print("NOUVEAU CHEMIN : ");
			System.err.println(this.path);
			this.path.remove(path.size() - 1);
			move = "SURFACE";
		}

		return move;
	}

	/**
	 * Récupère le meilleur emplacement possible où aller pour le silence
	 * si le nombre de possibilité de position pour le silence est inférieur à 4 on bouge normalement
	 * @param x
	 * @param y
	 * @return
	 */
	private String getSilence(int x, int y) {
		String move = null;
		System.err.println("                                                             on va faire SILENCE");
		Entry<Vertex, List<Vertex>> silenceTarget = this.getSilencePath(x, y);
		
		if (silenceTarget == null) {
			move = getMove(x, y);
		}
		else {
			move = getSilenceMove(x, y, silenceTarget.getKey());
			this.path = silenceTarget.getValue();
			this.position = this.path.remove(path.size() - 1);
			this.setVisitedFromTo(x, y, this.position, true);
			
		}
		return move;
	}

	private String getMove(int x, int y) {
		String direction;
		String move;
		this.position = this.path.remove(path.size() - 1);
		int[] nextCoord = this.position.getCoord();
		direction = getDirectionFromNextCoord(x, y, nextCoord);
		move =  "MOVE " + direction + " " + getPower();
		return move;
	}

	private String getDirectionFromNextCoord(int x, int y, int[] nextCoord) {
		String direction = "N";
		int nextX = nextCoord[0];
		int nextY = nextCoord[1];

		if (nextX != x) {
			if (nextX > x) {
				direction = "E";
			} else {
				direction = "W";
			}
		}
		else if (nextY != y) {
			if (nextY > y) {
				direction = "S";
			} else {
				direction = "N";
			}
		}
		
		return direction;
	}
	
	/**
	 * fourni une paire Vertex de départ / chemin pour le SILENCE
	 * @param x
	 * @param y
	 * @return
	 */
	private Entry<Vertex, List<Vertex>> getSilencePath(int x, int y) {
		Entry<Vertex, List<Vertex>> silenceTarget;
		// TODO permettre de choisir parmi plusieurs chemins si leurs longueurs se rapprochent (10% ?)
		
		List<Vertex> possibleMovements = getSilencePositions(x, y);
		
		if (possibleMovements.size() < 4) {
			silenceTarget = null;
		}
		else {
			System.err.println("Mouvement possible lors du silence : ");
			// trouver le chemin le plus long depuis chaque mouvements possibles du silence
			Map<Vertex, List<Vertex>> listLongestPath = new HashMap<Vertex, List<Vertex>>();
			for (Vertex v : possibleMovements) {
				// mettre temporairement à isVisited les vertex entre v et x,y pour éviter de fausser le pathfinding
				System.err.print(v + " ");
				this.setVisitedFromTo(x, y, v, true);
				listLongestPath.put(v, this.map.getLongestPathFrom(v));
				this.setVisitedFromTo(x, y, v, false);
				System.err.println(listLongestPath.get(v).size());
			}
			
			silenceTarget = listLongestPath.entrySet().stream().max((e1, e2) -> e1.getValue().size() > e2.getValue().size()?1:-1).get();
			
			System.err.println("Chemin le plus long depuis : " + silenceTarget.getKey());
			System.err.print("Chemin le plus long : " + silenceTarget.getValue());
			
		}
		
		return silenceTarget;
	}

	/**
	 * Fourni la liste des emplacements possibles pour faire un SILENCE 
	 * @param x
	 * @param y
	 * @return
	 */
	private List<Vertex> getSilencePositions(int x, int y) {
		List<Vertex> possibleMovements = new ArrayList<Vertex>();
		int[] coordTest;
		boolean isPositionPossible;
		int index;
		possibleMovements.add(this.map.getVertex(x, y));
		for (int[] dir : map.DIRECTIONS) {
			isPositionPossible = true;
			index = 1;
			// Tant que l'on peut se déplacer sur la case tester la position suivante jusqu'à 4 cases
			while(isPositionPossible && index <= 4) {
				coordTest = Util.arrayMultiply(dir, index);
				try {
					Vertex v = this.map.getMappy()[x + coordTest[0]][y + coordTest[1]];
					
					if (!v.isVisited() && v.isWater()) {
						possibleMovements.add(v);
					}
					else {
						// TODO Vérifier 
						isPositionPossible =false;
					}
				}catch (ArrayIndexOutOfBoundsException e) {}
				index++;
			}
		}
		return possibleMovements;
	}
	
	/**
	 * applique le paramètre isVisited de chaque Vertex rencontré 
	 * entre la position x,y et le vertex cible (position x,y incluse)
	 * 
	 *  Pour application temporaire de test de pathfinding pendant les silences
	 *  Pour application après silence sur le chemin emprunté
	 * 
	 * @param y
	 * @param x
	 * @param vertexTargeted
	 * @param isVisited
	 * @return
	 */
	private List<Vertex> setVisitedFromTo(int x, int y, Vertex vertexTargeted , boolean isVisited){
		List<Vertex> listVisited = new ArrayList<Vertex>();
		int nextX = vertexTargeted.getCoord()[0];
		int nextY = vertexTargeted.getCoord()[1];
		Vertex vertex;
		
		if (nextX != x) {
			if (nextX > x) {
				for(int mod = x;mod < nextX;mod++) {
					listVisited.add(getTempVisitedVertex(mod, y, isVisited));
				}
			} else {
				for(int mod = x;mod > nextX;mod--) {
					listVisited.add(getTempVisitedVertex(mod, y, isVisited));
				}
			}
		}
		else if (nextY != y) {
			if (nextY > y) {
				for(int mod = y;mod < nextY;mod++) {
					listVisited.add(getTempVisitedVertex(x, mod, isVisited));
				}
			} else {
				for(int mod = y;mod > nextY;mod--) {
					listVisited.add(getTempVisitedVertex(x, mod, isVisited));
				}
			}
		}
		
		
		return null;
	}

	private Vertex getTempVisitedVertex(int mod, int y, boolean isVisited) {
		Vertex vertex;
		vertex = this.map.getVertex(mod, y);
		vertex.setVisited(isVisited);
		return vertex;
	}
	
	
	/**
	 * gère les cooldowns d'action pour savoir dans lequel mettre la puissance
	 * @return
	 */
	private String getPower() {
		String power = "TORPEDO";
		
		if (this.torpedoCooldown == 0) {
			power = "SILENCE";
		}
		
		
		return power;
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
