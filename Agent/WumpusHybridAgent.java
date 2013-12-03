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
	
	public WumpusHybridAgent(int size) {
		worldSize = size;
	}
	
	// members
	KnowledgeBase 				kb = new KnowledgeBase();
	int 						time = 0;
	int							worldSize;
	ArrayList<WumpusAction> 	plan = new ArrayList<WumpusAction>();
	Position 					agentpos = new Position(1, 1);
	public DIRECTION 			agentdir = DIRECTION.DIRUP;
	public DIRECTION			plandir; // direction the agent will be facing after execution of the plan
	
	// private methods
	
	private ArrayList<WumpusAction> turnTo(DIRECTION current, DIRECTION goal) throws Exception {
		ArrayList<WumpusAction> moves = new ArrayList<WumpusAction>();
		int turns = goal.ordinal() - current.ordinal();
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
		return moves;
	}
	
	// given the agent's direction and the direction of the neighboring square to move to,
	// returns the actions necessary
	private ArrayList<WumpusAction> getMoves(DIRECTION facing, DIRECTION tomove) throws Exception {
		// update where the plan is taking us, direction-wise
		plandir = facing;
		ArrayList<WumpusAction> moves = new ArrayList<WumpusAction>();
		moves.addAll(turnTo(facing, tomove));
		moves.add(new WumpusAction(ACTION.MOVE));
		return moves;
	}
	
	// use A* search to plan a route to any of the goals using any of the allowed squares
	// returns int i where goals[i] is the square chosen to navigate to
	private int planRoute(ArrayList<Position> goals, ArrayList<Position> allowed) throws Exception {
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
				return -1;
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
		return goals.indexOf(cur);
	}

	private void planShot(ArrayList<Position> targets, ArrayList<Position> allowed) throws Exception {
		// figure out all goals
		ArrayList<Position> goals = new ArrayList<Position>();
		ArrayList<DIRECTION> dirs = new ArrayList<DIRECTION>();
		
		for(Position t : targets) {
			for(int i = 0; i < worldSize; i++) {
				if(i != t.x && allowed.contains(new Position(i, t.y))) {
					goals.add(new Position(i, t.y));
					if(i < t.x) {
						dirs.add(DIRECTION.DIRRIGHT);
					}
					else if(i > t.x) {
						dirs.add(DIRECTION.DIRLEFT);
					}
				}
			}
			for(int j = 0; j < worldSize; j++) {
				if(j != t.y && allowed.contains(new Position(t.x, j))) {
					goals.add(new Position(t.x, j));
					if(j < t.y) {
						dirs.add(DIRECTION.DIRUP);
					}
					else if(j > t.y) {
						dirs.add(DIRECTION.DIRDOWN);
					}
				}
			}
		}
		
		// planRoute to goals
		int idx = planRoute(goals, allowed);
		
		// if we have a plan / made it to a goal, turn the right way and plan a shot
		if(idx != -1) {
			// we want to turn to face dirs.get(idx) - the shooting direction associated with the goal we're going to
			plan.addAll(turnTo(plandir, dirs.get(idx)));
			plan.add(new WumpusAction(ACTION.SHOOT));
			plandir = dirs.get(idx);
		}
	}
	
	// do we want to associate percepts with location??
	// No. We can infer from percept and agent location at that time
	private void Make_Percept_Sentence(Percept p, int time)
	{
		if( p instanceof WumpusPercept ) 
		{
			WumpusPercept wp = (WumpusPercept)p;
			if(wp.isbBreezy())
				kb.Tell(String.format("Breeze(%d)", time));
			if(wp.isbGlitter())
				kb.Tell(String.format("Glitter(%d)", time));
			
			// Bump percept missing
			// Wumpus Dead is not a percept
			if(wp.isbDead())
				kb.Tell(String.format("WumpusDead(%d)", time));
			
			if(wp.isbScream())
				kb.Tell(String.format("WumpusScream(%d)", time));
			if(wp.isbSmelly())
				kb.Tell(String.format("Smell(%d)", time));
		}
	}
	
	// Make Action Sentence
	private void Make_Action_Sentence(Action act, int time)
	{
		if( act instanceof WumpusAction ) 
		{
			WumpusAction wAct = (WumpusAction)act;
			if(wAct.isClimb())
				kb.Tell(String.format("Action(Climb, %d)", time));
			if(wAct.isGrab())
				kb.Tell(String.format("Action(Grab, %d)", time));
			if(wAct.IsMove())
				kb.Tell(String.format("Action(Move, %d)", time));
			if(wAct.IsShoot())
				kb.Tell(String.format("Action(Shoot, %d)", time));
			if(wAct.IsTurnLeft())
				kb.Tell(String.format("Action(TurnLeft, %d)", time));
			if(wAct.IsTurnRight())
				kb.Tell(String.format("Action(TurnRight, %d)", time));
		}
	}
	
	// Tell KB the location of Agent?? Not sure
	private void Tell_Agent_Location(int time)
	{
		kb.Tell(String.format("At(Agent, [%d,%d], %d", agentpos.x, agentpos.y, time));
	}
	
	// Return list of Safe locations
	private ArrayList<Position> AskKBSafe()
	{
		ArrayList<Position> safePositions = new ArrayList<Position>();
	
		for(int i = 1; i <= 4; i++)
		{
			for(int j = 1; j <= 4; j++)
			{
				if(kb.Ask(String.format("Safe([%d,%d]", i, j)))
					safePositions.add(new Position(i, j));
			}
		}
	
		return safePositions;
	}
	
	// Returns list of locations that are not proven to be unsafe
	// Not sure about the Query
	private ArrayList<Position> AskKBNotUnsafe()
	{
		ArrayList<Position> unProvenSafePositions = new ArrayList<Position>();
		
		for(int i = 1; i <= 4; i++)
		{
			for(int j = 1; j <= 4; j++)
			{
				if(!kb.Ask(String.format("Safe([%d,%d]", i, j)))
					unProvenSafePositions.add(new Position(i, j));
			}
		}	
		
		return unProvenSafePositions;
	}
	
	// Returns whether Agent has arrows or not
	private Boolean AskKBHaveArrow(int time)
	{
		// Tricky to write the assertion as a successor predicate is required
		return kb.Ask(String.format("HaveArrow(%d)", time));
	}
	
	// Returns list of locations visited by agent
	private ArrayList<Position> AskKBUnvisited()
	{
		ArrayList<Position> visitedPositions = new ArrayList<Position>();
		for(int i = 1; i <= 4; i++)
		{
			for(int j = 1; j <= 4; j++)
			{
				if(kb.Ask(String.format("Visited([%d,%d]", i, j)))
					visitedPositions.add(new Position(i, j));
			}
		}	
		
		return visitedPositions;
	}
	
	// Returns all squares for which there might be a Wumpus
	private ArrayList<Position> AskKBWumpus()
	{
		ArrayList<Position> possibleWumpusPositions =  new ArrayList<Position>();
		for(int i = 1; i <= 4; i++)
		{
			for(int j = 1; j <= 4; j++)
			{
				//if(kb.Ask
					possibleWumpusPositions.add(new Position(i, j));
			}
		}
		return possibleWumpusPositions;
	}
	
	// Returns whether there is Glitter or not
	private Boolean AskKBGlitter(int time)
	{
		return kb.Ask(String.format("Glitter(%d)", time));
	}
	
	@Override
	public Action getAction(Percept p) throws Exception {
		// using textbook's algorithm, pg 270
		Make_Percept_Sentence(p, time);
		
		ArrayList<Position> safe = AskKBSafe(); // this method should ask the KB whether each square is safe
		ArrayList<Position> unvisited = AskKBUnvisited(); // this method returns all squares for which the KB knows we were located there once
		
		if(AskKBGlitter(time) == true) {
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
		if(plan.size() == 0 && AskKBHaveArrow(time) == true) {
			ArrayList<Position> possibleWumpus = AskKBWumpus(); // this method returns all squares for which there might be a wumpus
			plan = new ArrayList<WumpusAction>();
			planShot(possibleWumpus, safe);
		}
		if(plan.size() == 0) {
			// time to take a risk
			// consider turning this on/off as experiment parameter
			ArrayList<Position> notUnsafe = AskKBNotUnsafe(); // ask KB for all squares that aren't proven unsafe

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
		// update agentpos and agentdir
		if(act.IsTurnLeft()) {
			switch(agentdir) {
			case DIRUP:
				agentdir = DIRECTION.DIRLEFT;
				break;
			case DIRRIGHT:
				agentdir = DIRECTION.DIRUP;
				break;
			case DIRDOWN:
				agentdir = DIRECTION.DIRRIGHT;
				break;
			case DIRLEFT:
				agentdir = DIRECTION.DIRDOWN;
				break;
			}
		}
		else if(act.IsTurnRight()) {
			switch(agentdir) {
			case DIRUP:
				agentdir = DIRECTION.DIRRIGHT;
				break;
			case DIRRIGHT:
				agentdir = DIRECTION.DIRDOWN;
				break;
			case DIRDOWN:
				agentdir = DIRECTION.DIRLEFT;
				break;
			case DIRLEFT:
				agentdir = DIRECTION.DIRUP;
				break;
			}
		}
		else if(act.IsMove()) {
			switch(agentdir) {
			case DIRUP:
				if(agentpos.y < worldSize - 1) {
					agentpos.y++;
				}
				break;
			case DIRRIGHT:
				if(agentpos.x < worldSize - 1) {
					agentpos.x++;
				}
				break;
			case DIRDOWN:
				if(agentpos.y > 0) {
					agentpos.y--;
				}
				break;
			case DIRLEFT:
				if(agentpos.x > 0) {
					agentpos.x--;
				}
				break;
			}
		}
		
		time++;
		
		Make_Action_Sentence(act, time);
		Tell_Agent_Location(time);
		return act;
	}

}
