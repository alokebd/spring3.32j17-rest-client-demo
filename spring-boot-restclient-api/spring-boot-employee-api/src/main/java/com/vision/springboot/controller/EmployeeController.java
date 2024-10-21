package com.vision.springboot.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vision.springboot.dto.EmployeeDto;
import com.vision.springboot.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "RestClienDemo", description = "Employee management APIs by using RestClient")
//@CrossOrigin(origins = "http://localhost:9090")

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EmployeeController {
	
	private EmployeeService employeeService;

	@Operation(summary = "Create a new Employee", tags = { "emplyees", "post" })
	@ApiResponses({
			@ApiResponse(responseCode = "201", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@PostMapping("/employees")
	public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employee) {
		EmployeeDto savedEmployee = employeeService.createEmployee(employee);
		return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	}

	@Operation(summary = "Retrieve an Employee by Id", description = "Get an employee object by specifying its id. The response is an Employee object with id, title, description and published status.", tags = {
			"employees", "get" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping("/employees/{id}")
	public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {
		EmployeeDto employee = employeeService.getEmployeeById(employeeId);
		// return new ResponseEntity<>(employee, HttpStatus.OK);
		return ResponseEntity.ok(employee);
	}

	@Operation(summary = "Retrieve all Employees", tags = { "employees", "get", "filter" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "There are no Employee", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping ("/employees")
	public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
		List<EmployeeDto> employees = employeeService.getAllEmployees();
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}

	@Operation(summary = "Update an Employee by Id", tags = { "employees", "put" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }) })
	@PutMapping("/employees/{id}")
	public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") long id,
			@RequestBody EmployeeDto employeeDto) {
		employeeDto.setId(id);
		EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto);
		return new ResponseEntity<EmployeeDto>(updatedEmployee, HttpStatus.OK);
	}

	@Operation(summary = "Delete an Employee by Id", tags = { "employees", "delete" })
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@DeleteMapping("/employees/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable("id") long id) {
		// delete employee from DB
		employeeService.deleteEmployee(id);

		return new ResponseEntity<String>("Employee deleted successfully!.", HttpStatus.OK);
	}
}
