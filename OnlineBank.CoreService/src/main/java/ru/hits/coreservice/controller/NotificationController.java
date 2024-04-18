package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.coreservice.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Уведомления")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Отправить тестовое уведомление для аутентифицированного пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/send")
    public void sendNotification(@RequestParam String title, @RequestParam String body) {
        notificationService.sendNotification(title, body);
    }

}
