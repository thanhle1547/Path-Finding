# Path Finding

## Description
This program provides a visual demonstration of the process undergone by Dijkstra, A* (A star) and GAs (Genetic Algorithm).

### [YouTube Video](https://www.youtube.com/watch?v=xGdBwdd_FLc&t)

## Dijkstra
Dijkstra's algorithm works by first adding the starting node to a priority queue. It then takes the top node in the priority queue and looks at all of the nodes surrounding it. If the nodes are valid positions then they are added to the priority queue and the top node in the queue is deleted. These nodes that are added to the queue also have a knowledge of what node they were explored from (ie. their parent node) This process is continued until a node is discovered with the same location as the finish node. From that node, a path is created by retracing the steps to the starting node. 

![dijkstra](https://user-images.githubusercontent.com/36581610/50039437-a6cd8e80-0000-11e9-865a-1c6062046d4f.gif)

## A*
A* works similarly to dijkstra by creating a priority queue of nodes and then adding new nodes to the queue by exploring the top node on the queue. However in A* the nodes are placed into the queue with a heuristic of distance to the finish node. This means that the node at the top of the queue is always the node closest to the finish node.

![astar](https://user-images.githubusercontent.com/36581610/50039438-af25c980-0000-11e9-9fda-f96a2ee6cb2e.gif)

## Draw a maze
![drawmaze](https://user-images.githubusercontent.com/36581610/51815322-197f8a00-228e-11e9-80c9-b088d76b3ba2.gif)

## How to add your algorithm

1. Go to the line 76 and add your algorithm name.

```java
76   private String[] algorithms = { "GAs", "Dijkstra", "A*" };
```

1. Add your (public) algorithm function inside of class Algorithm, line 745.

```java
745  class Algorithm {	//ALGORITHM CLASS
```

3. Go to funtion ```startSearch()``` in line 542 and add a case (the number following the way you arranged in step 1) to call your function which you written in step 2. (```Alg``` is a instance of class ```Algorithm``` in line 745).

```java
542  public void startSearch() {	//START STATE
...     // ... Other code
544         switch(curAlg) {
545             case 0:
546                 Alg.GAs();
547                 break;
548             case 1:
549                 Alg.Dijkstra();
550                 break;
551             case 2:
552                 Alg.AStar();
553                 break;
554         }
...     // ... Other code
```

## Some usefull tips

1. If you wants to show the detail of processing or result of your algorithm, there is a instance of JTextArea (variable name ```textArea```) which you can call function setText()/append().

2. Call ```Update()``` to repaint the canvas, then call ```delay()```, so you'll have time to see the changed.

3. If you wants to show the path length, assign value to variable ```length``` (type double, call ```round()``` function).

4. If you wants to show how many cells have been checked by algorithm, assign value to variable ```checks``` (type int).

5. If you needs the wall list, there is a variable name ```wallList``` (type ArrayList\<Node\>).

6. If you wants to show the path as a line, add the path to variable ```pathList``` (type ArrayList\<ArrayList\<Node\>\>). Or set type = 5 to the cell (color yellow in the canvas).

7. Call ```setEnableWorkableComponents()``` function to dis/enable some controls (see the image below).

![control diasble](./screenshots/control-diasble.png)

8. If you needs map size like 16x16 or 13x9, press <kbd>Alt</kbd> + <kbd>C</kbd> to open Custom map size dialog.