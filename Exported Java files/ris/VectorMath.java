package ris;

import ray.rml.Vector3;
import ray.rml.Vector3f;

//This class deals with certain vector math. Originally used to test how the camera worked, its primary function is the distance method.
public class VectorMath {
	
	public static void DotProduct(Vector3f v1, Vector3f v2, Vector3f v3) {
		float product = v1.x() * v2.x() * v3.x();
		product += v1.y() * v2.y() * v3.y();
		product += v1.z() * v2.z() * v3.z();
		
		System.out.println(product);
	}
	
	public static boolean isOrthogonal(Vector3f v1, Vector3f v2) {
		
		float product = v1.x() * v2.x();
		product += v1.y() * v2.y();
		product += v1.z() * v2.z();
		
		if(product == 0) return true;
		return false;
	}
	
	//Takes two vectors and returns the distance between.
	public static float distance(Vector3f v1, Vector3f v2) {
		
		float x = v1.x() - v2.x();
		float y = v1.y() - v2.y();
		float z = v1.z() - v2.z();
		x *= x;
		y *= y;
		z *= z;
		return (float)Math.sqrt(x+y+z);
	}
	
	//Takes two vectors and returns the distance between.
		public static float distance(Vector3 v1, Vector3 v2) {
			
			float x = v1.x() - v2.x();
			float y = v1.y() - v2.y();
			float z = v1.z() - v2.z();
			x *= x;
			y *= y;
			z *= z;
			return (float)Math.sqrt(x+y+z);
		}
	
	public static Vector3f lerp(Vector3f v1, Vector3f v2, float t) {
		float x,y,z;
		
		x = SimpleMath.lerp(v1.x(), v2.x(), t);
		y = SimpleMath.lerp(v1.y(), v2.y(), t);
		z = SimpleMath.lerp(v1.z(), v2.z(), t);
		
		return (Vector3f)Vector3f.createFrom(x,y,z);
	}
	
}
