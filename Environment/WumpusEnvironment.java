/**
 * 
 */
package Environment;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import util.Pair;
import Agent.Action;
import Agent.WumpusAction;
import Agent.WumpusHybridAgent;
import Agent.WumpusSimpleAgent;
import KnowledgeBase.KnowledgeBase;

/**
 * @author NR
 *
 * TODO
 */
public class WumpusEnvironment implements Environment{

	private TreeSet<Pair<Integer,Integer>> _tsPitMap;
	private TreeSet<Pair<Integer,Integer>> _tsWumpusMap;
	private TreeSet<Pair<Integer,Integer>> _tsGoldMap;
	
	Random _rand;
	
	private int _nSize;
	
	private boolean _bAlive;
	private Pair<Integer,Integer>	currentLoc;
	private int _nArrow;
	private boolean	_bWumpusDied;
	private boolean	_bTrialOver;
	private int	_nHasGold;
	
	public enum DIRECTION{
		DIRUP, DIRRIGHT, DIRDOWN, DIRLEFT
	}
	private DIRECTION _nFacing;

	
	public static WumpusEnvironment getNewWumpusEnvironment(
			final int n, final int nWumpus, final int nGold, final int nArrows,
			double pitProb, long seed) {
		WumpusEnvironment we = new WumpusEnvironment();

		we._rand = new Random(seed);
		we._nSize = n;
		we._bAlive = true;
		we._tsGoldMap = new TreeSet<Pair<Integer,Integer>>();
		we._tsPitMap = new TreeSet<Pair<Integer,Integer>>();
		we._tsWumpusMap = new TreeSet<Pair<Integer,Integer>>();
		we.currentLoc = new Pair<Integer, Integer>(0, 0);
		we._nArrow = nArrows;
		we._bWumpusDied = false;
		we._bTrialOver = false;
		we._nHasGold = 0;
		we._nFacing = DIRECTION.DIRUP;
		
		while( true ) {
			int x = we._rand.nextInt(n);
			int y = we._rand.nextInt(n);
			if( x == 0 && y == 0 ) {
				continue;
			}
			
			we._tsGoldMap.add(new Pair<Integer, Integer>(x, y));
			if( we._tsGoldMap.size() == nGold ) {
				break;
			}
		}
		
		while( true ) {
			int x = we._rand.nextInt(n);
			
			int y = we._rand.nextInt(n);
			if( x == 0 && y == 0 ) {
				continue;
			}
			
			we._tsWumpusMap.add(new Pair<Integer, Integer>(x, y));
			if( we._tsWumpusMap.size() == nWumpus ) {
				break;
			}
		}
		
		for( int i = 0 ; i < n ; ++i ) {
			for( int j = 0; j < n; ++j ) {
				if( i == 0 && j == 0 ) {
					continue;
				}
				double d = we._rand.nextDouble();
				if( d <= pitProb ) {
					we._tsPitMap.add(new Pair<Integer, Integer>(i, j));
				}
			}
		}

		return we;
		
	}
	
	/* (non-Javadoc)
	 * @see Environment.Environment#getPercept()
	 */
	@Override
	public Percept getPercept() throws Exception{
		
		if( _bTrialOver ) {
			throw new Exception("Trial over");
		}
		
		WumpusPercept p = new WumpusPercept();
		
		if( _tsWumpusMap.contains(currentLoc) || _tsPitMap.contains(currentLoc) ) {
			p.setDead();
			_bTrialOver = true;
			return p;
		}
		
		
		
		Pair<Integer,Integer> left = leftGuy(currentLoc);
		Pair<Integer,Integer> right = rightGuy(currentLoc);
		Pair<Integer,Integer> top = topGuy(currentLoc);
		Pair<Integer,Integer> bottom = bottomGuy(currentLoc);
		
		
		if( _tsPitMap.contains(left) || _tsPitMap.contains(right) 
				|| _tsPitMap.contains(top) || _tsPitMap.contains(bottom) ) {
			p.setBreezy();
		}
		
		if( _tsWumpusMap.contains(left) || _tsWumpusMap.contains(right) 
				|| _tsWumpusMap.contains(top) || _tsWumpusMap.contains(bottom) ) {
			p.setSmelly();
		}
		
		if( _tsGoldMap.contains(currentLoc) ) {
			p.setGlitter();
		}
		
		if( _bWumpusDied ) {
			p.setScream();
			_bWumpusDied = false;
		}
	
		p.setLoc(currentLoc);
		
		return p;
		
	}

	/**
	 * @param pCurrentLoc
	 * @return
	 */
	public Pair<Integer, Integer> leftGuy(Pair<Integer, Integer> CurrentLoc) {
		Pair<Integer,Integer> ret = new Pair<Integer, Integer>(-1, -1);
		
		if( CurrentLoc._o1 == 0 ) {
			ret._o1 = 0;
		}else {
			ret._o1 = CurrentLoc._o1-1;
		}
		
		ret._o2 = CurrentLoc._o2;
		
		return ret;
	}

	public Pair<Integer, Integer> rightGuy(Pair<Integer, Integer> CurrentLoc) {
		Pair<Integer,Integer> ret = new Pair<Integer, Integer>(-1, -1);
		
		if( CurrentLoc._o1 == _nSize-1 ) {
			ret._o1 = _nSize-1;
		}else {
			ret._o1 = CurrentLoc._o1+1;
		}
		
		ret._o2 = CurrentLoc._o2;
		
		return ret;
	}
	
	public Pair<Integer, Integer> topGuy(Pair<Integer, Integer> CurrentLoc) {
		Pair<Integer,Integer> ret = new Pair<Integer, Integer>(-1, -1);
		
		if( CurrentLoc._o2 == _nSize-1 ) {
			ret._o2 = _nSize-1;
		}else {
			ret._o2 = CurrentLoc._o2+1;
		}
		
		ret._o1 = CurrentLoc._o1;
		
		return ret;
	}
	
	public Pair<Integer, Integer> bottomGuy(Pair<Integer, Integer> CurrentLoc) {
		Pair<Integer,Integer> ret = new Pair<Integer, Integer>(-1, -1);
		
		if( CurrentLoc._o2 == 0 ) {
			ret._o2 = 0;
		}else {
			ret._o2 = CurrentLoc._o2-1;
		}
		
		ret._o1 = CurrentLoc._o1;
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see Environment.Environment#takeAction(Agent.Action)
	 */
	@Override
	public void takeAction(Action act) throws Exception{
		
		if( ! (act instanceof WumpusAction ) ){
			throw new Exception("Invalid action");
		}
		
		WumpusAction wa = (WumpusAction)act;
		
		if( _bTrialOver ) {
			throw new Exception("Trial over");
		}
		//move, shoot, grab, climb
		if( wa.IsMove() ) {
			if(_nFacing == DIRECTION.DIRUP) {
			currentLoc = topGuy(currentLoc);
			}else if(_nFacing == DIRECTION.DIRDOWN) {
				currentLoc = bottomGuy(currentLoc);
			}else if(_nFacing == DIRECTION.DIRLEFT) {
				currentLoc = leftGuy(currentLoc);
			}else if(_nFacing == DIRECTION.DIRRIGHT) {
				currentLoc = rightGuy(currentLoc);
			}
		}else if( wa.IsShoot() && _nArrow > 0 ) {
			--_nArrow;
			Pair<Integer,Integer> current 
			= new Pair<Integer, Integer>(currentLoc._o1, currentLoc._o2);
			
			switch( _nFacing) {
				case DIRUP :
					
					while( current._o2 != _nSize-1 ) {
						current = topGuy(current);
						if( _tsWumpusMap.contains(current) ) {
							_tsWumpusMap.remove(current);
							_bWumpusDied = true;
						}
					}
					break;
					
				case DIRDOWN :
				
					while( current._o2 != 0 ) {
						current = bottomGuy(current);
						if( _tsWumpusMap.contains(current) ) {
							_tsWumpusMap.remove(current);
							_bWumpusDied = true;
						}
					}
					break;
					
				case DIRLEFT :
					
					while( current._o1 != 0 ) {
						current = leftGuy(current);
						if( _tsWumpusMap.contains(current) ) {
							_tsWumpusMap.remove(current);
							_bWumpusDied = true;
						}
					}
					break;
					
				case DIRRIGHT :
					
					while( current._o1 != _nSize-1 ) {
						current = rightGuy(current);
						if( _tsWumpusMap.contains(current) ) {
							_tsWumpusMap.remove(current);
							_bWumpusDied = true;
						}
					}
					break;
			}
		}else if( wa.isClimb() ) {
			if( currentLoc._o1 == 0 && currentLoc._o2 == 0 ) {
				_bTrialOver = true;
			}
		}else if( wa.isGrab() ) {
			if( _tsGoldMap.contains(currentLoc) ) {
				_tsGoldMap.remove(currentLoc);
				++_nHasGold;
			}
		}
	}

	/* (non-Javadoc)
	 * @see Environment.Environment#show()
	 */
	@Override
	public String show() {
		StringBuilder sb = new StringBuilder();
		sb.append("Agent Location: ");
		sb.append(currentLoc);
		sb.append("\n");
		
		sb.append("Agent Direction: ");
		sb.append(_nFacing);
		sb.append("\n");
		
		sb.append("Wumpus Locations: ");
		sb.append(_tsWumpusMap);
		sb.append("\n");
		
		sb.append("Gold Locations: ");
		sb.append(_tsGoldMap);
		sb.append("\n");
		
		sb.append("Pit Locations: ") ;
		sb.append(_tsPitMap);
		sb.append("\n");
		
		sb.append("Agent has " );
		sb.append(_nHasGold);
		sb.append(" gold");
		sb.append("\n");
		
		sb.append("Agent has " );
		sb.append(_nArrow);
		sb.append(" arrows\n");
		
		sb.append("Alive? ");
		sb.append(_bAlive);
		sb.append("\n");
		
		sb.append("Trial Over ");
		sb.append(_bTrialOver);
		sb.append("\n");
		
		return sb.toString();
	}

	public static void main(String[] args) {
		final int n = 3; // world is size nxn
		
		WumpusEnvironment we = WumpusEnvironment.getNewWumpusEnvironment(n, 1, 1, 1, 0.2, 7);//System.currentTimeMillis());
		WumpusSimpleAgent wa = new WumpusSimpleAgent();
		
		WumpusHybridAgent wha = new WumpusHybridAgent(n, true);
		//try {
		//	wha.getAction(we.getPercept());
		//} catch (Exception e1) {
		//	System.out.println(e1.getMessage());
		//	e1.printStackTrace();
		//}
		
		while( true ) {
			try {
				
				System.out.println(we.show());
				WumpusPercept p = (WumpusPercept)we.getPercept();
				System.out.println(p);
				if( p.isbDead() ) {
					break;
				}
				//WumpusAction wa = WumpusAction.getRandomAction();
				Action act = wha.getAction(we.getPercept());//WumpusAction.getRandomMoveAction(); 
				System.out.println("Action : " + act );
				we.takeAction((Action)act);
				System.out.println("-------");
				
				
			}catch( Exception e ) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				break;
			}
		}
		
	}
	
}
