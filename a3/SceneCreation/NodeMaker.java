package a3.SceneCreation;

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
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NodeMaker {

	private SceneManager sm;
	private Engine eng;
	private SceneNode ship;
	
	public NodeMaker(Engine e, SceneManager s) {
		sm = s;
		eng = e;
		ship = sm.getSceneNode("shipNode");
	}
	
	public SceneNode[] makeLasers() throws IOException {
		SceneNode[] list = new SceneNode[4];
		
		for(int i=0; i<4; i++) {
			String s = "playerLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		
		return list;
	}
	
	public SceneNode makeLaser(String name) throws IOException {
		
		SceneNode ln = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity le = sm.createEntity(name + "laser", "sphere.obj");
		le.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		le.setRenderState(texState);
		le.setMaterial(mat);
		
		ln.attachObject(le);
		
		float scale = 0.08f;
		
		ln.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		return ln;
	}
	
	
	float throttleGap = 0.2f;
	public SceneNode[] makeThrottleIndicators() throws IOException {
		SceneNode[] theHud = new SceneNode[10];
		
		for(int i=0; i<10; i++) {
			theHud[i] = makeThrottleIndicator("throttleIndicator" + i);
		}
		
		SceneNode ship = sm.getSceneNode("shipNode");
		
		for(int i=0;i<10;i++) {
			theHud[i].setLocalPosition(-1 * i*throttleGap + (throttleGap*10/2),0,1);
			
		}
		return theHud;
	}
	
	private SceneNode makeThrottleIndicator(String name) throws IOException {
		
		SceneNode ti = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity tie = sm.createEntity(name, "sphere.obj");
		tie.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		tie.setRenderState(texState);
		tie.setMaterial(mat);
		
		ti.attachObject(tie);
		
		float scale = 0.1f;
		
		ti.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		ship.attachChild(ti);
		
		return ti;
	}
	
	float scoreGap = 0.2f;
	public SceneNode[] makeScoreIndicators() throws IOException {
		SceneNode[] theHud = new SceneNode[10];
		
		for(int i=0; i<10; i++) {
			theHud[i] = makeThrottleIndicator("throttleIndicator" + i);
		}
		
		SceneNode ship = sm.getSceneNode("shipNode");
		
		for(int i=0;i<3;i++) {
			theHud[i].setLocalPosition(-1 * i*throttleGap + (throttleGap*10/2),0,1);
			
		}
		return theHud;
	}
	
	private SceneNode makeScoreIndicator(String name) throws IOException {
		
		SceneNode ti = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity tie = sm.createEntity(name, "sphere.obj");
		tie.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		tie.setRenderState(texState);
		tie.setMaterial(mat);
		
		ti.attachObject(tie);
		
		float scale = 0.1f;
		
		ti.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		ship.attachChild(ti);
		
		return ti;
	}
}
