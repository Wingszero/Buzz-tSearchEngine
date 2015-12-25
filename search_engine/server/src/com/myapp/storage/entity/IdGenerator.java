package com.myapp.storage.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class IdGenerator
{
	@PrimaryKey
	private String name;
	private long nextId; 

	public IdGenerator()
	{
		
	}

	public IdGenerator(String name) 
	{
		this.name = name;
		this.nextId = 0;
	}

	/*get funcs*/
	public long getNextId()
	{
		nextId ++;
		return nextId;
	}
}
