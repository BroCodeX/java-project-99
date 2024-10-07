package hexlet.code.app.controller.api;

import hexlet.code.app.dto.AuthDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import hexlet.code.app.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserUtils utils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public List<UserDTO> index() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("isAuthenticated() and @utils.isOwner(#id, authentication.principal.email)")
    @PostAuthorize("returnObject.email == authentication.principal.email")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable long id) {
        return userService.show(id);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("isAuthenticated() and #dto.email == authentication.principal.email")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserUpdateDTO dto, @PathVariable long id) {
        return userService.update(dto, id);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("isAuthenticated() and @utils.isOwner(#id, authentication.principal.email)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable long id) {
        userService.destroy(id);
    }
}
