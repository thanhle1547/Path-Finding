package path_finding.dialog;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * @author thanhLe1547
 */
public class JDialogli2006_GAsParams extends JDialogGAsParams {
    // default
    public static int NUM_OF_SIZE_FOR_SELECT = 10; // số lượng cá thể muốn chọn vào 1 nhóm để random
    public static int CROSSOVER_PROBABILITY = 50;
    public static int MUTATION_PROBABILITY = 5;

    SpinnerModel numSizeValueModel = new SpinnerNumberModel(NUM_OF_SIZE_FOR_SELECT, 1, 1000, 2);
    SpinnerModel crossoverPBTY = new SpinnerNumberModel(CROSSOVER_PROBABILITY, 1, 100, 1);
    SpinnerModel mutationPBTY = new SpinnerNumberModel(MUTATION_PROBABILITY, 1, 100, 5);
    JSpinner spNumSize = new JSpinner(numSizeValueModel);
    JSpinner spCrossoverPBTY = new JSpinner(crossoverPBTY);
    JSpinner spMutationPBTY = new JSpinner(mutationPBTY);

    public JDialogli2006_GAsParams() {
        message.add("Số lượng cá thể/nhiễm sắc thể muốn chọn \nvào 1 nhóm để random trong bước `Chọn lọc`");
        message.add(spNumSize);
        message.add("Xác suất lai ghép (%)");
        message.add(spCrossoverPBTY);
        message.add("Xác xuất dột biến (%)");
        message.add(spMutationPBTY);
    }

    public int getNumOfSizeForSelect() {
        return (int) spNumSize.getValue();
    }

    public int getCrossoverProbability() {
        return (int) spCrossoverPBTY.getValue();
    }
    
    public int getMutationProbability() {
        return (int) spMutationPBTY.getValue();
    }
}