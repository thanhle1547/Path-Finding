package path_finding.dialog;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * @author thanhLe1547
 */
public class JDialogGAsParams {
    // default
    public static int POPULATION_SIZE = 5;
    public static int GENERATION_NUMBER = 10; // maximum evolution
    public static int CROSSOVER_PROBABILITY = 50;
    public static int MUTATION_PROBABILITY = 5;

    SpinnerModel populationSizeModel = new SpinnerNumberModel(POPULATION_SIZE, 2, 5000, 1);
    SpinnerModel generationNumberModel = new SpinnerNumberModel(GENERATION_NUMBER, 1, 5000, 5);
    SpinnerModel crossoverPBTY = new SpinnerNumberModel(CROSSOVER_PROBABILITY, 1, 100, 1);
    SpinnerModel mutationPBTY = new SpinnerNumberModel(MUTATION_PROBABILITY, 1, 100, 5);
    JSpinner spPopulationSize = new JSpinner(populationSizeModel);
    JSpinner spGenerationNumber = new JSpinner(generationNumberModel);
    JSpinner spCrossoverPBTY = new JSpinner(crossoverPBTY);
    JSpinner spMutationPBTY = new JSpinner(mutationPBTY);

    ArrayList<Object> message = new ArrayList<>();

    Object[] option = { "OK" };

    public JDialogGAsParams() {
        message.add("Kích cỡ của quần thể");
        message.add(spPopulationSize);
        message.add("Số thế hệ");
        message.add(spGenerationNumber);
        message.add("Xác suất lai ghép (%)");
        message.add(spCrossoverPBTY);
        message.add("Xác xuất dột biến (%)");
        message.add(spMutationPBTY);
    }

    public int show() {
        return JOptionPane.showConfirmDialog(
            null, 
            message.toArray(),
            "Nhập các tham số cho thuật toán",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    }

    public int getPopulationSize() {
        return (int) spPopulationSize.getValue();
    }

    public int getGenerationNumber() {
        return (int) spGenerationNumber.getValue();
    }

    public int getCrossoverProbability() {
        return (int) spCrossoverPBTY.getValue();
    }

    public int getMutationProbability() {
        return (int) spMutationPBTY.getValue();
    }
}