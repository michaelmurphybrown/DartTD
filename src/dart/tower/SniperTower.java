package dart.tower;

import java.io.IOException;

import dart.Consts;
import dart.Player;

public class SniperTower extends Tower{
	
	public SniperTower(int x, int y, Player p) throws IOException
	{
		super(x, y, Consts.TextureType.LASERTEX, Consts.TowerType.VULCAN);
		
		//Make the damage low and the speed high
		this.damage=50;
		this.range=10;
		this.reloadSpeed=5000;
	}

}
