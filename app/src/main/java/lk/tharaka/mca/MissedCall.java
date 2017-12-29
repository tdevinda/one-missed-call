package lk.tharaka.mca;

public class MissedCall {

	public int simSlot = 0;
	public String number;
	public String name = "";
	public int count;
	public String date;
	public String ad;
	
	@Override
	public String toString() {
		return "Number: "+ number +" Name: "+ name +" Count: "+ count +" Date:"+ date + " Ad:"+ ad;
	}
}
