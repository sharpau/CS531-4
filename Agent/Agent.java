/**
 * 
 */
package Agent;

import Environment.Percept;

/**
 * @author NR
 *
 * TODO
 */
public interface Agent {
	public Action getAction(Percept p) throws Exception;
}
