package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.service.LocalizedMessagesServiceImpl;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizedMessagesServiceImplTest {

    @Mock
    private LocaleConfig localeConfig;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LocalizedMessagesServiceImpl localizedMessagesService;

    @Test
    void shouldReturnLocalizedMessage() {
        Locale locale = Locale.forLanguageTag("ru-RU");
        when(localeConfig.getLocale()).thenReturn(locale);
        when(messageSource.getMessage("ResultService.test.results", new Object[]{}, locale)).thenReturn("Результаты теста:");
        String actualMessage = localizedMessagesService.getMessage("ResultService.test.results");
        assertThat(actualMessage).isEqualTo("Результаты теста:");
    }
}