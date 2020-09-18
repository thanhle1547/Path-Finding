package path_finding;

import java.awt.geom.Line2D;
// import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Thuật toán này sử dụng các phương pháp được đề xuất trong các bài báo khoa học (papers) sau:
 * <ol>
 *   <li>
 *      Li Q, Zhang W, Yin Y, Wang Z, Liu G. <b>An improved genetic algorithm of optimum path planning 
 *      for mobile robots.</b> In: Sixth international conference on intelligent systems design and 
 *      applications, ISDA ’06; 2006
 *   </li>
 *   <li>
 *      A. Tuncer and M. Yildirim, <b>“Dynamic path planning of mobile robots with improved genetic 
 *      algorithm,”</b> Computers & Electrical Engineering, vol. 38, no. 6, pp. 1564–1572, 2012.
 *   </li>
 * </ol>
 * 
 * <p>
 * <b>Tiêu chí tối ưu:</b> đường đi ngắn nhất
 * </p>
 * 
 * @author thanhLe1547
 */
public class GeneticAlgorithm {
    Node startNode;
    Node finishNode;
    Node[][] map;
    ArrayList<Node> wallList;
    int CSIZE;

    ArrayList<ArrayList<Node>> population;
    ArrayList<Double> fitness;
    int penaltyValue;
    double crossoverPBTY;
    double mutationPBTY;

    ArrayList<Node> bestIndividual; // OR bestChromosome
    Double bestIndvFitness; // bestIndividualFitness

    public GeneticAlgorithm(Map m, ArrayList<Node> wallList, int cSize) {
        this.startNode = m.getStartNode();
        this.finishNode = m.getFinishNode();
        this.map = m.getMap();
        this.wallList = wallList;
        CSIZE = cSize;

        fitness = new ArrayList<>();
    }

    public GeneticAlgorithm(Map m, ArrayList<Node> wallList, int cSize, int penaltyValue) {
        this(m, wallList, cSize);
        this.penaltyValue = penaltyValue;
    }

    public void setCommonParams(int crossoverPBTY, int mutationPBTY) {
        this.crossoverPBTY = crossoverPBTY / 100.0;
        this.mutationPBTY = mutationPBTY / 100.0;
    }

    /**
     * Khởi tạo quần thể - Encoding of chromosomes: Decimal coding
     * 
     * @param mapCols - size of map (2d array)
     * @param capacity ~ size^2
     */
    public void initPopulation(int size, int mapCols, int mapRows, int capacity) {
        population = new ArrayList<>();
        Random rd = new Random();
        for (int i = 0; i < size; i++) {
            ArrayList<Node> chromosome = new ArrayList<>();
            chromosome.add(startNode);
            
            while (true) {
                /* int x = rd.nextInt(max - min + 1) + min,
                    y =	rd.nextInt(max - min + 1) + min;
                */

                // In case chromosome look like: [ 0, 12, 17, 35, 99 ]
                // In case chromosome NOT look like ABOVE
                // https://stackoverflow.com/a/13001636
                int x = rd.nextInt(capacity),
                    y =	rd.nextInt(capacity);
                Node node = map[x % mapCols][y / mapRows];

                if (node.isSameWith(finishNode) && chromosome.size() > 1)
                    break;

                // if (node.containIn(chromosome)) continue;
                // if (node.containIn(wallList)) continue;
                // result SAME as ABOVE
                if (chromosome.contains(node)) continue;
                if (wallList.contains(node)) continue;

                if (!isIntersectObstacle(chromosome.get(chromosome.size() - 1), node, map))
                    chromosome.add(node);
            }

            chromosome.add(finishNode);

            population.add(chromosome);
        }
    }

    public void evaluate() {
        fitness.clear();
        evaluate(population, fitness);
    }

    protected void evaluate(ArrayList<ArrayList<Node>> population, ArrayList<Double> fitness) {
        for (int i = 0; i < population.size(); i++)
            fitness.add(evaluate(population.get(i)));
    }

    /**
     * Hàm mục tiêu (fitness)/đánh giá (evaluate) lấy một tập các chuỗi nhiễm sắc
     * thể như là đầu vào và trả về giá trị tượng trưng cho chuỗi nhiễm sắc thể đó
     * để đánh giá trên vấn đề cần giải quyết
     */
    protected Double evaluate(ArrayList<Node> chromosome) {
        double distances = 0;
        for (int i = 0; i < chromosome.size() - 1; i++)
            distances += chromosome.get(i).getEuclidDistToNode(chromosome.get(i + 1));
        return distances;
    }

    /**
     * Chọn lọc
     *  - Sử dụng phương pháp Proportion Model (Roulette Wheel Selection)
     */
    public void select() {
        // Chứa index của các chromosome có gtri fitness > penalty
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            // if (isIntersectObstacle(population.get(i)))
            //     fitness.set(i, fitness.get(i) + penaltyValue)
            if (fitness.get(i) > penaltyValue)
                indexes.add(i);
        }

        // Khi xóa 1 phần tử thì index của ArrayList thay đổi --> xóa từ cuối
        Collections.reverse(indexes);

        indexes.forEach(i -> {
            population.remove((Object) i);
            fitness.remove((Object) i);
        });
    }

    /**
     * Lựa chọn cá thể tốt nhất trong quần thể
     * <p>
     *  <b>Note:</b>
     *  <ol>
     *      <li>
     *          Phương thức này sẽ lần lượt gọi lại hàm đánh giá (evaluate) và chọn lọc (select)
     *      </li>
     *      <li>
     *          Kết quả cuối cùng có thể sẽ ko tối ưu vì trong bước lai ghép ko đánh giá cá thể con 
     *          HOẶC trong bước chọn lọc không loại bỏ hoàn toàn các cá thể ko khả thi
     *      </li>
     *  </ol>
     * </p>
     */
    public void selectBestOnce() {
        evaluate();
        select();

        Double minFitness = Collections.min(fitness);
        int index = fitness.indexOf(minFitness);
        ArrayList<Node> chromosome = population.get(index);

        bestIndividual = chromosome;
        bestIndvFitness = minFitness;
    }
    
    /**
     * Lai ghép
     * 
     * @see <p>Bài báo số 1 đã được đề cập</p>
     *      <p>Đề xuất sử dụng phương pháp one-point crossover operation</p>
     */
    public void crossover() {
        // sortPopulation();
        int breakpoint = (int) (crossoverPBTY * population.size());
        ArrayList<ArrayList<Node>> subList = new ArrayList<>(population.subList(0, breakpoint));
        ArrayList<ArrayList<Node>> cloneP = new ArrayList<>(subList);
        ArrayList<Double> cloneF = new ArrayList<>(fitness.subList(0, breakpoint));

        for (int i = 0; i < subList.size(); i += 2) {
            ArrayList<Node> parent_1, parent_2;
            try {
                parent_1 = subList.get(i);
                parent_2 = subList.get(i + 1);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            
            // index of the common node
            int i1 = -1,
                i2 = -1;
            boolean isInterconnectedUsed = false,
                    isDiff = true;

            // Crossover at the location of the common node
            for (int j = 1; j < parent_1.size(); j++) {
                i1 = j;
                i2 = parent_1.get(j).getIndexOfNo(parent_2);

                if (i2 != -1){
                    // kiểm tra xem các node (gene) đằng sau có giống nhau ko
                    if (parent_1.size() - i1 == parent_2.size() - i2) {
                        for (int m = i1, n = i2; m < parent_1.size(); m++, n++) {
                            if (parent_1.get(m).getNo() != parent_2.get(n).getNo()) {
                                isDiff = false;
                                break;
                            }
                        }
                    }
                    break;
                }
            }

            if (isDiff) continue;

            // If not found then
            // Crossover at the location of the potential node
            //     OR
            // Crossover at the location of the interconnected node pair
            if (i2 == -1) {
                Random rd = new Random();
                // chứa index của các phần tử (parent_1) đã random được
                ArrayList<Integer> selectedList = new ArrayList<>();
                int index_1, index_2;
                while (true) {
                    if (selectedList.size() != parent_1.size() - 2) {
                        // Random theo công thức, ko bao gồm giá trị cuối/lớn nhất
                        // rd.nextInt(max - 1 - min + 1) + min
                        index_1 = rd.nextInt(parent_1.size() - 2) + 1;
                        // nằm trong khoảng của parent_2 ??? có cần
                        if (selectedList.contains(index_1))
                            continue;

                        selectedList.add(index_1);
                        index_2 = parent_1.get(index_1).getPossibleIndexOfNo(parent_2);
                        if (index_2 == -1)
                            continue;
                    } else {
                        // TH tìm hết parent_1 rồi nhưng ko có cái nào
                        index_1 = rd.nextInt(parent_1.size() - 2) + 1;
                        index_2 = rd.nextInt(parent_2.size() - 2) + 1;
                        isInterconnectedUsed = true;
                    }

                    i1 = ++index_1;
                    i2 = ++index_2;
                    break;
                }
            }

            // offspring/child
            // example: [0, 1, 2, 3, 4].subList(0, 3)
            //      --> result: [0, 1, 2]
            ArrayList<Node> child_1 = new ArrayList<>(parent_1.subList(0, i1)),
                            child_2 = new ArrayList<>(parent_2.subList(0, i2));

            child_1.addAll(isInterconnectedUsed ? --i1 : i1, parent_1.subList(i1, parent_1.size()));
            child_2.addAll(isInterconnectedUsed ? --i2 : i2, parent_2.subList(i2, parent_2.size()));
            
            cloneP.add(child_1);
            cloneF.add(evaluate(child_1));
            cloneP.add(child_2);
            cloneF.add(evaluate(child_2));
        }

        subList = new ArrayList<>(population.subList(breakpoint, population.size()));
        cloneP.addAll(subList);
        population = cloneP;

        cloneF.addAll(fitness.subList(breakpoint, fitness.size()));
        fitness = cloneF;
    }
    
    /**
     * Đột biến
     * 
     * @see <p>
     *          Bài báo số 2 đã được đề cập, 
     *          mục <code>3. A new mutation operator for path planning</code>
     *      </p>
     *      <ol>
     *          <li>
     *              Select one node, which is not the start or target node, randomly
     *              from the mutation individual as the mutation gene</li>
     *          <li>
     *              Define a set, which consists of all feasible (non-obstacle) neighbor
     *              nodes of mutation node</li>
     *          <li>
     *              Determine the fitness values of all paths, each one of them consists
     *              a neighbor node from the set</li>
     *          <li>
     *              The mutated node which have the best fitness value is replaced
     *              with the original mutation node</li>
     *      </ol>
     */
    public void mutation() {
        // sortPopulation();
        int breakpoint = (int) (mutationPBTY * population.size());
        ArrayList<ArrayList<Node>> subList = 
                new ArrayList<>(population.subList(breakpoint, population.size()));
        ArrayList<ArrayList<Node>> clone = new ArrayList<>(subList);

        Random rd = new Random();
        boolean isMutate;
        int nodeIndex;
        Node selectedNode, nodeBefore, nodeAfter;
        ArrayList<ArrayList<Node>> offsprings = new ArrayList<>();
        ArrayList<Double> offspringsF = new ArrayList<>();
        Double lowerFitness;
        int indexLowerFitness;

        /* 
        // Từ bài báo số 1
        Node substituteNode;
        ArrayList<Node> vicinityNodes; // include selectedNode
        Direction dirn; 
        */

        for (int i = 0; i < subList.size(); i++) {
            isMutate = rd.nextBoolean();
            if (isMutate) {
                ArrayList<Node> chromosome = subList.get(i);
                // Từ bài báo số 2
                // random select a node  (not the start node or the goal node)
                nodeIndex = rd.nextInt(chromosome.size() - 2) + 1;

                selectedNode = chromosome.get(nodeIndex);
                nodeBefore = chromosome.get(nodeIndex - 1);
                nodeAfter = chromosome.get(nodeIndex + 1);
                /* dirn = nodeBefore.getDirection(nodeAfter); */

                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        try {
                            Node node = map[selectedNode.getX() + k][selectedNode.getY() + k];
                            if (node.getType() == 3) {
                                ArrayList<Node> offspring = new ArrayList<>(chromosome);
                                offspring.set(nodeIndex, node);

                                if (!isIntersectObstacle(nodeBefore, node, nodeAfter, map))
                                    offsprings.add(offspring);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }

                // substituteNode = getSubtitudeNode(vicinityFreeNodes, dirn);

                if (offsprings.isEmpty())
                    continue;
                
                evaluate(offsprings, offspringsF);

                lowerFitness = Collections.min(offspringsF);
                indexLowerFitness = offspringsF.indexOf(lowerFitness);
                clone.set(i, offsprings.get(indexLowerFitness));
                fitness.set(breakpoint + i, lowerFitness);

                offsprings.clear();
                offspringsF.clear();
            }
        }

        subList = new ArrayList<>(population.subList(0, breakpoint));
        clone.addAll(0, subList);
        population = clone;
    }

    /* 
    // Từ bài báo số 1
    public Node getSubtitudeNode(ArrayList<Node> vicinityNodes, Direction dirn) {
        // Assume that vicinityNodes have 9 item
        int center = 4; // vicinityNodes.size() / 2 + 1;
        Node substituteNode = vicinityNodes.get(center);
    } */

    /**
     * @see <a href="https://stackoverflow.com/a/35718576">
     *          Get the indices of an array after sorting?
     *      </a>
     */
    protected void sortPopulation() 
    {
        ArrayList<ArrayList<Node>> cloneP = new ArrayList<>();
        int[] sortedIndices;

        Collections.sort(fitness);
        sortedIndices = IntStream.range(0, fitness.size()).boxed()
                .sorted((i, j) -> fitness.get(i).compareTo(fitness.get(j)))
                .mapToInt(e -> e)
                .toArray();

        for (int k : sortedIndices) {
            cloneP.add(population.get(k));
        }

        population = cloneP;
    }

    protected boolean isIntersectObstacle(ArrayList<Node> chromosome) {
        for (int i = 1; i < chromosome.size() - 1; i++)
            if (isIntersectObstacle(
                    chromosome.get(i - 1), 
                    chromosome.get(i), 
                    chromosome.get(i + 1), 
                    map)
                )
                return true;
        return false;
    }

    protected boolean isIntersectObstacle(Node nodeBefore, Node node, Node nodeAfter, Node[][] map) {
        return isIntersectObstacle(nodeBefore, node, map) 
                || isIntersectObstacle(node, nodeAfter, map);
    }

    protected boolean isIntersectObstacle(Node startNode, Node endNode, Node[][] map) {
        ArrayList<Node> wallNode = new ArrayList<>();
        Line2D line = new Line2D.Double(
                startNode.getCenterPoint(CSIZE), 
                endNode.getCenterPoint(CSIZE)
        );

        // TH: nút bđ và nút kt ko đi theo chiều tăng dần -> sắp xếp lại để lấy tọa độ
        // -> Kq: Tọa độ lớn nhất (0), Tọa độ nhỏ nhất (1)
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
                if (map[i][j].getType() == 2)
                    wallNode.add(map[i][j]);
            }
        }

        if (wallNode.isEmpty())
            return false;
            
        /* 
        // CÁCH 1
        ArrayList<Point2D> polyPoints = new ArrayList<>();

        // Đường đi là các đường thẳng/ngang/chéo, ko đi zic zac
        // -> kiểm tra xem đt có giao với vật cản (đa giác ~ hình vuông) hay ko
        // -> lấy tọa độ của 4 đỉnh
        for (int i = 0; i < wallNode.size(); i++) {
            Node node = wallNode.get(i);
            int x = node.getX() * CSIZE,
                y = node.getY() * CSIZE;
            polyPoints.add(new Point2D.Double(x, y));
            polyPoints.add(new Point2D.Double(x + CSIZE, y));
            polyPoints.add(new Point2D.Double(x + CSIZE, y + CSIZE));
            polyPoints.add(new Point2D.Double(x, y + CSIZE));
        }

        // Kiểm tra xem đt có giao với vật cản ko
        // https://stackoverflow.com/questions/14652337/java-class-to-check-if-a-line-intersects-a-shape/14652840#14652840
        boolean isIntersect = false;
        int size = polyPoints.size();

        // point: số đỉnh của vật cản (hình vuông) đã được duyệt
        for (int i = 0, point = 1; i < size - 1; i++, point++) {
            double  nextX = polyPoints.get(i + 1).getX(),
                    nextY = polyPoints.get(i + 1).getY();
            
            isIntersect = line.intersectsLine(
                polyPoints.get(i).getX(), polyPoints.get(i).getY(),
                nextX, nextY
            );

            if (isIntersect) {
                return true;
            } else if (point == 3) {
                isIntersect = line.intersectsLine(
                    nextX, nextY, 
                    polyPoints.get(i - 2).getX(), polyPoints.get(i - 2).getY()
                );
                
                if (isIntersect) return true;

                point = 1;
            }
        } */

        // CÁCH 2
        // Thay vì dùng cách trên, trong lớp Line2D đã có sẵn phương thức để kiểm tra
        for (int i = 0; i < wallNode.size(); i++) {
            Node node = wallNode.get(i);
            if (line.intersects(new Rectangle(node.getX() * CSIZE, node.getY() * CSIZE, CSIZE, CSIZE)))
                return true;
        }

        return false;
    }

    protected boolean isIntersectObstacle(Node startNode, Node endNode, Node node) {
        Line2D line = new Line2D.Double(
                startNode.getCenterPoint(CSIZE), 
                endNode.getCenterPoint(CSIZE)
        );
        return line.intersects(new Rectangle(node.getX() * CSIZE, node.getY() * CSIZE, CSIZE, CSIZE));
    }

    protected ArrayList<Node> getIntersectWallNode(Node startNode, Node endNode) {
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
        return wallNodes;
    }

    /* public void clearFitness() {
        fitness.clear();
    } */

    protected String dataToString(ArrayList<Node> chromosome, Double fitness) {
        return "[" + fitness + "]\n"
            + "<< " 
            + chromosome.stream()
                .map(n -> String.valueOf(n.getNo()))
                .collect(Collectors.joining(", "))
                .concat(" >>\n\n");
    }

    public String dataToString(boolean includeStringDataFormat) {
        StringBuilder result = new StringBuilder();
        if (includeStringDataFormat)
            result.append(getStringDataFormat());
        for (int i = 0; i < population.size(); i++) {
            result.append(
                String.valueOf(i + 1) + ".  "
                + dataToString(population.get(i), fitness.get(i))
            );
        }
        return result.toString();
    }
    
    public String[] dataToString() {
        String[] result = new String[population.size()];
        for (int i = 0; i < population.size(); i++) {
            result[i] = String.valueOf(i + 1) + ".  "
                    + dataToString(population.get(i), fitness.get(i));
        }
        return result;
    }
    
    public String bestIndvToString(boolean includeStringDataFormat) {
        return (includeStringDataFormat ? getStringDataFormat() : "")
            +   "Cá thể (tốt nhất):\n"
            +   dataToString(bestIndividual, bestIndvFitness);
    }
    
    public String[] bestIndvToString() {
        return new String[] {
                "Cá thể (tốt nhất):\n"
            +   dataToString(bestIndividual, bestIndvFitness)
        };
    }

    public String getStringDataFormat() {
        return "Dữ liệu được hiển thị theo mẫu sau:\n"
            +   "  +, Fitness của nhiễm sắc thể:\n"
            +   "               [ giá trị ]\n"
            +   "  +, Nhiễm sắc thể (chromosome)/cá thể (individual):\n"
            +   "           <<  các gene/node  >>\n\n"
            +   "------------------------------------------\n";
    }
        
    public ArrayList<ArrayList<Node>> getPopulation() {
        return population;
    }
    
    public ArrayList<ArrayList<Node>> getBestIndvAsPopulation() {
        ArrayList<ArrayList<Node>> population = new ArrayList<>();
        population.add(bestIndividual);
        return population;
    }

    public Double getBestIndvFitness() {
        return bestIndvFitness;
    }
}