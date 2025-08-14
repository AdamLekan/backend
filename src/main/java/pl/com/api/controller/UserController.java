package pl.com.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.api.model.CreateUserRequest;
import pl.com.api.model.User;
import pl.com.api.service.UserService;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private  UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //do odkodowaniua po ogarnieciu logowania
//    @PostMapping
//    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
//        User newUser = userService.createUser(
//                request.getFirstName(),
//                request.getLastName(),
//                request.getNationalId(),
//                request.getUsername(),
//                request.getPassword(),
//                request.getWalletAddress()
//        );
//
//        return ResponseEntity.ok(newUser); // lub DTO bez hasła
//    }

}// Placeholder for controller/UserController.java