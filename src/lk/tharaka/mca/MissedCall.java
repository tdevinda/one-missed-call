package lk.tharaka.mca;

public class MissedCall {
	
	public String number;
	public String name = "";
	public int count;
	public String date;
	
	@Override
	public String toString() {
		return "Number: "+ number +"Name: "+ name +"Count: "+ count +"Date:"+ date;
	}
}
