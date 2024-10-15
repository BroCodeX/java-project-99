package hexlet.code.app.controller.api;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService service;

    @Autowired
    private UserUtils userUtils;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() and @userUtils.isExists(authentication.principal.getClaim('sub'))")
    public ResponseEntity<List<LabelDTO>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var labels = service.getAll(limit);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(labels.size()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(labels);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() and @userUtils.isExists(authentication.principal.getClaim('sub'))")
    public LabelDTO show(@PathVariable Long id) {
        return service.showLabel(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and @userUtils.isExists(authentication.principal.getClaim('sub'))")
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO dto) {
        return service.createLabel(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() and @userUtils.isExists(authentication.principal.getClaim('sub'))")
    public LabelDTO update(@Valid @RequestBody LabelUpdateDTO dto, @PathVariable Long id) {
        return service.updateLabel(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated() and @userUtils.isExists(authentication.principal.getClaim('sub'))")
    public void destroy(@PathVariable long id) {
        service.destroyLabel(id);
    }
}
