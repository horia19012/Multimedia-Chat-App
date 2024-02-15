

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServUser {
    private String INSERT = "insert into users (username,email,password,pic) values (?,?,?,?)";
    protected static String ISINTABLE = "select username,password,email,pic from users where username=?";
    private static String SELECT_ALL_USERS = "select username, pic from users";
    private static final String GET_PIC_QUERY = "SELECT pic FROM users WHERE username=?";

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private String SELECTALL = "select * from users";

    public ServUser() {
        this.connection = DBConnection.getConnection();

    }

    public String register(UserModel data) throws SQLException {


        String result = new String();
        boolean continuee = true;
        try {
            PreparedStatement check = connection.prepareStatement(ISINTABLE);
            check.setString(1, data.getUserName());
            System.out.println(data.getUserName());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                result = "User Already Exists!";
                continuee = false;

            }
            rs.close();
            check.close();


        } catch (SQLException e) {
            System.out.println("Error in SQL statement");
            e.printStackTrace();
        }
        if (continuee) {
            try {
                System.out.println("CEVA");
                PreparedStatement insert = connection.prepareStatement(INSERT);
                insert.setString(1, data.getUserName());
                insert.setString(2, data.getEmail());
                insert.setString(3, data.getPassword());
                insert.setBytes(4, data.getPic());
                result = "" + data.getUserName() + " " + data.getPassword() + "has registered!";

                insert.executeUpdate();
                insert.close();


            } catch (SQLException e) {
                System.out.println("ERROR in sql statement");
                JOptionPane.showInputDialog(new JFrame(), "File too big!");
                e.printStackTrace();
            }
        }
        this.printTable();
        return result;
    }
    private static final String deleteUserByUsernameQuery = "DELETE FROM users WHERE username = ?";
    private static String INSERT_FRIENDS = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";
    private static String INSERT_MESSAGE = "INSERT INTO messages (sender_username, receiver_username, message_text) VALUES (?, ?, ?)";
    private static String SELECT_MESSAGES = "SELECT sender_username, receiver_username, message_text, timestamp FROM messages WHERE (sender_username = ? AND receiver_username = ?) OR (sender_username = ? AND receiver_username = ?) ORDER BY timestamp";
    private static String GET_ID_BY_USERNAME = "SELECT id FROM users WHERE username = ?";
    String getFriendIdsQuery = "SELECT user1_id AS friend_id FROM friends WHERE user2_id = ? " +
            "UNION " +
            "SELECT user2_id AS friend_id FROM friends WHERE user1_id = ?";

    String getFriendById = "SELECT id, username, email, password, pic FROM users WHERE id = ?";


    public void sendMessage(String senderUsername, String receiverUsername, String messageText) throws SQLException {
        try (PreparedStatement insertMessage = connection.prepareStatement(INSERT_MESSAGE)) {
            insertMessage.setString(1, senderUsername);
            insertMessage.setString(2, receiverUsername);
            insertMessage.setString(3, messageText);
            insertMessage.executeUpdate();
        }
    }
    public void deleteUserByUsername(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUserByUsernameQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public byte[] getProfilePicture(String username) throws SQLException {
        try (PreparedStatement getPicStatement = connection.prepareStatement(GET_PIC_QUERY)) {
            getPicStatement.setString(1, username);
            ResultSet resultSet = getPicStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println(resultSet);
                return resultSet.getBytes("pic");
            }
        }

        // Return null if no picture is found for the specified username
        return null;
    }

    public int getIdByUsername(String username) throws SQLException {
        int userId = -1; // Initialize with a default value indicating failure

        try (PreparedStatement getIdStatement = connection.prepareStatement(GET_ID_BY_USERNAME)) {
            getIdStatement.setString(1, username);
            ResultSet resultSet = getIdStatement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        }

        return userId;
    }

    public UserModel getUserById(int userId) {
        UserModel user = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(getFriendById)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    byte[] pic = resultSet.getBytes("pic");

                    user = new UserModel(username, email, password, pic);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return user;
    }

    public void addFriendsById(int user1Id, int user2Id) throws SQLException {
        try (PreparedStatement insertFriends = connection.prepareStatement(INSERT_FRIENDS)) {
            // Ensure that the smaller user ID comes first to maintain consistency
            if (user1Id < user2Id) {
                insertFriends.setInt(1, user1Id);
                insertFriends.setInt(2, user2Id);
            } else {
                insertFriends.setInt(1, user2Id);
                insertFriends.setInt(2, user1Id);
            }

            insertFriends.executeUpdate();
        }
    }

    public List<Message> getMessages(String user1, String user2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement selectMessages = connection.prepareStatement(SELECT_MESSAGES)) {
            selectMessages.setString(1, user1);
            selectMessages.setString(2, user2);
            selectMessages.setString(3, user2);
            selectMessages.setString(4, user1);

            ResultSet resultSet = selectMessages.executeQuery();

            while (resultSet.next()) {
                String senderUsername = resultSet.getString("sender_username");
                String receiverUsername = resultSet.getString("receiver_username");
                String messageText = resultSet.getString("message_text");
                String timestamp = resultSet.getString("timestamp");

                // Determine the senderIndex based on the senderUsername
                int senderIndex = (senderUsername.equals(user1)) ? 1 : 2;

                Message message = new Message(senderUsername, receiverUsername, messageText, timestamp, senderIndex);
                messages.add(message);
            }
        }
        return messages;
    }

    public List<Integer> getFriendsForUser(int userId) throws SQLException {
        List<Integer> friendsIds = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getFriendIdsQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int friendId = resultSet.getInt("friend_id");
                    friendsIds.add(friendId);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return friendsIds;


    }


    public UserModel getUserByUsername(String username) throws SQLException {

        PreparedStatement selectByUsername = connection.prepareStatement(ISINTABLE);
        selectByUsername.setString(1, username);

        ResultSet rs = selectByUsername.executeQuery();

        UserModel user = null;

        if (rs.next()) {
            // User found
            String retrievedUsername = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            byte[] pic = rs.getBytes("pic");

            user = new UserModel(retrievedUsername, password, email, pic);
        }

        rs.close();
        selectByUsername.close();

        return user;
    }

    public byte[] printTable() throws SQLException {
        PreparedStatement select = connection.prepareStatement(SELECTALL);
        ResultSet rs = select.executeQuery();
        byte pic1[] = null;
        while (rs.next()) {
            String userName = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            byte[] pic = rs.getBytes("pic");

            System.out.println(userName + " " + password + " " + email + " " + pic);
            pic1 = pic;
        }
        return pic1;
    }


    public List<UserModel> getAllUsers() throws SQLException {
        List<UserModel> users = new ArrayList<>();
        try (PreparedStatement selectUsers = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet resultSet = selectUsers.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                byte[] pic = resultSet.getBytes("pic");

                UserModel user = new UserModel(username, null, null, pic);
                users.add(user);
            }
        }
        return users;
    }

    public void addMessage(String senderUsername, String receiverUsername, String message, String color) throws SQLException {
        try {
            // Insert the message into the database
            String INSERT_MESSAGE = "INSERT INTO messages (sender_username, receiver_username, message_text, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement insertMessage = connection.prepareStatement(INSERT_MESSAGE);
            insertMessage.setString(1, senderUsername);
            insertMessage.setString(2, receiverUsername);
            insertMessage.setString(3, message);
            insertMessage.setString(4, "");

            insertMessage.executeUpdate();
            insertMessage.close();
        } catch (SQLException e) {
            System.out.println("Error in SQL statement");
            e.printStackTrace();
        }
    }
}

