import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class Client_rec_thread extends Thread {
    Socket socket;
    DataInputStream DIS;
    String file_path;
    Client client;

    public Client_rec_thread(Socket socket_chat, Client client) {
        this.socket = socket_chat;
        this.client = client;
        try {
            DIS = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            while (DIS!=null) {
                //Chat_room_manager manager = new Chat_room_manager();

                String str = DIS.readUTF();
                //DIS. = null;
                String[] str_arr = str.split(" ");
                if (str_arr[0].charAt(0) == '#') {
                    if(str_arr[0].equals("#JOIN")){
                        client.only_one_room = 1;
                        client.setchatname(str_arr[1]);
                        client.setname(str_arr[2]);
                    }
                    else if(str_arr[0].equals("#CREATE")){
                        client.setchatname(str_arr[1]);
                        client.setname(str_arr[2]);
                        client.only_one_room = 1;
                    }
                    else if (str_arr[0].equals("#PUT")) {
                        client.socket_file.close();

                        //
                    } else if (str_arr[0].equals("#GET")) {
                        if (str_arr.length == 2) {
                            //Socket file_socket = new Socket(client.Server_IP, client.Portnumber2);//client.socket_file;

                            int flag = DIS.readInt();
                            client.Connect_file_Server();


                            file_path = str_arr[1];


                            File file = new File("FileGetSpace");
                            if(!file.isDirectory()){
                                file.mkdirs();
                            }
                            String download_file_path = "FileGetSpace/"+file_path;
                            File download_file = new File(download_file_path);

                            System.out.println("downloading...");
                            //Socket file_socket = client.socket_file;

                            DataInputStream file_DIS = new DataInputStream(client.socket_file.getInputStream());
                            BufferedInputStream BIS = new BufferedInputStream(file_DIS);
                            //DataOutputStream file_DOS = new DataOutputStream(file_socket.getOutputStream());

                            //long size_length = socket.size;

                            FileOutputStream FOS = new FileOutputStream(download_file);
                            BufferedOutputStream BOS = new BufferedOutputStream(FOS);

                            byte[] buffer = new byte[64*1024];
                            int flag1 =0 ;
                            //System.out.print("progress(size: "+ flag +/*size_length+*/"): ");
                            //System.out.println("progress(size: "+(int)flag*64+"KB): ");
                            int data = 0;
                            System.out.println("----------------------");
                            while((data = BIS.read(buffer))!=-1){
                                BOS.write(buffer,0,data);
                                System.out.print("#");
                                //this.sleep(1000);
                                flag1++;
                                //System.out.println(flag1);
                                if(flag1 == flag) break;

                                //System.out.println(data);
                            }
                            BOS.flush();
                            System.out.println("\ndownload Complete");

                            System.out.println("----------------------");
                            client.socket_file.close();

                        } else {
                            System.out.println("wrong commend of #GET\n EX) #GET (FILE name) ");
                        }
                    }
                    else if(str_arr[0].equals("#EXIT")){
                        client.only_one_room = 0;
                    }
                    else{
                        System.out.println(str);
                    }
                }
                else if(str_arr[0].equals("!#")){
                    //this.sleep(1000); //진척도 표시를 위한 지표
                    System.out.print("#");
                }
                else {
                    System.out.println(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

