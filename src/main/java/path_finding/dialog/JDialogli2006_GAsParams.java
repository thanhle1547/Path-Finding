package path_finding.dialog;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author thanhLe1547
 */
public class JDialogli2006_GAsParams extends JDialogGAsParams {
    // default
    public static int NUM_OF_SIZE_FOR_SELECT = 3; // số lượng cá thể muốn chọn vào 1 nhóm để random

    SpinnerModel numSizeValueModel = new SpinnerNumberModel(NUM_OF_SIZE_FOR_SELECT, 1, 1000, 2);
    JSpinner spNumSize = new JSpinner(numSizeValueModel);

    public JDialogli2006_GAsParams() {
        message.add("Số lượng cá thể/nhiễm sắc thể muốn chọn \nvào 1 nhóm để random trong bước `Chọn lọc`");
        message.add(spNumSize);

        populationSizeModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SpinnerNumberModel model = (SpinnerNumberModel) e.getSource();
                Integer value = (Integer) model.getValue();
                if ((Integer) numSizeValueModel.getValue() > value)
                    numSizeValueModel.setValue(value);
            }
        });
        numSizeValueModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SpinnerNumberModel model = (SpinnerNumberModel) e.getSource();
                Integer value = (Integer) populationSizeModel.getValue();
                if ((Integer) model.getValue() > value)
                    model.setValue(value);
            }
        });
    }

    public int getNumOfSizeForSelect() {
        return (int) spNumSize.getValue();
    }
}