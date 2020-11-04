package a3;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import a3.myGameEngine.flightControls.FlightController;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.rage.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.util.BufferUtil;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
//my comment
public class MyGame extends VariableFrameRateGame {

	//Declaration area
	Random random = new Random();

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, counterStr, dispStr;

	int elapsTimeSec, counter = 0;

	//private CameraController cameraController;
	private Camera camera;
	private SceneNode dolphinN;
	
	private SceneNode[] earthPlanets = new SceneNode[13];

	private InputManager im;
	private TextureManager tm;
	
	private FlightController playerController;
	
	private float planetHeight = 1f;
	private float speedScale = 4;
	private float yawDegrees = 80;
	private float pitchDegrees = 80;

	public MyGame() {
		super();
	}

	//faster than typing System.out.println();
	private void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		Game game = new MyGame();
		try {
			game.startup();
			game.run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			game.shutdown();
			game.exit();
		}
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		print("setupWindow");
		//rs.createRenderWindow(true);
		rs.createRenderWindow(new DisplayMode(1002, 700, 24, 60), false);
	}
	
	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		//setupViewports(rw);
		
		print("setupCameras");
		SceneNode rootNode = sm.getRootSceneNode();
		Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
		rw.getViewport(0).setCamera(camera);

		camera.setRt((Vector3f) Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f) Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f) Vector3f.createFrom(0.0f, 0.0f, -1.0f));
		camera.setPo((Vector3f) Vector3f.createFrom(0.0f, 0.0f, 0.0f));
		SceneNode cameraNode = rootNode.createChildSceneNode(camera.getName() + "Node");
		cameraNode.attachObject(camera);
		camera.setPo((Vector3f) Vector3f.createFrom(0, 0, 1f));
		this.camera = camera;
		camera.setMode('r');
	}

	/*
	private void setupViewports(RenderWindow rw) {
		//Viewport topViewport = rw.getViewport(0);
		topViewport = rw.getViewport(0);
		topViewport.setDimensions(0.51f, 0.01f, 0.99f, 0.49f);
		topViewport.setClearColor(new Color(0.1f,0.1f,0.1f));
		
		//Viewport botViewport = rw.createViewport(0.01f, 0.01f, 0.99f, 0.49f);
		botViewport = rw.createViewport(0.01f, 0.01f, 0.99f, 0.49f);
		botViewport.setClearColor(new Color(0.5f,0.5f,0.5f));
	}*/
	
	@Override
	protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		
		print("Setup Scene");
		setupPlanets(eng, sm);
		setupDolphin(eng, sm);
		
	   	if (tm == null)
				tm = eng.getTextureManager();
	    	
	    	SkyBox sky = sm.createSkyBox("thesky");
	    	
	    	Texture skyFront = tm.getAssetByPath("2front.png");
	    	Texture skyBack = tm.getAssetByPath("2back.png");
	    	Texture skyBottom = tm.getAssetByPath("2top.png");
	    	Texture skyTop = tm.getAssetByPath("2bottom.png");
	    	Texture skyLeft = tm.getAssetByPath("2left.png");
	    	Texture skyRight = tm.getAssetByPath("2right.png");
	    	
	    	
	    	AffineTransform skyTransform = new AffineTransform();
	    	skyTransform.translate(0.0, skyFront.getImage().getHeight());
	    	skyTransform.scale(1.0, 1.0);
	    	skyFront.transform(skyTransform);
	    	skyBack.transform(skyTransform);
	    	skyLeft.transform(skyTransform);
	    	skyRight.transform(skyTransform);
			skyTop.transform(skyTransform);
			skyBottom.transform(skyTransform);
	    	
	    	sky.setTexture(skyFront, SkyBox.Face.FRONT);
	    	sky.setTexture(skyBack, SkyBox.Face.BACK);
	    	sky.setTexture(skyTop, SkyBox.Face.TOP);
	    	sky.setTexture(skyBottom, SkyBox.Face.BOTTOM);
	    	sky.setTexture(skyLeft, SkyBox.Face.LEFT);
	    	sky.setTexture(skyRight, SkyBox.Face.RIGHT);
	    	
	    	sm.setActiveSkyBox(sky);
	    	
	    	Tessellation tessE = sm.createTessellation("tessE", 7);
	    	// subdivisions per patch: min=0, try up to 32
	    	tessE.setSubdivisions(8f);
	    	SceneNode tessN =
	    	sm.getRootSceneNode().
	    	
	    	
	    	createChildSceneNode("TessN");
	    	tessN.attachObject(tessE);
	    	// to move it, note that X and Z must BOTH be positive OR negative
	    	tessN.translate(Vector3f.createFrom(-6.2f, -2.2f, 2.7f));
	    	// tessN.yaw(Degreef.createFrom(37.2f));
	    	tessN.scale(10, 144, 35);
	    	tessE.setHeightMap(this.getEngine(), "scribble.jpg");
	    	tessE.setTexture(this.getEngine(), "carpet.png");
		
		
		//setupFloor();
		
		//camera.getParentNode().lookAt(dolphinN);
		camera.getParentNode().yaw(Degreef.createFrom(180));
		camera.getParentNode().moveUp(2);
		
		dolphinN.setLocalPosition(0,2,-4);
		dolphinN.attachChild(camera.getParentNode());
		camera.getParentNode().setLocalPosition(0,0,0);
		print("dolphin position: " + dolphinN.getWorldPosition());
		
		//print("" + camera.getParentNode().getWorldPosition());

		//setupPyramid(eng, sm);

		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));

		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
		plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
		plight.setRange(5f);

		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
		plightNode.attachObject(plight);

		//headlight goes in dolphin
		Light headlight = sm.createLight("headlight", Light.Type.SPOT);
		headlight.setConeCutoffAngle(Degreef.createFrom(10));
		headlight.setSpecular(Color.white);

		SceneNode headlightNode = sm.getRootSceneNode().createChildSceneNode("headlightNode");
		headlightNode.attachObject(headlight);

		this.getEngine().getSceneManager().getSceneNode("myDolphinNode").attachChild(headlightNode);

		setupInputs();
	}
	
	//dolphin is setup with code provided
	private void setupDolphin(Engine eng, SceneManager sm) throws IOException {
		print("setupDolpin");
		Entity dolphinE = sm.createEntity("myDolphin", "cockpitMk2b.obj");
		dolphinE.setPrimitive(Primitive.TRIANGLES);

		//SceneNode dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
		dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");

		dolphinN.setLocalPosition((Vector3f) Vector3f.createFrom(-2, 0, 0));
		dolphinN.attachObject(dolphinE);
		dolphinN.yaw(Degreef.createFrom(180));

		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
	}

	//same initialization as dolphin, with a few rotation controllers.
	private void setupPlanets(Engine eng, SceneManager sm) throws IOException {
		
		for(int i=0; i<earthPlanets.length; i++) {
			earthPlanets[i] = setupPlanet(eng,sm,"earthPlanet" + i, "earth-day.jpeg");
			earthPlanets[i].setLocalPosition(Vector3f.createFrom(0,planetHeight,0));
		}
		
		earthPlanets[0].setLocalPosition(-3,planetHeight,-10);
		earthPlanets[1].setLocalPosition(3,planetHeight,-10);
		earthPlanets[2].setLocalPosition(20,planetHeight,-20);
		earthPlanets[3].setLocalPosition(14,planetHeight,-30);
		earthPlanets[4].setLocalPosition(20,planetHeight,-5);
		earthPlanets[5].setLocalPosition(-10,planetHeight,-20);
		earthPlanets[6].setLocalPosition(-20,planetHeight,-15);
		earthPlanets[7].setLocalPosition(-30,planetHeight,-5);
		earthPlanets[8].setLocalPosition(30,planetHeight,-5);
		earthPlanets[9].setLocalPosition(0,planetHeight,-40);
		earthPlanets[10].setLocalPosition(-15,planetHeight,-40);
		earthPlanets[11].setLocalPosition(15,planetHeight,-40);
		earthPlanets[12].setLocalPosition(-10,planetHeight,-15);
	}
	
	private SceneNode setupPlanet(Engine eng, SceneManager sm, String name, String texName) throws IOException {
		Entity planetE = sm.createEntity(name, "sphere.obj");
		planetE.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath(texName);
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		planetE.setRenderState(texState);
		planetE.setMaterial(mat);
		
		SceneNode planetN = sm.getRootSceneNode().createChildSceneNode(planetE.getName() + "Node");
		planetN.attachObject(planetE);
		
		return planetN;
	}
	
	private void setupFloor() throws IOException {
		Engine eng = getEngine();
		SceneManager sm = eng.getSceneManager();
		ManualObject floor = floor(eng, sm);
		
		SceneNode floorN = sm.getRootSceneNode().createChildSceneNode("floorN");
		floorN.attachObject(floor);
		floorN.setLocalPosition(0,-1,0);
		floorN.setLocalScale(Vector3f.createFrom(100,100,100));
	}
	
	private ManualObject floor(Engine eng, SceneManager sm) throws IOException {
		ManualObject floor = sm.createManualObject("floor");
		ManualObjectSection floorSec = floor.createManualSection("floorSec");
		floor.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		
		float[] vertices = new float[] {
			-1f,0,1f,//bottom right
			1f,0,1f,//top right
			1f,0,-1f,//top left
			-1f,0,1f,
			1f,0,-1f,
			-1f,0,-1f
		};
		
		float[] texcoords = new float[] {
				1,0, 1,1, 0,1,
				1,0, 0,1, 0,0
		};
		
		float[] normals = new float[] {
				0,1f,0,
				0,1f,0,
				0,1f,0,
				0,1f,0,
				0,1f,0,
				0,1f,0
		};
		
		int[] indices = new int[] {
				0,1,2,3,4,5
		};
		
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

		floorSec.setVertexBuffer(vertBuf);
		floorSec.setTextureCoordsBuffer(texBuf);
		floorSec.setNormalsBuffer(normBuf);
		floorSec.setIndexBuffer(indexBuf);

		Texture tex = eng.getTextureManager().getAssetByPath("carpet.png");
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

		floor.setDataSource(DataSource.INDEX_BUFFER);
		floor.setRenderState(texState);
		floor.setRenderState(faceState);
		
		return floor;
	}

	//seperate methods for keyboards/gamepads
	protected void setupInputs() {
		im = new GenericInputManager();
		playerController = new FlightController(this, camera, camera.getParentSceneNode(), dolphinN, im);
	}
	
	@Override
	protected void update(Engine engine) {
		
		updateDefaults(engine);
		
		
		playerController.update();
		
	}

	float testLerp = 0f;
	float elapsedTestTime = 10f;
	
	// method holds all actions that are used in every game, such as rendering and
	// calculating elapsed time.
	private void updateDefaults(Engine engine) {
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		im.update(elapsTime);
	}

	public float getSpeedScale() {
		return speedScale;
	}

	public float getYawDegrees() {
		return yawDegrees;
	}

	public float getPitchDegrees() {
		return pitchDegrees;
	}
}
