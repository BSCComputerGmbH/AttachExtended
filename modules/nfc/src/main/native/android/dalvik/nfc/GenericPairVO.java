package com.gluonhq.helloandroid.nfc;

import java.io.Serializable;

public class GenericPairVO<L, R> implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3843900774163542012L;
	private L left;
	private R right;

	public GenericPairVO(L left, R right)
	{
		this.left = left;
		this.right = right;
	}

	/**
	 * @return the left
	 */
	public L getLeft()
	{
		return left;
	}

	/**
	 * @return the right
	 */
	public R getRight()
	{
		return right;
	}

	public void setLeft(L leftValue)
	{
		this.left = leftValue;
	}

	public void setRight(R rightValue)
	{
		this.right = rightValue;
	}

	public boolean hasNullData()
	{
		return left == null || right == null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		GenericPairVO<?, ?> other = (GenericPairVO<?, ?>) obj;
		if (left == null)
		{
			if (other.left != null)
				return false;
		}
		else if (!left.equals(other.left))
			return false;
		if (right == null)
		{
			if (other.right != null)
				return false;
		}
		else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "GenericPairVO [left=" + left + ", right=" + right + "]";
	}

	public static <L, R> GenericPairVO<L, R> buildGenericPair(L left, R right)
	{
		return new GenericPairVO<L, R>(left, right);
	}

}