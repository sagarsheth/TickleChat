package com.techpro.chat.ticklechat.models;

import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.models.user.GetUserDetailsBody;
import com.techpro.chat.ticklechat.models.user.User;
import com.techpro.chat.ticklechat.models.user.UserDetailsModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sagar on 10/22/2016.
 */

public class DataStorage {

    public static UserDetailsModel UserDetails = null;
    public static GetUserDetailsBody userDetailsBody = null;
    public static Tickles.MessageList tickles = null;
    public static AllMessages.MessageList allMessages = null;
    public static HashMap<User, List<Tickles.MessageList.ChatMessagesTicklesList>> mymessagelist = null;
    public static List<User> myuserlist = null;

}
