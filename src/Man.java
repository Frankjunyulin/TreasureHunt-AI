/**
 * x and y is the coordinate of the player
 * lastX and lastY is to record the last coordinate of the player
 * hasKey and hasAxe is to recored the player has this tools or not
 * stopOnland and stopInwater is to estimate have the player visit all the land or sea or not
 * numRaft and numStone is to count how many raft and how many stone do the player have
 * @author chenhao
 *
 */
public class Man {
	// up 0 left 1 right 3 down 2
	private int direction = 0;
	private int x = 80;
	private int y = 80;
	private int lastX = 0;
	private int lastY = 0;
	private boolean hasKey = false;
	private int numRaft = 0;
	private boolean hasAxe = false;
	private int stopOnLand = 0;
	private int stopInWater = 0;
	private boolean finishWater = false;
	private boolean hasTreasure = false;
	private int numStone = 0;
	public boolean isHasTreasure() {
		return hasTreasure;
	}

	public void setHasTreasure(boolean hasTreasure) {
		this.hasTreasure = hasTreasure;
	}

	public boolean isFinishWater() {
		return finishWater;
	}

	public void setFinishWater(boolean finishWater) {
		this.finishWater = finishWater;
	}

	public int getStopOnLand() {
		return stopOnLand;
	}

	public void setStopOnLand(int stopOnLand) {
		this.stopOnLand = stopOnLand;
	}

	public int getStopInWater() {
		return stopInWater;
	}

	public void setStopInWater(int stopInWater) {
		this.stopInWater = stopInWater;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setLastY(int lastY) {
		this.lastY = lastY;
	}

	public boolean isHasAxe() {
		return hasAxe;
	}

	public void setHasAxe(boolean hasAxe) {
		this.hasAxe = hasAxe;
	}

	public boolean isHasKey() {
		return hasKey;
	}

	public void setHasKey(boolean hasKey) {
		this.hasKey = hasKey;
	}
	public int getNumRaft() {
		return numRaft;
	}

	public void setNumRaft(int numRaft) {
		this.numRaft = numRaft;
	}


	public int getNumStone() {
		return numStone;
	}

	public void setNumStone(int numStone) {
		this.numStone = numStone;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
		if (this.direction == 4) {
			this.direction = 0;
		}
		if (this.direction == -1) {
			this.direction = 3;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
