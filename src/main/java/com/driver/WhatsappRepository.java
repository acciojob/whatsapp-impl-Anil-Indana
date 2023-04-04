package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> userData;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userData = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public boolean isNewUser(String mobile){
        if(userData.containsKey(mobile)) return false;
        return true;
    }
    public void createUser(String name,String mobile){
        userData.put(mobile,new User(mobile,name));
    }
    public Group createGroup(List<User> users){
        if(users.size() == 2) {
            String groupName = users.get(1).getName();
            Group group = new Group(groupName,2);
            groupUserMap.put(group,users);
            return group;
        }
        this.customGroupCount++;
        String groupName = "Group " + this.customGroupCount;
        Group group = new Group(groupName,users.size());
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }
    public int createMessage(String content){
        this.messageId++;
        Message message = new Message(this.messageId,content,new Date());
        return this.messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws  Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        for(List<User> list : groupUserMap.values()){
            if(!list.contains(sender)) throw new Exception("You are not allowed to send message");
        }
        List<Message> list;
        if(groupMessageMap.containsKey(group)) list = groupMessageMap.get(group);
        else list = new ArrayList<>();

        list.add(message);
        groupMessageMap.put(group,list);
        return list.size();
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
        for(List<User> list : groupUserMap.values()){
            if(!list.contains(user)) throw new Exception("User is not a participant");
        }
        adminMap.put(group,user);
        return "SUCCESS";
    }
}
