package hexlet.code.app.util;

import hexlet.code.app.model.Status;
import hexlet.code.app.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Getter
public class ModelsGenerator {
    @Autowired
    private Faker faker;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Model<User> userModel;
    private List<Model<User>> userModelList;
    private Model<Status> statusModel;
    private List<Model<Status>> statusModelList;

    @PostConstruct
    public void initData() {
        userModel =  makeFakeUser();
        userModelList = IntStream.range(0, 6)
                .mapToObj(i -> makeFakeUser())
                .collect(Collectors.toList());

        List<String> slugs = List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");
        statusModel = makeFakeStatus();
        statusModelList = slugs.stream()
                .map(this::makeFakeStatus)
                .toList();
    }

    public Model<User> makeFakeUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode("password"))
                .toModel();
    }

    public Model<Status> makeFakeStatus() {
        return Instancio.of(Status.class)
                .ignore(Select.field(Status::getId))
                .supply(Select.field(Status::getName), () -> faker.name().name())
                .toModel();
    }

    public Model<Status> makeFakeStatus(String slug) {
        return Instancio.of(Status.class)
                .ignore(Select.field(Status::getId))
                .supply(Select.field(Status::getName), () -> faker.name().name())
                .supply(Select.field(Status::getSlug), () -> slug)
                .toModel();
    }
}
