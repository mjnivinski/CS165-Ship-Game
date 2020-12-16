package ris;

import java.io.IOException;

import ray.rage.Engine;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

public class ShipHudMaker {

	private SceneManager sm;
	private Engine eng;
	
	public ShipHudMaker(Engine e, SceneManager s) {
		sm = s;
		eng = e;
	}
	
	public SceneNode throttleIndicator(String name) throws IOException {
		
		SceneNode tin = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity tie = sm.createEntity(name + "entity", "entityfile");
		tie.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("throttletexture");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		tie.setRenderState(texState);
		tie.setMaterial(mat);
		
		tin.attachObject(tie);
		
		return tin;
	}
}
