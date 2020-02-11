package com.waes.diff.api.service;

import com.waes.diff.api.exception.InvalidDataException;
import com.waes.diff.api.exception.NotCompleteException;
import com.waes.diff.api.exception.NotFoundException;
import com.waes.diff.api.model.Diff;
import com.waes.diff.api.util.DiffComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class DiffServiceTest
{
	private static final String DIFF_ID = "1";
	private static final String DIFF_RIGHT_VALUE = "eyAibmFtZSI6IkpIT04iLCAiYWdlIjozMCwgIkNBUiI6ImNoZXZyb2xFVCJ9";
	private static final String DIFF_LEFT_VALUE = "eyAibmFtZSI6ImpvaG4iLCAiYWdlIjozMCwgImNhciI6ImNoZXZyb2xldCJ9";
	private static final String DIFF_INVALID_LEFT_VALUE = "abc";
	private static final String DIFF_NO_JSON_VALUE = "dGhpcyBpcyBteSB0ZXN0";
	private static final String DIFF_RESULT = "dGhpcyBpcyBteSB0ZXN0";

	private DiffService service;

	@Mock
	private DiffComparator comparator;

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private RMapCache map;

	@BeforeEach
	void setMockOutput() {
		comparator = mock(DiffComparator.class);
		redissonClient = mock(RedissonClient.class);
		when(redissonClient.getMapCache(anyString())).thenReturn((RMapCache<Object, Object>) createCacheMap());

		service = new DiffService(redissonClient, comparator);
	}

	private RMapCache createCacheMap()
	{
		map = mock(RMapCache.class);
		return map;
	}

	@Test
	public void testAddDiffRightValue(){

		when(map.get(anyString())).thenReturn(null);
		try
		{
			service.addDiffRightValue(DIFF_ID, DIFF_RIGHT_VALUE);

		} catch (InvalidDataException e)
		{
			fail();
		} finally
		{
			verify(map, times(1)).get(DIFF_ID);
			verify(map, times(1)).put(anyString(), any(Diff.class));
			verify(comparator, never()).diffValues(anyString(), anyString());
		}
	}

	@Test
	public void testAddDiffRightAndLeftValues(){

		when(map.get(DIFF_ID)).thenReturn(Diff.builder().leftValue(DIFF_LEFT_VALUE).build());
		try
		{
			service.addDiffRightValue(DIFF_ID, DIFF_RIGHT_VALUE);

		} catch (InvalidDataException e)
		{
			fail();
		} finally
		{
			verify(map, times(1)).get(DIFF_ID);
			verify(map, times(1)).put(anyString(), any(Diff.class));
			verify(comparator, times(1)).diffValues(anyString(), anyString());
		}
	}

	@Test
	public void testAddDiffLeftInvalidValue(){

		when(map.get(anyString())).thenReturn(null);
		try
		{
			service.addDiffRightValue(DIFF_ID, DIFF_INVALID_LEFT_VALUE);
			fail();
		} catch (InvalidDataException e)
		{
			assertEquals(e.getMessage(), "This value is not base64 encoded");
		} finally
		{
			verify(map, never()).get(DIFF_ID);
			verify(map, never()).put(anyString(), any(Diff.class));
			verify(comparator, never()).diffValues(anyString(), anyString());
		}
	}

	@Test
	public void testAddDiffLeftNoJsonValue(){

		when(map.get(anyString())).thenReturn(null);
		try
		{
			service.addDiffRightValue(DIFF_ID, DIFF_NO_JSON_VALUE);
			fail();
		} catch (InvalidDataException e)
		{
			assertEquals(e.getMessage(), "The value provided is not a JSON");
		} finally
		{
			verify(map, never()).get(DIFF_ID);
			verify(map, never()).put(anyString(), any(Diff.class));
			verify(comparator, never()).diffValues(anyString(), anyString());
		}
	}

	@Test
	public void testGetDiffNotFound(){

		when(map.get(anyString())).thenReturn(null);
		try
		{
			service.getDiffValue(DIFF_ID);
			fail();
		} catch (NotCompleteException e)
		{
			fail();
		} catch (NotFoundException e)
		{
			assertNotNull(e);
		} finally
		{
			verify(map, times(1)).get(DIFF_ID);
		}
	}

	@Test
	public void testGetDiffNotComplete(){

		when(map.get(DIFF_ID)).thenReturn(Diff.builder().leftValue(DIFF_RIGHT_VALUE).build());
		try
		{
			service.getDiffValue(DIFF_ID);
			fail();
		} catch (NotCompleteException e)
		{
			assertEquals(e.getMessage(), "The id to process has not complete data to diff. Please check you save both values first");
		} catch (NotFoundException e)
		{
			fail();
		} finally
		{
			verify(map, times(1)).get(DIFF_ID);
		}
	}

	@Test
	public void testGetDiffComplete(){

		when(map.get(DIFF_ID)).thenReturn(Diff.builder().leftValue(DIFF_LEFT_VALUE)
			.rightValue(DIFF_RIGHT_VALUE).result(DIFF_RESULT).build());
		try
		{
			String result = service.getDiffValue(DIFF_ID);
			assertNotNull(result);
			assertEquals(result, DIFF_RESULT);
		} catch (NotCompleteException e)
		{
			fail();
		} catch (NotFoundException e)
		{
			fail();
		} finally
		{
			verify(map, times(2)).get(DIFF_ID);
		}
	}
}
