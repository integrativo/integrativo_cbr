package br.ufpe.integrativocbr.plugin.prefs;

import java.io.Serializable;

public class DatabasePreference implements Serializable {

	private static final long serialVersionUID = -2793480993137224165L;
	
	private String host;
	private int port;
	private String userName;
	private char[] userPassword;
	private String databaseName;

	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(char[] password) {
		this.userPassword = password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
}
