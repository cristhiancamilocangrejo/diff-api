package com.waes.diff.api.service;

import com.waes.diff.api.exception.InvalidDataException;
import com.waes.diff.api.exception.NotCompleteException;
import com.waes.diff.api.exception.NotFoundException;
import com.waes.diff.api.model.Diff;
import com.waes.diff.api.util.DiffComparator;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import static com.waes.diff.api.validator.Validator.validateDiff;
import static com.waes.diff.api.validator.Validator.validateDiffValue;

@Component public class DiffService
{

	private final static String DIFF_CONFIG = "diffConfig";

	private final RMap<String, Diff> diffMap;
	private final DiffComparator diffComparator;

	public DiffService(RedissonClient redissonClient, DiffComparator diffComparator)
	{
		diffMap = redissonClient.getMapCache(DIFF_CONFIG);
		this.diffComparator = diffComparator;
	}

	/**
	 * Adds left diff values to map based on id
	 * Also check if data is valid
	 *
	 * @param id key
	 * @param value BASE 64 json string
	 * @throws InvalidDataException
	 */
	public void addDiffRightValue(String id, String value) throws InvalidDataException
	{
		validateDiffValue(value);
		Diff diff = processDiff(id, value, false);
		diffMap.put(id, compare(diff));
	}

	/**
	 * Adds right diff values to map based on id
	 * Also check if data is valid
	 *
	 * @param id key
	 * @param value BASE 64 json string
	 * @throws InvalidDataException
	 */
	public void addDiffLeftValue(String id, String value) throws InvalidDataException
	{
		validateDiffValue(value);
		Diff diff = processDiff(id, value, true);
		diffMap.put(id, compare(diff));
	}

	/**
	 * Creates a new Diff object if it does not exist and adds value
	 * In case it exists, it is pulled from map and adds value
	 *
	 * @param id key
	 * @param value BASE 64 json string
	 * @param left flag
	 * @return object with data configured
	 */
	private Diff processDiff(String id, String value, boolean left)
	{
		Diff diff = diffMap.get(id);
		if (diff != null)
		{
			diff = left ? Diff.builder(diff).leftValue(value).build() : Diff.builder(diff).rightValue(value).build();
		} else
		{
			diff = left ? Diff.builder().leftValue(value).build() : Diff.builder().rightValue(value).build();
		}
		return diff;
	}

	/**
	 * This method check if data is completed and then makes the comparison/diff of values
	 * @param diff object with diff values
	 * @return a object with result inside
	 */
	private Diff compare(Diff diff)
	{
		if (diff.getLeftValue() != null && diff.getRightValue() != null)
		{
			String result = diffComparator.diffValues(diff.getLeftValue(), diff.getRightValue());
			return Diff.builder(diff).result(result).build();
		}
		return diff;
	}

	public String getDiffValue(String id) throws NotCompleteException, NotFoundException
	{
		Diff diff = diffMap.get(id);
		validateDiff(diff);
		return diffMap.get(id).getResult();
	}
}
