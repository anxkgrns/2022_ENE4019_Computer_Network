import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public class Server_rec_thread extends Thread{
    Socket sc = null;

    Socket file_socket;
    Server server;
    String name;
    String chat_room;
    DataInputStream DIS;
    DataOutputStream DOS;

    String File_Server_IP;
    String file_path;
    public Server_rec_thread(Socket socket_chat, Server server) {
        this.sc = socket_chat;
        this.server = server;
        try{
            DIS = new DataInputStream(socket_chat.getInputStream());
            DOS = new DataOutputStream(socket_chat.getOutputStream());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void send_chat_room_msg(String msg){
        Chat_room c = server.manager.find(chat_room);
        if(c == null){
            return;
        }
        Set set = c.users.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            try{
                Server_rec_thread th = c.users.get(iterator.next());
                th.DOS.writeUTF(msg);
                th.DOS.flush();
                /*
                if(th != this){
                    th.DOS.writeUTF(msg);
                    th.DOS.flush();
                }
                */
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try{
            while(DIS != null) {

                //Chat_room_manager manager = new Chat_room_manager();

                String str = DIS.readUTF();
                //System.out.println("server로 들어온 입력어: "+str);
                String[] str_arr = str.split(" ");
                if (str_arr[0].charAt(0) == '#') {
                    //System.out.println("명령어입니다");

                    if (str_arr[0].equals("#JOIN")) {
                        if (str_arr.length == 3) {
                            this.chat_room = str_arr[1];
                            this.name = str_arr[2];
                            int flag = server.manager.join(chat_room,name,this);
                            if( flag < 0){
                                DOS.writeUTF("no room to join");
                            }
                            else if(flag == 0){
                                DOS.writeUTF("#JOIN "+ chat_room + " " + name);
                                DOS.writeUTF("joined successly");
                                DOS.writeUTF("========"+chat_room+"========");
                                send_chat_room_msg(name+" joined the chat_room");
                            }
                            else {
                                for(int i = 1;i<=flag;i++){
                                    this.name = name + "*";
                                }
                                DOS.writeUTF("name is overlaped so it is changed to "+ name );
                                DOS.writeUTF("#JOIN "+ chat_room + " " + name);
                                DOS.writeUTF("joined successly");
                                DOS.writeUTF("========"+chat_room+"========");
                                send_chat_room_msg(name+" joined the chat_room");
                            }
                        } else {
                            DOS.writeUTF("wrong commend of #JOIN\n EX) #JOIN (chat room) (nickname)");
                        }
                    } else if (str_arr[0].equals("#CREATE")) {
                        if (str_arr.length == 3) {
                            this.chat_room = str_arr[1];
                            this.name = str_arr[2];
                            if(server.manager.create(chat_room,name,this)){

                                DOS.writeUTF("#CREATE "+ chat_room + " " + name);
                                DOS.writeUTF("success creating chat room");
                                DOS.writeUTF("========"+chat_room+"========");
                            }
                            else{
                                DOS.writeUTF("failed creating");
                            }
                        } else {
                            DOS.writeUTF("wrong commend of #CREATE\n EX) #CREATE (chat room) (nickname)");
                        }
                    } else if (str_arr[0].equals("#PUT")) {
                        int flag = DIS.readInt();
                        file_socket = server.server_socket_file.accept();
                        //file_socket.setSoTimeout(50000);
                        System.out.println("file - client connected");
                        file_path =str_arr[1];
                        File file = new File(server.server_file_path+"/"+chat_room);
                        if(!file.isDirectory()){
                            file.mkdirs();
                        }
                        String download_file_path = server.server_file_path +"/"+chat_room+"/"+file_path;
                        File download_file = new File(download_file_path);

                        System.out.println("downloading...");

                        DataInputStream file_DIS = new DataInputStream(file_socket.getInputStream());
                        BufferedInputStream BIS = new BufferedInputStream(file_DIS);
                        //DataOutputStream file_DOS = new DataOutputStream(file_socket.getOutputStream());

                        //long size_length = socket.size;

                        FileOutputStream FOS = new FileOutputStream(download_file);
                        BufferedOutputStream BOS = new BufferedOutputStream(FOS);

                        byte[] buffer = new byte[64*1024];
                        int flag1 =0 ;
                        //System.out.print("progress(size: "+ flag +/*size_length+*/"): ");
                        DOS.writeUTF("progress(size: "+(int)flag*64+"KB): ");
                        int data = 0;
                        while((data = BIS.read(buffer))>0){
                            BOS.write(buffer,0,data);
                            DOS.writeUTF("!#");
                            flag1++;
                            //System.out.println(flag1);
                            if(flag1 == flag) break;

                            //System.out.println(data);
                        }
                        BOS.flush();
                        BOS.close();
                        BIS.close();
                        file_DIS.close();
                        FOS.close();


                        System.out.println("\nserver download success");
                        DOS.writeUTF("\nupload Complete");
                        DOS.writeUTF("----------------------");
                        send_chat_room_msg("FROM "+name+": sent a file - "+file_path);
                        DOS.writeUTF("#PUT");


                        server.file_list.add(download_file_path);
                        file_socket.close();
                        //DOS.writeUTF(str);

                    } else if (str_arr[0].equals("#GET")) {
                        if (str_arr.length == 2) {
                            file_path =str_arr[1];
                            String server_saving_path = server.server_file_path +"/"+chat_room+"/"+file_path;
                            File file = new File(server_saving_path);
                            if(server.file_list.contains(server_saving_path)){
                                if(file.isFile()){
                                    DOS.writeUTF("#GET "+str_arr[1]); // make socket
                                    long size_length = Files.size(Paths.get(server_saving_path));
                                    DOS.writeInt((int)Math.ceil(size_length/64*1024)); // flag 보내기
                                    file_socket = server.server_socket_file.accept();
                                    System.out.println("file - client connected");
                                    try {
                                        System.out.println("sending to client...");


                                        //DataInputStream file_DIS = new DataInputStream(file_socket.getInputStream());
                                        DataOutputStream file_DOS = new DataOutputStream(file_socket.getOutputStream());
                                        BufferedOutputStream BOS = new BufferedOutputStream(file_DOS);

                                        FileInputStream FIS = new FileInputStream(file);
                                        BufferedInputStream BIS = new BufferedInputStream(FIS);


                                        byte[] buffer = new byte[64 * 1024];


                                        int data = 0;
                                        int flag = 0;
                                        System.out.println("size_length:"+size_length);
                                        while ((data = BIS.read(buffer))>0) {
                                            BOS.write(buffer, 0, data);
                                            flag++;
                                            System.out.println(flag);
                                        }
                                        /*
                                        for (int i = 0; i <= (int)size_length/(64*1024); i++) {
                                            data = BIS.read(buffer);
                                            System.out.println(flag + " " + i);
                                            BOS.write(buffer, 0, data);
                                            flag++;
                                        }
                                         */
                                        BOS.flush();
                                        BOS.close();
                                        file_DOS.close();
                                        FIS.close();
                                        BIS.close();
                                        //DOS.writeInt(flag);
                                        System.out.println("Send All");
                                        send_chat_room_msg("FROM "+name+": got file - "+file_path);
                                        //DOS.writeUTF("Send Complete");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        System.out.println("Send fail...");
                                    }
                                    file_socket.close();

                                }
                                else{
                                    DOS.writeUTF("not a file");
                                }
                                //진척도 표시
                                //DOS.writeUTF(file.getName()+" file is downloaded");
                            }
                            else{
                                DOS.writeUTF("the file you want to get is not in putted in the server or in this chat room("+chat_room+")");
                            }
                        } else {
                            DOS.writeUTF("wrong commend of #GET\n EX) #GET (FILE name) ");
                        }
                    } else if (str_arr[0].equals("#EXIT")) {

                        if(! server.manager.exit(chat_room,name,this)){
                            DOS.writeUTF("can't exit");
                        }
                        else{
                            DOS.writeUTF("#EXIT");
                            DOS.writeUTF(name + " has exited the chat room : " + chat_room);
                            send_chat_room_msg(name + " has exited the chat room " + chat_room);
                            DOS.writeUTF("========"+chat_room+"========");
                        }

                    } else if (str_arr[0].equals("#STATUS")) {
                        Set<String> set = server.manager.status(this.chat_room);
                        if(set == null){
                            DOS.writeUTF("error can't find status");
                        }
                        else{
                            DOS.writeUTF("========STATUS========");
                            DOS.writeUTF("채팅방 이름: "+this.chat_room);

                            Iterator iterator = set.iterator();
                            while(iterator.hasNext()){
                                String key = (String)iterator.next();
                                DOS.writeUTF("구성원: " + key);
                            }
                            DOS.writeUTF("========STATUS========");
                        }
                    } else {
                        DOS.writeUTF("wrong commend inserted\n #JOIN / #CREATE / #PUT / #GET / #EXIT / #STATUS ");
                    }
                }
                else{
                    //send to chat group
                    send_chat_room_msg(str);
                    //DOS.writeUTF(str);
                }
                //DOS.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
