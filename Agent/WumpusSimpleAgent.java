/**
 * 
 */
package Agent;

import Agent.WumpusAction.ACTION;
import Agent.WumpusAction.DIRECTION;
import Environment.Percept;
import Environment.WumpusPercept;

/**
 * @author NR
 *
 * TODO
 */
public class WumpusSimpleAgent implements Agent{

	boolean havegold = false;
	boolean havearrow = true;
	/* (non-Javadoc)
	 * @see Agent.Agent#getAction(Environment.Percept)
	 */
	@Override
	public Action getAction(Percept p) {

		if( p instanceof WumpusPercept ) {
			WumpusPercept wp = (WumpusPercept)p;
			
			if( wp.isbGlitter() ) {
				try
				{
					havegold = true;
					return new WumpusAction(ACTION.GRAB, DIRECTION.DIRUP);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(  havegold && wp.getCurrentLoc()._o1 == 0 && wp.getCurrentLoc()._o2 == 0  ) {
					try
					{
						return new WumpusAction(ACTION.CLIMB, DIRECTION.DIRUP);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}else if( wp.isbSmelly() && havearrow ) {
				DIRECTION[] dirxns = DIRECTION.values();
				
				try
				{
					havearrow = false;
					return new WumpusAction(ACTION.SHOOT, 
							dirxns[(int)(Math.random()*dirxns.length)]);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				DIRECTION[] dirxns = DIRECTION.values();
				try
				{
					return new WumpusAction(ACTION.MOVE, 
							dirxns[(int)(Math.random()*dirxns.length)]);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
