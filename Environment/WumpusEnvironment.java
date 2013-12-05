/**
 * 
 */
package Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	private int _nTime;
	
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
		we._nTime = 0;
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
			_bAlive = false;
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
		}else if( wa.IsTurnLeft() ) {
			switch(_nFacing) {
				case DIRUP:
					_nFacing = DIRECTION.DIRLEFT;
					break;
				case DIRRIGHT:
					_nFacing = DIRECTION.DIRUP;
					break;
				case DIRDOWN:
					_nFacing = DIRECTION.DIRRIGHT;
					break;
				case DIRLEFT:
					_nFacing = DIRECTION.DIRDOWN;
					break;
			}
		}else if( wa.IsTurnRight() ) {
			switch(_nFacing) {
			case DIRUP:
				_nFacing = DIRECTION.DIRRIGHT;
				break;
			case DIRRIGHT:
				_nFacing = DIRECTION.DIRDOWN;
				break;
			case DIRDOWN:
				_nFacing = DIRECTION.DIRLEFT;
				break;
			case DIRLEFT:
				_nFacing = DIRECTION.DIRUP;
				break;
			}
		}
		
		_nTime++;
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
		
		sb.append("Actions Taken ");
		sb.append(_nTime);
		sb.append("\n");
		
		return sb.toString();
	}

	public static void main(String[] args) {
		final int n = 4; // world is size nxn
		
		String simple_results = new String();
		String hybrid_results = new String();
		String risky_results = new String();

		
		for(int i = 1; i <= 40; i++) {
			System.out.println("World " + i + " being solved");
			
			WumpusSimpleAgent wa = new WumpusSimpleAgent(); // random movements
			WumpusHybridAgent wha = new WumpusHybridAgent(n, false);
			WumpusHybridAgent wha_risky = new WumpusHybridAgent(n, true);
			WumpusEnvironment we1 = WumpusEnvironment.getNewWumpusEnvironment(n, 1, 1, 1, 0.2, i);
			WumpusEnvironment we2 = WumpusEnvironment.getNewWumpusEnvironment(n, 1, 1, 1, 0.2, i);
			WumpusEnvironment we3 = WumpusEnvironment.getNewWumpusEnvironment(n, 1, 1, 1, 0.2, i);
			
			// random agent, we1
			while( true ) {
				try {
					//System.out.println(we1.show());
					WumpusPercept p = (WumpusPercept)we1.getPercept();
					//System.out.println(p);
					if( p.isbDead()) {
						simple_results += we1.show();	
						break;
					}
					Action act = wa.getAction(p);
					we1.takeAction((Action)act);
					
					
				}catch( Exception e ) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					// if trial is over, exception here
					simple_results += we1.show();
					break;
				}
			}
			
			// hybrid agent, we2
			while( true ) {
				try {
					//System.out.println(we1.show());
					WumpusPercept p = (WumpusPercept)we2.getPercept();
					//System.out.println(p);
					if( p.isbDead()) {
						hybrid_results += we2.show();	
						break;
					}
					Action act = wha.getAction(p);
					we2.takeAction((Action)act);
					
					
				}catch( Exception e ) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					// if trial is over, exception here
					hybrid_results += we2.show();
					break;
				}
			}
			
			// risky agent, we3
			while( true ) {
				try {
					//System.out.println(we1.show());
					WumpusPercept p = (WumpusPercept)we3.getPercept();
					//System.out.println(p);
					if( p.isbDead()) {
						risky_results += we3.show();	
						break;
					}
					Action act = wha_risky.getAction(p);
					we3.takeAction((Action)act);
					
					
				}catch( Exception e ) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					// if trial is over, exception here
					risky_results += we3.show();
					break;
				}
			}
			simple_results += '\n';
			hybrid_results += '\n';
			risky_results += '\n';
			
		}
		
		try {
			// write each string to a separate output file
			FileWriter simple_file = new FileWriter("SimpleResults.txt");
			FileWriter hybrid_file = new FileWriter("HybridResults.txt");
			FileWriter risky_file = new FileWriter("RiskyResults.txt");

			BufferedWriter out1 = new BufferedWriter(simple_file);
			out1.write(simple_results);
			out1.close();
			
			BufferedWriter out2 = new BufferedWriter(hybrid_file);
			out2.write(hybrid_results);
			out2.close();
			
			BufferedWriter out3 = new BufferedWriter(risky_file);
			out3.write(risky_results);
			out3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
