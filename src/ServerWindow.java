import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ServerWindow extends JFrame {

    private JPanel contentPane;
    private JTextArea textArea;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ServerWindow frame = new ServerWindow();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ServerWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 561, 419);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        textArea = new JTextArea();
        textArea.setBounds(10, 34, 205, 338);

        // Wrap the text area in a scroll pane for better handling of long text
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 34, 205, 338);
        contentPane.add(scrollPane);


        ServerService.getInstance(textArea).startServer();
    }
}
