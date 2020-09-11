package path_finding;

import java.util.ArrayList;
import java.util.NoSuchElementException;
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
public class li2006_GAs_copy3 extends GeneticAlgorithm {
    /**
     * Số lượng cá thể muốn chọn vào 1 nhóm để random trong bước `Chọn lọc`
     */
    int numOfSizeForSelect;

    public li2006_GAs_copy3(Map m, ArrayList<Node> wallList, int cSize, int numOfSizeForSelect) {
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
        int currIndex = 0;
        boolean hasAddedNode = false,
                hasAddEndNode = false;
        Node startNode = this.startNode, 
            endNode = this.finishNode, 
            wallNode,
            freeNode;
        ArrayList<Node> chromosome = new ArrayList<>();
        ArrayList<Node> wallNodeList,
                        freeNodeList;
        ArrayList<Integer> foundIndex = new ArrayList<>();

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
                    startNode, 
                    wallNode, 
                    endNode
                );

                while (true) {
                    int i = rd.nextInt(freeNodeList.size());
                    if (foundIndex.size() == freeNodeList.size())
                        throw new NoSuchElementException();

                    if (foundIndex.contains(i))
                        continue;

                    foundIndex.add(i);
                    freeNode = freeNodeList.get(i);

                    if (!isIntersectObstacle(startNode, freeNode, map)) {
                        chromosome.add(!hasAddEndNode ? currIndex + 1 : currIndex - 1, freeNode);
                        currIndex += 2;
                        hasAddEndNode = false;
                        break;
                    } else if (!isIntersectObstacle(freeNode, endNode, map)) {
                        chromosome.add(chromosome.size() - currIndex + 1, freeNode);
                        currIndex -= 1;
                        hasAddEndNode = true;
                        break;
                    }
                }

                startNode = chromosome.get(!hasAddEndNode ? currIndex - 1 : currIndex);
                endNode = chromosome.get(hasAddEndNode ? currIndex + 1 : currIndex);
                foundIndex.clear();

                // for (int i = 0; i < chromosome.size(); i++) {
                //     if (isIntersectObstacle(chromosome.get(i), chromosome.get(i + 1), map)) {
                //         startNode = chromosome.get(i);
                //         endNode = chromosome.get(i + 1);
                //         currIndex = i + 1;
                //         break;
                //     }
                // }
                hasAddedNode = true;
            } catch (IndexOutOfBoundsException | IllegalArgumentException | NoSuchElementException e) {
                hasAddedNode = false;
            }
        }
    }

    /**
     * Chọn lọc 
     * <p>Các phương pháp được sử dụng:</p>
     * <ul>
     *  <li>Tournament Selection</li>
     *  <li>
     *      Elitism
     *      @see <p><a href="https://www.researchgate.net/post/What_is_meant_by_the_term_Elitism_in_the_Genetic_Algorithm">
     *              What is meant by the term Elitism in the Genetic Algorithm?
     *          </a></p>
     *          <p><a href="https://stackoverflow.com/questions/14622342/elitism-in-ga-should-i-let-the-elites-be-selected-as-parents?fbclid=IwAR3pYTT3Onyd_ZB-oMLGnrBi9ZOJpWM7X150P7Hau_ISx9uWxztVP9p7yCw">
     *              Elitism in GA: Should I let the elites be selected as parents?
     *          </a></p>
     *  </li>
     * </ul>
     */
    public void select() {
        int n = population.size(),
            b = 0;      // indexOfCurrentBestFitness
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

    protected ArrayList<Node> getRedialNodeList(
            Direction lineDirection, Node startNode, Node node, Node endNode)
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