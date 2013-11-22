/**
 * 
 */
package Agent;

import java.util.Random;

import Agent.WumpusAction.ACTION;
import Agent.WumpusAction.DIRECTION;
import Environment.Percept;
import Environment.WumpusPercept;

/**
 * @author NR
 *
 * TODO
 */
public class WumpusAction implements Action{
	
	public enum DIRECTION{
		DIRUP, DIRDOWN, DIRLEFT, DIRRIGHT
	}
	
	public enum ACTION{
		SHOOT, MOVE, GRAB, CLIMB
	}

	private ACTION _nAction;
	private DIRECTION _nDirxn;
	
	public WumpusAction(final ACTION action, final DIRECTION direction) throws Exception {
		if( action != ACTION.MOVE && action != ACTION.SHOOT && action != ACTION.GRAB && action != ACTION.CLIMB ) {
			throw new Exception("Invalid action");
		}
		
		_nAction = action;
		
		if( action == ACTION.MOVE || action == ACTION.SHOOT ) {
			if( direction != DIRECTION.DIRUP && direction != DIRECTION.DIRDOWN 
					&& direction != DIRECTION.DIRLEFT && direction != DIRECTION.DIRRIGHT ) {
				throw new Exception("Incorrect direction");
			}
		}
		
		_nDirxn = direction;
		
	}

	/**
	 * @return
	 */
	public boolean IsMoveUp() {
		return _nAction == ACTION.MOVE && _nDirxn == DIRECTION.DIRUP;
	}

	/**
	 * @return
	 */
	public boolean IsMoveDown() {
		return _nAction == ACTION.MOVE && _nDirxn == DIRECTION.DIRDOWN;
	}
	
	public boolean IsMoveLeft() {
		return _nAction == ACTION.MOVE && _nDirxn == DIRECTION.DIRLEFT;
	}
	
	public boolean IsMoveRight() {
		return _nAction == ACTION.MOVE && _nDirxn == DIRECTION.DIRRIGHT;
	}

	/**
	 * @return
	 */
	public boolean IsShoot() {
		return _nAction == ACTION.SHOOT;
	}

	/**
	 * @return
	 */
	public DIRECTION getDirxn() {
		return _nDirxn;
	}

	/**
	 * @return
	 */
	public boolean isClimb() {
		return _nAction == ACTION.CLIMB;
	}

	/**
	 * @return
	 */
	public boolean isGrab() {
		return _nAction == ACTION.GRAB;
	}

	/**
	 * @return
	 */
	public static WumpusAction getRandomAction() {
		
		
		ACTION[] acts = {ACTION.MOVE, ACTION.SHOOT, ACTION.GRAB, ACTION.CLIMB};
		DIRECTION[] dirxns = {DIRECTION.DIRUP, DIRECTION.DIRDOWN, DIRECTION.DIRLEFT, DIRECTION.DIRRIGHT};
		
		try
		{
			ACTION act = acts[(int)(Math.random()*acts.length)];
			DIRECTION dirxn = dirxns[(int)(Math.random()*dirxns.length)];
			
			WumpusAction wa = new WumpusAction(
					act, 
					dirxn);
			return wa;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "WumpusAction [_nAction=" + this._nAction + ", _nDirxn="
				+ this._nDirxn + "]";
	}
}
