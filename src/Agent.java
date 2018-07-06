
/**
 * Hao Chen z5077894 group with junyu Lin z5077890
 * In our design, we build our global map. Everything in the view that we had would be added into our global map. Every vertex has its attitudes 
 * including the "searchValue" which means the value of visiting this vertex based on our special value algorithm. Our algorithm will calculate the 
 * search value according to the distance between vertex and agent, the distance between the vertex and existed key, axe, door, tree and stone. So 
 * every existed key, axe, door, tree and stone will influence the search value of other vertexes in global map.
 * 
 * At the same time, the agent has some state: "its position (x,y) in our global map", "the flags about whether it has key, axes, stone or raft".
 * 
 * Our agent will mark visited vertex and keep to visit the unvisited vertex in our global map (except for something not possible to be visited like wall).
 * We will search unvisited vertex in our global map as the order from high search value to low search value (which is calculated by our special algorithm)
 * After finding the vertex we want, we will use astar algorithm to get the path and do the corresponding action to visit it.
 * 
 * If agent has axe, it can chop the tree and if the agent has the key, it can open the door. 
 * If the tree or door is chopped or open, it will become a floor in our global map.
 * If the agent has raft, it can get into sea if necessary.
 * If the agent has the stone, it will put the stone in the necessary place. After the stone is placed, it will become the "floor" in our global map.
 */
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Agent {

	int pointList[][] = { { -2, 2 }, { -1, 2 }, { 0, 2 }, { 1, 2 }, { 2, 2 }, { -2, 1 }, { -1, 1 }, { 0, 1 }, { 1, 1 },
			{ 2, 1 }, { -2, 0 }, { -1, 0 }, { 0, 0 }, { 1, 0 }, { 2, 0 }, { -2, -1 }, { -1, -1 }, { 0, -1 }, { 1, -1 },
			{ 2, -1 }, { -2, -2 }, { -1, -2 }, { 0, -2 }, { 1, -2 }, { 2, -2 } };

	private Queue<Character> pendingMoves = new LinkedList<Character>();
	private Vertex[][] globalMap = new Vertex[160][160];;
	private int[][] visitedMap = new int[160][160];;
	Man m = new Man();
	Queue<Character> temp = new LinkedList<Character>();
	Queue<Vertex> treeList = new LinkedList<Vertex>();
	Queue<Vertex> doorList = new LinkedList<Vertex>();
	ArrayList<Vertex> waterList = new ArrayList<Vertex>();
	private char lastAction = 'Z';
	private Queue<Character> move = new LinkedList<Character>();
	private ArrayList<Vertex> itemList = new ArrayList<Vertex>();
	int dir;
	int ii = 0;

	public Agent() {
		Vertex first = new Vertex();
		globalMap[80][80] = first;
		globalMap[80][80].setVisited(true);
		globalMap[80][80].setObj(' ');
		globalMap[80][80].setX(80);
		globalMap[80][80].setY(80);
	}

	/**
	 * 
	 * @param start
	 *            the start Vertex
	 * @param goal
	 *            the goal Vertex
	 * @return return a queue which is planning the instructions by the path
	 */
	public Queue<Character> makeMove(Vertex start, Vertex goal) {
		Queue<Character> moveQueue = new LinkedList<Character>();
		// move up
		if (goal.getY() - start.getY() > 0 && goal.getX() - start.getX() == 0) {
			if (dir == 0) {
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 1) {
				moveQueue.add('r');
				dir = 0;
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 2) {

				moveQueue.add('r');
				moveQueue.add('r');
				dir = 0;
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 3) {

				moveQueue.add('l');
				dir = 0;
				moveQueue.add('f');

				return moveQueue;
			}
		} else if (goal.getY() - start.getY() < 0 && goal.getX() - start.getX() == 0) {
			if (dir == 0) {

				moveQueue.add('l');
				moveQueue.add('l');
				dir = 2;
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 1) {
				moveQueue.add('l');
				dir = 2;
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 2) {
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 3) {
				moveQueue.add('r');
				dir = 2;
				moveQueue.add('f');
				return moveQueue;
			}
		} else if (goal.getX() - start.getX() > 0 && goal.getY() - start.getY() == 0) {
			if (dir == 0) {
				moveQueue.add('r');
				dir = 3;
				moveQueue.add('f');

				return moveQueue;
			} else if (dir == 1) {
				moveQueue.add('r');
				moveQueue.add('r');
				dir = 3;
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 2) {
				moveQueue.add('l');
				dir = 3;
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 3) {
				moveQueue.add('f');
				return moveQueue;
			}
		} else if (goal.getX() - start.getX() < 0 && goal.getY() - start.getY() == 0) {
			if (dir == 0) {
				moveQueue.add('l');
				dir = 1;
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 1) {
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 2) {
				moveQueue.add('r');
				dir = 1;
				moveQueue.add('f');
				return moveQueue;
			} else if (dir == 3) {
				moveQueue.add('l');
				moveQueue.add('l');
				dir = 1;
				moveQueue.add('f');
				return moveQueue;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param goalQueue
	 *            take the destination Vertex from goalQueue Get the path from
	 *            Astar and use makeMove function to get the instruction
	 */
	public void moveAlgorithm(Queue<Vertex> goalQueue) {
		Vertex parent = null;
		Vertex cutTree = null;
		Vertex openDoor = null;
		int if_tree = 0;
		int if_door = 0;
		int if_stone = 0;
		if (globalMap[m.getY()][m.getX()].getObj() == '$') {
			m.setHasTreasure(true);
		}
		if (goalQueue.isEmpty()) {
			if (m.isHasTreasure() == true) {
				Astar a = new Astar(globalMap, m);
				Vertex goal = new Vertex();
				goal.setX(80);
				goal.setY(80);
				Vertex mv = new Vertex();
				mv.setX(m.getX());
				mv.setY(m.getY());
				parent = a.AstarSearch(mv, goal);
			}
		}
		while (!goalQueue.isEmpty()) {
			Queue<Vertex> treeDoorList = new LinkedList<Vertex>();
			if_tree = 0;
			if_door = 0;
			Vertex goal = null;
			Vertex mv = new Vertex();
			mv.setX(m.getX());
			mv.setY(m.getY());
			if (globalMap[mv.getY()][mv.getX()].getObj() == 'a') {
				m.setHasAxe(true);
			}

			if (globalMap[mv.getY()][mv.getX()].getObj() == 'k') {
				m.setHasKey(true);
			}
			if (globalMap[mv.getY()][mv.getX()].getObj() == 'o') {
				m.setNumStone(m.getNumStone() + 1);
				if_stone = 0;
			}
			// set to openDoor status
			if (!doorList.isEmpty() && m.isHasKey() == true) {
				Vertex tempGoal = doorList.poll();
				openDoor = tempGoal;
				treeDoorList.add(tempGoal);
				if_door = 1;
			}
			// set to cut Tree status
			if (!treeList.isEmpty() && m.isHasAxe() == true) {
				Vertex tempGoal = treeList.poll();
				cutTree = tempGoal;
				treeDoorList.add(tempGoal);
				if_tree = 1;
			}
			if (if_tree == 0 && if_door == 0) {
				goal = goalQueue.poll();
			}
			Astar a = new Astar(globalMap, m);
			if (globalMap[mv.getY()][mv.getX()].getObj() == '$') {
				m.setHasTreasure(true);
				goal.setX(80);
				goal.setY(80);
			}
			if (goal != null) {
				parent = a.AstarSearch(mv, goal);
			}
			while (!treeDoorList.isEmpty()) {
				goal = treeDoorList.poll();
				parent = a.AstarSearch(mv, goal);
				if (parent != null && goal.getObj() == '-') {
					if_door = 1;
				} else {
					if_door = 0;
				}
				if (parent != null && goal.getObj() == 'T') {
					if_tree = 1;
				} else {
					if_tree = 0;
				}
			}
			if (parent != null) {
				break;
			}
		}
		ArrayList<Vertex> pathList = new ArrayList<Vertex>();
		while (parent != null) {
			Vertex v = new Vertex();
			v.setX(parent.getX());
			v.setY(parent.getY());
			pathList.add(v);
			parent = parent.getParent();
		}
		Collections.reverse(pathList);
		int waterCount = 0;
		dir = m.getDirection();
		for (int i = 0; i < pathList.size() - 1; i++) {
			Queue<Character> insList = new LinkedList<Character>();
			insList = makeMove(pathList.get(i), pathList.get(i + 1));
			while (!insList.isEmpty()) {
				char tempc = insList.poll();
				move.add(tempc);
			}
		}
		int tempflag = 0;
		if (if_door == 1) {
			move = removeLast(move);
			move.add('u');
			globalMap[openDoor.getY()][openDoor.getX()].setObj(' ');
			tempflag = 1;
		}

		if (if_tree == 1 && tempflag == 0) {
			m.setNumRaft(m.getNumRaft() + 1);
			move = removeLast(move);
			move.add('c');
			globalMap[cutTree.getY()][cutTree.getX()].setObj(' ');
			ii++;
		}

	}

	/**
	 * 
	 * @param queue1
	 *            the queue which need to remove the last element
	 * @return queue2 the queue which finish remove the last element
	 */
	public Queue<Character> removeLast(Queue<Character> queue1) {
		Queue<Character> queue2 = new LinkedList<Character>();
		while (!queue1.isEmpty()) {
			Character o = queue1.poll();
			if (!queue1.isEmpty()) {
				queue2.add(o);
			}
		}
		return queue2;
	}

	/**
	 * This function calculats the search value of index according to the distance and existing axe, key, tree, door and stone. 
	 * @param k the distance between the specific vertex and agent
	 * @param tempX the coordinate of vertex
	 * @param tempY the coordinate of vertex
	 */
	public void setSearchValue(int k, int tempX, int tempY) {
		int res = 0;
		res += 80-k;
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).getSearchValue() == 'k') {
				res += Math.abs(5
						- 0.5 * (Math.abs(tempX - itemList.get(i).getX()) + Math.abs(tempY - itemList.get(i).getY())));
			} else if (itemList.get(i).getSearchValue() == 'a') {
				res += Math.abs(5
						- 0.5 * (Math.abs(tempX - itemList.get(i).getX()) + Math.abs(tempY - itemList.get(i).getY())));
			} else if (itemList.get(i).getSearchValue() == '-') {
				res += Math.abs(3
						- 0.3 * (Math.abs(tempX - itemList.get(i).getX()) + Math.abs(tempY - itemList.get(i).getY())));
			} else if (itemList.get(i).getSearchValue() == 'T') {
				res += Math.abs(3
						- 0.3 * (Math.abs(tempX - itemList.get(i).getX()) + Math.abs(tempY - itemList.get(i).getY())));
			} else if (itemList.get(i).getSearchValue() == 'o') {
				res += Math.abs(5
						- 0.5 * (Math.abs(tempX - itemList.get(i).getX()) + Math.abs(tempY - itemList.get(i).getY())));
			}
		}
		if(globalMap[tempY][tempX] !=null){
			globalMap[tempY][tempX].setSearchValue(res);
		}
	}

	/**
	 * @return return a vertex which can get more vision Our agent will mark
	 *         visited vertex and keep to visit the unvisited vertex in our
	 *         global map and we will search unvisited vertex in our global map
	 *         as the order from low distance to high distance
	 */
	public Queue<Vertex> getVision() {

		int k = 1;
		int finished = 0;
		Vertex findVertex;
		Queue<Vertex> destList = new LinkedList<Vertex>();
		while (k < 79) {
			Queue<Vertex> pointQueue = new LinkedList<Vertex>();
			Comparator<Vertex> comparator = new searchValueComparator();
			PriorityQueue<Vertex> Pqueue = new PriorityQueue<Vertex>(9999, comparator);
			for (int n = 0; n <= k; n++) {
				int tempY = (m.getY() + (k - n));// 1
				int tempX = (m.getX() + n);
				Vertex a = null;
				if (tempX >= 0 && tempX <= 159 && tempY >= 0 && tempY <= 159) {
					setSearchValue(k, tempX, tempY);
					a = globalMap[tempY][tempX];
				}
				// if the player on the land
				if (globalMap[m.getY()][m.getX()].getObj() != '~') {
					if (k < 8) {
						if (a != null && a.getObj() == 'T' && !treeList.contains(a)) {
							treeList.add(a);
						}

						if (a != null && a.getObj() == '-' && !doorList.contains(a)) {
							doorList.add(a);
							if(!Pqueue.isEmpty()){
								Pqueue.remove();
							}
						}
					}

					if (a != null && (a.getObj() != 'T' || m.isHasAxe() == true)
							&& (a.getObj() != '-' || m.isHasKey() == true) && a.getObj() != '*'
							&& (a.getObj() != '~' || (m.getNumRaft() > 0 && m.getStopOnLand() >= 3))) {
						pointQueue.add(a);

					}
				} else {
					if (k < 8) {
						if (a != null && a.getObj() == 'T' && !treeList.contains(a)) {
							treeList.add(a);
						}

						if (a != null && a.getObj() == '-' && !doorList.contains(a)) {
							doorList.add(a);
						}
					}
					if (a != null && (a.getObj() != '-' || m.isHasKey() == true) && a.getObj() != '*'
							&& (a.getObj() == '~' || (m.getStopInWater() >= 3))) {

						pointQueue.add(a);
					}
				}

				// b
				tempY = (m.getY() + (k - n));// 1
				tempX = (m.getX() - n);
				Vertex b = null;
				if (tempX >= 0 && tempX <= 159 && tempY >= 0 && tempY <= 159) {
					setSearchValue(k, tempX, tempY);
					b = globalMap[tempY][tempX];
				}
				// if the player on the land
				if (globalMap[m.getY()][m.getX()].getObj() != '~') {
					if (k < 8) {
						if (b != null && b.getObj() == 'T' && !treeList.contains(b)) {
							treeList.add(b);
						}

						if (b != null && b.getObj() == '-' && !doorList.contains(b)) {
							doorList.add(b);
						}
					}

					if (b != null && (b.getObj() != 'T' || m.isHasAxe() == true)
							&& (b.getObj() != '-' || m.isHasKey() == true) && b.getObj() != '*'
							&& (b.getObj() != '~' || (m.getNumRaft() > 0 && m.getStopOnLand() >= 3))) {

						pointQueue.add(b);
					}
				} else {// if the player in the sea
					if (k < 8) {
						if (b != null && b.getObj() == 'T' && !treeList.contains(b)) {
							treeList.add(b);
						}

						if (b != null && b.getObj() == '-' && !doorList.contains(b)) {
							doorList.add(b);
						}
					}
					if (b != null && b.getObj() != '*' && (b.getObj() != '-' || m.isHasKey() == true)
							&& (b.getObj() == '~' || (m.getStopInWater() >= 3))) {

						pointQueue.add(b);
					}
				}

				// c
				tempY = (m.getY() - (k - n));// 1
				tempX = (m.getX() + n);
				Vertex c = null;
				if (tempX >= 0 && tempX <= 159 && tempY >= 0 && tempY <= 159) {
					setSearchValue(k, tempX, tempY);
					c = globalMap[tempY][tempX];
				}
				// if the player on the land
				if (globalMap[m.getY()][m.getX()].getObj() != '~') {
					if (k < 8) {
						if (c != null && c.getObj() == 'T' && !treeList.contains(c)) {
							treeList.add(c);
						}

						if (c != null && c.getObj() == '-' && !doorList.contains(c)) {
							doorList.add(c);
						}
					}
					if (c != null && (c.getObj() != 'T' || m.isHasAxe() == true)
							&& (c.getObj() != '-' || m.isHasKey() == true) && c.getObj() != '*'
							&& (c.getObj() != '~' || (m.getNumRaft() > 0 && m.getStopOnLand() >= 3))) {
						pointQueue.add(c);
					}
				} else {// if the player in the sea
					if (k < 8) {
						if (c != null && c.getObj() == 'T' && !treeList.contains(c)) {
							treeList.add(c);
						}
						if (c != null && c.getObj() == '-' && !doorList.contains(c)) {
							doorList.add(c);
						}
					}
					if (c != null && c.getObj() != '*' && (c.getObj() != '-' || m.isHasKey() == true)
							&& (c.getObj() == '~' || (m.getStopInWater() >= 3))) {
						pointQueue.add(c);
					}
				}
				// d
				tempY = (m.getY() - (k - n));// 1
				tempX = (m.getX() - n);
				Vertex d = null;
				if (tempX >= 0 && tempX <= 159 && tempY >= 0 && tempY <= 159) {
					setSearchValue(k, tempX, tempY);
					d = globalMap[tempY][tempX];
				}
				// if the player on the land
				if (globalMap[m.getY()][m.getX()].getObj() != '~') {
					if (k < 8) {
						if (d != null && d.getObj() == 'T' && !treeList.contains(d)) {
							treeList.add(d);
						}

						if (d != null && d.getObj() == '-' && !doorList.contains(d)) {
							doorList.add(d);
						}
					}
					if (d != null && (d.getObj() != 'T' || m.isHasAxe() == true)
							&& (d.getObj() != '-' || m.isHasKey() == true) && d.getObj() != '*'
							&& (d.getObj() != '~' || (m.getNumRaft() > 0 && m.getStopOnLand() >= 3))) {
						pointQueue.add(globalMap[tempY][tempX]);
					}
				} else {// if the player in the sea
					if (k < 8) {
						if (d != null && d.getObj() == 'T' && !treeList.contains(d)) {
							treeList.add(d);
						}

						if (d != null && d.getObj() == '-' && !doorList.contains(d)) {
							doorList.add(d);
						}
					}
					if (d != null && d.getObj() != '*' && (d.getObj() != '-' || m.isHasKey() == true)
							&& (d.getObj() == '~' || (m.getStopInWater() >= 3))) {
						pointQueue.add(d);
					}
				}
			}
			while (!pointQueue.isEmpty()) {
				Vertex tempV = pointQueue.poll();
				if (tempV.isVisited() == false && !destList.contains(tempV)) {
					findVertex = tempV;
					destList.add(findVertex);
				}
			}
			k += 1;
		}
		return destList;
	}

	/**
	 * 
	 * @param view
	 *            the 5 times 5 view create a global map by 5 times 5 view map
	 */
	public void addToItemList(int globalx, int globaly) {
		if (globalMap[globaly][globalx] != null
				&& (globalMap[globaly][globalx].getObj() == 'T' || globalMap[globaly][globalx].getObj() == 'k'
						|| globalMap[globaly][globalx].getObj() == 'a' || globalMap[globaly][globalx].getObj() == '-'
						|| globalMap[globaly][globalx].getObj() == 'o')
				&& !itemList.contains(globalMap[globaly][globalx])) {
			itemList.add(globalMap[globaly][globalx]);
		}
	}

	public void addToGobalMap(char view[][]) {
		if (m.getDirection() == 0) {
			for (int i = 0; i < 25; i++) {
				int x = pointList[i][0];
				int y = pointList[i][1];

				int globalx = x;
				int globaly = y;

				globalx = globalx + m.getX();
				globaly = globaly + m.getY();
				// add the tree to treeList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == 'T'
						&& !treeList.contains(globalMap[globaly][globalx])) {
					treeList.add(globalMap[globaly][globalx]);
				}
				// add the door to doorList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == '-'
						&& !doorList.contains(globalMap[globaly][globalx])) {
					doorList.add(globalMap[globaly][globalx]);
				}

				addToItemList(globalx, globaly);

				if (globalMap[globaly][globalx] == null && view[4 - (y + 2)][x + 2] != '.') {
					Vertex v = new Vertex();
					globalMap[globaly][globalx] = v;
					globalMap[globaly][globalx].setObj(view[4 - (y + 2)][x + 2]);

					globalMap[globaly][globalx].setX(globalx);
					globalMap[globaly][globalx].setY(globaly);
				}

			}
		}
		// left

		if (m.getDirection() == 1) {
			for (int i = 0; i < 25; i++) {
				int x = pointList[i][0];
				int y = pointList[i][1];
				int globalx = -y;
				int globaly = x;

				globalx = globalx + m.getX();
				globaly = globaly + m.getY();
				// add the tree to treeList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == 'T'
						&& !treeList.contains(globalMap[globaly][globalx])) {
					treeList.add(globalMap[globaly][globalx]);
				}
				// add the door to doorList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == '-'
						&& !doorList.contains(globalMap[globaly][globalx])) {
					doorList.add(globalMap[globaly][globalx]);
				}

				addToItemList(globalx, globaly);

				if (globalMap[globaly][globalx] == null && view[4 - (y + 2)][(x + 2)] != '.') {
					Vertex v = new Vertex();
					globalMap[globaly][globalx] = v;
					globalMap[globaly][globalx].setObj(view[4 - (y + 2)][(x + 2)]);
					globalMap[globaly][globalx].setX(globalx);
					globalMap[globaly][globalx].setY(globaly);
					int a = 4 - (y + 2);
					int b = x + 2;

				}
			}
		}
		// down
		if (m.getDirection() == 2) {
			for (int i = 0; i < 25; i++) {

				int x = pointList[i][0];
				int y = pointList[i][1];
				int globalx = -x;
				int globaly = -y;

				globalx = globalx + m.getX();
				globaly = globaly + m.getY();
				// add the tree to treeList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == 'T'
						&& !treeList.contains(globalMap[globaly][globalx])) {
					treeList.add(globalMap[globaly][globalx]);
				}
				// add the door to doorList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == '-'
						&& !doorList.contains(globalMap[globaly][globalx])) {
					doorList.add(globalMap[globaly][globalx]);
				}

				addToItemList(globalx, globaly);

				if (globalMap[globaly][globalx] == null && view[4 - (y + 2)][(x + 2)] != '.') {
					Vertex v = new Vertex();
					globalMap[globaly][globalx] = v;
					globalMap[globaly][globalx].setObj(view[4 - (y + 2)][(x + 2)]);
					globalMap[globaly][globalx].setX(globalx);
					globalMap[globaly][globalx].setY(globaly);
				}
			}
		}
		// right
		if (m.getDirection() == 3) {
			for (int i = 0; i < 25; i++) {

				int x = pointList[i][0];
				int y = pointList[i][1];
				int globalx = y;
				int globaly = -x;

				globalx = globalx + m.getX();
				globaly = globaly + m.getY();
				// add the tree to treeList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == 'T'
						&& !treeList.contains(globalMap[globaly][globalx])) {
					treeList.add(globalMap[globaly][globalx]);
				}
				// add the door to doorList if the treeList do not contains it
				if (globalMap[globaly][globalx] != null && globalMap[globaly][globalx].getObj() == '-'
						&& !doorList.contains(globalMap[globaly][globalx])) {
					doorList.add(globalMap[globaly][globalx]);
				}

				addToItemList(globalx, globaly);

				if (globalMap[globaly][globalx] == null && view[4 - (y + 2)][(x + 2)] != '.') {
					Vertex v = new Vertex();
					globalMap[globaly][globalx] = v;
					globalMap[globaly][globalx].setObj(view[4 - (y + 2)][(x + 2)]);
					globalMap[globaly][globalx].setX(globalx);
					globalMap[globaly][globalx].setY(globaly);
				}
			}
		}
	}

	public char get_action(char view[][]) {
		// when the player go-back to land, destroy the raft
		if (globalMap[m.getLastY()][m.getLastX()] != null && globalMap[m.getY()][m.getX()] != null) {
			if (globalMap[m.getLastY()][m.getLastX()].getObj() == '~'
					&& globalMap[m.getY()][m.getX()].getObj() != '~') {
				for (int i = 0; i < waterList.size(); i++) {
					globalMap[waterList.get(i).getY()][waterList.get(i).getX()].setVisited(false);
				}
				m.setNumRaft(m.getNumRaft() - 1);
			}
		}
		// set the last action
		if (globalMap[m.getY()][m.getX()].getObj() != '~') {

			if (m.getLastX() == m.getX() && m.getLastY() == m.getY()) {
				m.setStopOnLand((m.getStopOnLand() + 1));
			} else {
				m.setLastX(m.getX());
				m.setLastY(m.getY());
				m.setStopOnLand(0);
			}
		} else {
			if (m.getLastX() == m.getX() && m.getLastY() == m.getY()) {
				m.setStopInWater((m.getStopInWater() + 1));
			} else {
				m.setLastX(m.getX());
				m.setLastY(m.getY());
				m.setStopInWater(0);
			}
		}
		view[2][2] = ' ';
		// estimate the direction
		if (lastAction == 'l') {
			m.setDirection(m.getDirection() + 1);
		} else if (lastAction == 'r') {
			m.setDirection(m.getDirection() - 1);
		} else if (lastAction == 'f') {
			m.setDirection(m.getDirection());
			if (m.getDirection() == 0) {
				m.setY(m.getY() + 1);
			} else if (m.getDirection() == 1) {
				m.setX(m.getX() - 1);
			} else if (m.getDirection() == 2) {
				m.setY(m.getY() - 1);
			} else if (m.getDirection() == 3) {
				m.setX(m.getX() + 1);
			}
		}
		// if global map contains water, add it to waterList
		if (globalMap[m.getY()][m.getX()].getObj() == '~') {
			waterList.add(globalMap[m.getY()][m.getX()]);
		}

		addToGobalMap(view);
		globalMap[m.getY()][m.getX()].setVisited(true);
		if (move.isEmpty()) {
			visitedMap[m.getY()][m.getX()] = 1;
			Queue<Vertex> q = getVision();
			moveAlgorithm(q);
		}

		char action = 0;
		while (!move.isEmpty()) {
			action = move.poll();
			lastAction = action;
			return action;
		}
		lastAction = action;
		return action;
	}

	void print_view(char view[][]) {
		int i, j;

		System.out.println("\n+-----+");
		for (i = 0; i < 5; i++) {
			System.out.print("|");
			for (j = 0; j < 5; j++) {
				if ((i == 2) && (j == 2)) {
					System.out.print('^');
				} else {
					System.out.print(view[i][j]);
				}
			}
			System.out.println("|");
		}
		System.out.println("+-----+");
	}

	public static void main(String[] args) {
		InputStream in = null;
		OutputStream out = null;
		Socket socket = null;
		Agent agent = new Agent();
		char view[][] = new char[5][5];
		char action = 'F';
		int port;
		int ch;
		int i, j;
		// globalMap = new Vertex[160][160];

		if (args.length < 2) {
			System.out.println("Usage: java Agent -p <port>\n");
			System.exit(-1);
		}

		port = Integer.parseInt(args[1]);

		try { // open socket to Game Engine
			socket = new Socket("localhost", port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Could not bind to port: " + port);
			System.exit(-1);
		}

		try { // scan 5-by-5 wintow around current location
			while (true) {
				for (i = 0; i < 5; i++) {
					for (j = 0; j < 5; j++) {
						if (!((i == 2) && (j == 2))) {
							ch = in.read();
							if (ch == -1) {
								System.exit(-1);
							}
							view[i][j] = (char) ch;
							// System.out.println("ttttttttttttt");
						}
					}
				}
				// agent.print_view(view); // COMMENT THIS OUT BEFORE SUBMISSION
				action = agent.get_action(view);
				out.write(action);
			}
		} catch (IOException e) {
			System.out.println("Lost connection to port: " + port);
			System.exit(-1);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}

class searchValueComparator implements Comparator<Vertex> {

	@Override
	public int compare(Vertex o1, Vertex o2) {
		if (o1.getSearchValue() > o2.getSearchValue())
			return 1;
		else if (o1.getSearchValue() < o2.getSearchValue())
			return -1;
		return 0;
	}

}
