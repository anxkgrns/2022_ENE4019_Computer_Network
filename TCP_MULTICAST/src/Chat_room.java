import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Chat_room { //Thread that controls all of the client in the chat room
    HashMap<String,Server_rec_thread> users = new HashMap<>(); //chat_name
    int member_num = 0;
    String chat_room; // name of chat room


    public Chat_room(String chat_room, String name, Server_rec_thread th) { //make chat room
        //users = new HashMap<String,Server_rec_thread>();
        this.chat_room = chat_room;
        this.member_num = 1;
        users.put(name,th);

    }

    int join(String chat_room, String name, Server_rec_thread th) { // flag is to tall if the user name changed or not
        int flag = 0;
        Set set = users.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            if(key.equals(name)){
                name += "*";
                flag += 1;
            }
        }
        users.put(name,th);
        member_num +=1;
        return flag;
    }
    boolean exit(String chat_room, String name, Server_rec_thread th){
        Set set = users.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            if(key.equals(name)){
                users.remove(name);
                member_num -=1;
                return true;
            }
        }
        return false;
    }



    Set<String> status() {
        return users.keySet();
    }
}
