package bzh.motot.fred.longestpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.sound.midi.SysexMessage;

import bzh.motot.fred.oceanofcode.Detector;
import bzh.motot.fred.oceanofcode.Submarine;
import bzh.motot.fred.pathfinding.MatrixMap;
import bzh.motot.fred.pathfinding.Vertex;

public class Launcher {

	public static int turn = 0;
	public static int turnLimit;
	public static Random random = new Random();
	public static ArrayList<Vertex> path;
	public static final int[][] AUTHORIZED_TARGET = { { 2, -2 }, { 2, -1 }, { 2, 0 }, { 2, 1 }, { 2, 2 },

			{ -2, -2 }, { -2, -1 }, { -2, 0 }, { -2, 1 }, { -2, 2 },

			{ 1, 2 }, { 0, 2 }, { -1, 2 },

			{ 1, -2 }, { 0, -2 }, { -1, -2 },

			{ 3, 1 }, { 3, 0 }, { 3, -1 },

			{ -3, 1 }, { -3, 0 }, { -3, -1 },

			{ 1, 3 }, { 0, 3 }, { -1, 3 },

			{ 1, -3 }, { 0, -3 }, { -1, -3 },

			{ 4, 0 }, { -4, 0 }, { 0, 4 }, { 0, -4 } };
	public static MatrixMap map;
	
	
	public static int x = 0;
	public static int y = 0;
	public static int oppLife = 6;
	public static int myLife = 6;
	public static int torpedoCooldown = 3;
	public static int sonarCooldown = 3;
	public static int silenceCooldown = 3;
	public static int mineCooldown = 3;
	public static String sonarResult = "";
	public static String[] opponentOrders = {
			"NA",
			"MOVE E",
			"MOVE E",
			"MOVE E",
			"TORPEDO 7 7",
//			"SURFACE 4",
			"MOVE E",
			"MOVE E",
			"MOVE E",
			"MOVE E",
			"MOVE E",
			"MOVE E"
	};

	public static void main(String[] args) {
		String[] data = { 
				"x..xx.......xx.", 
				"...xx.......xx.", 
				"............xx.", 
				"...............", 
				"...............",
				".............xx", 
				".........xx..xx", 
				".........xx...x", 
				"..............x", 
				"...............",
				"...............", 
				"..xxx..........", 
				"..xxx..xx......", 
				"..xxx..xx......", 
				"..............." };
		map = new MatrixMap();

		for (int i = 0; i < data.length; i++) {
			map.setLine(data[i], i);
		}
		
		/*
		 * 
		 * https://www.codingame.com/replay/442555029
		 * Pour vérifier les tirs de torpille avec life et le silence
		 * 
		 * 
		 * 
		 * 
		 */
		// lien pour test silence https://www.codingame.com/replay/442521417
		Submarine suby = new Submarine(map);
		
		
		String[] coord = suby.firstCoord().split(" ");
		x = Integer.parseInt(coord[0]);
		y = Integer.parseInt(coord[1]);
		
		
		for (int i = 1; i <= opponentOrders.length ; i++) {
			System.err.println("tour " + i);
			System.err.println("pos suby : " + x + "," + y);
			System.err.println("actions de l'adversaire : " + opponentOrders[i-1]);
			String actions = suby.nextAction(x, y, myLife, oppLife, torpedoCooldown, sonarCooldown, silenceCooldown, mineCooldown, sonarResult, opponentOrders[i-1]);
			
			System.err.println("mes actions pour le prochain tour : " + actions);
			System.err.println("-----------------------");
			nextTurn(actions);
		}
	}

	private static void nextTurn(String actions) {
		
		String[] listActions = actions.split("\\|");

		for (String e : listActions) {
			String[] action = e.split(" ");

			switch (action[0]) {
			case "MOVE":
				setPositionFromMove(action[1]);
				break;
			case "TORPEDO":
				torpedoCooldown = 3;
				break;
			case "SURFACE":
				
				break;
			default:
				break;
			}
		}
		// TODO myLife ?
		
		// TODO oppLife ?
		
		// TODO torpedoCoolDown commence à 3, -1 par tour jusqu'à 0 où il reste tant que pas torpedo
		if (torpedoCooldown > 0) {
			torpedoCooldown--;
		}
		
		// TODO 
		
	}

	private static void setPositionFromMove(String string) {
		// TODO Auto-generated method stub
		switch (string) {
		case "N" :
			y--;
			break;
		case "S" :
			y++;
			break;
		case "E" :
			x++;
			break;
		case "W" :
			x--;
			break;
		}
	}

	
	

}
