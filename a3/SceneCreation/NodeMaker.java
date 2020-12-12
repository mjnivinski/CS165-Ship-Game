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

public class NodeMaker {

	private SceneManager sm;
	private Engine eng;
	
	public NodeMaker(Engine e, SceneManager s) {
		sm = s;
		eng = e;
	}
	
	public SceneNode makeLaser(String name) throws IOException {
		
		SceneNode ln = sm.getRootSceneNode().createChildSceneNode(name);
		
		//Entity le = sm.createEntity(name + "laser", "thelaserfile");
		
		Entity le = sm.createEntity(name + "laser", "sphere.obj");
		le.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		le.setRenderState(texState);
		le.setMaterial(mat);
		
		ln.attachObject(le);
		
		return ln;
	}
	
	public SceneNode[] makeLasers() throws IOException {
		SceneNode[] list = new SceneNode[4];
		
		for(int i=0; i<4; i++) {
			String s = "playerLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		return list;
	}
}
