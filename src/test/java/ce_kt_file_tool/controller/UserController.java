package ce_kt_file_tool.controller;

import ce_kt_file_tool.entity.User;
import ce_kt_file_tool.entity.request.AddUserRequest;
import ce_kt_file_tool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void addUser(@RequestBody AddUserRequest addUserRequest){
        User user = new User();
        user.setGivenname(addUserRequest.getGivenName());
        user.setSurename(addUserRequest.getSurname());
        user.setUsername(addUserRequest.getUsername());
        userRepository.save(user);
    }

    @GetMapping(path="")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @GetMapping(path="/{id}")
    public @ResponseBody
    User getParameterDetails (@PathVariable(value="id") long id) {
        return userRepository.findOne(id);
    }
}
