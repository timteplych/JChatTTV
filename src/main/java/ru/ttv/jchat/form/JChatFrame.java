package ru.ttv.jchat.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Timofey Teplykh
 *
 */

public class JChatFrame extends JFrame {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private boolean authorized;

    //Chat text area
    final JTextArea chatTextArea = new JTextArea();

    public JChatFrame(){
    }

    private void start(){
        try{
            socket = new Socket("localhost",8189);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(()->{
                try{
                    while(true){
                        String str = inputStream.readUTF();
                        if(str.startsWith("/authok")){
                            String[] strs = str.split("\\s");
                            setAuthorized(true);
                            setTitle("JChatTTV - "+strs[1]);
                            break;
                        }
                        //sendMessage(str);
                        addMessageToChatTextArea(str);
                    }
                    while(true){
                        String str = inputStream.readUTF();
                        if(str.equals("/end")){
                            break;
                        }
                        //sendMessage(str);
                        addMessageToChatTextArea(str);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                    try{
                        socket.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            });
            t.setDaemon(true);
            t.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void init(){
        setTitle("JChatTTV");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300,50,450,600);

        setLayout(new BorderLayout());

        chatTextArea.setEditable(false);

        add(chatTextArea,BorderLayout.CENTER);

        //Panel containg text enter elements
        final JPanel enterPanel = new JPanel();
        enterPanel.setLayout(new BorderLayout());
        final JPanel loginPanel = new JPanel();
        //login and pass fields
        final JTextField loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(150,30));
        final JTextField passwordField = new JTextField();
        passwordField.setPreferredSize(new Dimension(150,30));
        loginPanel.setLayout(new FlowLayout());
        loginPanel.add(loginField);
        loginPanel.add(passwordField);
        //login button
        final JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(100,30));
        ActionListener loginButtonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAuthClick(loginField,passwordField);
            }
        };
        loginBtn.addActionListener(loginButtonActionListener);
        loginPanel.add(loginBtn);
        enterPanel.add(loginPanel,BorderLayout.NORTH);

        final JPanel sendMessagePanel = new JPanel();
        sendMessagePanel.setLayout(new FlowLayout());
        //Message field
        final JTextField messageField = new JTextField();


        ActionListener messageFieldActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                //addMessageToChatTextArea(messageField.getText());
                messageField.setText("");
                messageField.requestFocusInWindow();
            }
        };

        ActionListener buttonSendActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                //addMessageToChatTextArea(messageField.getText());
                messageField.setText("");
                messageField.requestFocusInWindow();
            }
        };


        messageField.setPreferredSize(new Dimension(300,30));
        messageField.addActionListener(messageFieldActionListener);

        sendMessagePanel.add(messageField);
        //Text sending button
        final JButton sendBtn = new JButton("Send");
        sendBtn.setPreferredSize(new Dimension(100,30));
        sendBtn.addActionListener(buttonSendActionListener);
        sendMessagePanel.add(sendBtn);

        enterPanel.add(sendMessagePanel,BorderLayout.SOUTH);

        add(enterPanel,BorderLayout.SOUTH);

        setVisible(true);
        messageField.requestFocusInWindow();
    }

    void sendMessage(String message){
        if(message.equals("")){
            return;
        }
        try{
            outputStream.writeUTF(message);
        }catch (IOException e){
            System.out.println("Unable to send message");
        }
    }

    private void addMessageToChatTextArea(String message){
        if(message.equals("")){
            return;
        }
        chatTextArea.append(message);
        chatTextArea.append("\n");
    }

    private void onAuthClick(JTextField loginField, JTextField passwordField){
        if(socket == null || socket.isClosed()){
            start();
        }
        try {
            outputStream.writeUTF("/auth "+loginField.getText()+" "+passwordField.getText());
            loginField.setText("");
            passwordField.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAuthorized(boolean authorized){
        this.authorized = authorized;
    }
}
