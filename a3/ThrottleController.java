package a3;

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

	public ThrottleController(SceneManager sm, Engine eng, MyGame g, SceneNode ship) {
		this.sm = sm;
		this.eng = eng;
		this.g = g;
		this.ship = ship;
	}
	
	private void createAnimations(SceneManager sm) throws IOException {
		 

	    //Right Hand	
	  	
	    	
		SkeletalEntity rightHand = sm.createSkeletalEntity("rightHandAv", "MyFettHandVer5.rkm", "MyFettHandVer5.rks");
	    	
	    	
		Texture tex6 = sm.getTextureManager().getAssetByPath("FettArmVer5.png");
	    TextureState tstate6 = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	    tstate6.setTexture(tex6);
	    rightHand.setRenderState(tstate6);
	   	
	    SceneNode rightHandN =
	    			sm.getRootSceneNode().createChildSceneNode("rightHandNode");
	    			rightHandN.attachObject(rightHand);
	    			rightHandN.scale(0.1f, 0.1f, 0.1f);
	    			rightHandN.translate(0, 0.5f, 0);
	    			
	    		
	    			
	    rightHand.loadAnimation("throttleUpAnimation", "MyFettHandVer5_Thrust_Up.rka");
	    rightHand.loadAnimation("throttleUpReturnAnimation", "MyFettHandVer5_Thrust_Up_Return.rka");
	    rightHand.loadAnimation("throttleDownAnimation", "MyFettHandVer5_Thrust_Down.rka");
	    rightHand.loadAnimation("throttleDownReturnAnimation", "MyFettHandVer5_Thrust_Down_Return.rka");
	    			
	    			
	    //FlagPlatform
	    /*			
		SkeletalEntity flagPlatform =
							sm.createSkeletalEntity("flagAv", "FlagIndicatorVer2.rkm", "FlagIndicatorVer2.rks");
			    	
			    	Texture tex7 = sm.getTextureManager().getAssetByPath("FlagshipIndicatorVer2.png");
			    	TextureState tstate7 = (TextureState) sm.getRenderSystem()
			    	.createRenderState(RenderState.Type.TEXTURE);
			    	tstate6.setTexture(tex7);
			    	flagPlatform.setRenderState(tstate7);
			    	
			    	SceneNode flagPlatformN =
			    			sm.getRootSceneNode().createChildSceneNode("FlagNode");	
			    	flagPlatformN.attachObject(flagPlatform);
			    	flagPlatformN.scale(0.1f, 0.1f, 0.1f);
			    	flagPlatformN.translate(0, 0.5f, 0);
			    	
			    	flagPlatformN.moveBackward(1f);
			    	
			    			
			    	flagPlatform.loadAnimation("flagLitAnimation", "FlagLit.rka");
			    	flagPlatform.loadAnimation("flagUnlitAnimation", "FlagUnlit.rka");
			    	flagPlatform.loadAnimation("flagLitExtendAnimation", "FlagLitExtended.rka");
	    	

		*/
		}
}
