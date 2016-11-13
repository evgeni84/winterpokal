package de.ea.winterpokal.utils.web;

public enum RequestType {

	GET("GET"),
	POST("POST");
	
	RequestType(String type){
		this.type= type;
	}
	
	public String getRequestType(){
		return type;
	}
	
	 String type;
}
