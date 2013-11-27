/**
 * 
 */
package Agent;

import java.util.Random;

import Agent.WumpusAction.ACTION;
import Environment.Percept;
import Environment.WumpusPercept;

/**
 * @author NR
 *
 * TODO
 */
public class WumpusAction implements Action{
	
	public enum ACTION{
		SHOOT, MOVE, TURNLEFT, TURNRIGHT, GRAB, CLIMB
	}

	private ACTION _nAction;
	
	public WumpusAction(final ACTION action) throws Exception {
		if( action != ACTION.MOVE && action != ACTION.SHOOT && action != ACTION.GRAB && action != ACTION.CLIMB && action != ACTION.TURNLEFT && action != ACTION.TURNRIGHT ) {
			throw new Exception("Invalid action");
		}
		
		_nAction = action;
	}

	/**
	 * @return
	 */
	public boolean IsMove() {
		return _nAction == ACTION.MOVE;
	}

	/**
	 * @return
	 */
	public boolean IsTurnLeft() {
		return _nAction == ACTION.TURNLEFT;
	}
	
	/**
	 * @return
	 */
	public boolean IsTurnRight() {
		return _nAction == ACTION.TURNRIGHT;
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
		
		
		ACTION[] acts = {ACTION.MOVE, ACTION.TURNLEFT, ACTION.TURNRIGHT, ACTION.SHOOT, ACTION.GRAB, ACTION.CLIMB};
		
		try
		{
			ACTION act = acts[(int)(Math.random()*acts.length)];
			
			WumpusAction wa = new WumpusAction(act);
			return wa;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	public static WumpusAction getRandomMoveAction() {
		
		
		ACTION[] acts = {ACTION.MOVE, ACTION.TURNLEFT, ACTION.TURNRIGHT};
		
		try
		{
			ACTION act = acts[(int)(Math.random()*acts.length)];
			
			WumpusAction wa = new WumpusAction(act);
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
		return "WumpusAction [_nAction=" + this._nAction + "]";
	}
}
