
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;


public class Client{
    String name;
    String chat_room = null;
    int only_one_room;
    Chat_room chat_roomThread = null;
    String Server_IP = null; // Server address
    int Portnumber1 = 0; // for chat send write
    int Portnumber2 = 0; // for chat send write file
    Socket socket_chat;
    Socket socket_file = null;

    Client(String Server_IP,int Portnumber1, int Portnumber2){
        only_one_room = 0;
        this.Server_IP = Server_IP;
        this.Portnumber1 = Portnumber1;
        this.Portnumber2 = Portnumber2;
    }

    public void setname(String user) {
        this.name = user;
    }
    public  void setchatname(String chat_room){
        this.chat_room = chat_room;
    }

    public void setSocket_file(Socket socket_file) {
        this.socket_file = socket_file;
    }

    void Connect_Server(){
        try{
            Client client = null;
            BufferedReader sc2 = new BufferedReader(new InputStreamReader(System.in));
            String get_str2 = sc2.readLine();
            if (get_str2.matches("java Client(.*)")) {
                String[] get_strs2 = get_str2.split(" "); // get_strs[2] = serverIP [3]:portNo1 [4]portNo4

                try {
                    client = new Client(get_strs2[2], Integer.parseInt(get_strs2[3]), Integer.parseInt(get_strs2[4]));
                } catch (Exception e) {
                    System.out.println("Wrong format");
                    return;
                }
            } else {
                System.out.println("wrong commend inserted\n java Client (ServerId) (Portnumber1) (Portnumber2) ");
            }
            sc2.close();
            socket_chat = new Socket(Server_IP,Portnumber1);
            System.out.println("서버와 연결되었습니다");
            Thread sender = new Client_send_thread(socket_chat,client);
            Thread receiver = new Client_rec_thread(socket_chat,client);
            sender.start();
            receiver.start();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    void Connect_file_Server(){
        try{
            if(socket_file == null){
                socket_file = new Socket(Server_IP,Portnumber2);
            }
            if(socket_file.isClosed()){
                socket_file = new Socket(Server_IP,Portnumber2);
            }
            System.out.println("파일 서버와 연결되었습니다");


        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    /*
    void Connect_file_Server(String file_path){
        try{
            socket_file = new Socket(Server_IP,Portnumber2);
            System.out.println("파일 서버와 연결되었습니다");
            Thread sender = new File_send_thread(socket_file,file_path,this);
            Thread receiver = new File_rec_thread(socket_file,file_path,this);
            sender.start();
            receiver.start();

        }
        catch(Exception e){
            System.out.println(e);
        }
    }*/


    // java Client 127.0.0.1 2020 2021
    public static void main(String[] args) throws IOException {
        try{

            //System.out.println("기본: ");
            Client client = null;
            //Scanner sc2 = new Scanner(System.in);
            //String get_str2 = sc2.nextLine();
            //System.out.print("sc2: "+get_str2);
            BufferedReader sc2 = new BufferedReader(new InputStreamReader(System.in));
            String get_str2 = sc2.readLine();
            //sc2.readLine();
            if (get_str2.matches("java Client(.*)")) {
                String[] get_strs2 = get_str2.split(" "); // get_strs[2] = serverIP [3]:portNo1 [4]portNo4

                try {
                    client = new Client(get_strs2[2], Integer.parseInt(get_strs2[3]), Integer.parseInt(get_strs2[4]));
                } catch (Exception e) {
                    System.out.println("Wrong format");
                    return;
                }
            } else {
                System.out.println("wrong commend inserted\n java Client (ServerId) (Portnumber1) (Portnumber2) ");
            }
            client.socket_chat = new Socket(client.Server_IP,client.Portnumber1);
            //Client client = new Client("127.0.0.1",2020,2021);
            //client.socket_chat = new Socket(client.Server_IP,client.Portnumber1);
            System.out.println("서버와 연결되었습니다");
            Thread sender = new Client_send_thread(client.socket_chat,client);
            Thread receiver = new Client_rec_thread(client.socket_chat,client);
            sender.start();
            receiver.start();
        }
        catch(Exception e){
            System.out.println(e);
        }
        //client.Connect_Server();
    }
}

