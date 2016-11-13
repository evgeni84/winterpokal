package de.ea.winterpokal;

import de.ea.winterpokal.utils.web.RemoteRequest;
import de.ea.winterpokal.utils.web.RequestType;

public class TestWP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteRequest
				.DoRequest(
						"https://winterpokal.rennrad-news.de//api/v1/users/get/40624.json",
						RequestType.GET);
	}

}
