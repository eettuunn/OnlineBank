package ru.hits.coreservice.service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.coreservice.exception.InternalServerException;
import ru.hits.coreservice.security.JwtUserData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title, String body) {
        log.info("Отправка уведомления пользователю с id {}. title {} body {}", getAuthenticatedUserId(), title, body);
        var notification = buildNotification(title, body);
        var messages = buildMessages(notification);
        sendMessages(messages);
    }

    public void sendNotifications(List<String> titles, List<String> bodyList) {
        log.info("Отправка уведомлений");
        var notifications = buildNotifications(titles, bodyList);
        var messages = buildMessages(notifications);
        sendMessages(messages);
    }

    private Notification buildNotification(String title, String body) {
        return Notification
                .builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    private List<Notification> buildNotifications(List<String> titles, List<String> bodyList) {
        List<Notification> notifications = new ArrayList<>(titles.size());
        for (int i = 0; i < titles.size(); i++) {
            Notification notification = Notification
                    .builder()
                    .setTitle(titles.get(i))
                    .setBody(bodyList.get(i))
                    .build();
            notifications.add(notification);
        }
        return notifications;
    }

    private List<Message> buildMessages(Notification notification) {
        List<Message> messages = new ArrayList<>(1);
        String deviceToken = "cZAh3cNZQDyMyitdROWaO_:APA91bHzxpfM9bFCNlun6QmeU_knEP3uUVOUywC_i_qHKRvRtq2sEBhVE01TL1aDl7bIsXCdeCLts34QgYEwr662Aglk-r-QzKWTE8QZmAI4mKBbSBYxFKNYttEmrw7JisVH7QSOrreO";
        messages.add(
                Message
                        .builder()
                        .setNotification(notification)
                        .setToken(deviceToken)
                        .build()
        );
        log.info("Создалось сообщение для отправки уведомления по токену {} пользователю {}",
                deviceToken, getAuthenticatedUserId());
        return messages;
    }

    private List<Message> buildMessages(List<Notification> notifications) {
        List<Message> messages = new ArrayList<>(1);
        String deviceToken = "cZAh3cNZQDyMyitdROWaO_:APA91bHzxpfM9bFCNlun6QmeU_knEP3uUVOUywC_i_qHKRvRtq2sEBhVE01TL1aDl7bIsXCdeCLts34QgYEwr662Aglk-r-QzKWTE8QZmAI4mKBbSBYxFKNYttEmrw7JisVH7QSOrreO";
        for (var notification : notifications) {
            messages.add(
                    Message
                            .builder()
                            .setNotification(notification)
                            .setToken(deviceToken)
                            .build()
            );
            log.info("Создалось сообщение для отправки уведомления по токену {} пользователю {}",
                    deviceToken, getAuthenticatedUserId());
        }
        return messages;
    }

    private void sendMessages(List<Message> messages) {
        for (var message : messages) {
            try {
                sendMessage(message);
            } catch (InternalServerException e) {
                log.error("Не удалось отправить уведомление пользователю", e);
            }
        }
    }

    private void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new InternalServerException("Не удалось отправить уведомление");
        }
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}