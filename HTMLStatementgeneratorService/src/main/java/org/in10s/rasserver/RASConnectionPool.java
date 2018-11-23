package org.in10s.rasserver;

public interface RASConnectionPool {

	RASConnection getConnection() throws Exception;

	void releaseConnection(RASConnection connection) throws Exception;

	public int getNumberOfAvailableConnections();

	public int getNumberOfBusyConnections();

	public void closeAllConnections();

}
