package dart.tower;

import java.io.IOException;

import dart.Consts;
import dart.Player;

public class VulcanTower extends Tower{
	

	public VulcanTower(int x, int y, Player p) throws IOException
	{
		super(x, y, Consts.TextureType.LASERTEX, Consts.TowerType.VULCAN);
		
		//Make the damage low and the speed high
		this.damage=1;
		this.reloadSpeed=100;
	}

}
