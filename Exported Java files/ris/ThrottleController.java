package ris;

import static ray.rage.scene.SkeletalEntity.EndType.NONE;

import java.io.IOException;

import ris.MyGame;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;

public class ThrottleController {
	
	private SceneManager sm;
	private Engine eng;
	private MyGame g;
	private SceneNode ship;
	private SkeletalEntity rightHand;

	public ThrottleController(SceneManager sm, Engine eng, MyGame g, SceneNode ship) throws IOException {
		this.sm = sm;
		this.eng = eng;
		this.g = g;
		this.ship = ship;
	}
	
	private int throttleSign = 0;
	private int lastThrottleSign;
	private float timer = 0;
	
	private boolean latch = true;
	
	private float throttleBackTime = 1;
	public void update(float deltaTime) {
		timer += deltaTime;
		lastThrottleSign = throttleSign;
		throttleSign = g.getThrottleSign();
		
		
		switch(throttleSign) {
			//throttle backward
			case -1:
				if(lastThrottleSign == 0) {
					g.throttleDownAndPauseAnimation();
				}
				break;
			
			//no throttle
			case 0:
				if(lastThrottleSign == -1) {
					g.throttleBackFromDownAnimation();
				}
				if(lastThrottleSign == 1) {
					g.throttleBackFromUpAnimation();
				}
				break;
			
			//throttle forward
			case 1:
				if(lastThrottleSign == 0) {
					g.throttleUpAndPauseAnimation();
				}
				break;
		}
		
		//"throttleUpAndPause"
		//"throttleDownAndBackAnimation"
		//"throttleBackFromUp"
		//"throttleBackFromDown"
	}
}



















