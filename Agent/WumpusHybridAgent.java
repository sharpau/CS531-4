package Agent;

import java.util.ArrayList;

import Agent.WumpusAction.ACTION;
import Agent.WumpusAction.DIRECTION;
import Environment.Percept;
import Environment.WumpusPercept;

// see textbook pg 270 algorithm

public class WumpusHybridAgent implements Agent {
	class Position {
		public int x;
		public int y;
		
		public Position(int newx, int newy) {
			x = newx;
			y = newy;
		}
	}
	
	// members
	ArrayList<String> knowledgebase;
	int time = 0;
	ArrayList<WumpusAction> plan;
	Position current;
	
	// private methods
	private void tellKB(String sentence) {
		// TODO 
	}
	
	private Boolean askKB(String sentence) {
		return false;
	}
	
	private void planRoute(ArrayList<Position> goals, ArrayList<Position> allowed) {
		// TODO use A* to plan a route
	}

	@Override
	public Action getAction(Percept p) {
		// TODO pg 270 algorithm
		return null;
	}

}
