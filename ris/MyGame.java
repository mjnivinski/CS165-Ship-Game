package ris;

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

import a3.EntityMaker;
import a3.NPCS.Patroller.PatrolEnemy;
import a3.NPCS.Patroller.PatrolStrategyContext;
import a3.Networking.GhostAvatar;
import a3.Networking.ProtocolClient;
import a3.SceneCreation.NodeMaker;
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
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsObject;

import java.util.UUID;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import static ray.rage.scene.SkeletalEntity.EndType.*;
import ray.audio.*;
//import com.jogamp.openal.ALFactory;



public class MyGame extends VariableFrameRateGame {

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isConnected;
	private Vector<UUID> gameObjectsToRemove;
	
	public static boolean isTerrain;
	
	public IAudioManager audioMgr;
	Sound backgroundMusic, flagUp, stationSound;
	
	
	
	//Declaration area
	Random random = new Random();
	
	SceneManager sm;
	Engine eng;
	EntityMaker eMaker;
	NodeMaker nm;
	
	private PhysicsEngine physicsEng;

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, counterStr, dispStr;

	int elapsTimeSec, counter = 0;

	//private CameraController cameraController;
	private Camera camera;
	//private SceneNode dolphinN, stationN;
	private SceneNode shipN, stationN, terrainContN, enemyCraftN, dropShipN, rightHandN, 
	flagPlatformdN, laserBoltN, SecondShipN, Object4N, Object3N, Object2N, Object1N, stationBlueN, Object2bN, BlueCockpitN;
	
	private PhysicsObject shipPhysObj;
	
	private PatrolEnemy npc1;
	private SceneNode patrolNPC;
	
	private SceneNode[] earthPlanets = new SceneNode[13];
	
	throttleUp controlTest;
	throttleDown controlTest2;
	throttleLeft controlTest3;
	throttleRight controlTest4;
	destroyTerrain controlTest8;
	
	

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
		
		//FSEM();
		
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
		
		if(fullScreen == 0) {
			rs.createRenderWindow(true);
		}
		else if(fullScreen == 1) {
			rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		}
		
		
	}
	
	static int fullScreen = 1;
	private static void FSEM() {
		
		fullScreen = JOptionPane.showConfirmDialog(null,  "Full Screen?", "choose one", JOptionPane.YES_NO_OPTION);
	    //0 is yes
		//1 is no
		System.out.println("fullScreen: " + fullScreen);
	}
	
	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		
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

	@Override
	protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		this.sm = sm;
		this.eng = eng;
		eMaker = new EntityMaker(eng,sm);
		
		
		
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
	    	
			    	createAllNodes(sm);
			    	
			    	createAnimations(sm);
			    	
			    	



		
		setupNetworking();
		
		print("setup audio");
		//initAudio(sm);
		print("setup physics");
		setupPhysics();
		setupPatrolNPC(eng,sm);
		setupInputs(sm);
		print("setup done");
	}
	
	private void createAnimations(SceneManager sm) throws IOException {
 
		//Right Handl
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
    			//rightHandN.translate(0, 0.5f, 0);
    		
    		
    			
    			rightHand.loadAnimation("throttleUpAndBackAnimation", "ThrustUpAndBack.rka");
    			rightHand.loadAnimation("throttleDownAndBackAnimation", "ThrustDownAndBack.rka");
    			rightHand.loadAnimation("throttleLeftAndBackAnimation", "ThrustLeftandBack.rka");
    			rightHand.loadAnimation("throttleRightAndBackAnimation", "ThrustRightandBack.rka");
    			
    		
    			System.out.println("right here");
    			shipN.attachChild(rightHandN);
    			rightHandN.moveDown(0.5f);
    			
		
	}

	private void createAllNodes(SceneManager sm) throws IOException {
	  	Tessellation tessE = sm.createTessellation("tessE", 7);
    	tessE.setSubdivisions(8f);
    	SceneNode tessN =
    	sm.getRootSceneNode().
	    	createChildSceneNode("TessN");
    	tessN.attachObject(tessE);
    	tessN.translate(Vector3f.createFrom(-6.2f, -2.2f, 3.2f));
    	// tessN.yaw(Degreef.createFrom(37.2f));
    	tessN.scale(2000, 956, 2000);
    	tessE.setHeightMap(this.getEngine(), "scribble.jpg");
    	tessE.setTexture(this.getEngine(), "carpet.png");
    	tessE.setTextureTiling(55, 155);
    	tessE.setMultiplier(5);
    	
    	isTerrain = true;
    	
    	tessN.setLocalPosition(-200.0f, -50.0f, -45.0f);

    	
    	Entity stationE = sm.createEntity("station", "SpaceStationAlpha-b.obj");
    	stationE.setPrimitive(Primitive.TRIANGLES);
		stationN = sm.getRootSceneNode().createChildSceneNode("stationNode");
		stationN.moveBackward(60.0f);
		stationN.moveUp(25f);
		stationN.moveLeft(4f);
		stationN.attachObject(stationE);
		stationN.moveUp(10);
		
		RotationController rc2 =
		    	new RotationController(Vector3f.createUnitVectorY(), .03f);
		    	rc2.addNode(stationN);
		    	sm.addController(rc2);
		    	
		    	Entity Object2bE = sm.createEntity("object2b", "Object2.obj");
		    	Object2bE.setPrimitive(Primitive.TRIANGLES);
		    	Object2bN = sm.getRootSceneNode().createChildSceneNode(Object2bE.getName() + "Node");
		    	Object2bN.moveBackward(80.0f);
		    	Object2bN.moveUp(25f);
		    	Object2bN.moveLeft(4f);
		    	Object2bN.attachObject(Object2bE);
		    	
		    	//
		    	Entity stationBlueE = sm.createEntity("stationBlue", "SpaceStationAlpha-b.obj");
		    	stationBlueE.setPrimitive(Primitive.TRIANGLES);
		    	stationBlueN = sm.getRootSceneNode().createChildSceneNode(stationBlueE.getName() + "Node");
		    	stationBlueN.moveForward(350.0f);
		    	stationBlueN.moveUp(25f);
		    	stationBlueN.moveLeft(4f);
		    	stationBlueN.attachObject(stationBlueE);
				
				
				RotationController rc4 =
				    	new RotationController(Vector3f.createUnitVectorY(), .03f);
				    	rc4.addNode(stationBlueN);
				    	sm.addController(rc4);
				    	
				    	Entity Object2E = sm.createEntity("object2", "Object2.obj");
				    	Object2E.setPrimitive(Primitive.TRIANGLES);
				    	Object2N = sm.getRootSceneNode().createChildSceneNode(Object2E.getName() + "Node");
				    	Object2N.moveForward(370.0f);
				    	Object2N.moveUp(25f);
				    	Object2N.moveRight(4f);
				    	Object2N.attachObject(Object2E);
				    	
					      TextureManager tm = eng.getTextureManager();
					        Texture blueTexture = tm.getAssetByPath("stationBlue.png");
					        RenderSystem rs = sm.getRenderSystem();
					        TextureState state = (TextureState)rs.createRenderState(RenderState.Type.TEXTURE);
					        state.setTexture(blueTexture);
					        stationBlueE.setRenderState(state);
		    	
		    	
		    	Entity Object3E = sm.createEntity("object3", "Object3.obj");
		    	Object3E.setPrimitive(Primitive.TRIANGLES);
		    	Object3N = sm.getRootSceneNode().createChildSceneNode(Object3E.getName() + "Node");
		    	Object3N.moveForward(100.0f);
		    	Object3N.moveUp(72f);
		    	Object3N.moveRight(300f);
		    	Object3N.setLocalScale(20, 20, 20);
		    	Object3N.attachObject(Object3E);
				
				
				RotationController rc3 =
				    	new RotationController(Vector3f.createUnitVectorY(), .01f);
				    	rc3.addNode(Object3N);
				    	sm.addController(rc3);
				    	
				    	
				    	Entity Object4E = sm.createEntity("object4", "Object4.obj");
				    	Object4E.setPrimitive(Primitive.TRIANGLES);
				    	Object4N = sm.getRootSceneNode().createChildSceneNode(Object4E.getName() + "Node");
				    	Object4N.moveBackward(100.0f);
				    	Object4N.moveUp(150f);
				    	Object4N.moveLeft(300f);
				    	Object4N.setLocalScale(64, 64, 64);
				    	Object4N.attachObject(Object4E);
						
						
						RotationController rc5 =
						    	new RotationController(Vector3f.createUnitVectorZ(), -.06f);
						    	rc5.addNode(Object4N);
						    	sm.addController(rc5);
				    	
				    	
				    	Entity Object1E = sm.createEntity("object1", "Object1Ver2.obj");
				    	Object1E.setPrimitive(Primitive.TRIANGLES);
				    	Object1N = sm.getRootSceneNode().createChildSceneNode(Object1E.getName() + "Node");
				    	Object1N.moveBackward(400.0f);
				    	Object1N.moveUp(88f);
				    	Object1N.moveRight(100f);
				    //	Object1N.setLocalScale(20, 20, 20);
				    	Object1N.attachObject(Object1E);
				    	
				    	

				    	
				    	Entity BlueCockpitE = sm.createEntity("BlueCockput", "cockpitMk3j.obj");
				    	BlueCockpitE.setPrimitive(Primitive.TRIANGLES);
				    	BlueCockpitN = sm.getRootSceneNode().createChildSceneNode(BlueCockpitE.getName() + "Node");
				    	BlueCockpitN.moveForward(0.0f);
				    	BlueCockpitN.moveUp(25f);
				    	BlueCockpitN.moveRight(0f);
				    	BlueCockpitN.attachObject(BlueCockpitE);
				    	
					      TextureManager tm1 = eng.getTextureManager();
					        Texture blueTexture2 = tm1.getAssetByPath("cockpitMk3jB-Blue.png");
					        RenderSystem rs2 = sm.getRenderSystem();
					        TextureState state1 = (TextureState)rs.createRenderState(RenderState.Type.TEXTURE);
					        state1.setTexture(blueTexture2);
					        BlueCockpitE.setRenderState(state1);
				    	

				    	
		/*	
				    	
	
		    	Entity enemyCraftE = sm.createEntity("enemyCraft", "EnemyCraftVer2-b.obj");
		    	enemyCraftE.setPrimitive(Primitive.TRIANGLES);
		    	enemyCraftN = sm.getRootSceneNode().createChildSceneNode(enemyCraftE.getName() + "Node");
		    	enemyCraftN.moveBackward(7.0f);
		    	enemyCraftN.moveUp(100f);
		    	enemyCraftN.moveRight(4f);
		    	enemyCraftN.attachObject(enemyCraftE);
				*/
		    	Entity dropShipE = sm.createEntity("dropShip", "DropShipVer4.obj");
		    	dropShipE.setPrimitive(Primitive.TRIANGLES);
		    	dropShipN = sm.getRootSceneNode().createChildSceneNode(dropShipE.getName() + "Node");
		    	dropShipN.moveBackward(30.0f);
		    	dropShipN.moveUp(55f);
		    	dropShipN.moveRight(4f);
		    	dropShipN.attachObject(dropShipE);
		    	
		
		    	
				camera.getParentNode().moveUp(2);
				
				shipN.setLocalPosition(0,2,-4);
				shipN.attachChild(camera.getParentNode());
				camera.getParentNode().setLocalPosition(0,0,0);
				print("ship position: " + shipN.getWorldPosition());
				

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

				//this.getEngine().getSceneManager().getSceneNode("myShipNode").attachChild(headlightNode);
				shipN.attachChild(headlightNode);
		
	}

	//ship is setup with code provided
	private void setupShip(Engine eng, SceneManager sm) throws IOException {
		print("setupShip");
		Entity shipE = sm.createEntity("ship", "cockpitMk3j.obj");
		shipE.setPrimitive(Primitive.TRIANGLES);

		//SceneNode dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
		shipN = sm.getRootSceneNode().createChildSceneNode(shipE.getName() + "Node");

		shipN.setLocalPosition((Vector3f) Vector3f.createFrom(-2, 0, 0));
		shipN.attachObject(shipE);
		shipN.yaw(Degreef.createFrom(180));

		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
	}
	
	private void setupPatrolNPC(Engine eng, SceneManager sm) throws IOException{
		print("setupPatrolNPC");
		
		patrolNPC = sm.getRootSceneNode().createChildSceneNode("PatrolEnemyNode");
		
		
		patrolNPC.attachObject(eMaker.earth("the EARTH"));
		
		
		
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] temptf;
		
		temptf = toDoubleArray(patrolNPC.getLocalTransform().toFloatArray());
		PhysicsObject npcPhysObj = physicsEng.addSphereObject(physicsEng.nextUID(), mass, temptf, 1.0f);
		patrolNPC.setPhysicsObject(npcPhysObj);
		print("PATROL NPC PHYSICS: " + patrolNPC.getPhysicsObject() + " ##########################");
		
		npc1 = new PatrolEnemy(patrolNPC,stationN, this,5,100,100);
		SceneNode[] targets = {shipN};
		
		npc1.setTargets(targets);
		print("done setting up patrolNPC");
	}

	//same initialization as ship, with a few rotation controllers.
	private void setupPlanets(Engine eng, SceneManager sm) throws IOException {
		/*
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
		earthPlanets[12].setLocalPosition(-10,planetHeight,-15);*/
	}
	
	private SceneNode setupPlanet(Engine eng, SceneManager sm, String name, String texName) throws IOException {
		
		/*
		Entity planetE = sm.createEntity(name, "sphere.obj");
		planetE.setPrimitive(Primitive.TRIANGLES);
		
		Material mat = sm.getMaterialManager().getAssetByPath("default.mtl");
		
		Texture tex = eng.getTextureManager().getAssetByPath(texName);
		
		TextureState texState = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		planetE.setRenderState(texState);
		planetE.setMaterial(mat);
		*/
		//SceneNode planetN = sm.getRootSceneNode().createChildSceneNode(planetE.getName() + "Node");
		SceneNode planetN = sm.getRootSceneNode().createChildSceneNode(name + "Node");
		planetN.attachObject(eMaker.earth(name));
		
		return planetN;
	}
	
	private void setupPhysics() {
		System.out.println("setupPhysics");
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		
		physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEng.initSystem();
		
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] temptf;
		
		temptf = toDoubleArray(shipN.getLocalTransform().toFloatArray());
		PhysicsObject shipPhysicsObject = physicsEng.addSphereObject(physicsEng.nextUID(), mass, temptf, 1.0f);
		shipN.setPhysicsObject(shipPhysicsObject);
	}

	//seperate methods for keyboards/gamepads
	protected void setupInputs(SceneManager sm) throws IOException {
		im = new GenericInputManager();
		playerController = new FlightController(this, camera, camera.getParentSceneNode(), shipN, im, sm, physicsEng);
		
		setupAdditionalTestControls(im);
		
		//animationThrottleUp()
	}
	
	private void setupAdditionalTestControls(InputManager im) {
		ArrayList<Controller> controllers = im.getControllers();
		ArrayList<String> keyboards = new ArrayList<String>();
		
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).getType() == Controller.Type.KEYBOARD)
				keyboards.add(controllers.get(i).getName());
		}
		
		controlTest = new throttleUp();
		controlTest2 = new throttleDown();
		controlTest3 = new throttleLeft();
		controlTest4 = new throttleRight();
		controlTest8 = new destroyTerrain();
		
		
		for (int i = 0; i < keyboards.size(); i++) {
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.O, controlTest,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.P, controlTest2,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.K, controlTest3,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.L, controlTest4,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.U, controlTest8,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
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
		
		//System.out.println("update");//
		
		updateDefaults(engine);
		
		processNetworking(engine.getElapsedTimeMillis());
		
		SceneManager sm = engine.getSceneManager();
		SceneNode stationN = sm.getSceneNode("stationNode");
		
		playerController.update();
		

		//npc1.update(engine.getElapsedTimeMillis());
		
		
		//System.out.println("x:" + shipN.getLocalPosition().x());
		//System.out.println("y:" + shipN.getLocalPosition().y());
		//System.out.println("z:" + shipN.getLocalPosition().z());
		Object1N.moveLeft(.3f);
		Object3N.moveRight(.1f);
		Object4N.moveBackward(.1f);
		
		
	
			SkeletalEntity rightHand =
		(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
		
		rightHand.update();
		
		//System.out.println("update");
		
		//System.out.println("station world position is " + stationN.getWorldPosition());
		
		//print("" + stationN.getWorldPosition());
		//print("" + stationSound);
		//stationSound.setLocation(stationN.getWorldPosition());
	//	oceanSound.setLocation(earthN.getWorldPosition());
		//setEarParameters(sm);
		
		Matrix4 mat;
		physicsEng.update(eng.getElapsedTimeMillis());
		for (SceneNode s : engine.getSceneManager().getSceneNodes())
		{ 
			if (s.getPhysicsObject() != null){
				mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
				s.setLocalPosition(mat.value(0,3),mat.value(1,3),
				mat.value(2,3));
			}
		}
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
	
	
	
	private void throttleUpAndBackAnimatio()
	{ 

		SkeletalEntity rightHand =
	(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
	rightHand.stopAnimation();
	rightHand.playAnimation("throttleUpAndBackAnimation", 0.5f, NONE, 0);

	}
	
	private void throttleDownAndBackAnimation()
	{ 
		System.out.println("throttleDownAndBackAnimation");
		SkeletalEntity rightHand =
				(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
		rightHand.stopAnimation();
		rightHand.playAnimation("throttleDownAndBackAnimation", 0.5f, NONE, 0);

	}
	
	private void throttleLeftAndBackAnimation()
	{ 

		SkeletalEntity rightHand =
	(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
	rightHand.stopAnimation();
	rightHand.playAnimation("throttleLeftAndBackAnimation", 0.5f, NONE, 0);

	}
	
	private void throttleRightAndBackAnimation()
	{ 

		SkeletalEntity rightHand =
	(SkeletalEntity) eng.getSceneManager().getEntity("rightHandAv");
	rightHand.stopAnimation();
	rightHand.playAnimation("throttleRightAndBackAnimation", 0.5f, NONE, 0);

	}
	
	
	
	private class destroyTerrain extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			
			SceneNode tessN = eng.getSceneManager().
			getSceneNode("TessN");
			
			//tessN.setLocalPosition(8000.0f, 8000.0f, 8000.0f);
			tessN.moveDown(8000);
		}
	}

	
	private class throttleUp extends AbstractInputAction {
				
		@Override
		public void performAction(float arg0, Event e) {
			throttleUpAndBackAnimatio();
		}
	}
	
	
	private class throttleDown extends AbstractInputAction {
		
		@Override
		public void performAction(float arg0, Event e) {
			throttleDownAndBackAnimation();
		}
	}
	
	private class throttleLeft extends AbstractInputAction {
		
		@Override
		public void performAction(float arg0, Event e) {
			throttleLeftAndBackAnimation();
		}
	}
	
	
	private class throttleRight extends AbstractInputAction {
		
		@Override
		public void performAction(float arg0, Event e) {
			throttleRightAndBackAnimation();
		}
	}
	

	
	
	public void setEarParameters(SceneManager sm)
	{ 
		
		SceneNode shipN = sm.getSceneNode("myShipNode");
		Vector3 avDir = shipN.getWorldForwardAxis();
		// note - should get the camera's forward direction
		// - avatar direction plus azimuth
		//audioMgr.getEar().setLocation(stationN.getWorldPosition());
		//audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
		audioMgr.getEar().setLocation(shipN.getWorldPosition());
		audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,0,0));
	}
	
	public void initAudio(SceneManager sm)
	{ 
		print("initAudio setup");
		Configuration configuration = sm.getConfiguration();
		String sfxPath = configuration.valueOf("assets.sounds.path");
		String musicPath = configuration.valueOf("assets.music.path");
		AudioResource theMusic, theStation;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
		
		print("audioMgr: " + audioMgr);
		if (!audioMgr.initialize()) {
			System.out.println("The Audio Manager failed to initialize :(");
			return;
		}
		
		print("here though");
		
		theMusic = audioMgr.createAudioResource(musicPath + "bensound-epic.wav", AudioResourceType.AUDIO_STREAM);
		theStation = audioMgr.createAudioResource(sfxPath + "Cartoon-warp-02.wav", AudioResourceType.AUDIO_SAMPLE);

	

		
		backgroundMusic = new Sound(theMusic, SoundType.SOUND_MUSIC, 4, true);
		stationSound = new Sound(theStation, SoundType.SOUND_EFFECT, 400, true);
		
	
			backgroundMusic.initialize(audioMgr);
			backgroundMusic.play();
			

			stationSound.initialize(audioMgr);
			stationSound.setMaxDistance(10.0f);
			stationSound.setMinDistance(0.5f);
			stationSound.setRollOff(5f);
		
			SceneNode stationN = sm.getSceneNode("stationNode");
			stationSound.setLocation(stationN.getWorldPosition());
			
			//setEarParameters(sm);
			
			stationSound.play();
	}
	

	private int getThrottleSign() {
		return playerController.getThrottleSign();
	}
	
	private int getPitchSign() {
		return playerController.getPitchSign();
	}

	private int getRollSign() {
		return playerController.getRollSign();
	}

	private int getYawSign() {
		return playerController.getYawSign();
}
	
	private float[] toFloatArray(double[] arr)
	{ 
		if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++)
		{
			ret[i] = (float)arr[i];
		}
	return ret;
	}
	
	public static double[] toDoubleArray(float[] arr) 
	{
		if (arr == null) return null;
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++)
		{ 
			ret[i] = (double)arr[i];
		}
		return ret;
	}
	
	public SceneManager getSceneManager() { return sm; }
	public Engine getEngine() { return eng; }
	public PhysicsEngine getPhysicsEngine() { return physicsEng; }
	public NodeMaker getNodeMaker() { return nm; }
}
