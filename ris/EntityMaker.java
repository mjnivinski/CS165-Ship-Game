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

public class EntityMaker {

	private SceneManager sm;
	private Engine eng;
	
	public EntityMaker(Engine e, SceneManager s) {
		sm = s;
		eng = e;
	}
	
	public Entity earth(String name) throws IOException {
		
		Entity planet = sm.createEntity(name, "EnemyCraftVer2-b.obj");
		planet.setPrimitive(Primitive.TRIANGLES);
		//planet.setPrimitive(Primitive.TRIANGLES);
		
		//Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
			
		//Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
			
	//	TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
	//	texState.setTexture(tex);
	//	planet.setRenderState(texState);
	//	planet.setMaterial(mat);
			
			
		return planet;
	}
	
	public Entity throttleIndicator(String name) throws IOException {
		Entity ti = sm.createEntity(name, "");
		ti.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("throttletexture");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		ti.setRenderState(texState);
		ti.setMaterial(mat);
		
		return ti;
	}
	
	
}
