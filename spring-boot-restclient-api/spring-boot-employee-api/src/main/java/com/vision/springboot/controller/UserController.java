package com.vision.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vision.springboot.dto.EmployeeDto;
import com.vision.springboot.entity.User;
import com.vision.springboot.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "RestClienDemo", description = "User management APIs by using RestClient")
@RestController
@RequestMapping("/api/user")
public class UserController {

	// Declare the service as final to ensure its immutability
	private final UserService userService;

	// Use constructor-based dependency injection
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@Operation(summary = "Retrieve all Users", tags = { "users", "get", "filter" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "There are no User", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })

	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@Operation(summary = "Retrieve an User by Id", description = "Get user object by specifying its id. The response is a user object with id, title, description and published status.", tags = {
			"employees", "get" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		return userService.getUserById(id).map(entity -> ResponseEntity.ok(entity))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Create a new User", tags = { "User", "post" })
	@ApiResponses({
			@ApiResponse(responseCode = "201", content = {
					@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		return ResponseEntity.ok(userService.saveUser(user));
	}

	@Operation(summary = "Update a User by Id", tags = { "user", "put" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }) })

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		return ResponseEntity.ok(userService.saveUser(user));
	}

	@Operation(summary = "Delete a user by Id", tags = { "user", "delete" })
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
