public class Wall{
	int[] xArr;
	int[] yArr;
	int points;

	public Wall(int[] xArr, int[] yArr, int points){
		this.xArr = xArr;
		this.yArr = yArr;
		this.points = points;
	}

	public int[] getXArr(){
		return xArr;
	}

	public int[] getYArr(){
		return yArr;
	}


	public int getPoints(){
		return points;
	}


	public String toString(){
		return "wall";
	}
}