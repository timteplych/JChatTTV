package ru.ttv.jchat.form;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JChat extends JFrame {
    public JChat(){
        setTitle("JChatTTV");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300,50,450,600);

        setLayout(new BorderLayout());

        //Chat text area
        final JTextArea chatTextArea = new JTextArea();

        add(chatTextArea,BorderLayout.CENTER);

        //Panel which contains text enter elements
        JPanel enterPanel = new JPanel();
        enterPanel.setLayout(new FlowLayout());
        //Message field
        final JTextField messageField = new JTextField();


        ActionListener messageFieldActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChatMessageInMessageArea(chatTextArea,messageField);
            }
        };

        ActionListener buttonSendActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChatMessageInMessageArea(chatTextArea,messageField);
            }
        };


        messageField.setPreferredSize(new Dimension(300,30));
        messageField.addActionListener(messageFieldActionListener);

        enterPanel.add(messageField);
        //Text sending button
        final JButton sendBtn = new JButton("Send");
        sendBtn.setPreferredSize(new Dimension(100,30));
        sendBtn.addActionListener(buttonSendActionListener);
        enterPanel.add(sendBtn);

        add(enterPanel,BorderLayout.SOUTH);

        setVisible(true);

    }

    void addChatMessageInMessageArea(JTextArea chatTextArea, JTextField messageField){
        if(messageField.getText().equals("")){
            return;
        }
        chatTextArea.append(messageField.getText());
        chatTextArea.append("\n");
        messageField.setText("");
    }
}
