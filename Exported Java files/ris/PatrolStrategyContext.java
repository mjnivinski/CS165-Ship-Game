package ris;

import java.util.Arrays;

//import a3.myGameEngine.VectorMath;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class PatrolStrategyContext {
	PatrolStrategy strategy;
	
	PatrolEnemy patrolEnemy;
	//PatrolChaseStrategy PCS; don't need a PCS because when it is created we give it a target, so its just going be instantiated each time the chase begins
	private PatrolReturnStrategy PRS;
	private PatrolPatrolStrategy PPS;
	
	private SceneNode npc;
	private SceneNode target;
	private SceneNode chaseTarget;
	
	private Vector3 position;
	
	float defenseTether = 150;
	float enemyTether = 150;
	
	SceneNode[] lasers;
	
	public PatrolStrategyContext(PatrolEnemy pe, SceneNode n, SceneNode t, float radius, float dT, float eT, SceneNode[] ls) {
		patrolEnemy = pe;
		System.out.println("Patrol Context Constructor");
		npc = n;
		target = t;
		
		defenseTether = dT;
		enemyTether = eT;
		
		lasers = ls;
		
		
		PRS = new PatrolReturnStrategy(n, t, radius);
		//PPS = new PatrolPatrolStrategy(n,t,enemyTether);
		PPS = new PatrolPatrolStrategy(n,t.getWorldPosition(),enemyTether);
		
		strategy = PPS;
	}
	
	public PatrolStrategyContext(PatrolEnemy pe, SceneNode n, Vector3 p, SceneNode[] ls) {
		patrolEnemy = pe;
		npc = n;
		position = p;
		lasers = ls;
		
		PRS = new PatrolReturnStrategy(npc, position);
		PPS = new PatrolPatrolStrategy(npc, position);
		
		strategy = PPS;
	}
	
	
	public void chaseEnemy(SceneNode t) {
		//npc.getPhysicsObject().setLinearVelocity(new float[]{1,1,1});
		strategy = new PatrolChaseStrategy(patrolEnemy, npc,t, lasers);
		chaseTarget = t;
	}
	
	public boolean stillChasing() {
		System.out.println("chase check distance outside:"  + VectorMath.distance(chaseTarget.getWorldPosition(), npc.getWorldPosition()));
		if(VectorMath.distance(chaseTarget.getWorldPosition(), npc.getWorldPosition()) > enemyTether) {
			System.out.println("chase check distance inside:"  + VectorMath.distance(chaseTarget.getWorldPosition(), npc.getWorldPosition()));
			return false;
		}
		return true;
	}
	
	public boolean stillReturning() {
		if(VectorMath.distance(npc.getWorldPosition(),position) < 1) {
			npc.setLocalPosition(position);
			
			return false;
		}
		
		return true;
	}
	
	public void returnHome() {
		strategy = PRS;
	}
	
	public void patrolHome() {
		strategy = PPS;
	}
	
	public void execute(float time) {
		strategy.move(time);
	}
}