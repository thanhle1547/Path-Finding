package path_finding.dialog;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * @author thanhLe1547
 */
public class JDialogMyCustomGAsParams extends JDialogGAsParams {
    // default
    public static int PENALTY_VALUE = 200;

    SpinnerModel penaltyValueModel = new SpinnerNumberModel(PENALTY_VALUE, 1, 1000, 2);
    JSpinner spPenaltyValue = new JSpinner(penaltyValueModel);

    public JDialogMyCustomGAsParams() {
        super();
        message.add("Giá trị đánh giá tối thiểu");
        message.add(spPenaltyValue);
    }

    public int getPenaltyValue() {
        return (int) spPenaltyValue.getValue();
    }
}