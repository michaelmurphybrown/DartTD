package dart;

import java.util.ArrayList;

import dart.runner.AttackerRunner;
import dart.runner.Runner;
import dart.tower.Tower;

public class RunnerManager {

	//An Array of ArrayLists holding runners for each team
	//Each team has an ArrayList of runners
	private ArrayList<ArrayList<Runner>> Runners;
	
	private ArrayList<ArrayList<AttackerRunner>> AttackerRunners;
		
//--------------------------------
//Constructors
//--------------------------------

	//Create n lists of lists of Runners for n teams 
	public RunnerManager(int teams)
	{
		//Set up ArrayLists to track runners
		Runners = new ArrayList<ArrayList<Runner>>();
		AttackerRunners = new ArrayList<ArrayList<AttackerRunner>>();
		for(int i=0;i<teams;i++)
		{
			Runners.add(new ArrayList<Runner>());
			AttackerRunners.add(new ArrayList<AttackerRunner>());
		}
		//Set the board reference
	}
	
//--------------------------------
//Methods
//--------------------------------

//Getter Methods
	
	public ArrayList<ArrayList<Runner>> getAllRunners() {
		return Runners;
	}
	
	//Get the list of Runners from the team opposite the player
	public ArrayList<Runner> getEnemyRunners(Player player)
	{
		ArrayList<Runner> r = new ArrayList<Runner>();
		r.addAll(Runners.get(player.getTeam()));
		r.addAll(AttackerRunners.get(player.getTeam()));
		//Currently only works with 2 teams, although it could be modified easily enough to work with more
		return r;
	}
	
//Setter Methods
	
	public void setRunners(int team, ArrayList<Runner> r)
	{
		Runners.set(team, r);
	}
	
		//--------------------------------//
	
	
	
	//Add a runner to a team's list of runners
	public void addRunner(Runner r)
	{
		Runners.get(r.getTeam()).add(r);
	}
	//No need to manually remove them because board does that
	
	//Add a runner to a team's list of runners
	public void addAttackerRunner(AttackerRunner r)
	{
		AttackerRunners.get(r.getTeam()).add(r);
	}
	
	
	//Move the runners by one time-step along the map and
	public ArrayList<Runner> moveAllRunners(long time, Board board)
	{
		
		ArrayList<Runner> r = new ArrayList<Runner>();
		Runner current;
		Map m;
		
		//Loop through each team's list of runners
		for(int i=0;i<Runners.size();i++)
		{
			//Loop through each runner for that team
			for(int j=0;j<Runners.get(i).size();j++)
			{
				current=Runners.get(i).get(j);
				//If a runner can move, move him
				if(current.canMove(time))
					//If movement results in a Runner reaching the checkpoint, reroute his checkpoint
					if(current.move(time,board))
					{
						//We'll have to make this look nicer
						//If there's no checkpoint past the one the runner has gotten to, it has gotten to an endpoint.
						//So remove it from the RunnerManager list and add it to the list of runners to be returned.
						if(null==((CheckPoint)board.getSquare(current.getPosX(),current.getPosY())).getNextCheckPoint(current.getTeam()))
						{
							
							r.add(current);
							Runners.get(i).remove(j);
							continue;
						}
						
						//If it's not the end, go find the next waypoint.
						m = ( (CheckPoint)board.getSquare(current.getPosX(),current.getPosY())).getNextCheckPoint(current.getTeam()).getMap();
						current.setCheckPointMap( m );
					}
			}
		}
		for(int i=0;i<AttackerRunners.size();i++)
		{
			//Loop through each runner for that team
			for(int j=0;j<AttackerRunners.get(i).size();j++)
			{
				current=AttackerRunners.get(i).get(j);
				//If a runner can move, move him
				if(current.canMove(time))
					//If movement results in a Runner reaching the checkpoint, reroute his checkpoint
					if(current.move(time,board))
					{
						//We'll have to make this look nicer
						//If there's no checkpoint past the one the runner has gotten to, it has gotten to an endpoint.
						//So remove it from the RunnerManager list and add it to the list of runners to be returned.
						if(null==((CheckPoint)board.getSquare(current.getPosX(),current.getPosY())).getNextCheckPoint(current.getTeam()))
						{
							
							r.add(current);
							AttackerRunners.get(i).remove(j);
							continue;
						}
						
						//If it's not the end, go find the next waypoint.
						m = ( (CheckPoint)board.getSquare(current.getPosX(),current.getPosY())).getNextCheckPoint(current.getTeam()).getMap();
						current.setCheckPointMap( m );
					}
			}
		}	
		return r;
	}
	
	//Have runners of the player attack towers of the other team	
 	public void runnersAttackTowers(Player p, long time, ArrayList<Tower> enemyTowers)
	{					
		//Holder variables
		AttackerRunner r;
		
		//Runners of the player
		ArrayList<AttackerRunner> ARList = new ArrayList<AttackerRunner>();
		
	//Extract the right attackerrunners
		
		//Run through each team
		for(int i=0;i<AttackerRunners.size();i++)
		{
			//Skip any of the teams that aren't the player's
			if(AttackerRunners.get(i).size()>0 && AttackerRunners.get(i).get(0).getTeam()!=p.getTeam()) continue;
			
			//Get all the runners
			for(int j=0;j<AttackerRunners.get(i).size();j++)
			{
				if(AttackerRunners.get(i).get(j).getTeam()==p.getTeam()) ARList.add(AttackerRunners.get(i).get(j));
			}
		}
				
		//Run through every runer in the indicated player's list of runners
		for(int i=0;i<ARList.size();i++)
		{
			//Abbreviate the tower
			r=ARList.get(i);
			
			//If the tower is already locked on to a runner which is in range, don't go looking for another one
			if(r.getTowerLock()!=null && r.towerIsWithinRange(r.getTowerLock()))
			{				
				//If the tower can attack
				if(r.canAttack(time))
				{	
					//Attack the runner
					r.attackTower(r.getTowerLock(),time);					
				}
				///Don't bother looking through the rest of the runners
				continue;
			}
			
			//If the tower isn't locked on, Run through every runner
			for(int j=0;j<enemyTowers.size();j++)
			{
				//If the runner is within the range of the tower and the tower can shoot
				if(r.towerIsWithinRange(enemyTowers.get(j)) && enemyTowers.get(j).getArmor()>0)
				{
					//Set the runner lock so we don't have to do this search every time we want to shoot a runner
					r.setTowerLock(enemyTowers.get(j));
					if( r.canAttack(time))
					{
						//Attack the runner
						r.attackTower(enemyTowers.get(j),time);
					}
				}
			}
		}
		
	}

	 
	 
	 
}
