package com.waes.diff.api.model;

import java.io.Serializable;

/**
 * Diff class to manage data
 */
public class Diff implements Serializable
{
	private String leftValue;
	private String rightValue;
	private String result;

	private Diff() {}

	public Diff(Builder builder)
	{
		this.leftValue = builder.leftValue;
		this.rightValue = builder.rightValue;
		this.result = builder.result;
	}

	public String getLeftValue()
	{
		return leftValue;
	}

	public String getRightValue()
	{
		return rightValue;
	}

	public String getResult()
	{
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(Diff diff) {
		return new Builder(diff);
	}

	public static class Builder {
		private String leftValue;
		private String rightValue;
		private String result;

		public Builder() {
		}

		public Builder(Diff diff)
		{
			this.leftValue = diff.leftValue;
			this.rightValue = diff.rightValue;
			this.result = diff.result;
		}

		public Builder leftValue(String leftValue){
			this.leftValue = leftValue;
			return this;
		}

		public Builder rightValue(String rightValue){
			this.rightValue = rightValue;
			return this;
		}

		public Builder result(String result){
			this.result = result;
			return this;
		}

		public Diff build() {
			return new Diff(this);
		}
	}
}
