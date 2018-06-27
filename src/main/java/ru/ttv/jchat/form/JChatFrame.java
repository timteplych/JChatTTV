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

    public void init(){
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
                            setAuthorized(true);
                            break;
                        }
                        addChatMessageInMessageArea(str);
                    }
                    while(true){
                        String str = inputStream.readUTF();
                        if(str.equals("/end")){
                            break;
                        }
                        addChatMessageInMessageArea(str);
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
        setTitle("JChatTTV");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300,50,450,600);

        setLayout(new BorderLayout());

        chatTextArea.setEditable(false);

        add(chatTextArea,BorderLayout.CENTER);

        //Panel which contains text enter elements
        final JPanel enterPanel = new JPanel();
        enterPanel.setLayout(new FlowLayout());
        //Message field
        final JTextField messageField = new JTextField();


        ActionListener messageFieldActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChatMessageInMessageArea(messageField.getText());
                messageField.setText("");
                messageField.requestFocusInWindow();
            }
        };

        ActionListener buttonSendActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChatMessageInMessageArea(messageField.getText());
                messageField.setText("");
                messageField.requestFocusInWindow();
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
        messageField.requestFocusInWindow();
    }

    void addChatMessageInMessageArea(String message){
        if(message.equals("")){
            return;
        }
        chatTextArea.append(message);
        chatTextArea.append("\n");

    }

    private void setAuthorized(boolean authorized){
        this.authorized = authorized;
    }
}
