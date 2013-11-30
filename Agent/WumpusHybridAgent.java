package Agent;

import java.util.ArrayList;

import Agent.WumpusAction.ACTION;
import Environment.Percept;
import Environment.WumpusPercept;
import KnowledgeBase.KnowledgeBase;

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
	//ArrayList<String> knowledgebase;
	KnowledgeBase kb = new KnowledgeBase();
	int time = 0;
	ArrayList<WumpusAction> plan;
	Position current;
	
	// private methods
	private void tellKB(String sentence) {
		kb.Tell(sentence);
	}
	
	private Boolean askKB(String sentence) {
		return kb.Ask(sentence);
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
