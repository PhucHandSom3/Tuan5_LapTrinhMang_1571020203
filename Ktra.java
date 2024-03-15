import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class ChatApplication extends JFrame {
    private JTextField messageField;
    private JButton sendButton;
    private JTextPane chatArea;
    private JButton sendImageButton;
    private JButton sendFileButton;

    public ChatApplication() {
        setTitle("Chat Application");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
        // Components
        messageField = new JTextField();
        sendButton = new JButton("Gửi tin nhắn");
        chatArea = new JTextPane();
        sendImageButton = new JButton("Gửi ảnh");
        sendFileButton = new JButton("Gửi File");

        // Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        Font chatFont = new Font("Arial", Font.PLAIN, 14);
        chatArea.setFont(chatFont);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        // Set constraints for messageField
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(5, 5, 5, 5);
        inputPanel.add(messageField, constraints);
        inputPanel.add(sendButton);
        inputPanel.add(sendImageButton);
        inputPanel.add(sendFileButton);

        add(inputPanel, BorderLayout.SOUTH);
       // inputPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // Action Listeners
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendImage();
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
    }

    private void sendMessage() {
        String message = messageField.getText();
        appendToChat("You: " + message, Color.BLACK);
        messageField.setText("");
    }

    private void sendImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Resize image to specified dimensions
                ImageIcon originalIcon = new ImageIcon(selectedFile.getPath());
                Image originalImage = originalIcon.getImage();
                Image resizedImage = originalImage.getScaledInstance(240, 240, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);

                // Display resized image in chat
                appendToChat("You sent an image: ", Color.BLACK, resizedIcon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            appendFileToChat(selectedFile);
        }
    }

   // ...

   private void appendFileToChat(File file) {
    StyledDocument doc = chatArea.getStyledDocument();
    Style style = chatArea.addStyle("Style", null);
    StyleConstants.setForeground(style, Color.BLACK);

    try {
        // Append file link to chat
        doc.insertString(doc.getLength(), "You sent a file: ", style);

        SimpleAttributeSet linkAttr = new SimpleAttributeSet();
        StyleConstants.setUnderline(linkAttr, true);
        StyleConstants.setForeground(linkAttr, Color.BLUE);

        doc.insertString(doc.getLength(), file.getName(), linkAttr);
        doc.insertString(doc.getLength(), "\n", style);

        // Handle click on the file link
        chatArea.setCaretPosition(doc.getLength());
        chatArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    downloadFile(file);
                }
            }
        });

        // Ensure the scroll pane is updated
        JScrollPane scrollPane = (JScrollPane) chatArea.getParent().getParent();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    } catch (BadLocationException ex) {
        ex.printStackTrace();
    }
}



// ...

    
class FileObject {
    private String fileName;
    private File file;

    public FileObject(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }
}

private void downloadFile(File file) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Specify a file to save");
    fileChooser.setSelectedFile(file);

    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File destinationFile = fileChooser.getSelectedFile();
        try {
            Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(this, "File downloaded successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error downloading file");
        }
    }
}

// ...



    private void appendToChat(String text, Color color) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendToChat(String text, Color color, ImageIcon icon) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text, style);

            // Add image to chat
            chatArea.setCaretPosition(doc.getLength());
            chatArea.insertIcon(icon);
            chatArea.setCaretPosition(doc.getLength());
            doc.insertString(doc.getLength(), "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatApplication().setVisible(true);
            }
        });
    }
}