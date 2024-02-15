

import com.corundumstudio.socketio.SocketIOServer;
import com.mysql.cj.xdevapi.Client;
import io.socket.client.Ack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class UserCreate extends JFrame {

    private JPanel contentPane;
    private UserModel user;
    private JTextField userTxt;
    private JTextField emailTxt;
    private JTextField pwTxt;
    private JTextField rpwTxt;
    private JTextField nameText;
    private JFrame frame;
    private JLabel imageLabel;
    private byte[] imageBytes;
    private SocketIOServer server;

    public SocketIOServer getServer() {
        return server;
    }

    public void setServer(SocketIOServer server) {
        this.server = server;
    }

    int targetWidth = 200;
    int targetHeight = 200;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UserCreate frame = new UserCreate();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public UserCreate() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 501, 642);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel title = new JLabel("REGISTER USER");
        title.setFont(new Font("Tahoma", Font.PLAIN, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(120, 20, 251, 36);
        contentPane.add(title);

        JLabel userName = new JLabel("username:");
        userName.setHorizontalAlignment(SwingConstants.RIGHT);
        userName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        userName.setBounds(108, 123, 111, 19);
        contentPane.add(userName);

        JLabel details = new JLabel("Insert details:");
        details.setFont(new Font("Trebuchet MS", Font.BOLD | Font.ITALIC, 18));
        details.setBounds(60, 66, 165, 13);
        contentPane.add(details);

        JLabel password = new JLabel("password:");
        password.setHorizontalAlignment(SwingConstants.RIGHT);
        password.setFont(new Font("Tahoma", Font.PLAIN, 15));
        password.setBounds(108, 181, 111, 19);
        contentPane.add(password);

        JLabel repPassword = new JLabel("repeat password:");
        repPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        repPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));
        repPassword.setBounds(90, 210, 129, 19);
        contentPane.add(repPassword);

        JLabel email = new JLabel("E-mail:");
        email.setHorizontalAlignment(SwingConstants.RIGHT);
        email.setFont(new Font("Tahoma", Font.PLAIN, 15));
        email.setBounds(90, 152, 129, 19);
        contentPane.add(email);

        userTxt = new JTextField();
        userTxt.setBounds(229, 125, 228, 19);
        contentPane.add(userTxt);
        userTxt.setColumns(10);

        emailTxt = new JTextField();
        emailTxt.setColumns(10);
        emailTxt.setBounds(229, 154, 228, 19);
        contentPane.add(emailTxt);

        pwTxt = new JTextField();
        pwTxt.setColumns(10);
        pwTxt.setBounds(229, 183, 228, 19);
        contentPane.add(pwTxt);

        rpwTxt = new JTextField();
        rpwTxt.setColumns(10);
        rpwTxt.setBounds(229, 212, 228, 19);
        contentPane.add(rpwTxt);

        String imagePath = "images/default.jpg"; // Adjust the path as needed

        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);

                imageBytes = bufferedImageToByteArray(image);
                imageLabel = new JLabel(new ImageIcon(image));
                imageLabel.setBounds(150, 250, 200, 200); // Adjust the position and size
                contentPane.add(imageLabel);


            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading the image.", "Error", JOptionPane.ERROR_MESSAGE);

        }
        JButton chooseImageButton = new JButton("Choose Image");
        chooseImageButton.setBounds(311, 65, 146, 50);
        contentPane.add(chooseImageButton);
        chooseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        BufferedImage originalImage = ImageIO.read(selectedFile);


                        if (originalImage != null) {
                            contentPane.remove(imageLabel);
                            imageBytes = bufferedImageToByteArray(originalImage);
                            System.out.println(imageBytes);
                            Image image = convertBlobToImage(imageBytes);
                            Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);


                            imageLabel = new JLabel(new ImageIcon(scaledImage));
                            imageLabel.setBounds(150, 250, 200, 200); // Adjust the position and size


                            imageLabel.repaint();
                            contentPane.add(imageLabel);
                            contentPane.revalidate();
                            contentPane.repaint();


                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }
            }
        });
        JButton registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
        registerButton.setBounds(100, 500, 300, 30);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServUser servUser=new ServUser();
                UserModel userModel=new UserModel(userTxt.getText(),pwTxt.getText(),emailTxt.getText(),imageBytes);
                try {
                    String returnedString=servUser.register(userModel);
                    //LoginPage.register(userModel);


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
                LoginPage loginPage=new LoginPage();
                loginPage.setVisible(true);
            }
        });

        contentPane.add(registerButton);
        contentPane.revalidate();
        contentPane.repaint();

//        new ServerWindow();
        DBConnection.getConnection();

//	        frame.add(chooseImageButton, BorderLayout.SOUTH);
//	        frame.setVisible(true);

    }



    static byte[] bufferedImageToByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static Image convertBlobToImage(byte[] blobData) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
            BufferedImage image = ImageIO.read(inputStream);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle the exception as needed
        }
    }

    public static boolean validPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[@#$%^&*!]).{8,}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();

    }


}
