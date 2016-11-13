package de.ea.winterpokalIBC;

import de.ea.winterpokalIBC.utils.web.RemoteRequest;
import de.ea.winterpokalIBC.utils.web.RequestType;

public class TestWP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteRequest
				.DoRequest(
						"https://winterpokalIBC.rennrad-news.de//api/v1/users/get/40624.json",
						RequestType.GET);
	}

}
