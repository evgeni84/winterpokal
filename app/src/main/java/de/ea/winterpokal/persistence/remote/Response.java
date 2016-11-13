package de.ea.winterpokal.persistence.remote;


public class Response<T> extends ResponseBase {
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
