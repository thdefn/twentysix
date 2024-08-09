package cm.twentysix.user.service;

import cm.twentysix.user.client.MailgunClient;
import cm.twentysix.user.client.dto.SendMailForm;
import cm.twentysix.user.constant.MailContent;
import cm.twentysix.user.constant.MailSender;
import cm.twentysix.user.controller.dto.SendAuthEmailForm;
import cm.twentysix.user.controller.dto.SendAuthEmailResponse;
import cm.twentysix.user.domain.model.EmailAuth;
import cm.twentysix.user.domain.repository.EmailAuthRedisRepository;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceTest {
    @Mock
    private MailgunClient mailgunClient;
    @Mock
    private EmailAuthRedisRepository emailAuthRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CipherManager cipherManager;
    @InjectMocks
    private EmailAuthService emailAuthService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(emailAuthService, "serverUrl", "http://26cm.com");
    }

    @Test
    @DisplayName("인증 메일 발송_성공")
    void sendAuthEmail_success() {
        //given
        SendAuthEmailForm form = new SendAuthEmailForm("abcde@gmail.com");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypted");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(emailAuthRepository.save(any())).willReturn(EmailAuth.builder()
                .email("abcde@gmail.com")
                .sessionId("sessionId")
                .build());
        //when
        SendAuthEmailResponse response = emailAuthService.sendAuthEmail(form);
        //then
        ArgumentCaptor<EmailAuth> authCaptor = ArgumentCaptor.forClass(EmailAuth.class);
        verify(emailAuthRepository, times(1)).save(authCaptor.capture());
        EmailAuth savedEmailAuth = authCaptor.getValue();
        assertEquals(savedEmailAuth.getEmail(), form.email());
        assertFalse(savedEmailAuth.isVerified());
        assertEquals(32, savedEmailAuth.getCode().length());

        ArgumentCaptor<SendMailForm> sendMailCaptor = ArgumentCaptor.forClass(SendMailForm.class);
        verify(mailgunClient, times(1)).sendEmail(sendMailCaptor.capture());
        SendMailForm sentMail = sendMailCaptor.getValue();
        assertEquals(sentMail.getFrom(), MailSender.AUTH.getEmailFrom());
        assertEquals(sentMail.getTo(), form.email());
        assertEquals(sentMail.getSubject(), MailContent.EMAIL_VERIFY.title);

        assertEquals(response.sessionId(), "sessionId");
    }

    @Test
    @DisplayName("인증 메일 발송 실패_ALREADY_REGISTER_EMAIL")
    void sendAuthEmail_fail_ALREADY_REGISTER_EMAIL() {
        //given
        SendAuthEmailForm form = new SendAuthEmailForm("abcde@gmail.com");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypted");
        given(userRepository.existsByEmail(anyString())).willReturn(true);
        //when
        UserException e = assertThrows(UserException.class, () -> emailAuthService.sendAuthEmail(form));
        //then
        assertEquals(e.getError(), Error.ALREADY_REGISTER_EMAIL);
    }


    @Test
    @DisplayName("메일 인증 성공")
    void verifyEmail_success() {
        //given
        String email = "abcde@gmail.com";
        String code = "veryverylonglongcode";
        EmailAuth emailAuth = EmailAuth.builder()
                .email("abcde@gmail.com")
                .code("veryverylonglongcode")
                .isVerified(false)
                .build();
        given(emailAuthRepository.findById(anyString()))
                .willReturn(Optional.of(emailAuth));
        //when
        emailAuthService.verifyEmail(email, code);
        //then
        verify(emailAuthRepository, times(1)).save(any());
        assertTrue(emailAuth.isVerified());
    }

    @Test
    @DisplayName("메일 인증 실패_NOT_VALID_EMAIL")
    void verifyEmail_fail_NOT_VALID_EMAIL() {
        //given
        String email = "abcde@gmail.com";
        String code = "veryverylonglongcode";
        given(emailAuthRepository.findById(anyString()))
                .willReturn(Optional.empty());
        //when
        EmailAuthException e = assertThrows(EmailAuthException.class, () -> emailAuthService.verifyEmail(email, code));
        //then
        assertEquals(e.getError(), Error.NOT_VALID_EMAIL);
    }

    @Test
    @DisplayName("메일 인증 실패_EMAIL_VERIFY_CODE_UNMATCHED")
    void verifyEmail_fail_EMAIL_VERIFY_CODE_UNMATCHED() {
        //given
        String email = "abcde@gmail.com";
        String code = "veryverylonglongcode";
        EmailAuth emailAuth = EmailAuth.builder()
                .email("abcde@gmail.com")
                .code("veryveryshooooortcode")
                .isVerified(false)
                .build();
        given(emailAuthRepository.findById(anyString()))
                .willReturn(Optional.of(emailAuth));
        //when
        EmailAuthException e = assertThrows(EmailAuthException.class, () -> emailAuthService.verifyEmail(email, code));
        //then
        assertEquals(e.getError(), Error.EMAIL_VERIFY_CODE_UNMATCHED);
    }

    @Test
    @DisplayName("메일 인증 실패_ALREADY_VERIFIED")
    void verifyEmail_fail_ALREADY_VERIFIED() {
        //given
        String email = "abcde@gmail.com";
        String code = "veryverylonglongcode";
        EmailAuth emailAuth = EmailAuth.builder()
                .email("abcde@gmail.com")
                .code("veryverylonglongcode")
                .isVerified(true)
                .build();
        given(emailAuthRepository.findById(anyString()))
                .willReturn(Optional.of(emailAuth));
        //when
        EmailAuthException e = assertThrows(EmailAuthException.class, () -> emailAuthService.verifyEmail(email, code));
        //then
        assertEquals(e.getError(), Error.ALREADY_VERIFIED);
    }


}