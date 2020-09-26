public class Explorer{
	Location loc;
	public Explorer(Location loc){
		this.loc = loc;
	}

	public int getX(){
		return loc.getX();
	}

	public int getY(){
		return loc.getY();
	}

	public void move(Location newLoc){
		loc = newLoc;

	}
}