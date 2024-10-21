package com.vision.spring.restclient.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.vision.spring.restclient.client.RestClientConfig;
import com.vision.spring.restclient.dto.EmployeeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RestClient-tutorial", description = "RestCall management APIs")
//@CrossOrigin(origins = "http://localhost:9091")
@RestController
@RequestMapping("/restclient")
public class EmployeeRestClientController {
	private static final Logger logger = LoggerFactory.getLogger(EmployeeRestClientController.class);
	
	@Autowired
	@Qualifier("defaultRestClient")
	private RestClient restClient;
	
	@Value("${application.resource.base-url}")
	private String apiBaseUrl;

	@Operation(summary = "Create a new employee", tags = { "post", "employee" })
	@ApiResponses({
			@ApiResponse(responseCode = "201", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@PostMapping(value = "/empployeeByEntity",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
		var uri =apiBaseUrl+ "/api/employees";
		logger.info("createEmployee() base uri: " + uri);
		/*
		public ResponseEntity<Void> createEmployee(@RequestBody EmployeeDto employeeDto) {
		return restClient.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(employeeDto)
				.retrieve()
				.toBodilessEntity();
		*/
		EmployeeDto savedEmployee= restClient.post()
		.uri(uri)
		.contentType(MediaType.APPLICATION_JSON)
	    .accept(MediaType.APPLICATION_JSON)
	    .body(employeeDto)
		.retrieve()
		.body(EmployeeDto.class);
		
		if (savedEmployee != null) {
			return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Retrieve an employee by Id", description = "Get an employee object by specifying its id. The response is EmpployeeDto object with id, title, description and published status.", tags = {
			"employees", "get" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping("/empployeeByEntity/{id}")
	public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable final Long id) {
		var uri = apiBaseUrl+"/api/employees";
		logger.info("findEmployeeById() base uri:"+uri);
	
		EmployeeDto employeeDto = restClient.get()
				.uri(uri+"/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(EmployeeDto.class);
		if (employeeDto != null) {
			return new ResponseEntity<>(employeeDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Retrieve all Employees", tags = { "employees", "get", "filter" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "There are no Employee", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping(value = "/empployeeByEntity", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
		var uri = apiBaseUrl+"/api/employees";
		logger.info("getAllEmployees() base uri:"+uri);
	
		try {
			List<EmployeeDto> employees = restClient.get()
					.uri(uri)
				    .accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.body(new ParameterizedTypeReference<List<EmployeeDto>>() {
					});

			if (employees.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(employees, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Update an Employee by Id", tags = { "employees", "put" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = {
					@Content(schema = @Schema(implementation = EmployeeDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }) })
	@PutMapping(value="/empployeeByEntity/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") long id,
			@RequestBody EmployeeDto employeeDto) {
		
		var uri = apiBaseUrl+"/api/employees";
		logger.info("updateEmployee() base uri:"+uri);
	
		EmployeeDto updatedDto = restClient.put()
				.uri(uri+"/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
				.body(employeeDto)
				.retrieve()
				.body(EmployeeDto.class);
		if (updatedDto != null) {
			return new ResponseEntity<>(updatedDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Delete an Employee by Id", tags = { "employees", "delete" })
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@DeleteMapping("/empployeeByEntity/{id}")
	public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable("id") long id) {
		var uri = apiBaseUrl+"/api/employees";
		logger.info("deleteEmployee() base uri:"+uri);
	
		try {
			restClient.delete()
			.uri(uri+"/{id}", id)
		    .accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.toBodilessEntity();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
