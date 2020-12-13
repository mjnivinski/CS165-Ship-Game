package a3.SceneCreation;

import java.io.IOException;

import a3.MyGame;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsObject;
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
	private PhysicsEngine physics;
	private SceneNode ship;
	
	public NodeMaker(Engine e, SceneManager s, PhysicsEngine physics) {
		sm = s;
		eng = e;
		this.physics = physics;
		ship = sm.getSceneNode("shipNode");
	}
	
	public SceneNode[] makeLasers() throws IOException {
		int laserCount = 16;
		SceneNode[] list = new SceneNode[laserCount];
		
		for(int i=0; i<list.length; i++) {
			String s = "playerLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		
		return list;
	}
	
	public SceneNode makeLaser(String name) throws IOException {
		
		SceneNode ln = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity le = sm.createEntity(name + "laser", "otherLaser.obj");
		le.setPrimitive(Primitive.TRIANGLES);
		
		ln.attachObject(le);
		
		float scale = 1f;
		
		ln.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] temptf;
		
		temptf = MyGame.toDoubleArray(ln.getLocalTransform().toFloatArray());
		PhysicsObject shipPhysicsObject = physics.addSphereObject(physics.nextUID(), mass, temptf, 1.0f);
		shipPhysicsObject.setDamping(0, 0);
		ln.setPhysicsObject(shipPhysicsObject);
		
		return ln;
	}
	
	
	float throttleGap = 0.2f;
	public SceneNode[] makeThrottleIndicators() throws IOException {
		SceneNode[] theHud = new SceneNode[10];
		
		for(int i=0; i<10; i++) {
			theHud[i] = makeThrottleIndicator("throttleIndicator" + i);
		}
		
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
			theHud[i] = makeScoreIndicator("scoreIndicator" + i);
		}
		
		for(int i=0;i<3;i++) {
			theHud[i].setLocalPosition(-1 * i*throttleGap + (throttleGap*10/2),0,1);
		}
		return theHud;
	}
	
	private SceneNode makeScoreIndicator(String name) throws IOException {
		
		SceneNode n = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity ne = sm.createEntity(name, "sphere.obj");
		ne.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath("earth-day.jpeg");
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		ne.setRenderState(texState);
		ne.setMaterial(mat);
		
		n.attachObject(ne);
		
		float scale = 0.1f;
		
		n.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		ship.attachChild(n);
		
		return n;
	}
}
