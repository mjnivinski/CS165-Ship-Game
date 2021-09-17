package ris;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import ris.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient {
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;

	public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game) throws IOException {
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
		
		
	}

	private void print(String s) {
		System.out.println(s);
	}

	@Override
	protected void processPacket(Object msg) {
		String strMessage = (String) msg;
		String[] msgTokens = strMessage.split(",");

		//System.out.println("packet recieved: " + strMessage);

		if (msgTokens.length > 0) {
			if (msgTokens[0].compareTo("join") == 0) { // receive "join"
				// format: join,success or join,failure
				if (msgTokens[1].compareTo("success") == 0) {
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition(), game.getPlayerDirection(), game.getPlayerTeam());
				}

				if (msgTokens[1].compareTo("failure") == 0) {
					game.setIsConnected(false);
				}
			}

			if (msgTokens[0].compareTo("bye") == 0) { // Received "bye" informs all clients with the name of a client
														// who quit
				print("bye");
				// formate bye,remoteId
				UUID remID = UUID.fromString(msgTokens[1]);
				removeGhostAvatar(remID);
			}

			if ((msgTokens[0].compareTo("dsfr") == 0)) {

				// format create,remoteid,x,y,z or dsfr,remoteid,x,y,z
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				Vector3 linear = Vector3f.createFrom(Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]), Float.parseFloat(msgTokens[7]));

				try {
					updateGhost(ghostID, ghostPosition, linear);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(msgTokens[0].compareTo("create") == 0) {
				
				System.out.println("create message recieved");
				
				UUID ghostId = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				Vector3 linear = Vector3f.createFrom(Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]), Float.parseFloat(msgTokens[7]));
				int team = (int) Float.parseFloat(msgTokens[8]);
				
				try {
					createGhostAvatar(ghostId, ghostPosition, linear, team);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (msgTokens[0].compareTo("wants") == 0) { // Received "wants" informs client that a remote client wants a
														// local status update
				// format should be "wants,clientID"
				UUID remID = UUID.fromString(msgTokens[1]);
				Vector3 pos = game.getPlayerPosition();
				Vector3 linear = game.getPlayerDirection();
				sendDetailsForMessage(remID, pos, linear);
			}

			if (msgTokens[0].compareTo("move") == 0) { // Received "move" informs client of a change in status of a
														// remote avatar
				UUID remID = UUID.fromString(msgTokens[1]);
				Vector3 pos = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				Vector3 linear = Vector3f.createFrom(Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]), Float.parseFloat(msgTokens[7]));
				try {
					updateGhost(remID, pos, linear);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(msgTokens[0].compareTo("shoot") == 0) { // received "shoot" informs client that the ghost avatar has shot
				UUID remID = UUID.fromString(msgTokens[1]);
				ghostShoot(remID);
			}

		}

	}

	// send messages
	/*
	 * join (client is joining) create (lets server know of a new avatar)
	 * move/rotate (informs server about a change in a client avatar) detailsfor
	 * (informs server of local avatar position/orientation) bye (informs server
	 * that client is leaving
	 */

	public void sendJoinMessage() {
		System.out.println("In ProtocolClient: sendJoinMessage()");
		try {
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendCreateMessage(Vector3 pos, Vector3 linear, int team) {
		System.out.println("send create message");
		// format: create,localid,x,y,z
		try {
			String message = new String("create," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			message += "," + linear.x() + "," + linear.y() + "," + linear.z();
			message += "," + String.valueOf(team);
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMoveMessage(Vector3 pos, Vector3 linear) {

		try {
			String message = new String("move," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			message += "," + linear.x() + "," + linear.y() + "," + linear.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDetailsForMessage(UUID remID, Vector3 pos, Vector3 linear) {
		try {
			String message = new String("dsfr," + remID.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			message += "," + linear.x() + "," + linear.y() + "," + linear.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendShootMessage() {
		try {
			String message = new String("shoot," + id.toString());
			sendPacket(message);
		} catch (IOException e) { e.printStackTrace(); }
	}

	public void createGhostAvatar(UUID ghostID, Vector3 ghostPosition, Vector3 linear, int team) throws IOException {
		GhostAvatar gh = new GhostAvatar(game, ghostID, ghostPosition, linear, team);
		ghostAvatars.add(gh);
		game.setupGhostAvatar(gh, team);
	}

	public void removeGhostAvatar(UUID ghostID) {
		int index = lookupGhost(ghostID);
	}

	public int lookupGhost(UUID ghostID) {
		for (int i = 0; i < ghostAvatars.size(); i++) {
			if (ghostAvatars.elementAt(i).getID().compareTo(ghostID) == 0) {
				return i;
			}
		}
		return -1;
	}

	public void updateGhost(UUID ghostID, Vector3 pos, Vector3 linear) throws IOException {
		// has a remote id ghost and its new position

		// use lookupGhost to get index location in ghostAvatars Vector
		int index = lookupGhost(ghostID);
		if (index != -1) {
			ghostAvatars.elementAt(index).setPosition(pos, linear);
		} else {
			createGhostAvatar(ghostID, pos, linear, 0);
		}
		// update its position
	}
	
	private void ghostShoot(UUID ghostID) {
		
		int index = lookupGhost(ghostID);
		if(index != -1) {
			ghostAvatars.elementAt(index).shoot();
		}
		 
	}
	
	public boolean hitUpdate() {
		for(GhostAvatar ga : ghostAvatars) {
			if(ga.hitCheck()) return true;
		}
		return false;
	}
}
