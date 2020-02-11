package com.waes.diff.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waes.diff.api.exception.InvalidDataException;
import com.waes.diff.api.exception.NotCompleteException;
import com.waes.diff.api.exception.NotFoundException;
import com.waes.diff.api.model.Diff;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates data
 */
public class Validator
{
	private final static String BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";

	public static void validateDiff(Diff diff) throws NotFoundException, NotCompleteException
	{
		if (diff == null)
		{
			throw new NotFoundException();
		}

		if (diff.getRightValue() == null || diff.getLeftValue() == null)
		{
			throw new NotCompleteException("The id to process has not complete data to diff. Please check you save both values first");
		}
	}

	public static void validateDiffValue(String value) throws InvalidDataException
	{
		Pattern pattern = Pattern.compile(BASE64_REGEX);
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			throw new InvalidDataException("This value is not base64 encoded");
		}

		String decodedValue = decodeBase64(value);
		if (!isJSONValid(decodedValue)) {
			throw new InvalidDataException("The value provided is not a JSON");
		}
	}

	private static String decodeBase64(String encodedData)
	{
		byte[] decodedBytes = Base64.getDecoder().decode(encodedData.getBytes());
		return new String(decodedBytes);
	}

	private static boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
