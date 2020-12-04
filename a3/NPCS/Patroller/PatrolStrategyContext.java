package a3.NPCS.Patroller;

import java.util.Arrays;

import a3.myGameEngine.VectorMath;
import ray.rage.scene.SceneNode;

public class PatrolStrategyContext {
	PatrolStrategy strategy;
	
	//PatrolChaseStrategy PCS; don't need a PCS because when it is created we give it a target, so its just gonna be instnatiated each time the chase begins
	private PatrolReturnStrategy PRS;
	private PatrolPatrolStrategy PPS;
	
	private SceneNode npc;
	private SceneNode target;
	private SceneNode chaseTarget;
	
	float defenseTether;
	float enemyTether;
	
	public PatrolStrategyContext(SceneNode n, SceneNode t, float radius, float dT, float eT) {
		System.out.println("Patrol Context Constructor");
		npc = n;
		target = t;
		
		defenseTether = dT;
		enemyTether = eT;
		
		System.out.println("### " + npc.getPhysicsObject() + " ###");
		
		PRS = new PatrolReturnStrategy(n, t, radius);
		PPS = new PatrolPatrolStrategy(n,t,enemyTether);
		
		strategy = PPS;
	}
	
	
	public void chaseEnemy(SceneNode t) {
		System.out.println("chase begins");
		npc.getPhysicsObject().setLinearVelocity(new float[]{1,1,1});
		strategy = new PatrolChaseStrategy(npc,t);
		chaseTarget = t;
	}
	
	public boolean stillChasing() {
		if(VectorMath.distance(chaseTarget.getWorldPosition(), npc.getWorldPosition()) > enemyTether) {
			return false;
		}
		return true;
	}
	
	public boolean stillReturning() {
		if(VectorMath.distance(npc.getWorldPosition(), target.getWorldPosition()) < 1) {
			System.out.println("stillReturningFalse, back at home");
			npc.setLocalPosition(target.getWorldPosition());
			
			return false;
		}
		
		return true;
	}
	
	public void returnHome() {
		strategy = PRS;
	}
	
	public void patrolHome() {
		
		//npc.getPhysicsObject().setLinearVelocity(new float[]{0,0,0});
		//System.out.println("patrol home linear velocity: " +  Arrays.toString(npc.getPhysicsObject().getLinearVelocity()));
		strategy = PPS;
	}
	
	public void execute(float time) {
		//System.out.println("context.execute");
		strategy.move(time);
	}
}