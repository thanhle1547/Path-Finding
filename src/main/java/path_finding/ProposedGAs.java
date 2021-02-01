package path_finding;

import java.util.ArrayList;

public class ProposedGAs extends li2006_GAs {

    public ProposedGAs(Map m, ArrayList<Node> wallList, int cSize, int numOfSizeForSelect) {
        super(m, wallList, cSize, numOfSizeForSelect);
    }
    
    @Override
    public void refinement() {
        // 	(-2, -2)               (0, -1)	            (2, -2)
        // 		    O (-1, -1)   F (0, -1)	  O (1, -1)
        // 	(-2, 0) F (-1, 0) 	   (0, 0)     F (1, 0)  (0, 2)
        // 		    O (-1, 1)    F (0, 1)	  O (1, 1)
        // 	(-2, 2)                (0, 2)               (2, 2)
        
        // NE: o at NW f -> s, o at SE s -> f;  NE, NW same column ; NE, SE same row
        // SW: o at NW s -> f, o at SE f -> s;  SW, NW same row ; SW, SE same column
        // SE: o at NE s -> f, o at SW f -> s
        // NW: o at NE f -> s, o at SW s -> f
        // ==> same row: s -> f & same column f -> s
        ArrayList<ArrayList<Node>> cloneP = new ArrayList<>(population);
        for (int i = 0; i < population.size(); i++) {
            ArrayList<Node> cloneC = cloneP.get(i);
            int j = 1;
            while (j < cloneC.size() - 1) {
                Node current = cloneC.get(j),
                    obstacle = null;
                int numberOfObs = 0;

                for (int r = -1; r < 2; r += 2) {
                    for (int c = -1; c < 2; c += 2) {
                        try {
                            Node node = map[current.getX() + r][current.getY() + c];
                            if (node.getType() == 2) {
                                obstacle = node;
                                numberOfObs++;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }

                if (numberOfObs != 1 || obstacle == null) {
                    j++;
                    continue;
                }

                Node first = map[obstacle.getX()][current.getY()],
                    second = map[current.getX()][obstacle.getY()];

                // direction of the path
                Direction dPath = cloneC.get(j - 1).getDirection(current);
                // direction from current to obstacle
                Direction dCurToObs = current.getDirection(obstacle);

                if (dPath.getX() == dCurToObs.getX()) {
                    cloneC.set(j, second);
                    cloneC.add(j + 1, first);
                } else {
                    cloneC.set(j, first);
                    cloneC.add(j + 1, second);
                }
            }
            cloneP.set(i, cloneC);
        }
        population = cloneP;
    }
}
