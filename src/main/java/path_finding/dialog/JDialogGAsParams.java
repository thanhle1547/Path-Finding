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

    SpinnerModel populationSizeModel = new SpinnerNumberModel(POPULATION_SIZE, 2, 5000, 1);
    SpinnerModel generationNumberModel = new SpinnerNumberModel(GENERATION_NUMBER, 1, 5000, 5);
    JSpinner spPopulationSize = new JSpinner(populationSizeModel);
    JSpinner spGenerationNumber = new JSpinner(generationNumberModel);

    ArrayList<Object> message = new ArrayList<>();

    Object[] option = { "OK" };

    public JDialogGAsParams() {
        message.add("Kích cỡ của quần thể");
        message.add(spPopulationSize);
        message.add("Số thế hệ");
        message.add(spGenerationNumber);
    }

    public void show() {
        JOptionPane.showConfirmDialog(
            null, 
            message.toArray(),
            "Nhập các tham số cho thuật toán",
            JOptionPane.DEFAULT_OPTION, // only `OK` button
            JOptionPane.PLAIN_MESSAGE
        );
    }

    public int getPopulationSize() {
        return (int) spPopulationSize.getValue();
    }

    public int getGenerationNumber() {
        return (int) spGenerationNumber.getValue();
    }
}