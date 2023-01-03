import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class Server{
    Chat_room_manager manager;
    ArrayList<String> file_list = new ArrayList<>();
    String server_file_path = "FileServerSave";
    int Portnumber1 = 0; // for chat send write
    int Portnumber2 = 0; // for chat send write file

    ServerSocket server_socket_chat;
    ServerSocket server_socket_file;

    private Socket socket_chat;
    Socket socket_file;

    Server(int Portnumber1, int Portnumber2){
        manager = new Chat_room_manager();
        this.Portnumber1 = Portnumber1;
        this.Portnumber2 = Portnumber2;
    }

    void Start_Server(){
        try{
            server_socket_chat = new ServerSocket(Portnumber1);
            server_socket_file = new ServerSocket(Portnumber2);
            System.out.println("=======Server Started=======");

            //client 접속시 깨어남
            while(true) {
                socket_chat = server_socket_chat.accept();

                System.out.println("client connected");

                Server_rec_thread th1 = new Server_rec_thread(socket_chat,this);
                th1.start();
/*
                socket_file = server_socket_file.accept();
                //socket_file.setSoTimeout(50000);
                System.out.println("file - client connected");
                Server_File_rec_thread th2 = new Server_File_rec_thread(socket_file,this);
                th2.start();*/
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    // java Server 2020 2021
    public static void main(String[] args) throws IOException {
        // java Server 2020 2021
        Server server = null;
        BufferedReader sc1 = new BufferedReader(new InputStreamReader(System.in));
        String get_str1 = sc1.readLine();
        if (get_str1.matches("java Server(.*)")) {
            String[] get_strs1 = get_str1.split(" "); // get_strs[2] = serverIP [3]:portNo1 [4]portNo4

            try {
                server = new Server(Integer.parseInt(get_strs1[2]), Integer.parseInt(get_strs1[3]));
            } catch (Exception e) {
                System.out.println("Wrong format");
                return;
            }
        } else {
            System.out.println("wrong commend inserted\n java Server (Portnumber1) (Portnumber2) ");
        }
        server.Start_Server();
    }

}

