package path_finding;

import java.util.ArrayList;

public class li2006_GAsImprove extends li2006_GAs {
    int aCOEFF;
    int bCOEFF;
    public li2006_GAsImprove(Map m, ArrayList<Node> wallList, int cSize, int numOfSizeForSelect) {
        super(m, wallList, cSize, numOfSizeForSelect);
    }

    public void setCommonParams(int crossoverPBTY, int mutationPBTY, int aCOEFF, int bCOEFF) {
        super.setCommonParams(crossoverPBTY, mutationPBTY);
        this.aCOEFF = aCOEFF;
        this.bCOEFF = bCOEFF;
    }

    /**
     * Path Smoothness: Sử dụng công thức trong bài báo: Mobile Robot Path
     * Planning with a Non-Dominated
     */
    @Override
    protected Double evaluate(ArrayList<Node> chromosome) {
        ArrayList<Double> distances = new ArrayList<>();
        double smoothness = 0;
        int n = chromosome.size();
        for (int i = 0; i < n - 1; i++)
            distances.add(chromosome.get(i).getEuclidDistToNode(chromosome.get(i + 1)));

        for (int i = 0; i < n - 1; i++) {
            Node n1 = chromosome.get(i),
                 n2 = chromosome.get(i + 1),
                 n3 = chromosome.get(i + 2);
            double a = (n2.getX() - n1.getX()) * (n3.getX() - n2.getX()),
                   b = (n2.getY() - n1.getY()) * (n3.getY() - n2.getY());
            smoothness += Math.PI - Math.acos((a + b) / (distances.get(i) * distances.get(i + 1)));
        }

        double totalDistance = distances.stream().mapToDouble(d -> d).sum();

        return aCOEFF * (1 / totalDistance) + bCOEFF * ((1 / n) * smoothness);
    }
}
