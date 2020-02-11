package com.waes.diff.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.waes.diff.api.exception.InvalidDataException;
import com.waes.diff.api.exception.NotCompleteException;
import com.waes.diff.api.exception.NotFoundException;
import com.waes.diff.api.service.DiffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/v1/diff")
@Tag(name = "diff", description = "This API accepts two different sets of data and diffs them by providing a result")
public class DiffController
{

	private final DiffService service;

	public DiffController(DiffService service)
	{
		this.service = service;
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Gets the result of sets of data by ID",
		responses = { @ApiResponse(responseCode = "200", content = { @Content() }),
			          @ApiResponse(responseCode = "404", description = "diff process not found"),
					  @ApiResponse(responseCode = "400", description = "The id to process has not complete data to diff. Please check you save both values first")},
		parameters = {@Parameter(name = "id", description = "identifier of diff process ") })
	public ResponseEntity<JsonNode> getDiff(@PathVariable  String id)
	{
		ResponseEntity responseEntity;
		try
		{
			responseEntity = ResponseEntity.ok(service.getDiffValue(id));
		} catch (NotCompleteException e)
		{
			responseEntity = ResponseEntity.badRequest().body(e.getMessage());
		} catch (NotFoundException e)
		{
			responseEntity = ResponseEntity.notFound().build();
		}
		return responseEntity;
	}

	@PostMapping(value = "/{id}/left", consumes = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Sets data for left diff",
		responses = { @ApiResponse(responseCode = "202"),
			          @ApiResponse(responseCode = "400", description = "invalid data") },
		parameters = { @Parameter(name = "id", description = "identifier of diff process") },
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "base64 json encoded value"))
	public ResponseEntity<String> diffLeft(@PathVariable String id, @RequestBody @NotNull @NotEmpty String value)
	{
		ResponseEntity responseEntity = ResponseEntity.accepted().build();
		try
		{
			service.addDiffLeftValue(id, value);
		} catch (InvalidDataException e)
		{
			responseEntity = ResponseEntity.badRequest().body(e.getMessage());
		}
		return responseEntity;
	}

	@PostMapping(value = "/{id}/right", consumes = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Sets data for right diff",
		responses = { @ApiResponse(responseCode = "202"),
			@ApiResponse(responseCode = "400", description = "invalid data") },
		parameters = { @Parameter(name = "id", description = "identifier of diff process") },
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "base64 json encoded value"))
	public ResponseEntity<String> diffRight(@PathVariable String id, @RequestBody @NotNull @NotEmpty String value)
	{
		ResponseEntity responseEntity = ResponseEntity.accepted().build();
		try
		{
			service.addDiffRightValue(id, value);
		} catch (InvalidDataException e)
		{
			responseEntity = ResponseEntity.badRequest().body(e.getMessage());
		}
		return responseEntity;
	}
}
