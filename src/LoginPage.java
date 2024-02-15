

import io.socket.client.Ack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JLabel lblNewLabel;
    private JLabel createAccountLabel;
    private JButton btnLogin;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginPage frame = new LoginPage();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginPage() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ClientService.getInstance().startServer();

        setBounds(100, 100, 420, 480);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(175, 238, 238));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(175, 238, 238));
        panel.setBounds(10, 10, 400, 460);
        contentPane.add(panel);
        panel.setLayout(null);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(UIManager.getColor("Button.light"));
        panel_1.setBounds(20, 20, 355, 400);
        panel.add(panel_1);
        panel_1.setLayout(null);

        JLabel LoginLabel = new JLabel("Login");
        LoginLabel.setFont(new Font("Yu Gothic UI Semilight", Font.BOLD, 36));
        LoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        LoginLabel.setBounds(10, 22, 335, 57);
        panel_1.add(LoginLabel);

        lblNewLabel = new JLabel("________________________________");
        lblNewLabel.setForeground(new Color(128, 128, 128));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(23, 65, 332, 13);
        panel_1.add(lblNewLabel);

        txtUsername = new JTextField();
        txtUsername.setBackground(UIManager.getColor("Button.light"));
        txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 17));
        txtUsername.setText("Username");
        txtUsername.setBounds(35, 121, 299, 32);
        txtUsername.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Username")) {
                    txtUsername.setText("");
                    txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 17));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setText("Username");
                    txtUsername.setFont(new Font("Tahoma", Font.ITALIC, 17));
                }
            }
        });
        panel_1.add(txtUsername);
        txtUsername.setColumns(10);

        txtPassword = new JTextField();
        txtPassword.setBackground(UIManager.getColor("Button.light"));
        txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 17));
        txtPassword.setText("Password");
        txtPassword.setBounds(35, 181, 299, 32);
        txtPassword.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtPassword.getText().equals("Password")) {
                    txtPassword.setText("");
                    txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 17));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtPassword.getText().isEmpty()) {
                    txtPassword.setText("Password");
                    txtPassword.setFont(new Font("Tahoma", Font.ITALIC, 17));
                }
            }
        });
        panel_1.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnLogin.setBackground(new Color(176, 224, 230));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(125, 250, 100, 40);
        //actiune buton login
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServUser s = new ServUser();
                try {
                    PreparedStatement select = s.getConnection().prepareStatement(ServUser.ISINTABLE);
                    select.setString(1, txtUsername.getText());
                    ResultSet rs = select.executeQuery();
                    if (rs.next()) {
                        System.out.println("Found user");
                        String password = rs.getString("password");
                        if (!txtPassword.getText().equals(password)) {
                            JOptionPane.showMessageDialog(contentPane, "Wrong password!");
                        } else {
                            // Get the user information from the result set
                            String username = rs.getString("username");
                            String email = rs.getString("email");
                            byte[] pic = rs.getBytes("pic");

                            // Create a UserModel object
                            UserModel currentUser = new UserModel(username, password, email, pic);

                            // Open TextingInterface with the found UserModel

                            TextingInterface textingInterface = new TextingInterface(currentUser);

                            textingInterface.setVisible(true);
                            dispose(); // Dispose of the current login frame
                        }
                    } else {
                        throw new SQLException("NOT FOUND!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(contentPane, "User does not exist!");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Login button action triggered");
            }
        });

        panel_1.add(btnLogin);

        // Restul codului rămâne neschimbat...

        // Modificarea efectului de subliniere la trecerea mouse-ului peste textul "Create account"
        createAccountLabel = new JLabel("Create account");
        createAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        createAccountLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        createAccountLabel.setForeground(new Color(0, 0, 0));
        createAccountLabel.setBounds(125, 300, 105, 30);
        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createAccountLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK)); // Adăugarea efectului de subliniere la trecerea mouse-ului peste text
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createAccountLabel.setBorder(null); // Eliminarea efectului de subliniere la ieșirea mouse-ului de deasupra textului
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                createAccountAction();
            }
        });
        panel_1.add(createAccountLabel);
    }

    public static void register(UserModel data) {
        ClientService.getInstance().getClient().emit("register", data.toJsonObject(), new Ack() {
            @Override
            public void call(Object... os) {
//                if (os.length > 0) {
//                    String ms = new String((boolean) os[0], os[1].toString())
//                    //  call message back when done register
//                }
            }
        });
    }


    // Metoda pentru acțiunea etichetei "Create account"
    private void createAccountAction() {
        // Adăugați acțiunea dorită pentru eticheta "Create account" aici
        System.out.println("Create account action triggered");
        this.dispose();
        UserCreate userCreateView = new UserCreate();

        userCreateView.setVisible(true);
    }

    // Metodele getUsername și getPassword rămân neschimbate
    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return txtPassword.getText();
    }
}