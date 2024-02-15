

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static java.lang.Math.abs;

public class TextingInterface extends JFrame {
    private static JEditorPane chatArea;

    private JLabel profilePictureLabel;
    private JButton addFriendBtn;
    private JTextField textInput;
    ProfilePictureViewer receiverPicImage;
    private String selectedFriend = new String();
    private static StringBuilder chatMessages = new StringBuilder("<html>");
    DefaultListModel<String> peopleListModel = new DefaultListModel<>();


    private boolean flag = true;
    private JPanel buttonPanel;
    private final int ui = 500;
    private Timer ut;


    private JLabel loggedInUserLabel;
    JList<String> peopleList = new JList<>(peopleListModel);
    private static JLabel selectedFriendLabel;
    private String blue = "#ADD8E6";
    private String orange = "FFA500";
    byte[] receiverPic;
    public JFrame frame;
    private UserModel currentUser; // Add a field to store the current user
    private UserModel commonChat;
    ServUser servUser = new ServUser();
    private static final String[] EMOJIS = {"😊", "😂", "🤣", "❤️", "😍", "💕", "😒", "👌", "😘", "😁", "👍", "🙌"};
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TextingInterface frame = new TextingInterface();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public TextingInterface(UserModel user) throws SQLException {
        this(); // Call the default constructor
        this.currentUser = user;

        updateFriendList();
        selectedFriend = peopleList.getSelectedValue();

        if(currentUser.getUserName().equals("admin")){
            JButton delete=new JButton("DELETE USER");

            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Prompt the user for the username to delete (you can use a JTextField or any other means)
                    String usernameToDelete = JOptionPane.showInputDialog(frame, "Enter username to delete:");

                    // Check if username is not empty to avoid null pointer exceptions
                    if (usernameToDelete != null && !usernameToDelete.isEmpty()) {
                        // Assuming you have an instance of ServUser named 'servUser'
                        try {
                            servUser.deleteUserByUsername(usernameToDelete);
                        }catch (RuntimeException ex){
                            JOptionPane.showMessageDialog(frame, "User does not exist!");
                        }
                        // Display a confirmation message or handle it as needed
                        JOptionPane.showMessageDialog(frame, "User deleted successfully!");
                        updateFriendList();
                    } else {
                        // Handle the case where the username is empty or null
                        JOptionPane.showMessageDialog(frame, "Invalid username.");
                    }

                }
            });
            buttonPanel.add(delete);

        }

        // Display the selected friend in the chat area






        startut();
        //System.out.println(user);
//        ProfilePictureViewer viewer = new ProfilePictureViewer(UserCreate.convertBlobToImage(user.getPic()), 800, 100, 200,"YOU");
//        viewer.setVisible(true);


        frame.setTitle("Texting App - Logged in as: " + currentUser.getUserName());
        ClientService.getInstance().startServer();

        List<Message> messageList = servUser.getMessages(currentUser.getUserName(), "x");


        // Now you can use 'currentUser' to access user information in your interface
        // For example: currentUser.getUserName(), currentUser.getPic(), etc.
    }

    private void showEmojiDialog() {
        JDialog emojiDialog = new JDialog(frame, "Select Emoji", true);
        emojiDialog.setSize(300, 200);
        emojiDialog.setLocationRelativeTo(frame);

        DefaultListModel<String> emojiListModel = new DefaultListModel<>();
        JList<String> emojiList = new JList<>(emojiListModel);
        emojiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (String emoji : EMOJIS) {
            emojiListModel.addElement(emoji);
        }

        emojiList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // Insert the selected emoji into the text input
                    String selectedEmoji = emojiList.getSelectedValue();
                    textInput.setText(textInput.getText() + selectedEmoji);

                    // Close the emoji dialog
                    emojiDialog.dispose();
                }
            }
        });

        JScrollPane emojiScrollPane = new JScrollPane(emojiList);

        emojiDialog.setLayout(new BorderLayout());
        emojiDialog.add(emojiScrollPane, BorderLayout.CENTER);
        emojiDialog.setVisible(true);
    }

    public TextingInterface() {
        frame = new JFrame("Texting App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);


        selectedFriendLabel = new JLabel("");
        selectedFriendLabel.setFont(new Font("Arial", Font.BOLD, 14));
        peopleList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {
                    // Get the selected friend
                    selectedFriend = peopleList.getSelectedValue();
                    System.out.println(selectedFriend);
                    selectedFriendLabel.setText(selectedFriend);
                    updateGUI();
                    try {
                        if (receiverPicImage != null) {
                            receiverPicImage.dispose();
                        }
                         receiverPic = servUser.getProfilePicture(selectedFriend);

                        receiverPicImage = new ProfilePictureViewer(UserCreate.convertBlobToImage(receiverPic), 800, 350, 200,selectedFriend);
                        receiverPicImage.setVisible(true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }


                }
            }
        });




        chatArea = new JEditorPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(flag);
                // When the mouse is clicked on the chatScrollPane, disable automatic updates
                flag = false;
            }
        });


        textInput = new JTextField();
        textInput.setPreferredSize(new Dimension(700, 30));
        textInput.setBorder(new LineBorder(new Color(30, 60, 100), 2));

        addFriendBtn=new JButton("Add Friends");
        addFriendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new JDialog for adding friends
                JDialog addFriendDialog = new JDialog(frame, "Add Friend", true);

                // Create components for the dialog
                JLabel usernameLabel = new JLabel("Enter Friend's Username:");
                JTextField usernameField = new JTextField();
                JButton confirmButton = new JButton("Add Friend");

                // Set layout for the dialog
                addFriendDialog.setLayout(new GridLayout(3, 1));

                // Add components to the dialog
                addFriendDialog.add(usernameLabel);
                addFriendDialog.add(usernameField);
                addFriendDialog.add(confirmButton);

                // Set action listener for the confirmButton
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Retrieve the entered friend's username
                        String friendUsername = usernameField.getText();

                        if (!friendUsername.isEmpty()) {
                            try {
                                // Get the IDs of the current user and the entered friend
                                int currentUserID = servUser.getIdByUsername(currentUser.getUserName());
                                int friendID = servUser.getIdByUsername(friendUsername);

                                // Add friends using the addFriendsById method
                                servUser.addFriendsById(currentUserID, friendID);

                                // Print a message indicating success (you can handle this part differently)
                                System.out.println("Friendship added between " + currentUser.getUserName() +
                                        " and " + friendUsername);

                                // Close the dialog after adding the friend
                                addFriendDialog.dispose();
                                peopleListModel.clear();
                                try {
                                    // Assuming you have the current user's ID, replace 'currentUserId' with the actual ID
                                    int currentUserId = servUser.getIdByUsername(currentUser.getUserName());

                                    List<Integer> friendUsers = servUser.getFriendsForUser(currentUserId);
                                    for (Integer userK : friendUsers) {
                                        UserModel friend=servUser.getUserById(userK);
                                        peopleListModel.addElement(friend.getUserName());
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }

                            } catch (SQLException ex) {
                                // Handle the exception appropriately (e.g., display an error message)

                                JOptionPane.showMessageDialog(null, "User does not exist!", "Error", JOptionPane.ERROR_MESSAGE);

                                ex.printStackTrace();
                            }
                        } else {
                            // Handle the case where the username field is empty (display a message or take appropriate action)
                            System.out.println("Please enter a friend's username");
                        }
                    }
                });

                // Set properties for the dialog
                addFriendDialog.setSize(300, 150);
                addFriendDialog.setLocationRelativeTo(frame);
                addFriendDialog.setVisible(true);
            }
        });




            JButton sendPictureButton = new JButton("Send Picture");
        JButton sendTextButton = new JButton();
        try {
            BufferedImage arrowImage = ImageIO.read(new File("images/arrow.png")); // Replace with your arrow icon file path
            int arrowSize = 20; // Set the desired size
            BufferedImage scaledArrowImage = new BufferedImage(arrowSize, arrowSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledArrowImage.createGraphics();
            g2d.drawImage(arrowImage, 0, 0, arrowSize, arrowSize, null);
            g2d.dispose();
            ImageIcon arrowIcon = new ImageIcon(scaledArrowImage);
            sendTextButton.setIcon(arrowIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }


        JButton sendEmojiButton = new JButton("Send Emoji");

        buttonPanel = new JPanel();
        buttonPanel.add(sendTextButton);
        buttonPanel.add(sendPictureButton);
        buttonPanel.add(sendEmojiButton);
        buttonPanel.add(addFriendBtn);



        startut();
        sendPictureButton.setBackground(Color.LIGHT_GRAY);
        sendPictureButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendEmojiButton.setBackground(Color.LIGHT_GRAY);
        sendEmojiButton.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(textInput);
        inputPanel.add(buttonPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, peopleList, chatScrollPane);
        splitPane.setDividerLocation(150);
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelPanel.add(selectedFriendLabel);

        frame.add(labelPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);


//        Service.getInstance().startServer();

        sendPictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "You sent a picture";
                /*try {
//                    addMessageToChatArea(message, "", blue);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }*/
                textInput.setText("");
            }
        });

        sendTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textInput.getText();

                try {
                    sendMessage("", message, blue);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                textInput.setText("");



            }
        });


        sendEmojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the emoji dialog when the button is clicked
                showEmojiDialog();
            }
        });

        textInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the message from the text input
//                String message = "You: " + textInput.getText();

                try {
                    // Add the message to the chat area
//                    addMessageToChatArea(message, "", blue);

                    // Send the message and add it to the database
                    sendMessage(currentUser.getUserName(), textInput.getText(), blue);

                    // Clear the text input after sending the message
                    textInput.setText("");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        frame.setVisible(true);

    }

    private void clearChatArea() {
        // Implement logic to clear the chat area
        // Example: chatTextArea.setText("");
        // Replace this with your specific logic to clear the chat area
        chatMessages = new StringBuilder("<html>");
        frame.repaint();
    }


    public void sendMessage(String name, String message, String color) throws SQLException {
        // Get the current hour, minute, and AM/PM
        if (!message.isEmpty()) {
            // Get the current hour, minute, and AM/PM
            LocalDateTime currentTime = LocalDateTime.now();
            int hour = currentTime.getHour();
            int minute = currentTime.getMinute();

            // Determine if it's AM or PM
            String amOrPm = (hour < 12) ? "AM" : "PM";

            // Format the message with the current time including AM/PM
            String formattedMessage = String.format("(%02d:%02d %s): %s", (hour % 12 == 0) ? 12 : hour % 12, minute, amOrPm, message);

            // Check if the message is too long for one line
            int maxCharactersPerLine = calculateMaxCharactersPerLine();
            if (formattedMessage.length() > maxCharactersPerLine) {
                // Add newline characters to break the message into lines
                formattedMessage = insertNewlines(formattedMessage, maxCharactersPerLine);
            }

            // Add the formatted message to the chat area

            addMessageToChatArea(formattedMessage, name, color, currentUser.getPic());
            servUser.addMessage(currentUser.getUserName(), selectedFriend, formattedMessage, blue);

        }
    }

//    private void addMessageToChatArea(String message, String name, String color) throws SQLException {
//        chatMessages.append("<p style='color: ").append(color).append("; font-weight: bold;'>").append(name).append("</p>")
//                .append("<p style='background-color: ").append(color).append("; padding: 5px; margin: 5px;'>").append(message).append("</p>");
//        chatArea.setText(chatMessages.toString());
//
//        /*servUser.addMessage(currentUser.getUserName(), "x", message, color);*/
//    }

    private void startut() {
        ut = new Timer(true);
        ut.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Periodically update the GUI only if shouldUpdateAutomatically is true
                if (flag) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           updateGUI();
                        }
                    });
                }
            }
        }, ui, ui);
    }

    private void stoput() {
        if (ut != null) {
            ut.cancel();
            ut.purge();
        }
    }


    private static class FriendListCellRenderer extends DefaultListCellRenderer {
        private HashMap<String, ImageIcon> profileIcons;

        public FriendListCellRenderer(List<UserModel> allUsers) {
            profileIcons = createProfileIcons(allUsers);
        }

        private HashMap<String, ImageIcon> createProfileIcons(List<UserModel> allUsers) {
            HashMap<String, ImageIcon> icons = new HashMap<>();
            for (UserModel user : allUsers) {
                String username = user.getUserName();
                byte[] pic = user.getPic();
                ImageIcon icon = createProfileIconFromImage(pic);
                icons.put(username, icon);
            }
            return icons;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (renderer instanceof JLabel) {
                JLabel label = (JLabel) renderer;
                String username = (String) value;
                ImageIcon icon = profileIcons.get(username);

                if (icon != null) {
                    label.setIcon(icon);
                    label.setText(username);
                }
            }

            return renderer;
        }

        private ImageIcon createProfileIconFromImage(byte[] picData) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(picData));
                int diameter = 30;
                BufferedImage scaledImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
                g2d.drawImage(image, 0, 0, diameter, diameter, null);
                g2d.dispose();
                return new ImageIcon(scaledImage);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private int calculateMaxCharactersPerLine() {
        // Calculate the maximum number of characters that can fit in one line
        int frameWidth = frame.getWidth(); // Get the width of the frame
        int textInputWidth = chatArea.getWidth(); // Get the width of the text input field
        int averageCharacterWidth = 3; // Assuming an average character width of 8 pixels
        int maxCharactersPerLine = (frameWidth - textInputWidth) / averageCharacterWidth;
        return maxCharactersPerLine;
    }


    private String insertNewlines(String message, int maxCharactersPerLine) {
        // Insert newline characters to break the message into lines
        StringBuilder formattedMessage = new StringBuilder();
        int remainingCharacters = message.length();
        int currentPosition = 0;

        while (remainingCharacters > 0) {
            int charactersInLine = Math.min(remainingCharacters, maxCharactersPerLine);
            formattedMessage.append(message, currentPosition, currentPosition + charactersInLine);
            currentPosition += charactersInLine;
            formattedMessage.append("<br>"); // Use <br> for HTML newline
            remainingCharacters -= charactersInLine;
        }


        return formattedMessage.toString();
    }
    public void updateFriendList(){
        peopleListModel.clear();
        ServUser servUser = new ServUser();
        try {
            // Assuming you have the current user's ID, replace 'currentUserId' with the actual ID
            int currentUserId = servUser.getIdByUsername(currentUser.getUserName());

            List<Integer> friendUsers = servUser.getFriendsForUser(currentUserId);
            for (Integer userK : friendUsers) {
                UserModel friend=servUser.getUserById(userK);
                peopleListModel.addElement(friend.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGUI() {




        try {
            // Retrieve messages between the current user and the selected friend
            List<Message> messages = servUser.getMessages(currentUser.getUserName(), selectedFriend);

            // Clear existing messages in the chat area
            clearChatArea();

            // Display each message in the chat area
            for (Message message : messages) {
                // Assuming Message class has properties like sender, content, and color
                if (message.getSenderIndex() == 1) {
                    //UserCreate.convertBlobToImage(receiverPic)
                    addMessageToChatArea(message.getMessageText(), currentUser.getUserName(), blue, currentUser.getPic());
                } else {
                    addMessageToChatArea(message.getMessageText(), selectedFriend, orange,receiverPic);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void addMessageToChatArea(String message, String name, String color, byte[] senderPic) throws SQLException {
        StringBuilder messageHtml = new StringBuilder("<div style='display: flex; align-items: center;'>");

        // Add sender's profile picture
        if (senderPic != null) {
            try {
                String senderPicBase64 = Base64.getEncoder().encodeToString(senderPic);
                messageHtml.append("<img src='data:image/png;base64,").append(senderPicBase64)
                        .append("' width='100' height='100' style='border-radius: 50%; margin-right: 5px;'>");
            } catch (Exception e) {
                // Log or print the exception details for debugging
                e.printStackTrace();
            }
        }

        // Add the formatted message to the chat area
        messageHtml.append("<div style='flex-direction: column;'>")
                .append("<p style='color: ").append(color).append("; font-weight: bold;'>").append(name).append("</p>")
                .append("<p style='background-color: ").append(color).append("; padding: 5px; margin: 5px;'>").append(message).append("</p>")
                .append("</div>");

        messageHtml.append("</div>");

        // Update the chat area
        chatMessages.append(messageHtml);
        chatArea.setText(chatMessages.toString());
    }


    // Helper method to create an ImageIcon from a byte array
    private ImageIcon createImageIcon(byte[] imageData) {
        if (imageData != null) {
            try {
                return new ImageIcon(ImageIO.read(new ByteArrayInputStream(imageData)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private String convertImageToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }


}