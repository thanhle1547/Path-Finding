package path_finding.dialog;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * 
 * @author thanhLe1547
 */
public class JDialogli2006_GAsImproveParams extends JDialogli2006_GAsParams {
    // default
    /**
     * Trọng số cho đường đi ngắn nhất
     */
    public static int A_COEFFICIENT = 3;
    /**
     * Trọng số cho độ mượt/trơn tru của đường đi
     */
    public static int B_COEFFICIENT = 1;

    SpinnerModel aCOEFFValueModel = new SpinnerNumberModel(A_COEFFICIENT, 1, 1000, 1);
    SpinnerModel bCOEFFValueModel = new SpinnerNumberModel(B_COEFFICIENT, 1, 1000, 1);
    JSpinner spACOEFF = new JSpinner(aCOEFFValueModel);
    JSpinner spBCOEFF = new JSpinner(bCOEFFValueModel);

    public JDialogli2006_GAsImproveParams() {
        message.add("Trọng số cho đường đi ngắn nhất");
        message.add(spACOEFF);
        message.add("Trọng số cho độ mượt/trơn tru của đường đi");
        message.add(spBCOEFF);
    }

    public int getACoefficient() {
        return (int) spACOEFF.getValue();
    }
    
    public int getBCoefficient() {
        return (int) spBCOEFF.getValue();
    }
}