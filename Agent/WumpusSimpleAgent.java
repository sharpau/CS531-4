/**
 * 
 */
package Agent;

import Agent.WumpusAction.ACTION;
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
					return new WumpusAction(ACTION.GRAB);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(  havegold && wp.getCurrentLoc()._o1 == 0 && wp.getCurrentLoc()._o2 == 0  ) {
					try
					{
						return new WumpusAction(ACTION.CLIMB);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}else if( wp.isbSmelly() && havearrow ) {				
				try
				{
					havearrow = false;
					return new WumpusAction(ACTION.SHOOT);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				try
				{
					return WumpusAction.getRandomMoveAction();
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
