/**
 * gValue and fValue and hValue is record for aStar search 
 * 
 * @author chenhao
 *
 */
public class Vertex {
	private boolean status;
	private int gValue;
	private int fValue;
	private int hValue;
	private int x;
	private int y;
	private Vertex parent;
	private boolean visited = false;
	private char obj;
	private int searchValue = 0;
	
	public int getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(int searchValue) {
		this.searchValue = searchValue;
	}
	public boolean isVisited() {
		return visited;
	}
	public boolean getVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public char getObj() {
		return obj;
	}

	public void setObj(char obj) {
		this.obj = obj;
	}

	public Vertex getParent() {
		return parent;
	}

	public void setParent(Vertex parent) {
		this.parent = parent;
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

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int gethValue() {
		return hValue;
	}

	public void sethValue(int hValue) {
		this.hValue = hValue;
	}

	public int getgValue() {
		return gValue;
	}

	public void setgValue(int gValue) {
		this.gValue = gValue;
	}

	public int getfValue() {
		return fValue;
	}

	public void setfValue(int fValue) {
		this.fValue = fValue;
	}

	public void calcF() {
		this.fValue = this.gValue + this.hValue;
	}
}
