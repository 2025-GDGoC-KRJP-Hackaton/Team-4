package kr.ac.korea.gdg.disasterassistantforblind.modules.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import kr.ac.korea.gdg.disasterassistantforblind.modules.auth.security.JwtTokenProvider;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user with basic information only
     * Medical information will be added separately
     */
    @Operation(summary = "Register a new user", description = "Register a new user with basic information only. Medical information will be added separately.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or id already in use",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "User to register", required = true) 
            @RequestBody User user) {
        System.out.println("Registering user:" + user.getId());
        try {
            String id = user.getId();
            String password = user.getPassword();
            // Validate required fields
            if (id == null || password == null) {
                throw new IllegalArgumentException("ID and password are required");
            }

            // Register user with basic information only
            User registeredUser = userService.registerUser(user);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password));

            // Generate JWT token
            String jwt = jwtTokenProvider.createToken(authentication);

            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User logged in successfully");
            response.put("token", jwt);
            response.put("user", registeredUser);

            // Set token in Authorization header
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Authenticate user", description = "Authenticate a user with ID and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully", 
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", 
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "Login credentials (id and password)", required = true)
            @RequestBody Map<String, String> loginRequest) {
        try {
            String id = loginRequest.get("id");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password));

            // Generate JWT token
            String jwt = jwtTokenProvider.createToken(authentication);

            // Get user with medical info for the response
            User user = userService.getUserByIdWithMedicalInfo(id);

            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User logged in successfully");
            response.put("token", jwt);
            response.put("user", user);

            // Set token in Authorization header
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (AuthenticationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid ID or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}