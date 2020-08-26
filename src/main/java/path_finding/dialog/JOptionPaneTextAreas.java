package path_finding.dialog;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author thanhLe1547
 */
public class JOptionPaneTextAreas {
    JScrollPane scrollPane;
    JTextArea textArea;

    JFrame parentFrame;

    public JOptionPaneTextAreas(JFrame parentFrame, boolean lineWrap) {
        this.parentFrame = parentFrame;
        textArea = new JTextArea();
        textArea.setLineWrap(lineWrap);
        textArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(textArea);
    }

    public void show(int xPos, int yPos, int width, int height) {
        JOptionPane pane = new JOptionPane(new Object[] { scrollPane }, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = pane.createDialog(parentFrame, "Danh sách quần thể");
        dialog.setBounds(xPos, yPos, width, height);
        dialog.setVisible(true);
    }

    public void appendText(String text) {
        textArea.append(text);
        // force the text area to scroll to the bottom by moving the caret to the end of
        // the text area
        // https://docs.oracle.com/javase/tutorial/uiswing/components/textarea.html
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void setText(String text) {
        textArea.setText(text);
    }
}