package de.ea.winterpokalIBC.persistence.remote;

import java.util.HashMap;

import de.ea.winterpokalIBC.model.WinterpokalException;

public class DAORemoteException extends WinterpokalException {

	private HashMap<String, String> errors = new HashMap<String, String>();

	public DAORemoteException(String message, HashMap<String, String> errors) {
		super(message);
		this.setErrors(errors);
	}

	public HashMap<String, String> getErrors() {
		return errors;
	}

	public void setErrors(HashMap<String, String> errors) {
		this.errors = errors;
	}
}
