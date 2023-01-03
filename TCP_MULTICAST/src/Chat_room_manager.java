import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Chat_room_manager {
    List<Chat_room> room_list;
    int room_number;
    Chat_room_manager(){
        room_list = new ArrayList<>();
        room_number = 0;
    }

    Chat_room find(String chat_room){
        for(Chat_room c:room_list){
            if(c.chat_room.equals(chat_room)){
                return c;
            }
        }
        return null;
    }

    boolean create(String chat_room, String name, Server_rec_thread th){
        if(room_number != 0){
            for(Chat_room c:room_list){
                if(c.chat_room.equals(chat_room)){
                    return false;
                }
            }
            Chat_room room = new Chat_room(chat_room,name,th);
            room_list.add(room);
        }
        else{
            Chat_room room = new Chat_room(chat_room,name,th);
            room_list.add(room);
            room_number +=1;
            System.out.println("created!");
            return true;
        }
        return true;
    }

    int join(String chat_room, String name, Server_rec_thread th){
        if(room_number == 0){
            return -1;
        }
        for(Chat_room c:room_list){
            if(c.chat_room.equals(chat_room)){
                Chat_room room = c;
                return room.join(chat_room,name,th); // name change = 1 not = 0;
            }
        }
        return -1;
    }
    boolean exit(String chat_room, String name, Server_rec_thread th){
        for(Chat_room c:room_list){
            if(c.chat_room.equals(chat_room)){
                Chat_room room = c;
                boolean b = room.exit(chat_room,name,th); // name change = 1 not = 0;
                if(room.member_num == 0){
                    room_list.remove(chat_room);
                }
                return b;
            }
        }
        return false;
    }
    Set<String> status(String chat_room){
        for(Chat_room c:room_list){
            if(c.chat_room.equals(chat_room)){
                Chat_room room = c;
                return room.status();
            }
        }
        return null;
    }
    boolean delete(String chat_room){
        for(Chat_room c:room_list){
            if(c.chat_room.equals(chat_room)){
                room_list.remove(c);
                c = null;
                return true;
            }
        }
        return false;

    }
}
