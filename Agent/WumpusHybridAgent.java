package Agent;

import java.util.ArrayList;

import Agent.WumpusAction.ACTION;
import Environment.Percept;
import Environment.WumpusEnvironment.DIRECTION;
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
		
		boolean equals(Position other) {
			return other.x == x && other.y == y;
		}
	}
	
	// for search
	class Node {
		public Position pos;
		public DIRECTION dir;
		public int cost;
		public ArrayList<WumpusAction> moves;
		
		public Node(Position newp, DIRECTION newd, int newcost, ArrayList<WumpusAction> newmoves) {
			pos = newp;
			cost = newcost;
			moves = newmoves;
			dir = newd;
		}
	}
	
	// members
	KnowledgeBase kb = new KnowledgeBase();
	int time = 0;
	ArrayList<WumpusAction> plan;
	Position agentpos;
	public DIRECTION agentdir;
	
	// private methods
	private void tellKB(String sentence) {
		kb.Tell(sentence);
	}
	
	private Boolean askKB(String sentence) {
		return kb.Ask(sentence);
	}
	
	// given the agent's direction and the direction of the square to move to,
	// returns the actions necessary
	private ArrayList<WumpusAction> getMoves(DIRECTION facing, DIRECTION tomove) {
		ArrayList<WumpusAction> moves = new ArrayList<WumpusAction>();
		try {
			int turns = tomove.ordinal() - facing.ordinal();
			switch(turns) {
			case 0:
				break;
			case 1: 
				moves.add(new WumpusAction(ACTION.TURNRIGHT));
				break;
			case 2: 
				moves.add(new WumpusAction(ACTION.TURNRIGHT));
				moves.add(new WumpusAction(ACTION.TURNRIGHT));
				break;
			case 3: 
				moves.add(new WumpusAction(ACTION.TURNLEFT));
				break;
			case -1: 
				moves.add(new WumpusAction(ACTION.TURNLEFT));
				break;
			case -2: 
				moves.add(new WumpusAction(ACTION.TURNLEFT));
				moves.add(new WumpusAction(ACTION.TURNLEFT));
				break;
			case -3: 
				moves.add(new WumpusAction(ACTION.TURNRIGHT));
				break;
			}
			moves.add(new WumpusAction(ACTION.MOVE));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return moves;
	}
	
	private void planRoute(ArrayList<Position> goals, ArrayList<Position> allowed) {
		// TODO use A* to plan a route
		ArrayList<Node> frontier = new ArrayList<Node>();
		ArrayList<Position> explored = new ArrayList<Position>();
		Node cur = new Node(agentpos, agentdir, 0, plan);
		
		while(goals.contains(cur.pos) == false) {
			// get list of successors
			ArrayList<Node> succs = new ArrayList<Node>();
			
			ArrayList<WumpusAction> succmoves = cur.moves;
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRRIGHT));
			succs.add(new Node(new Position(cur.pos.x + 1, cur.pos.y), DIRECTION.DIRRIGHT, cur.cost + 1, succmoves));

			succmoves = cur.moves;
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRLEFT));
			succs.add(new Node(new Position(cur.pos.x + 1, cur.pos.y), DIRECTION.DIRLEFT, cur.cost + 1, succmoves));
			
			succmoves = cur.moves;
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRUP));
			succs.add(new Node(new Position(cur.pos.x + 1, cur.pos.y), DIRECTION.DIRUP, cur.cost + 1, succmoves));
			
			succmoves = cur.moves;
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRDOWN));
			succs.add(new Node(new Position(cur.pos.x + 1, cur.pos.y), DIRECTION.DIRDOWN, cur.cost + 1, succmoves));

			// for each successor, if  allowed and not explored add to frontier
			for(Node n : succs) {
				if(allowed.contains(n.pos) && !explored.contains(n.pos)) {
					frontier.add(n);
				}
			}
			
			// find and expand best node as cur
			if(frontier.size() == 0) {
				// no nodes to expand, no path found, so we don't update the plan
				return;
			}
			
		}
		
		// goal state found, update plan to match moves to get to that state
		plan = cur.moves;
	}

	@Override
	public Action getAction(Percept p) {
		// TODO pg 270 algorithm
		return null;
	}

}
