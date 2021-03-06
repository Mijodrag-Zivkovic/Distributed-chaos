package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;

	private final String ipAddress;
	private final int listenerPort;
	private List<Integer> neighborsPorts;
	
	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		neighborsPorts = new ArrayList<>();

	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public List<Integer> getNeighborsPorts() {
		return neighborsPorts;
	}

	@Override
	public String toString() {
		return "[" + ipAddress + "|" + listenerPort + "]";
	}
}
