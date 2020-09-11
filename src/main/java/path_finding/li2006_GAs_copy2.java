package path_finding;

import java.util.ArrayList;
import java.util.Random;

/**
 * Thuật toán này sử dụng các phương pháp được đề xuất trong bài báo khoa học
 * (paper) <b>An improved genetic algorithm of optimum path planning for mobile
 * robots.</b> In: Sixth international conference on intelligent systems design
 * and applications, ISDA ’06; <b>2006</b> của <i>Li Q, Zhang W, Yin Y, Wang Z,
 * Liu G.</i>
 * 
 * @author thanhLe1547
 */
public class li2006_GAs_copy2 extends GeneticAlgorithm {
    /**
     * Số lượng cá thể muốn chọn vào 1 nhóm để random trong bước `Chọn lọc`
     */
    int numOfSizeForSelect;

    public li2006_GAs_copy2(Map m, ArrayList<Node> wallList, int cSize, int numOfSizeForSelect) {
        super(m, wallList, cSize);
        this.numOfSizeForSelect = numOfSizeForSelect;
    }

    public void initPopulation(int size, int mapSize, int capacity) {
        population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(initChromosome());
        }
    }

    /**
     * @see <p>Bài báo số 1 đã được đề cập.</p>
     * <p>Obstacle avoidance algorithm</p>
     * <code>The algorithm consists of the following steps:</code>
     * <ol>
     *    <li>
     *      Draw a line segment XY from the start node X to the goal node Y.</li>
     *    <li>
     *      If XY intersects the obstacle area O, continue; 
     *      otherwise output XY as the shortest path.</li>
     *    <li>
     *      Select one node A from the obstacle area O randomly 
     *      and then draw a redial that is vertical to XY from node A. 
     *      If the redial intersects the free areas F,
     *      select one node B from F randomly; otherwise stop.</li>
     *    <li>
     *      Connect node X and node B, as well as node B and node Y.</li>
     *    <li>
     *      Set B to Y in line segment XB and set B to X in line segment BY.</li>
     *    <li>
     *      Repeat steps 1 to 5 until all the line segments 
     *      from X to Y do not intersect any obstacle areas.</li>
     *    <li>
     *      Connect the grids’ number of each line segment from X to Y 
     *      and a collision-free initial path is then generated.</li>
     * </ol>
     */
    public ArrayList<Node> initChromosome() {
        Random rd = new Random();
        boolean hasAddedNode = false;
        Node startNode = this.startNode, 
            endNode = this.finishNode, 
            wallNode;
        ArrayList<Node> chromosome = new ArrayList<>();
        ArrayList<Node> wallNodeList,
                        freeNodeList;

        chromosome.add(startNode);
        chromosome.add(finishNode);
        wallNodeList = getIntersectWallNode(startNode, endNode);
        freeNodeList = new ArrayList<>();

        if (!isIntersectObstacle(startNode, endNode, map))
            return chromosome;
        
        while (true) {
            try {
                if (hasAddedNode) {
                    wallNodeList = getIntersectWallNode(startNode, endNode);
                    if (wallNodeList.size() == 0 && chromosome.size() > 2)
                        return chromosome;
                }

                wallNode = wallNodeList.get(rd.nextInt(wallNodeList.size()));
                freeNodeList = getRedialNodeList(
                    startNode.getDirection(endNode), 
                    wallNode, 
                    endNode 
                );
                
                chromosome.add(1, freeNodeList.get(rd.nextInt(freeNodeList.size())));

                endNode = chromosome.get(1);
                hasAddedNode = true;
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                hasAddedNode = false;
            }
        }
    }

    /**
     * Chọn lọc - Sử dụng phương pháp Tournament Selection
     */
    public void select() {
        int n = population.size(),
            b = 0;                  // indexBestFitness
        Random rd = new Random();
        ArrayList<ArrayList<Node>> newPopulation = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            b = 0;
            for (int j = 0; j < numOfSizeForSelect; j++) {
                int rdIndex = rd.nextInt(n);
                if (b == 0 || fitness.get(rdIndex) > fitness.get(b))
                    b = rdIndex;
            }
            newPopulation.add(population.get(b));
            fitness.add(fitness.get(b));
        }
        population = newPopulation;
    }

    protected ArrayList<Node> getRedialNodeList(Direction lineDirection, Node node, Node endNode)
            throws IndexOutOfBoundsException
    {
        // 2 dimensional array
		// 	(-2, -2)                (0, -1)	            (2, -2)
		// 		    NW (-1, -1)   N (0, -1)	  NE (1, -1)
		// 	(-2, 0) W (-1, 0) 		(0, 0)     E (1, 0) (0, 2)
		// 		    SW (-1, 1)    S (0, 1)	  SE (1, 1)
		// 	(-2, 2)                 (0, 2)              (2, 2)
        Node n1 = node, 
            n2 = node;
        ArrayList<Node> freeNodeList = new ArrayList<>();
        int step = 1,
            x1, y1, x2, y2;
        boolean hasStNodeOutBound = false,
                isStNodeFound = false,
                hasNdNodeOutBound = false,
                isNdNodeFound = false;

        if (lineDirection.name().length() == 1) {
            x1 = lineDirection.getY();
            y1 = lineDirection.getX();
            x2 = - lineDirection.getY();
            y2 = - lineDirection.getX();
        } else {
            x1 = lineDirection.getX();
            y1 = - lineDirection.getY();
            x2 = - lineDirection.getX();
            y2 = lineDirection.getY();
        }

        while (true) {
            if (hasStNodeOutBound && hasNdNodeOutBound)
                break;

            if (!hasStNodeOutBound) {
                try {
                    n1 = map[node.getX() + (x1 * step)][node.getY() + (y1 * step)];
                    if (n1.getType() == 3) {
                        if (!isIntersectObstacle(n1, endNode, map))
                            freeNodeList.add(n1);
                        isStNodeFound = true;
                    }
                    else if (isStNodeFound)
                        hasStNodeOutBound = true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    hasStNodeOutBound = true;
                }
            }
            
            if (!hasNdNodeOutBound) {
                try {
                    n2 = map[node.getX() + (x2 * step)][node.getY() + (y2 * step)];
                    if (n2.getType() == 3) {
                        if (!isIntersectObstacle(n2, endNode, map))
                            freeNodeList.add(n2);
                        isNdNodeFound = true;
                    } 
                    else if (isNdNodeFound)
                        hasNdNodeOutBound = true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    hasNdNodeOutBound = true;
                }
            }
            
            step++;
        }

        return freeNodeList;
    }
}