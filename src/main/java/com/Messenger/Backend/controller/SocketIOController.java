//package com.Messenger.Backend.controller;
//
//import com.Messenger.Backend.entity.ChatData;
//import com.Messenger.Backend.model.ReceivedMsg;
//import com.Messenger.Backend.repo.ChatRepository;
//import com.corundumstudio.socketio.AckRequest;
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.listener.ConnectListener;
//import com.corundumstudio.socketio.listener.DataListener;
//import com.corundumstudio.socketio.listener.DisconnectListener;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//@Log4j2
//public class SocketIOController {
//
//    @Autowired
//    private SocketIOServer socketServer;
//
//    @Autowired
//            private ChatRepository chatRepository;
//
//    SocketIOController(SocketIOServer socketServer){
//        this.socketServer=socketServer;
//
//        this.socketServer.addConnectListener(onUserConnectWithSocket);
//        this.socketServer.addDisconnectListener(onUserDisconnectWithSocket);
//
//        /**
//         * Here we create only one event listener
//         * but we can create any number of listener
//         * messageSendToUser is socket end point after socket connection user have to send message payload on messageSendToUser event
//         */
//        this.socketServer.addEventListener("messageSendToUser", ReceivedMsg.class, onSendMessage);
//
//    }
//
//
//    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
//        @Override
//        public void onConnect(SocketIOClient client) {
//            client.joinRoom(client.);
//            log.info("Perform operation on user connect in controller");
//        }
//    };
//
//
//    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
//        @Override
//        public void onDisconnect(SocketIOClient client) {
//            log.info("Perform operation on user disconnect in controller");
//        }
//    };
//
//
//    public DataListener<ReceivedMsg> onSendMessage = new DataListener<ReceivedMsg>() {
//        @Override
//        public void onData(SocketIOClient client, ReceivedMsg message, AckRequest acknowledge) throws Exception {
//
//            /**
//             * Sending message to target user
//             * target user should subscribe the socket event with his/her name.
//             * Send the same payload to user
//             */
//
//
//            ChatData chatData = chatRepository.findByChatId(message.getChatId());
//
//            String receiverId;
//            if (chatData.getUser1Id().equals(message.getSenderId())){
//                receiverId = chatData.getUser2Id();
//            } else {
//                receiverId = chatData.getUser1Id();
//            }
//
//            log.info(message.getSenderId()+" user send message to user "+receiverId+" and message is "+message.getContent());
//            socketServer.getBroadcastOperations().sendEvent(receiverId,client, message);
//
//
//            /**
//             * After sending message to target user we can send acknowledge to sender
//             */
//            acknowledge.sendAckData("Message send to target user successfully");
//        }
//    };
//}
