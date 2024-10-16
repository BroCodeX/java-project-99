package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.dto.user.UserDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import hexlet.code.app.util.ModelsGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import org.springframework.security.test.web.servlet
    .request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ModelsGenerator generator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService service;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private WebApplicationContext wac;

	private JwtRequestPostProcessor token;

	private JwtRequestPostProcessor tokenUser;

	private UserDTO user;

	private List<UserCreateDTO> users;

	@BeforeEach
	void prepare() {

		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
				.apply(springSecurity())
				.build();

		token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

		var dto = Instancio.of(generator.getUserModel()).create();
		user = service.createUser(dto);
		tokenUser = jwt().jwt(builder -> builder.subject(user.getEmail()));

		users = generator.getUserModelList().stream().map(Instancio::create).toList();


	}


	@Test
	void loginTest() throws Exception {
		Map<String, String> logData = new HashMap<>();
		logData.put("username", "noexist@google.com");
		logData.put("password", "noexist");
		token = jwt().jwt(builder -> builder.subject(logData.get("username")));
		var request = post("/api/login")
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(logData));

		mockMvc.perform(request)
				.andExpect(status().isUnauthorized());

		assertThat(userRepository.findByEmail(logData.get("username"))).isEmpty();
	}

	@Test
	void indexTest() throws Exception {
		users.forEach(service::createUser);

		var request = get("/api/users").with(jwt());
		var response = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();
		var body = response.getResponse().getContentAsString();

		List<UserDTO> userDTOS = objectMapper.readValue(body, new TypeReference<>() { });
		List<User> actual = userDTOS.stream().map(userMapper::map).toList();
		List<User> expected = userRepository.findAll();

		assertThatJson(body).isArray();
		assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
	}


	@Test
	void showTest() throws Exception {
		long id  = user.getId();

		var request = get("/api/users/{id}", id).with(tokenUser);
		var response = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();
		var body = response.getResponse().getContentAsString();
		var testUser = userRepository.findById(id);

		assertThat(testUser).isNotEmpty();
		assertThatJson(body).and(
				n -> n.node("email").isEqualTo(user.getEmail()),
				n -> n.node("firstName").isEqualTo(user.getFirstName()),
				n -> n.node("lastName").isEqualTo(user.getLastName())
		);
	}

	@Test
	void showTestFailed() throws Exception {
		long id  = user.getId();

		var request = get("/api/users/{id}", id).with(token);
		mockMvc.perform(request)
				.andExpect(status().isForbidden());
		var testUser = userRepository.findById(id);

		assertThat(testUser).isNotEmpty();
	}

	@Test
	void createTest() throws Exception {
		Map<String, String> refData = new HashMap<>();
		refData.put("email", "yandextestcreate@test.com");
		refData.put("firstName", "yandexfirstName@test.com");
		refData.put("lastName", "yandexlastName@test.com");
		refData.put("password", "yandexPass");

		var request = post("/api/users")
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refData));
		var response = mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andReturn();
		var body = response.getResponse().getContentAsString();
		var testUser = userRepository.findByEmail(refData.get("email")).orElse(null);

		assertThat(testUser).isNotNull();
		assertThatJson(body).and(
				n -> n.node("email").isEqualTo(refData.get("email")),
				n -> n.node("firstName").isEqualTo(refData.get("firstName")),
				n -> n.node("lastName").isEqualTo(refData.get("lastName"))
		);
	}

	@Test
	void createTestFailed() throws Exception {
		Map<String, String> refData = new HashMap<>();
		refData.put("email", "yandextestcreate");
		refData.put("firstName", "yandexfirstName@test.com");
		refData.put("lastName", "yandexlastName@test.com");
		refData.put("password", "yandexPass");

		var request = post("/api/users")
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refData));
		mockMvc.perform(request)
				.andExpect(status().isBadRequest());
		var testUser = userRepository.findByEmail(refData.get("email")).orElse(null);

		assertNull(testUser);
	}

	@Test
	void updateTest() throws Exception {
		long id  = user.getId();

		Map<String, String> refData = new HashMap<>();
		refData.put("email", "yandextestupdate@test.com");
		refData.put("firstName", "nowaFirstName@test.com");
		refData.put("lastName", "nowaLastName@test.com");

		var request = put("/api/users/{id}", id)
				.with(tokenUser)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refData));
		var response = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();
		var body = response.getResponse().getContentAsString();

		assertThatJson(body).and(
				n -> n.node("email").isEqualTo(refData.get("email")),
				n -> n.node("firstName").isEqualTo(refData.get("firstName")),
				n -> n.node("lastName").isEqualTo(refData.get("lastName"))
		);
		assertThat(userRepository.findById(id).get().getEmail()).isEqualTo(refData.get("email"));
	}

	@Test
	void updateTestFailed() throws Exception {
		long id  = user.getId();

		Map<String, String> refData = new HashMap<>();
		refData.put("email", "yandextestupdate@test.com");
		refData.put("firstName", "nowaFirstName@test.com");
		refData.put("lastName", "nowaLastName@test.com");

		var request = put("/api/users/{id}", id)
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refData));
		mockMvc.perform(request)
				.andExpect(status().isForbidden());
		assertThat(userRepository.findById(id).get().getEmail()).isEqualTo(user.getEmail());
	}

	@Test
	void destroyTest() throws Exception {
		long id  = user.getId();

		var request = delete("/api/users/{id}", id).with(tokenUser);
		mockMvc.perform(request)
				.andExpect(status().isNoContent());

		var maybeUser = userRepository.findById(id).orElse(null);
		assertThat(maybeUser).isNull();
	}

	@Test
	void destroyTestFailed() throws Exception {
		long id  = user.getId();

		var request = delete("/api/users/{id}", id).with(token);
		mockMvc.perform(request)
				.andExpect(status().isForbidden());

		var maybeUser = userRepository.findById(id).orElse(null);
		assertThat(maybeUser).isNotNull();
	}

}
