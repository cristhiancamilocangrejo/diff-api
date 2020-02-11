package com.waes.diff.api.comparator;

import com.waes.diff.api.util.DiffComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComparatorTest
{

	private static final String DIFF_RIGHT_VALUE = "eyAibmFtZSI6IkpIT04iLCAiYWdlIjozMCwgIkNBUiI6ImNoZXZyb2xFVCJ9";
	private static final String DIFF_LEFT_VALUE = "eyAibmFtZSI6ImpvaG4iLCAiYWdlIjozMCwgImNhciI6ImNoZXZyb2xldCJ9";
	private static final String EXPECTED_RESULT = "{\"result\":[{\"difference\":\"'john' and 'JHON'\"},{\"difference\":\"'car' and 'CAR'\"},{\"difference\":\"'et' and 'ET'\"}]}";
	private static final String EQUAL_EXPECTED_RESULT = "{\"result\":\"The messages provided are equal\"}";
	private static final String NOT_EQUAL_EXPECTED_RESULT = "{\"result\":\"The messages provided does not have equal size\"}";

	private DiffComparator diffComparator;

	@BeforeEach
	public void init() {
		diffComparator = new DiffComparator();
	}

	@Test
	public void compareValues() {
		String result = diffComparator.diffValues(DIFF_LEFT_VALUE, DIFF_RIGHT_VALUE);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(result, EXPECTED_RESULT);
	}

	@Test
	public void compareEqualValues() {
		String result = diffComparator.diffValues(DIFF_RIGHT_VALUE, DIFF_RIGHT_VALUE);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(result, EQUAL_EXPECTED_RESULT);
	}

	@Test
	public void compareDifferentSizeValues() {
		String result = diffComparator.diffValues(DIFF_RIGHT_VALUE, DIFF_RIGHT_VALUE+"NN");

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(result, NOT_EQUAL_EXPECTED_RESULT);
	}
}