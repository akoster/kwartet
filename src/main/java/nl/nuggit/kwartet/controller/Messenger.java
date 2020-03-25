package nl.nuggit.kwartet.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

public class Messenger {

    private SimpMessagingTemplate template;

    public Messenger(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendTo(Object payload, String... ids) {
        for (String id : ids) {
            this.template.convertAndSendToUser(id, destination(payload), payload);
        }
    }

    public void broadcast(Object payload) {
        this.template.convertAndSend(destination(payload), payload);
    }

    private String destination(Object payload) {
        return "/topic/" + payloadType(payload);
    }

    private String payloadType(Object payload) {
        return payload.getClass().getSimpleName().toLowerCase();
    }
}
