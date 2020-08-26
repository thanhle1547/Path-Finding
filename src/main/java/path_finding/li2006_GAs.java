package path_finding;

import java.util.ArrayList;
import java.util.Collections;
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
public class li2006_GAs extends GeneticAlgorithm {

    public li2006_GAs(Map m, ArrayList<Node> wallList, int cSize, int penaltyValue) {
        super(m, wallList, cSize, penaltyValue);
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
        int currIndex = 1;
        ArrayList<Node> chromosome = new ArrayList<>();
        chromosome.add(startNode);
        chromosome.add(finishNode);
        while (true) {
            try {
                // Node startNode = chromosome.get(currIndex - 1); // ~  size - 2
                // Node endNode = chromosome.get(currIndex); // ~ size - 1
                chromosome.add(currIndex, randomIntersectNode(
                    chromosome.get(currIndex - 1), 
                    chromosome.get(currIndex), 
                    map
                ));
                currIndex++;
            } catch (Exception e) {
                return chromosome;
            }
        }
    }

    /**
     * Chọn lọc - Sử dụng phương pháp Tournament Selection
     */
    public void select() {
        int s = 10,                 // selectedNumber ~ số lượng cá thể muốn chọn
            n = population.size(),
            b = 0;                  // indexBestFitness
        Random rd = new Random();
        ArrayList<ArrayList<Node>> newPopulation = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            b = 0;
            for (int j = 0; j < s; j++) {
                int rdIndex = rd.nextInt(n);
                if (b == 0 || fitness.get(rdIndex) > fitness.get(b))
                    b = rdIndex;
            }
            newPopulation.add(population.get(b));
        }
        population = newPopulation;
    }

    protected Node randomIntersectNode(Node startNode, Node endNode, Node[][] map) 
            throws IndexOutOfBoundsException 
    {
        ArrayList<Node> wallNodes = new ArrayList<>();
        ArrayList<Integer> 	x_coords = new ArrayList<>(),
                            y_coords = new ArrayList<>();
        x_coords.add(startNode.getX());
        x_coords.add(endNode.getX());
        y_coords.add(startNode.getY());
        y_coords.add(endNode.getY());

        Collections.sort(x_coords);
        Collections.sort(y_coords);

        for (int i = x_coords.get(0); i <= x_coords.get(1); i++) {
            for (int j = y_coords.get(0); j <= y_coords.get(1); j++) {
                if (map[i][j].getType() == 2 && isIntersectObstacle(startNode, endNode, map[i][j]))
                    wallNodes.add(map[i][j]);
            }
        }

        return wallNodes.get(new Random().nextInt(wallNodes.size()));
    }

    /* protected Node getRedialNode(Node node, Node[][] map) {

    } */
}