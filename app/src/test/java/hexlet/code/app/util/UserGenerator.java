package hexlet.code.app.util;

import hexlet.code.app.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Getter
public class UserGenerator {
    private User userModel;
    private List<User> usersModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    public void initData() {
        userModel =  makeFakeUser();
        usersModel = IntStream.range(0, 10)
                .mapToObj(i -> makeFakeUser())
                .collect(Collectors.toList());
    }

    public User makeFakeUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();
    }
}