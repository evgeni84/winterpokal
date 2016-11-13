package de.ea.winterpokal.persistence.remote;

import java.util.HashMap;

import com.google.gson.internal.StringMap;

public class ResponseBase {
	private String status;

	private Object messages;

	public Object getMessages() {
		return messages;
	}

	public void setMessages(Object messages) {
		this.messages = messages;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public HashMap<String, String> getMessagesAsMap() {
		StringMap<String> errors = (StringMap<String>) getMessages();

		HashMap<String, String> errorMap = new HashMap<String, String>();
		if (errors != null)
			errorMap.putAll(errors);

		return errorMap;
	}
}
