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
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    private static final String APPLICATION_CAPTION = "JChatTTV";
    private static final String AUTH_OK_STRING = "/authok";
    private static final String END_STRING = "/end";
    private static final String AUTH_STRING = "/auth";

    //Chat text area
    final JTextArea chatTextArea = new JTextArea();

    public JChatFrame(){
    }

    private void start(){
        try{
            socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);

            //receiving messages thread
            Thread receivingMessageThread = new Thread(()->{
                try{
                    while(true){
                        String str = inputStream.readUTF();
                        if(str.startsWith(AUTH_OK_STRING)){
                            String[] strs = str.split("\\s");
                            setAuthorized(true);
                            setTitle(APPLICATION_CAPTION+" - "+strs[1]);
                            break;
                        }
                        //sendMessage(str);
                        addMessageToChatTextArea(str);
                    }
                    while(true){
                        String str = inputStream.readUTF();
                        if(str.equals(END_STRING)){
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
            receivingMessageThread.setDaemon(true);
            receivingMessageThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void init(){
        setTitle(APPLICATION_CAPTION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300,50,450,600);

        //setting border layout to main frame
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
        if("".equals(message)){
            return;
        }
        try{
            outputStream.writeUTF(message);
        }catch (IOException e){
            System.out.println("Unable to send message");
        }
    }

    private void addMessageToChatTextArea(String message){
        if("".equals(message)){
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
            outputStream.writeUTF(AUTH_STRING+" "+loginField.getText()+" "+passwordField.getText());
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
