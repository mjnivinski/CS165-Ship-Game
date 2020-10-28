package a3.myGameEngine;

public class SimpleMath {
	public static float lerp(float start, float end, float t) {
		if(t > 1) t = 1;
		return start * (1 - t) + end * t;
	}
}