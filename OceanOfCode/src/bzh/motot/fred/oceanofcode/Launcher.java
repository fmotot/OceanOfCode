package bzh.motot.fred.oceanofcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.sound.midi.SysexMessage;

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
	
	
	public static int oppStartX = 7;
	public static int oppStartY = 7;
	public static String[] opponentOrders = {
			"NA",
			"MOVE E",
			"MOVE E",
			"MOVE E",
			"MOVE N",
			"MOVE N",
			"MOVE W|TORPEDO 9 8",
			"MOVE S",
			"MOVE W",
			"MOVE W|TORPEDO 7 10",
			"MOVE W",
			"MOVE S",
			"MOVE S|TORPEDO 10 8",
			"MOVE E",
			"MOVE E",
			"MOVE E|TORPEDO 9 12",
			"MOVE E",
			"MOVE E",
			"MOVE E|TORPEDO 9 8",
			"MOVE E",
			"MOVE E",
			"MOVE S",
			"MOVE S",
			"MOVE S",
			"MOVE S"
	};
	public static String strMyPath = "[6 14, 6 13, 6 12, 5 12, 5 13, 5 14, 4 14, 4 13, 3 13, 2 13, 1 13, 1 14, 0 14, 0 13, 0 12, 1 12, 1 11, 2 11, 2 10, 3 10, 4 10, 5 10, 6 10, 6 11, 7 11, 8 11, 9 11, 9 12, 8 12, 8 13, 7 13, 7 14, 8 14, 9 14, 10 14, 11 14, 12 14, 13 14, 14 14, 14 13, 14 12, 13 12, 13 11, 14 11, 14 10, 14 9, 14 8, 14 7, 13 7, 13 8, 13 9, 12 9, 12 10, 11 10, 11 9, 10 9, 9 9, 9 10, 8 10, 8 9, 8 8, 8 7, 9 7, 10 7, 10 8, 11 8, 12 8, 12 7, 12 6, 13 6, 14 6, 14 5, 14 4, 14 3, 14 2, 13 2, 13 1, 12 1, 12 2, 12 3, 11 3, 11 4, 11 5, 10 5, 9 5, 9 6, 8 6, 7 6, 7 7, 7 8, 6 8, 5 8, 4 8, 4 9, 3 9, 2 9, 1 9, 1 8, 1 7, 0 7, 0 6, 1 6, 1 5, 2 5, 3 5, 4 5, 4 4, 4 3, 4 2, 5 2, 5 3, 6 3, 6 2, 6 1, 6 0, 5 0, 4 0, 4 1, 3 1, 3 2, 2 2, 2 1, 2 0, 1 0, 0 0, 0 1, 1 1]";

	public static String[] data2 = { 
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
	public static String[] data = {
			".......xx......",
			".......xxx.....",
			".......xxx.....",
			".......xxxx....",
			".......xxxx.xx.",
			".......xx...xx.",
			"...............",
			"..xx...........",
			"..xx...........",
			"...............",
			"...............",
			"...xx.....xxx..",
			"...xx.....xxx..",
			"..........xxx..",
			"..............."
			};

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
	public static void main(String[] args) {

		map = new MatrixMap();

		for (int i = 0; i < data.length; i++) {
			map.setLine(data[i], i);
		}

		Submarine suby = new Submarine(map);
		
		suby.setPath(getPathFromString(strMyPath));
		
		String[] coord = suby.firstCoord().split(" ");
		x = Integer.parseInt(coord[0]);
		y = Integer.parseInt(coord[1]);
		
		String actions = "NA";
		for (int i = 0; i < opponentOrders.length ; i++) {
			System.err.println("tour " + i);
			System.err.println("pos suby : " + x + "," + y);
			System.err.println("pos adv : " + getCoordOpp(opponentOrders[i]));
			System.err.println("actions de l'adversaire : " + opponentOrders[i]);
			System.err.println("mes actions : " + actions);
			 actions = suby.nextAction(x, y, myLife, oppLife, torpedoCooldown, sonarCooldown, silenceCooldown, mineCooldown, sonarResult, opponentOrders[i]);
			
			
			System.err.println("-----------------------");
			nextTurn(actions);
		}
	}

	private static String getCoordOpp(String opponentOrders) {
		// TODO Auto-generated method stub
		
		if (!opponentOrders.equals("NA")) {
			if (opponentOrders.contains("|")) {
				opponentOrders = opponentOrders.split("\\|")[0];
				System.err.println(opponentOrders);
			}
			switch (opponentOrders.split(" ")[1]) {
			case "N" : 
				oppStartY--;
				break;
			case "S" : 
				oppStartY++;
				break;
			case "E" : 
				oppStartX++;
				break;
			case "W" : 
				oppStartX--;
				break;
			}
		}
		
		return oppStartX + " " + oppStartY; 
	}

	private static ArrayList<Vertex> getPathFromString(String strMyPath) {
		ArrayList<Vertex> myPath = new ArrayList<Vertex>();
		
		for (String str : strMyPath.substring(1, strMyPath.length()-1).split(", ")) {
			String[] coord = str.split(" ");
			myPath.add(map.getVertex(Integer.parseInt(coord[0]),Integer.parseInt(coord[1])));
		}
		
		return myPath;
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
