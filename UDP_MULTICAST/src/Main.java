import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader sc1 = new BufferedReader(new InputStreamReader(System.in));
        String get_str1 = sc1.readLine();
        if(get_str1.matches("java Peer(.*)")) {
            String[] get_strs1 = get_str1.split(" "); // get_strs[2] = portNo
            Peer t_peer;
            try{
                t_peer = new Peer(Integer.parseInt(get_strs1[2]));
            }
            catch (Exception e){
                System.out.println("Wrong format");
                return;
            }
            while(true){
                Scanner sc = new Scanner(System.in);
                String line = sc.nextLine();
                String[] get_strs2 = line.split(" ");
                try{
                    if(get_strs2[0].charAt(0) == '#') {

                        if (get_strs2[0].equals("#JOIN")){
                            if(get_strs2.length == 3){
                                t_peer.join_chat(get_strs2[1],get_strs2[2]);
                            }
                            else{
                                System.out.println("wrong commend of #JOIN\n EX) #JOIN (chat room) (nickname)");
                            }
                        }
                        else if(get_strs2[0].equals("#EXIT")){
                            t_peer.disconnect();
                        }
                        else if(get_strs2[0].equals("#CLOSE")){
                            System.out.println("close program");
                            System.exit(0);
                        }
                        else{
                            System.out.println("wrong commend inserted\n #JOIN (chat room) (nickname) or #EXIT or #CLOSE");
                        }
                    }
                    else{
                        t_peer.send(line);
                    }
                }
                catch (Exception e){
                    System.out.println("not expected commend or message");
                }

            }
        }
        else{
            System.out.println("wrong commend inserted\n java Peer (Portnumber)");
        }
    }
}