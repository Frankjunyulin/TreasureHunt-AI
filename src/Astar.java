import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
/**
 * get The globalMap and the Player's status from Agent
 * And use the manhattan heuristic 
 * @author chenhao
 *
 */
public class Astar {
	char[][] view;
	Vertex[][] globalMap;
	Man man;
	int crossWaterCount = 0;

	public Astar(char[][] view) {
		this.view = view;

	}
	public Astar(Vertex[][] globalMap, Man man) {
		this.man = man;
		this.globalMap = globalMap;

	}
	public static final int STEPcost = 10;
	/**
	 * @param start the start vertex of the map
	 * @param goal the goal vertex of the map
	 * @return the Vertex of the Path 
	 */
	public Vertex AstarSearch(Vertex start, Vertex goal) {
		crossWaterCount = man.getNumStone();
		int hCost = 0;
		int gCost = 0;
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		ArrayList<Vertex> openList = new ArrayList<Vertex>();
		ArrayList<Vertex> closed = new ArrayList<Vertex>();
		openList.add(start);
		while (openList.size() > 0) {
			Vertex curr = findMinFVertex(openList);
			openList.remove(curr);
			closed.add(curr);
			ArrayList<Vertex> neighborNodes = findNeighbors(curr, closed);
			for (Vertex v : neighborNodes) {
				if (existsInOpenList(openList, v)) {
					foundVertex(curr, v);
				} else {
					notFoundVertex(curr, goal, v, openList);
				}
			}
			if (find(openList, goal) != null) {
				return find(openList, goal);
			}
		}
		return find(openList, goal);
	}
	//compare the fvalue of vertex in openList
	public Vertex findMinFVertex(ArrayList<Vertex> openList) {
		Vertex tempVertex = openList.get(0);
		for (Vertex v : openList) {
			if (v.getfValue() < tempVertex.getfValue()) {
				tempVertex = v;
			}
		}
		return tempVertex;
	}
	//if find the correct vertex, return
	public static Vertex find(List<Vertex> openList, Vertex point) {
		for (Vertex v : openList) {
			if ((v.getX() == point.getX()) && (v.getY() == point.getY())) {
				return v;
			}
		}
		return null;
	}
	private int calcGvalue(Vertex start, Vertex v) {
		int G = STEPcost;
		int parentG = v.getParent() != null ? v.getParent().getgValue() : 0;
		return G + parentG;
	}
	// use the manhatton manhattan heuristic
	private int calcHvalue(Vertex end, Vertex v) {
		int cost = Math.abs(v.getX() - end.getX()) + Math.abs(v.getY() - end.getY());
		return cost * STEPcost;
	}
	//if found vertex in openlist
	private void foundVertex(Vertex tempStart, Vertex v) {
		int G = calcGvalue(tempStart, v);
		if (G < v.getgValue()) {
			v.setParent(tempStart);
			v.setgValue(G);
			v.calcF();
		}
	}
	//if not found vertex in openList
	private void notFoundVertex(Vertex tempStart, Vertex end, Vertex v, ArrayList<Vertex> openList) {
		v.setParent(tempStart);
		v.setgValue(calcGvalue(tempStart, v));
		v.sethValue(calcHvalue(end, v));
		v.calcF();
		openList.add(v);
	}

	public int existsInCloseList(List<Vertex> closeList, int x, int y) {
		for (Vertex v : closeList) {
			if ((v.getX() == x) && (v.getY() == y)) {
				return 1;
			}
		}
		return 0;
	}

	public static boolean existsInOpenList(ArrayList<Vertex> openList, Vertex vertex) {
		for (Vertex v : openList) {
			if ((v.getX() == vertex.getX()) && (v.getY() == vertex.getY())) {
				return true;
			}
		}
		return false;
	}

	public boolean canReach(int x, int y) {
		int ifHasAxe = 0;
		int ifHasKey = 0;
		int ifHasRaft = 0;
		int isFWater = 0;
		int canCross = 0;
		if (globalMap[y][x] != null) {
			if (x >= 0 && x < globalMap.length && y >= 0 && y < globalMap[0].length) {
				if (man.isHasAxe() == true) {
					ifHasAxe = 1;
				}
				if (man.isHasKey() == true) {
					ifHasKey = 1;
				}
				if (man.getNumRaft() > 0) {
					ifHasRaft = 1;
				}
				if (man.isFinishWater() == true) {
					isFWater = 1;
				}
				if (man.getNumStone() > 0) {
					canCross = 1;
				}
				if (globalMap[man.getY()][man.getX()].getObj() == '~') {
					return (globalMap[y][x].getObj() == '~')
							|| (globalMap[y][x].getObj() == ' ' && man.getStopInWater() >= 3)
							|| (globalMap[y][x].getObj() == '-' && ifHasKey == 1);

				} else {
					return (globalMap[y][x].getObj() == ' ') || (globalMap[y][x].getObj() == '$')
							|| (globalMap[y][x].getObj() == 'k') || (globalMap[y][x].getObj() == 'a')
							|| (globalMap[y][x].getObj() == 'T' && ifHasAxe == 1)
							|| (globalMap[y][x].getObj() == '-' && ifHasKey == 1) || (globalMap[y][x].getObj() == 'o')
							|| (globalMap[y][x].getObj() == '~' && canCross == 1)
							|| (globalMap[y][x].getObj() == '~' && ifHasRaft == 1 && man.getStopOnLand() >= 3);
				}

			}
		}
		return false;
	}

	public ArrayList<Vertex> findNeighbors(Vertex curr, ArrayList<Vertex> closed) {
		ArrayList<Vertex> neighborList = new ArrayList<Vertex>();
		int topX = curr.getX();
		int topY = curr.getY() - 1;
		if (canReach(topX, topY) && existsInCloseList(closed, topX, topY) == 0) {
			Vertex v = new Vertex();
			v.setX(topX);
			v.setY(topY);
			neighborList.add(v);
		}
		int bottomX = curr.getX();
		int bottomY = curr.getY() + 1;
		if (canReach(bottomX, bottomY) && existsInCloseList(closed, bottomX, bottomY) == 0) {
			Vertex v = new Vertex();
			v.setX(bottomX);
			v.setY(bottomY);
			neighborList.add(v);
		}
		int leftX = curr.getX() - 1;
		int leftY = curr.getY();
		if (canReach(leftX, leftY) && existsInCloseList(closed, leftX, leftY) == 0) {
			Vertex v = new Vertex();
			v.setX(leftX);
			v.setY(leftY);
			neighborList.add(v);
		}
		int rightX = curr.getX() + 1;
		int rightY = curr.getY();
		if (canReach(rightX, rightY) && existsInCloseList(closed, rightX, rightY) == 0) {
			Vertex v = new Vertex();
			v.setX(rightX);
			v.setY(rightY);
			neighborList.add(v);
		}
		return neighborList;
	}

	class FValueComparator implements Comparator<Vertex> {
		public int compare(Vertex o1, Vertex o2) {
			if (o1.getfValue() >= o2.getfValue()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
