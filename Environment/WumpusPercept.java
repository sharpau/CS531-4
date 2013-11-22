/**
 * 
 */
package Environment;

import util.Pair;

/**
 * @author NR
 *
 * TODO
 */
public class WumpusPercept implements Percept {
	private boolean _bSmelly = false;
	private boolean _bBreezy = false;
	private boolean _bScream = false;
	private boolean _bGlitter = false;
	private boolean _bDead = false;
	
	private Pair<Integer, Integer> currentLoc;
	/**
	 * @param loc 
	 * 
	 */
	/**
	 * @return the currentLoc
	 */
	public Pair<Integer, Integer> getCurrentLoc() {
		return this.currentLoc;
	}
	
	public void setLoc(Pair<Integer,Integer> loc) {
		currentLoc = new Pair<Integer, Integer>(loc._o1, loc._o2);
	}
	public void setDead() {
		_bDead = true;
	}
	public boolean isbSmelly() {
		return this._bSmelly;
	}
	public boolean isbBreezy() {
		return this._bBreezy;
	}
	public boolean isbScream() {
		return this._bScream;
	}
	public boolean isbGlitter() {
		return this._bGlitter;
	}
	public boolean isbDead() {
		return this._bDead;
	}
	/**
	 * 
	 */
	public void setBreezy() {
		_bBreezy = true;
	}
	/**
	 * 
	 */
	public void setSmelly() {
		_bSmelly = true;
	}
	/**
	 * 
	 */
	public void setGlitter() {
		_bGlitter = true;
	}
	/**
	 * 
	 */
	public void setScream() {
		_bScream = true;
	}
	@Override
	public String toString() {
		return "WumpusPercept [_bSmelly=" + this._bSmelly + ", _bBreezy="
				+ this._bBreezy + ", _bScream=" + this._bScream
				+ ", _bGlitter=" + this._bGlitter + ", _bDead=" + this._bDead
				+ "]";
	}
	
	
}
