package com.skupina1.notificationservice;


import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.repository.NotificationRepository;
import com.skupina1.notificationservice.service.EmailService;
import com.skupina1.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppTest {

    @Test
    void notificationService_canBeCreated() {
        NotificationService newService = new NotificationService();
        assertNotNull(newService);
    }

}
