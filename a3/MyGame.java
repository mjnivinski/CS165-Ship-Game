package a3;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import a3.Networking.GhostAvatar;
import a3.Networking.ProtocolClient;
import a3.myGameEngine.flightControls.FlightController;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.audio.AudioManagerFactory;
import ray.audio.AudioResource;
import ray.audio.AudioResourceType;
import ray.audio.IAudioManager;
import ray.audio.Sound;
import ray.audio.SoundType;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.rage.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.RotationController;
import ray.rage.util.BufferUtil;
import ray.rage.util.Configuration;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.networking.IGameConnection.ProtocolType;
import java.util.UUID;
import java.util.Vector;
import static ray.rage.scene.SkeletalEntity.EndType.*;
import static ray.rage.scene.SkeletalEntity.*;


public class MyGame extends VariableFrameRateGame {

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isConnected;
	private Vector<UUID> gameObjectsToRemove;
	
	public IAudioManager audioMgr;
	Sound backgroundMusic, flagUp;
	
	
	
	//Declaration area
	Random random = new Random();
	
	Engine eng;

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, counterStr, dispStr;

	int elapsTimeSec, counter = 0;

	//private CameraController cameraController;
	private Camera camera;
	//private SceneNode dolphinN, stationN;
	private SceneNode shipN, stationN, terrainContN, enemyCraftN, dropShipN, rightHandN;
	//private SkeletalEntity rightHand;
	
	private SceneNode[] earthPlanets = new SceneNode[13];
	
	ControlTest controlTest;
	
	

	private InputManager im;
	private TextureManager tm;
	
	private FlightController playerController;
	
	private float planetHeight = 1f;
	private float speedScale = 4;
	private float yawDegrees = 80;
	private float pitchDegrees = 80;

	public MyGame(String serverAddr, int sPort) {
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.UDP;
	}

	//faster than typing System.out.println();
	private void print(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		System.out.println("args: " + args[0] + " " + args[1]);
		Game game = new MyGame(args[0], Integer.parseInt(args[1]));
		//Game game = new MyGame("yes", 5);
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
	
	private void setupNetworking() {
		print("setupNetworking");
		gameObjectsToRemove = new Vector<UUID>();
		isConnected = false;
		try {
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		}
		catch(UnknownHostException e) {e.printStackTrace();}
		catch(IOException e) {e.printStackTrace(); }
		
		
		
		if(protClient == null) {
			print("missing protocol host");
		}
		else {
			//ask client protocol to send initial join message
			//to server, with a unique modifier for this client
			
			print("sendJoinMessage()");
			
			protClient.sendJoinMessage();
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
		this.eng = eng;
		print("Setup Scene");
		setupPlanets(eng, sm);
		setupShip(eng, sm);
		
	
		
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
	    	tessN.translate(Vector3f.createFrom(-6.2f, -2.2f, 3.2f));
	    	// tessN.yaw(Degreef.createFrom(37.2f));
	    	tessN.scale(10, 144, 35);
	    	tessE.setHeightMap(this.getEngine(), "scribble.jpg");
	    	tessE.setTexture(this.getEngine(), "carpet.png");
	    	
	    	tessN.setLocalPosition(-15.0f, 0.0f, -45.0f);

			Entity terrainCont = sm.createEntity("terrainCont", "TerrainContainerb.obj");
	    	terrainCont.setPrimitive(Primitive.TRIANGLES);
	    	terrainContN = sm.getRootSceneNode().createChildSceneNode(terrainCont.getName() + "Node");
	    	terrainContN.setLocalPosition(-15.0f, 0.0f, -40.0f);
	    	terrainContN.setLocalScale(.062f, .1f, .155f);
	    	terrainContN.moveBackward(7.0f);
	    	terrainContN.moveUp(.1f);
	    	terrainContN.attachObject(terrainCont);
	    	
	    	//terrainContN.attachChild(tessN);
	    	
	    	
	    	Entity stationE = sm.createEntity("spacestation", "SpaceStationAlpha-b.obj");
	    	stationE.setPrimitive(Primitive.TRIANGLES);
			stationN = sm.getRootSceneNode().createChildSceneNode(stationE.getName() + "Node");
			stationN.moveForward(7.0f);
			stationN.moveUp(.1f);
			stationN.moveLeft(4f);
			stationN.attachObject(stationE);
			
			RotationController rc2 =
			    	new RotationController(Vector3f.createUnitVectorY(), .02f);
			    	rc2.addNode(stationN);
			    	sm.addController(rc2);
		
			    	Entity enemyCraftE = sm.createEntity("enemyCraft", "EnemyCraftVer2-b.obj");
			    	enemyCraftE.setPrimitive(Primitive.TRIANGLES);
			    	enemyCraftN = sm.getRootSceneNode().createChildSceneNode(enemyCraftE.getName() + "Node");
			    	enemyCraftN.moveBackward(7.0f);
			    	enemyCraftN.moveDown(.1f);
			    	enemyCraftN.moveRight(4f);
			    	enemyCraftN.attachObject(enemyCraftE);
					
			    	Entity dropShipE = sm.createEntity("dropShip", "DropShipVer4.obj");
			    	dropShipE.setPrimitive(Primitive.TRIANGLES);
			    	dropShipN = sm.getRootSceneNode().createChildSceneNode(dropShipE.getName() + "Node");
			    	dropShipN.moveBackward(7.0f);
			    	dropShipN.moveDown(8f);
			    	dropShipN.moveRight(4f);
			    	dropShipN.attachObject(dropShipE);
			   /* 	
			    	
			    	SkeletalEntity manSE =
			    			sm.createSkeletalEntity("manAv", "myRobot.rkm", "myRobot.rks");
			    	
			    	Texture tex = sm.getTextureManager().getAssetByPath("blue.jpeg");
			    	TextureState tstate = (TextureState) sm.getRenderSystem()
			    	.createRenderState(RenderState.Type.TEXTURE);
			    	tstate.setTexture(tex);
			    	manSE.setRenderState(tstate);
			    	
			    	SceneNode manN =
			    			sm.getRootSceneNode().createChildSceneNode("manNode");
			    			manN.attachObject(manSE);
			    			manN.scale(0.1f, 0.1f, 0.1f);
			    			manN.translate(0, 0.5f, 0);
			    	*/
			  //  	manSE.loadAnimation("walkAnimation", "clap2.rka");
			   // 	manSE.loadAnimation("waveAnimation", "wave.rka");
			    	
			    	
			    	
			    	SkeletalEntity rightHand =
							sm.createSkeletalEntity("rightHandAv", "MyFettHandVer5.rkm", "MyFettHandVer5.rks");
			    	
			    	Texture tex6 = sm.getTextureManager().getAssetByPath("FettArmVer5.png");
			    	TextureState tstate6 = (TextureState) sm.getRenderSystem()
			    	.createRenderState(RenderState.Type.TEXTURE);
			    	tstate6.setTexture(tex6);
			   	rightHand.setRenderState(tstate6);
			    	
			    	SceneNode rightHandN =
			    			sm.getRootSceneNode().createChildSceneNode("rightHandNode");
			    			rightHandN.attachObject(rightHand);
			    			rightHandN.scale(0.1f, 0.1f, 0.1f);
			    			rightHandN.translate(0, 0.5f, 0);
			    			
			    		//	rightHand.loadAnimation("throttleUpAnimation", "FlagLit.rka");
			    		//	rightHand.loadAnimation("throttleUpReturnAnimation", "FlagUnlit.rka");
			    	
			    	
			    //	manSE.loadAnimation("walkAnimation", "walk.rka");
			    	
			  //enemyCraftN  	
			    	
		//setupFloor();
		
		camera.getParentNode().yaw(Degreef.createFrom(180));
		camera.getParentNode().moveUp(2);
		
		shipN.setLocalPosition(0,2,-4);
		shipN.attachChild(camera.getParentNode());
		camera.getParentNode().setLocalPosition(0,0,0);
		print("ship position: " + shipN.getWorldPosition());
		
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

		//headlight goes in ship
		Light headlight = sm.createLight("headlight", Light.Type.SPOT);
		headlight.setConeCutoffAngle(Degreef.createFrom(10));
		headlight.setSpecular(Color.white);

		SceneNode headlightNode = sm.getRootSceneNode().createChildSceneNode("headlightNode");
		headlightNode.attachObject(headlight);

		this.getEngine().getSceneManager().getSceneNode("myShipNode").attachChild(headlightNode);

		setupInputs();
		setupNetworking();
	//	initAudio(sm);
	}
	
	//ship is setup with code provided
	private void setupShip(Engine eng, SceneManager sm) throws IOException {
		print("setupShip");
		Entity shipE = sm.createEntity("myShip", "cockpitMk3j.obj");
		shipE.setPrimitive(Primitive.TRIANGLES);

		//SceneNode dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
		shipN = sm.getRootSceneNode().createChildSceneNode(shipE.getName() + "Node");

		shipN.setLocalPosition((Vector3f) Vector3f.createFrom(-2, 0, 0));
		shipN.attachObject(shipE);
		shipN.yaw(Degreef.createFrom(180));

		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
	}

	//same initialization as ship, with a few rotation controllers.
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
		playerController = new FlightController(this, camera, camera.getParentSceneNode(), shipN, im);
		
	//	setupAdditionalTestControls(im);
		
		//animationThrottleUp()
	}
	
	private void setupAdditionalTestControls(InputManager im) {
		ArrayList<Controller> controllers = im.getControllers();
		ArrayList<String> keyboards = new ArrayList<String>();
		
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).getType() == Controller.Type.KEYBOARD)
				keyboards.add(controllers.get(i).getName());
		}
		
		controlTest = new ControlTest();
		

		
		for (int i = 0; i < keyboards.size(); i++) {
			
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.F, controlTest,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
		}
	}
	
	private class ControlTest extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			animationThrottleUp();
		}
	}
	
	
	public void setupGhostAvatar(GhostAvatar ghost) throws IOException {
		
		SceneManager sm = eng.getSceneManager();
		
		SceneNode ghostN = ghost.getNode();
		
		print("setupGhostShip");
		
		Entity shipE = sm.createEntity("ghostShip" + ghost.getID() , "GhostShips-c.obj");
		shipE.setPrimitive(Primitive.TRIANGLES);

		//SceneNode dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
		ghostN = sm.getRootSceneNode().createChildSceneNode(shipE.getName() + "Node");

		ghostN.setLocalPosition((Vector3f) Vector3f.createFrom(-2, 0, 0));
		ghostN.attachObject(shipE);
		ghostN.yaw(Degreef.createFrom(180));
		
		ghost.setNode(ghostN);
	}
	
	@Override
	protected void update(Engine engine) {
		
		updateDefaults(engine);
		
		processNetworking(engine.getElapsedTimeMillis());
		
		
		playerController.update();
		
	//	SkeletalEntity rightHand =
	//(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
		
		//rightHand.update();
		
	//	SkeletalEntity manSE =
	//			(SkeletalEntity) engine.getSceneManager().getEntity("manAv");
	//	manSE.update();
		
	//	hereSound.setLocation(robotN.getWorldPosition());
	//	oceanSound.setLocation(earthN.getWorldPosition());
	//	setEarParameters(sm);
	}
	
	
	float networkTimer = 0;
	protected void processNetworking(float deltaTime) {
		
		networkTimer += deltaTime/1000;
		
		if(networkTimer > 0.3f) {
			networkTimer = 0;
			protClient.sendMoveMessage(getPlayerPosition());
		}
		
		if(protClient!=null) {
			protClient.processPackets();
			
			//remove ghost avatars for players who have left the game
			/*Iterator<UUID> it = gameObjectsToRemove.iterator();
			
			while(it.hasNext()) {
				
			}*/
		}
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
	
	public void setIsConnected(boolean b) { isConnected = b; }
	
	public Vector3 getPlayerPosition() {
		return shipN.getWorldPosition();
	}
	
	private void animationThrottleUp()
	{ 
	//EndType NONE = null;
	//	SkeletalEntity rightHand =
//	(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
	//rightHand.stopAnimation();
//	rightHand.playAnimation("throttleUpAnimation", 0.5f, NONE, 0);
//	rightHand.playAnimation("throttleUpReturnAnimation", 0.5f, NONE, 0);
//		SkeletalEntity manSE =
//				(SkeletalEntity) eng.getSceneManager().getEntity("manAv");
//		manSE.playAnimation("waveAnimation", 0.5f, LOOP, 0);
		
//		flagUp.play();
	}
	
	public void setEarParameters(SceneManager sm)
	{ SceneNode dolphinNode = sm.getSceneNode("dolphinNode");
	Vector3 avDir = dolphinNode.getWorldForwardAxis();
	// note - should get the camera's forward direction
	// - avatar direction plus azimuth
	audioMgr.getEar().setLocation(dolphinNode.getWorldPosition());
	audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
	}
	
	public void initAudio(SceneManager sm)
	{ 
		
		Configuration configuration = sm.getConfiguration();
		String sfxPath = configuration.valueOf("assets.sounds.path");
		String musicPath = configuration.valueOf("assets.music.path");
		AudioResource theMusic, theFlag;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
		
		if (!audioMgr.initialize()) {
			System.out.println("The Audio Manager failed to initialize :(");
			return;
		}
		
		theMusic = audioMgr.createAudioResource(musicPath + "bensound-epic.wav", AudioResourceType.AUDIO_STREAM);
	//	theFlag = audioMgr.createAudioResource(sfxPath + "energy_station.mp3", AudioResourceType.AUDIO_SAMPLE);

	

		
		backgroundMusic = new Sound(theMusic, SoundType.SOUND_MUSIC, 100, true);
	//	flagUp = new Sound(theFlag, SoundType.SOUND_EFFECT, 25, false);

		
	
			backgroundMusic.initialize(audioMgr);
			backgroundMusic.play(4, true);
			
			flagUp.initialize(audioMgr);


	}
	
	
	
	
}
