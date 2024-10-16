package hexlet.code.app.service;

import hexlet.code.app.dto.label.LabelCreateDTO;
import hexlet.code.app.dto.label.LabelDTO;
import hexlet.code.app.dto.label.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundExcepiton;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelMapper mapper;

    @Autowired
    private LabelRepository repository;

    public List<LabelDTO> getAll(int limit) {
        return repository.findAll().stream()
                .limit(limit)
                .map(mapper::map)
                .toList();
    }

    public LabelDTO showLabel(long id) {
        var maybeLabel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExcepiton("This id: " + id + " is not found"));
        return mapper.map(maybeLabel);
    }

    public LabelDTO createLabel(LabelCreateDTO dto) {
        var label = mapper.map(dto);
        repository.save(label);
        return mapper.map(label);
    }

    public LabelDTO updateLabel(LabelUpdateDTO dto, long id) {
        var maybeLabel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExcepiton("This id: " + id + " is not found"));
        mapper.update(dto, maybeLabel);
        repository.save(maybeLabel);
        return mapper.map(maybeLabel);
    }

    public void destroyLabel(long id) {
        repository.deleteById(id);
    }
}
