package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import hexlet.code.app.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDTO>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var users = userService.getAll(limit);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(users.size()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @userUtils.isOwner(#id, authentication.principal.getClaim('sub'))")
    @PostAuthorize("returnObject.email == authentication.principal.getClaim('sub')")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable long id) {
        return userService.show(id);
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @userUtils.isOwner(#id, authentication.principal.getClaim('sub'))")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserUpdateDTO dto, @PathVariable long id) {
        return userService.update(dto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @userUtils.isOwner(#id, authentication.principal.getClaim('sub'))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable long id) {
        userService.destroy(id);
    }
}
