package com.spyro.messenger.security.controller;

import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.AuthenticationService;
import com.spyro.messenger.security.service.DeviceInfoService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.security.service.SessionService;
import com.spyro.messenger.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest(
        classes = ApplicationRunner.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = {"classpath:application-test.properties"})
//@ContextConfiguration(classes = {MessengerApplication.class})
//@DataJpaTest
//@Sql(value = "/auth-controller-test-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthenticationControllerIT {
    private final MockMvc mockMvc;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private EmailSenderService emailSenderService;
    @MockBean
    private final DeviceInfoService deviceInfoService;
    @MockBean
    private SessionService sessionService;
    @MockBean
    private SessionRepo sessionRepo;
    @InjectMocks
    private final AuthenticationService authenticationService;

    @BeforeEach
    void generateUser() {
//        userRepo.save(User.builder().firstName("sdsd").lastName("sffsff").email("fdsfsf@sffs.com").username("alex").role(Role.USER).password("sfsffs").build());
        System.out.println(userRepo.findAll());
    }

//    void test() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put(null, "HTTP/1.1 200 OK");
//        headers.put("Content-Type", "text/html");
//
//        // create an Enumeration over the header keys
//        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
//
//        // mock HttpServletRequest
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        // mock the returned value of request.getHeaderNames()
//        when(request.getHeaderNames()).thenReturn(headerNames);
//
//        System.out.println("demonstrate output of request.getHeaderNames()");
//        while (headerNames.hasMoreElements()) {
//            System.out.println("header name: " + headerNames.nextElement());
//        }
//
//        // mock the returned value of request.getHeader(String name)
//        doAnswer((Answer<String>) invocation -> {
//            Object[] args = invocation.getArguments();
//            return headers.get((String) args[0]);
//        }).when(request).getHeader("Content-Type");
//
//        System.out.println("demonstrate output of request.getHeader(String name)");
//        String headerName = "Content-Type";
//        System.out.printf("header name: [%s]   value: [%s]%n",
//                headerName, request.getHeader(headerName));
//    }

    @Test
    void refreshTokenTest() throws Exception {
//        var user = User.builder().build();
//        user.setActivated(true);
//        user.setConfirmed(true);
//        when(userRepo.findByUsername("")).thenReturn(Optional.of(user));
//
//        this.mockMvc.perform(
//                        post("/api/v1/auth/refresh")
//                                .header(HttpHeaders.AUTHORIZATION, )
//                                .content()
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenAccessDeniedTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/auth/refresh"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}