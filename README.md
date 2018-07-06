# TreasureHunt-AI

##Algorithm

In our design, we build our global map. Everything in the view that we had would be added into our global map. Every vertex has its attitudes including the "searchValue" which means the value of visiting this vertex based on our special value algorithm. Our algorithm will calculate the search value according to the distance between vertex and agent, the distance between the vertex and existed key, axe, door, tree and stone. So every existed key, axe, door, tree and stone will influence the search value of other vertexes in global map. <br> <br>
 
At the same time, the agent has some state: "its position (x,y) in our global map", "the flags about whether it has key, axes, stone or raft". <br> <br>
 
Our agent will mark visited vertex and keep to visit the unvisited vertex in our global map (except for something not possible to be visited like wall). <br> <br>

We will search unvisited vertex in our global map as the order from high search value to low search value (which is calculated by our special algorithm) <br><br>

After finding the vertex we want, we will use astar algorithm to get the path and do the corresponding action to visit it. <br>

If agent has axe, it can chop the tree and if the agent has the key, it can open the door. <br>

If the tree or door is chopped or open, it will become a floor in our global map. <br>

If the agent has raft, it can get into sea if necessary. <br>

If the agent has the stone, it will put the stone in the necessary place. After the stone is placed, it will become the "floor" in our global map.
