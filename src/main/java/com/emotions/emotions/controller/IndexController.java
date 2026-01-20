package com.emotions.emotions.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

import com.emotions.emotions.entities.CreateUserDto;
import com.emotions.emotions.entities.Email;
import com.emotions.emotions.entities.EmailCount;
import com.emotions.emotions.entities.EmailDetails;
import com.emotions.emotions.entities.Paginator;
import com.emotions.emotions.entities.RegisterDto;
import com.emotions.emotions.entities.Token;
import com.emotions.emotions.entities.User;
import com.emotions.emotions.helpers.PaginatorController;
import com.emotions.emotions.repositories.EmailRepository;
import com.emotions.emotions.repositories.TokenRepository;
import com.emotions.emotions.repositories.UserRepository;
import com.emotions.emotions.services.EmailCountService;
import com.emotions.emotions.services.SmtpService;
import com.emotions.emotions.services.TokenService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {

    @Value("${app.base-url}")
    private String baseUrl;


    @Autowired
    private EmailCountService emailCountService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaginatorController paginatorController;

    @Autowired
    private SmtpService smtpService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;


    @GetMapping("/hello")
    public String getMethodName(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model) {
        model.addAttribute("name", name);
        return "hello";
    }

    @GetMapping("/")
    public String getMethodName(Model model) {
        EmailCount emailCount = emailCountService.getLastCount();

        model.addAttribute("joy", emailCount.getJoy());
        model.addAttribute("sadness", emailCount.getSadness());
        model.addAttribute("anger", emailCount.getAnger());
        model.addAttribute("fear", emailCount.getFear());
        model.addAttribute("love", emailCount.getLove());
        model.addAttribute("surprise", emailCount.getSurprise());

        System.out.println(emailCount.getAnger());

        return "index";
    }

    @GetMapping("/email/{id}")
    public String getEmail(@PathVariable("id") Long id, Model model) {
        Email email = emailRepository.getReferenceById(id);
        model.addAttribute("email", email);

        return "email";
    }

    @GetMapping("/emails")
    public String getEmails(@PageableDefault(size = 5, page = 0) Pageable pageable, Model model) {
        Page<Email> page = emailRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        Paginator paginator = paginatorController.generatePaginator(page);

        model.addAttribute("pageNumbers", paginator.getPageNumbers());
        model.addAttribute("page", page);
        model.addAttribute("prevDots", paginator.isPrevDots());
        model.addAttribute("nextDots", paginator.isNextDots());
        model.addAttribute("emails", page.getContent());

        return "emails";
    }

    @GetMapping("/users")
    public String getUsers(@PageableDefault(size = 5, page = 0) Pageable pageable, Model model) {
        Page<User> page = userRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        Paginator paginator = paginatorController.generatePaginator(page);

        model.addAttribute("pageNumbers", paginator.getPageNumbers());
        model.addAttribute("page", page);
        model.addAttribute("prevDots", paginator.isPrevDots());
        model.addAttribute("nextDots", paginator.isNextDots());
        model.addAttribute("users", page.getContent());

        System.out.println(page.getContent());
        return "users";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/create-user")
    public String createUser(Model model) {
        CreateUserDto createUser = new CreateUserDto();
        model.addAttribute("create_user", createUser);
        return "create-user";
    }

    @PostMapping("/create-user")
    public String postCreateUser(@ModelAttribute("create_user") @Valid CreateUserDto createUser,
            BindingResult bindingResult, Model model) {
        try {
            
            if (bindingResult.hasErrors()) {
                System.out.println("ERRORRRR!!!");
                return "create-user";
            }
    
            EmailDetails emailDetails = new EmailDetails();
    
            emailDetails.setRecipient(createUser.getEmail());
            emailDetails.setMsgBody("This is a test");
            emailDetails.setSubject("This is a subject test");
    
            String token = tokenService.createToken(createUser.getEmail(), Duration.ofMinutes(30));
            if(token.equals("error")) {
                model.addAttribute("error", "Usuario ya registrado");
                return "create-user";
            } 
    
            
            Context context = new Context();
            context.setVariable("name", "test user");
            context.setVariable("token", token);
            context.setVariable("url", baseUrl);
            String emailStatus = smtpService.sendHtmlEmail(emailDetails, context, "notify-user");
            
            if(emailStatus.equals("error")) {
                model.addAttribute("error", "Usuario ya registrado");
                return "create-user";
            }

            System.out.println("token: ");
            System.out.println(token);
    
            System.out.println("EMAILLL!!!!!!!!");

            model.addAttribute("success", "An email was sent to create the user");
            return "create-user";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "create-user";
        }
    }

    @GetMapping("/register/{token}")
    public String register(@PathVariable String token, Model model) {
        RegisterDto register = new RegisterDto();
        register.setToken(token);
        model.addAttribute("register", register);
        model.addAttribute("token", token);
        return "register";
    }

    @PostMapping("/register/{token}")
    public String postRegister(@PathVariable String token, @ModelAttribute("register") @Valid RegisterDto register, BindingResult bindingResult,
            Model model) throws Exception {
        if (bindingResult.hasErrors())
            return "register";

        String error = "";

        System.out.println(register);

        Optional<Token> tokenQuery = tokenRepository.findById(register.getToken());

        if (!tokenQuery.isPresent())
            error = "Token not valid";

        Token tokenRegister = tokenQuery.get();

        System.out.println("TOKEN REGISTER");
        System.out.println(tokenRegister);

        if (!tokenRegister.getEmail().equals(register.getEmail()) ||
                tokenRegister.isUsed() || tokenRegister.isUsed()) {
            error = "Token not valid";
        }

        if (LocalDateTime.now().isAfter(tokenRegister.getExpiresAt()))
            error = "Token expired";
        
        if(!error.isEmpty()) {
            model.addAttribute("error", error);
            model.addAttribute("token", token);
            return "register";
        } else {
            tokenRegister.setUsed(true);
            tokenRepository.save(tokenRegister);
            model.addAttribute("success", "Usuer created");
            // User user = new User();
            // user.setEmail(register.getEmail());
            // user.setPassword(passwordEncoder.encode(register.getPassword()));
    
            // userRepository.save(user);
            return "register";
        } 
    }
}
