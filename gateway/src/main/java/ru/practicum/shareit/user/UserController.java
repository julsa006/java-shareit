package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Validated
@AllArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return client.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateUserDto user) {
        return client.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable @Positive Long id, @Valid @RequestBody UpdateUserDto user) {
        return client.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long id) {
        return client.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Long id) {
        return client.delete(id);
    }

}
