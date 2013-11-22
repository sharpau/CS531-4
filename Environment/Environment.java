/**
 * 
 */
package Environment;

import Agent.Action;

/**
 * @author NR
 *
 * TODO
 */
public interface Environment {
	public Percept getPercept() throws Exception;
	public void takeAction(Action a) throws Exception;
	public String show();
}
