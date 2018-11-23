package org.in10s.rasserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RASConnectionPoolImpl implements Runnable, RASConnectionPool {

	private int m_nMaxConnections;
	private boolean m_bwaitIfBusy;
	private List<RASConnection> m_availableConnections, m_busyConnections;
	private boolean m_bconnectionPending = false;

	public RASConnectionPoolImpl(int initialConnections,
			int maxConnections, boolean waitIfBusy) throws Exception{

		if (maxConnections <= 0) {
			throw new IllegalArgumentException(
					"The maximum number of connections must be greater than 0.");
		}

		this.m_nMaxConnections = maxConnections;
		this.m_bwaitIfBusy = waitIfBusy;

		if (initialConnections > maxConnections) {
			initialConnections = maxConnections;
		}

		m_availableConnections = Collections.synchronizedList(new ArrayList<RASConnection>(initialConnections));
		m_busyConnections = Collections.synchronizedList(new ArrayList<RASConnection>());

		for (int i = 0; i < initialConnections; i++) {
			m_availableConnections.add(makeNewConnection());
		}

	}

	public synchronized RASConnection getConnection() throws Exception {

		if (!m_availableConnections.isEmpty()) {

			int lastIndex = m_availableConnections.size() - 1;

			RASConnection existingConnection = (RASConnection) m_availableConnections.get(lastIndex);

			m_availableConnections.remove(lastIndex);

			if(existingConnection.getRASClientSk() != null && existingConnection.getRASClientSk().isClosed()) {
				//notifyAll();
                                notify();
				return (getConnection());
			} else {

				m_busyConnections.add(existingConnection);

				return (existingConnection);
			}

		} else {
			if ((getNumberOfAvailableConnections() + getNumberOfBusyConnections()) < m_nMaxConnections
					&& !m_bconnectionPending)
				//makeBackgroundConnection();
                            try{
                                RASConnection connection1 = makeNewConnection();
                                m_busyConnections.add(connection1);
                                return connection1;
                            }catch(Exception e){
                                System.out.println("Error in makeNewConnection");
                                e.printStackTrace();
                            }

			else if (!m_bwaitIfBusy) {
				throw new Exception("WB-Connection limit reached");
			}

			try {
				m_bconnectionPending = true;
				wait(300000);	// 5 mins. wait
			} catch (InterruptedException ie) {
				System.out.println(ie.getMessage());
			}

                        if(m_bconnectionPending)
				throw new Exception("CP-Connection limit reached");

                        // Someone freed up a connection, so try again.
			return (getConnection());
		}
	}

	private void makeBackgroundConnection() {
		m_bconnectionPending = true;
		try {
			Thread connectThread = new Thread(this);
			connectThread.start();
		} catch (OutOfMemoryError oome) {
			// Give up on new connection
		}
	}

	public void run() {
		try {
			RASConnection connection = makeNewConnection();
			synchronized (this) {
				m_availableConnections.add(connection);
				m_bconnectionPending = false;
				//notifyAll();
                                notify();
			}
		} catch (Exception e) {
			// Give up on new connection and wait for existing one to free up.
		}
	}

	private RASConnection makeNewConnection() {

		// Establish network connection to appropriate Server
		RASConnection connection = new RASConnection();
		return (connection);
	}

	public synchronized void releaseConnection(RASConnection connection)
	throws Exception {
		//connection.closeConnection();
		m_busyConnections.remove(connection);

		//connection = makeNewConnection();
		m_availableConnections.add(connection);
		m_bconnectionPending = false;
		// Wake up threads that are waiting for a connection
		//notifyAll();
                notify();
	}

	public synchronized void closeAllConnections() {
		closeConnections(m_availableConnections);
		m_availableConnections = Collections.synchronizedList(new ArrayList<RASConnection>());
		closeConnections(m_busyConnections);
		m_busyConnections = Collections.synchronizedList(new ArrayList<RASConnection>());
	}

	private void closeConnections(List<RASConnection> connections) {
		try {
			for (RASConnection connection : connections) {
				connection.closeConnection();
			}
		} catch (Exception e) {
			// Ignore errors; garbage collect anyhow
		}
	}

	public synchronized int getNumberOfAvailableConnections() {
		return m_availableConnections.size();
	}

	public synchronized int getNumberOfBusyConnections() {
		return m_busyConnections.size();
	}

	@Override
	public synchronized String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Class: ").append(this.getClass().getName()).append("\n");
		result.append(" available: ").append(m_availableConnections.size())
		.append("\n");
		result.append(" busy: ").append(m_busyConnections.size()).append("\n");
		result.append(" max: ").append(m_nMaxConnections).append("\n");
		return result.toString();
	}

}
