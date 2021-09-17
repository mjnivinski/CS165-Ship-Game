package ris;

//This class helps calculate a circular deadzone, so when the stick has low vertical values but high horizontal values,
//that a pure horizontal check wont turn off.
//Since we want that horizontal precision when we are pushing the stick north, this class helps with that.
//This class is subject to being used properly. Every time the magnitude methods are called, it is imperative the stick values are up to date.f
public class DeadZones {
	public static float LHorizontal = 0;
	public static float LVertical = 0;
	public static float RHorizontal = 0;
	public static float RVertical = 0;
	
	public static float LMagnitude() {
		
		return (float) Math.sqrt((LHorizontal * LHorizontal) + (LVertical * LVertical));
	}
	
	public static float RMagnitude() {
		
		return (float) Math.sqrt((RHorizontal * RHorizontal) + (RVertical * RVertical));
	}
}
