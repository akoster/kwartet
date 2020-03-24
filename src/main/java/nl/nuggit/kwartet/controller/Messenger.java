package nl.nuggit.kwartet.controller;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class Messenger {

    private SimpMessagingTemplate template;

    public Messenger(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendToUser(String id, Object payload) {
        this.template.convertAndSendToUser(id, destination(payload), payload);
    }

    public void sendToAll(Object payload) {
        this.template.convertAndSend(destination(payload), payload);
    }

    private String destination(Object payload) {
        return "/topic/" + payloadType(payload);
    }

    private String payloadType(Object payload) {
        return payload.getClass().getSimpleName().toLowerCase();
    }
}
