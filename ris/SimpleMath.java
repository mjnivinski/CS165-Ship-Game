package ris;

public class SimpleMath {
	public static float lerp(float start, float end, float t) {
		if(t > 1) t = 1;
		return start * (1 - t) + end * t;
	}
	
	public static float parabolicSmooth(float v) {
		if(v < 0) {
			v*=v;
			v*=-1;
		}
		else v*=v;
		
		return v;
	}
}