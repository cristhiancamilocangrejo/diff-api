package com.waes.diff.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.waes.diff.api.model.Diff;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;
import java.util.Stack;

/**
 * Component in charge of diff values
 */
@Component
public class DiffComparator
{
	private static final String EQUAL_MESSAGE_RESPONSE = "The messages provided are equal";
	private static final String NOT_EQUAL_MESSAGES_RESPONSE = "The messages provided does not have equal size";
	private static final String RESULT = "result";

	/**
	 * Check if values are equal or different size
	 * If not equal, then diff comparison is made
	 *
	 * @param leftValue BASE 64 json string
	 * @param rightValue BASE 64 json string
	 * @return diff result
	 */
	public String diffValues(String leftValue, String rightValue)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode result = objectMapper.createObjectNode();

		Optional<String> checkMessages = checkMessages(leftValue, rightValue);
		if (checkMessages.isPresent()){
			result.put(RESULT, checkMessages.get());
			return result.toString();
		}

		String textLeft = decodeBase64(leftValue);
		String textRight = decodeBase64(rightValue);

		ArrayNode differences = findDifferences(textLeft, textRight);
		result.set(RESULT, differences);
		return result.toString();
	}

	private ArrayNode findDifferences(String textLeft, String textRight) {
		Stack<Diff> stack = new Stack<>();
		ArrayNode differences = new ObjectMapper().createArrayNode();
		stack.push(Diff.builder().leftValue(textLeft).rightValue(textRight).build());

		while (stack.size() > 0) {
			Diff diff = stack.pop();

			textLeft = diff.getLeftValue();
			textRight = diff.getRightValue();
			int commonLength = diffIndex(textLeft, textRight);

			textLeft = textLeft.substring(commonLength);
			textRight = textRight.substring(commonLength);

			if (textLeft.isEmpty()) {
				return differences;
			}

			int endIndex = findEndIndex(textLeft, textRight);
			ObjectNode difference = findDifferenceText(textLeft, textRight, endIndex);
			differences.add(difference);

			textLeft = textLeft.substring(endIndex);
			textRight = textRight.substring(endIndex);

			stack.push(Diff.builder().leftValue(textLeft).rightValue(textRight).build());
		}

		return null;
	}

	private ObjectNode findDifferenceText(String textLeft, String textRight, int index) {
		String leftDifference = textLeft.substring(0, index);
		String rightDifference = textRight.substring(0, index);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode differenceNode = mapper.createObjectNode();
		differenceNode.put("difference", "'" + leftDifference + "' and '" + rightDifference + "'");
		return differenceNode;
	}

	private int findEndIndex(String textLeft, String textRight) {
		int index = 0;
		for (int i = 0; i < textLeft.length(); i++)
		{
			if (textLeft.charAt(i) == textRight.charAt(i)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private int diffIndex(String textLeft, String textRight) {
		for(int i = 0; i < textLeft.length(); ++i) {
			if (textLeft.charAt(i) != textRight.charAt(i)) {
				return i;
			}
		}
		return textLeft.length();
	}

	private Optional<String> checkMessages(String leftValue, String rightValue){
		if (leftValue.equals(rightValue)) {
			return Optional.of(EQUAL_MESSAGE_RESPONSE);
		}

		if (leftValue.length() > rightValue.length() || leftValue.length() < rightValue.length()) {
			return Optional.of(NOT_EQUAL_MESSAGES_RESPONSE);
		}

		return Optional.empty();
	}

	private String decodeBase64(String encodedData)
	{
		byte[] decodedBytes = Base64.getDecoder().decode(encodedData.getBytes());
		return new String(decodedBytes);
	}
}
