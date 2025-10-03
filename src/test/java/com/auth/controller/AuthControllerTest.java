package com.auth.controller;

import com.auth.dto.AuthDTO;
import com.auth.dto.LoginDTO;
import com.auth.dto.RegisterDTO;
import com.auth.exception.GlobalExceptionHandler;
import com.auth.exception.InvalidCredentialsException;
import com.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Web layer tests")
class AuthControllerTest {

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@Mock
	private AuthService authService;

	@InjectMocks
	private AuthController authController;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	HttpMessageConverter<?>[] messageConverters = {
		new StringHttpMessageConverter(),
		new MappingJackson2HttpMessageConverter(objectMapper)
	};
	mockMvc = MockMvcBuilders.standaloneSetup(authController)
		.setControllerAdvice(new GlobalExceptionHandler())
		.setMessageConverters(messageConverters)
		.build();
	}

	@Test
	@DisplayName("Should register user successfully")
	void shouldRegisterUserSuccessfully() throws Exception {
	RegisterDTO request = new RegisterDTO("john.doe@example.com", "password123", "John", "Doe");
	LocalDateTime expiresAt = LocalDateTime.of(2025, 1, 1, 12, 0);
		AuthDTO response = new AuthDTO("token-123", "Bearer", "john.doe@example.com", "John", "Doe", expiresAt);
		when(authService.register(any(RegisterDTO.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("token-123"))
			.andExpect(jsonPath("$.type").value("Bearer"))
			.andExpect(jsonPath("$.email").value("john.doe@example.com"))
			.andExpect(jsonPath("$.firstName").value("John"))
			.andExpect(jsonPath("$.lastName").value("Doe"))
			.andExpect(jsonPath("$.expiresAt").value(expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

		verify(authService).register(any(RegisterDTO.class));
	}

	@Test
	@DisplayName("Should return validation error when register payload is invalid")
	void shouldReturnValidationErrorWhenRegisterPayloadIsInvalid() throws Exception {
	RegisterDTO invalidRequest = new RegisterDTO("invalid-email", "123", "", "");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
			.andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.path").value("/api/auth/register"))
			.andExpect(jsonPath("$.timestamp").exists());

		verifyNoInteractions(authService);
	}

	@Test
	@DisplayName("Should authenticate user successfully")
	void shouldAuthenticateUserSuccessfully() throws Exception {
	LoginDTO request = new LoginDTO("john.doe@example.com", "password123");
	LocalDateTime expiresAt = LocalDateTime.of(2025, 1, 1, 12, 0);
		AuthDTO response = new AuthDTO("token-456", "Bearer", "john.doe@example.com", "John", "Doe", expiresAt);
		when(authService.login(any(LoginDTO.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("token-456"))
			.andExpect(jsonPath("$.email").value("john.doe@example.com"))
			.andExpect(jsonPath("$.firstName").value("John"))
			.andExpect(jsonPath("$.lastName").value("Doe"))
			.andExpect(jsonPath("$.expiresAt").value(expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

		verify(authService).login(any(LoginDTO.class));
	}

	@Test
	@DisplayName("Should return unauthorized when credentials are invalid")
	void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
		LoginDTO request = new LoginDTO("john.doe@example.com", "wrong-password");
		when(authService.login(any(LoginDTO.class))).thenThrow(new InvalidCredentialsException("Credenciais inválidas"));

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Credenciais inválidas"))
			.andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.path").value("/api/auth/login"));

		verify(authService).login(any(LoginDTO.class));
	}

	@Test
	@DisplayName("Should return service status message")
	void shouldReturnServiceStatusMessage() throws Exception {
		mockMvc.perform(get("/api/auth/test"))
			.andExpect(status().isOk())
			.andExpect(content().string("Auth service is working!"));
	}
}
