import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Peer implements Runnable{
    String name;
    String chatgroup = null;
    int Portnumber = 0;
    private MulticastSocket socket = null;
    private InetAddress ip =null;
    Peer(int Portnumber){
        this.Portnumber = Portnumber;

    }
    void join_chat(String chatgroup, String peer_name){
        if(ip != null){
            System.out.println("already in the chat room");
            return;
        }
        this.chatgroup = chatgroup;
        String hash_ip = "225.";
        this.name = peer_name;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(chatgroup.getBytes());
            byte secret[] = md.digest();
            int x,y,z;
            x= Byte.toUnsignedInt(secret[secret.length-3]);
            hash_ip = hash_ip + Integer.toString(x);
            y= Byte.toUnsignedInt(secret[secret.length-2]);
            hash_ip = hash_ip + '.' + Integer.toString(y);
            z= Byte.toUnsignedInt(secret[secret.length-1]);
            hash_ip = hash_ip + '.' + Integer.toString(z);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        System.out.println("============="+ chatgroup + "=============");

        try{
            ip = InetAddress.getByName(hash_ip);
            socket = new MulticastSocket(Portnumber);
            socket.joinGroup(ip);

            Thread th1 = new Thread(this);
            th1.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        send(chatgroup + " 채팅방에 " + peer_name + "님이 들어왔습니다");
    }
    @Override
    public void run(){
        try{
            while(true){
                byte[] chunk = new byte[512];
                DatagramPacket packet = new DatagramPacket(chunk, chunk.length);
                socket.receive(packet);
                String str = new String(packet.getData()).trim();
                System.out.println(str);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            //disconnect();
        }
    }

    void send(String message){
        if(ip == null) {
            System.out.println("you are not in the chat room");
            return;
        }

        String str = message.trim();
        if(str.length() == 0 ){
            System.out.println("message should have a word");
            return;
        }

        byte[] chunk = (name +": "+str).getBytes();

        try {
            DatagramPacket packet = new DatagramPacket(chunk,chunk.length,ip,Portnumber);

            socket.send(packet);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    void disconnect(){
        if(ip == null){
            System.out.println("not in the chat room");
            return;
        }
        try {
            send(chatgroup + " 채팅방에 " + name + "님이 나갔습니다");
            socket.leaveGroup(ip);
            ip = null;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("============="+ chatgroup + "============= >> 나가기");
    }
}
