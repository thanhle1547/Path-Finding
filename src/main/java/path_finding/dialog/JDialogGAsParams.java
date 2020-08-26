package path_finding.dialog;

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
    public static int PENALTY_VALUE = 200;

    SpinnerModel populationSizeModel = new SpinnerNumberModel(POPULATION_SIZE, 2, 5000, 1);
    SpinnerModel generationNumberModel = new SpinnerNumberModel(GENERATION_NUMBER, 1, 5000, 5);
    SpinnerModel penaltyValueModel = new SpinnerNumberModel(PENALTY_VALUE, 1, 1000, 2);
    JSpinner spPopulationSize = new JSpinner(populationSizeModel);
    JSpinner spGenerationNumber = new JSpinner(generationNumberModel);
    JSpinner spPenaltyValue = new JSpinner(penaltyValueModel);

    Object[] message = {
        "Kích cỡ của quần thể", spPopulationSize,
        "Số thế hệ", spGenerationNumber,
        "Giá trị đánh giá tối thiểu", spPenaltyValue,
    };

    Object[] option = { "OK" };

    public void show() {
        JOptionPane.showConfirmDialog(
            null, 
            message,
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

    public int getPenaltyValue() {
        return (int) spPenaltyValue.getValue();
    }
}