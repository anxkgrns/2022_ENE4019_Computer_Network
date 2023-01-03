import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client_send_thread extends Thread{
    Socket socket;
    DataOutputStream DOS;
    Client client;
    String file_path;
    public Client_send_thread(Socket socket_chat, Client client) {
        this.socket = socket_chat;
        this.client = client;
        try{
            DOS = new DataOutputStream(this.socket.getOutputStream());
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while(DOS != null){
        //System.out.println("명령어: ");
        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
        //Scanner sc = new Scanner(System.in);
        //sc.nextLine();
        //String line = sc.nextLine();
        String line = null;

        try {
            //sc.readLine();
            line = sc.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("line: "+line);
        String[] get_strs2 = line.split(" ");
            try{
                if(get_strs2[0].charAt(0) == '#') {
                    if (get_strs2[0].equals("#JOIN")){
                        if(get_strs2.length == 3){
                            if(client.only_one_room == 1){
                                System.out.println("already in the chat room : "+client.chat_room);
                            }
                            else {
                                DOS.writeUTF(line);
                            }
                        }
                        else{
                            System.out.println("wrong commend of #JOIN\n EX) #JOIN (chat room) (nickname)");
                        }
                    }
                    else if (get_strs2[0].equals("#CREATE")){
                        if(get_strs2.length == 3){
                            if(client.only_one_room == 1){
                                System.out.println("already in the chat room : "+client.chat_room);
                            }
                            else {
                                DOS.writeUTF(line);
                            }
                        }
                        else{
                            System.out.println("wrong commend of #CREATE\n EX) #CREATE (chat room) (nickname)");
                        }
                    }
                    else if (get_strs2[0].equals("#PUT")){
                        if (get_strs2.length == 2) {
                            if(client.only_one_room == 0){
                                System.out.println("client need to join or create a chat room to unload file");
                            }
                            else {
                                file_path = "FilePutSpace/" + get_strs2[1]; // client sender file
                                File file = new File(file_path);
                                String available_file = "txt,java,png,gif,hwp,mp4";
                                if (file.isFile()) {
                                    String file_extension = file_path.substring(file_path.lastIndexOf(".") + 1);
                                    if (available_file.contains(file_extension)) {
                                        DOS.writeUTF("#PUT " + get_strs2[1]); // DOS.writeUTF(str);
                                        //file send thread
                                        client.Connect_file_Server();
                                        //Socket socket_file = new Socket(client.Server_IP, client.Portnumber2);
                                        //client.setSocket_file((socket_file));
                                        try {
                                            System.out.println("----------------------");
                                            System.out.println("sending to server...");

                                            long size_length = Files.size(Paths.get(file_path));

                                            //DataInputStream file_DIS = new DataInputStream(file_socket.getInputStream());
                                            DataOutputStream file_DOS = new DataOutputStream(client.socket_file.getOutputStream());
                                            BufferedOutputStream BOS = new BufferedOutputStream(file_DOS);

                                            FileInputStream FIS = new FileInputStream(file);
                                            BufferedInputStream BIS = new BufferedInputStream(FIS);


                                            byte[] buffer = new byte[64 * 1024];

                                            int data = 0;
                                            int flag = 0;
                                            while ((data = BIS.read(buffer)) != -1) {
                                                BOS.write(buffer, 0, data);
                                                flag++;
                                            }
                                            DOS.writeInt(flag);
                                            BOS.flush();
                                            //DOS.writeUTF("Send Complete");

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            System.out.println("Send fail...");
                                        }


                                        // 진척도 표시
                                        //server.file_list.add(file);
                                        //send_chat_room_msg("#PUTTING "+ str_arr[1] ); //file.getName()+" file is uploaded"
                                        //DOS.writeUTF(file.getName()+" file is uploaded");
                                    } else {
                                        System.out.println("not allowed file extension");
                                    }
                                } else {
                                    System.out.println("not a file");
                                }
                            }
                        } else {
                            System.out.println("wrong commend of #PUT\n EX) #PUT (FILE name) ");
                        }
                    }
                    else if (get_strs2[0].equals("#GET")){
                        if(get_strs2.length == 2){
                            if(client.only_one_room == 0){
                                System.out.println("client need to join or create a chat room to download file");
                            }
                            else{
                                DOS.writeUTF("#GET "+get_strs2[1]);


                                //client.Connect_file_Server();
                                DOS.flush();
                                //file_path =get_strs2[1];
                                //String server_saving_path = server.server_file_path +"/"+client.chat_room+"/"+file_path;
                                //File file = new File(server_saving_path);

                            }
                        }
                        else{
                            System.out.println("wrong commend of #GET\n EX) #GET (FILE name) ");
                        }
                    }

                    else{
                        DOS.writeUTF(line);
                    }
                    /*


                    else if(get_strs2[0].equals("#EXIT")){
                        System.exit(0);
                        DOS.writeUTF(line);
                    }

                    else if(get_strs2[0].equals("#STATUS")){
                        DOS.writeUTF(line);
                    }

                    else if(get_strs2[0].equals("#CLOSE")){
                        DOS.writeUTF(line);
                        //System.out.println("close program");
                        System.exit(0);
                    }
                    else{
                        System.out.println("wrong commend inserted\n #JOIN / #CREATE / #PUT / #GET / #EXIT / #STATUS / #CLOSE");
                    }

                     */
                }
                else{
                    if(client.only_one_room == 1){
                        DOS.writeUTF("FROM "+ client.name +": "+ line);
                    }
                    else{
                        System.out.println("join in the chat room before messaging");
                    }
                }
                DOS.flush();
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("not expected commend or message");
            }
        }
    }
}
