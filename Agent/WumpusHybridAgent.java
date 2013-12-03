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
		
		@Override
		public boolean equals(Object object) {
			boolean same = false;
			if(object != null && object instanceof Position) {
				same = ((Position)object).x == x && ((Position)object).y == y;
			}
			return same;
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
	KnowledgeBase 				kb = new KnowledgeBase();
	int 						time = 0;
	ArrayList<WumpusAction> 	plan = new ArrayList<WumpusAction>();
	Position 					agentpos = new Position(1, 1);
	public DIRECTION 			agentdir = DIRECTION.DIRUP;
	
	// private methods
	
	// given the agent's direction and the direction of the neighboring square to move to,
	// returns the actions necessary
	private ArrayList<WumpusAction> getMoves(DIRECTION facing, DIRECTION tomove) throws Exception {
		ArrayList<WumpusAction> moves = new ArrayList<WumpusAction>();
		
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

		return moves;
	}
	
	// use A* search to plan a route to any of the goals using any of the allowed squares
	private void planRoute(ArrayList<Position> goals, ArrayList<Position> allowed) throws Exception {
		ArrayList<Node> frontier = new ArrayList<Node>();
		ArrayList<Position> explored = new ArrayList<Position>();
		Node cur = new Node(agentpos, agentdir, 0, plan);
		
		while(goals.contains(cur.pos) == false) {
			// get list of successors
			ArrayList<Node> succs = new ArrayList<Node>();
			
			ArrayList<WumpusAction> succmoves = new ArrayList<WumpusAction>(cur.moves);
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRRIGHT));
			succs.add(new Node(new Position(cur.pos.x + 1, cur.pos.y), DIRECTION.DIRRIGHT, succmoves.size(), succmoves));

			succmoves = new ArrayList<WumpusAction>(cur.moves);
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRLEFT));
			succs.add(new Node(new Position(cur.pos.x - 1, cur.pos.y), DIRECTION.DIRLEFT, succmoves.size(), succmoves));
			
			succmoves = new ArrayList<WumpusAction>(cur.moves);
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRUP));
			succs.add(new Node(new Position(cur.pos.x, cur.pos.y + 1), DIRECTION.DIRUP, succmoves.size(), succmoves));
			
			succmoves = new ArrayList<WumpusAction>(cur.moves);
			succmoves.addAll(getMoves(cur.dir, DIRECTION.DIRDOWN));
			succs.add(new Node(new Position(cur.pos.x, cur.pos.y - 1), DIRECTION.DIRDOWN, succmoves.size(), succmoves));

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
			int min = 9999;
			int idx = -1;
			for(int i = 0; i < frontier.size(); i++) {
				if(frontier.get(i).cost < min) {
					min = frontier.get(i).cost;
					idx = i;
				}
			}
			explored.add(cur.pos);
			cur = frontier.get(idx);
			frontier.remove(idx);
			
		}
		
		// goal state found, update plan to match moves to get to that state
		plan = cur.moves;
	}

	@Override
	public Action getAction(Percept p) throws Exception {
		// test pathfinding
//		agentpos = new Position(5, 5);
//		ArrayList<Position> goals = new ArrayList<Position>();
//		goals.add(new Position(1, 6));
//		goals.add(new Position(2, 3));
//		ArrayList<Position> allowed = new ArrayList<Position>();
//		allowed.add(new Position(4, 5));
//		allowed.add(new Position(4, 4));
//		allowed.add(new Position(3, 5));
//		allowed.add(new Position(2, 5));
//		allowed.add(new Position(2, 6));
//		allowed.add(new Position(1, 6));
//		
//		planRoute(goals, allowed);
		
		// TODO pg 270 algorithm
		// Tell(KB, Make-Percept-Sentence(Percept p, time t); // do we want to associate percepts with location??
		
		ArrayList<Position> safe = null; // = AskKBSafe(); // this method should ask the KB whether each square is safe
		ArrayList<Position> unvisited = null; // = AskKBVisited(); // this method returns all squares for which the KB knows we were located there once
		
		if(/*AskKB(Glitter, t) ==*/ true) {
			plan = new ArrayList<WumpusAction>();
			// get gold
			plan.add(new WumpusAction(ACTION.GRAB));
			
			// return to starting square
			ArrayList<Position> goals = new ArrayList<Position>();
			goals.add(new Position(1, 1));
			planRoute(goals, safe);
			
			// leave
			plan.add(new WumpusAction(ACTION.CLIMB));
		}
		if(plan.size() == 0) {
			ArrayList<Position> unvisitedSafe = new ArrayList<Position>();
			
			for(Position pos : unvisited) {
				if(safe.contains(pos)) {
					unvisitedSafe.add(pos);
				}
			}
			
			plan = new ArrayList<WumpusAction>();
			planRoute(unvisitedSafe, safe);
		}
		if(plan.size() == 0 && /*AskKB(HaveArrow, t) = */ true) {
			ArrayList<Position> possibleWumpus = null; //  = AskKBWumpus(); // this method returns all squares for which there might be a wumpus
			plan = new ArrayList<WumpusAction>();
			// planRoute(possibleWumpus, safe); // TODO replace with planShot!!!
			// TODO: figure out which way to face!!!
			plan.add(new WumpusAction(ACTION.SHOOT));
		}
		if(plan.size() == 0) {
			// time to take a risk
			// consider turning this on/off as experiment parameter
			ArrayList<Position> notUnsafe = null; // = AskKBNotUnsafe(); // ask KB for all squares that aren't proven unsafe

			ArrayList<Position> unvisitedNotUnsafe = new ArrayList<Position>();
			
			for(Position pos : unvisited) {
				if(notUnsafe.contains(pos)) {
					unvisitedNotUnsafe.add(pos);
				}
			}
			
			plan = new ArrayList<WumpusAction>();
			planRoute(unvisitedNotUnsafe, safe);
		}
		if(plan.size() == 0) {
			// nothing left to try, time to leave
			ArrayList<Position> goals = new ArrayList<Position>();
			goals.add(new Position(1, 1));
			planRoute(goals, safe);
			plan.add(new WumpusAction(ACTION.CLIMB));
		}
		
		WumpusAction act = plan.remove(0);
		
		return act;
	}

}
