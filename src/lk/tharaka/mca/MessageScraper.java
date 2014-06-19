package lk.tharaka.mca;

import java.util.ArrayList;

public class MessageScraper {
	
	private static String missedCallsFrom = " Missed call(s) from";
	
	public void replaceNamesForNumbers(String message) {
		
		String numberEncountered = message.replaceAll("\\d"+ missedCallsFrom + " (\\d+).*", "$1");
		System.out.println(numberEncountered);
		
		
	}
	
	
}
