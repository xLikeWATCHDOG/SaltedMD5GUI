package cn.watchdog.appeal;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.nio.charset.*;
import java.security.*;

public class SaltedMD5GUI extends JFrame implements ActionListener {
    private final JTextField textField;
    private final JLabel resultLabel;
    private final JButton copyButton;

    public SaltedMD5GUI() {
        setTitle("Password Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建文本框和标签
        textField = new JTextField();
        resultLabel = new JLabel();

        // 设置文本框和标签的样式
        textField.setPreferredSize(new Dimension(300, 30));
        resultLabel.setPreferredSize(new Dimension(300, 30));

        // 添加组件到界面
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.add(textField);
        centerPanel.add(resultLabel);
        add(centerPanel, BorderLayout.CENTER);

        // 创建按钮和面板
        copyButton = new JButton("Copy Password");
        copyButton.addActionListener(this);
        copyButton.setEnabled(false); // 结果为空时，不允许复制

        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
        buttonPanel.add(copyButton);
        add(buttonPanel, BorderLayout.EAST);

        // 添加文本框的监听器
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateResult();
            }

            public void removeUpdate(DocumentEvent e) {
                updateResult();
            }

            public void insertUpdate(DocumentEvent e) {
                updateResult();
            }

            private void updateResult() {
                // 生成加密后的结果
                String input = textField.getText();
                String salt = "kinomc"; // 这里使用固定的盐值，实际应该使用随机生成的盐值
                String saltedInput = salt + input;
                String hashedOutput = md5(saltedInput);
                resultLabel.setText(hashedOutput);
                assert hashedOutput != null;
                copyButton.setEnabled(!hashedOutput.isEmpty()); // 结果不为空时，允许复制
            }
        });

        // 设置窗口大小和位置
        pack(); // 自适应大小
        setLocationRelativeTo(null); // 居中显示
        setVisible(true);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHexString(digest);
        } catch (Exception e) {
            return null;
        }
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SaltedMD5GUI::new);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Copy Password")) {
            // 复制结果到剪贴板
            StringSelection stringSelection = new StringSelection(resultLabel.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            // 弹出提示框
            JOptionPane.showMessageDialog(this, "Password copied successfully!");
        }
    }
}