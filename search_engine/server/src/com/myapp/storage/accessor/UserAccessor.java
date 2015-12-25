package com.myapp.storage.accessor;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.myapp.storage.entity.UserEntity;
import java.util.Date;

/*
 * DB Accessor for UserEntity 
 * @author Haoyun 
 * 
 */

public class UserAccessor
{
	private PrimaryIndex<String, UserEntity> userByName;

	//static final Logger logger = Logger.getLogger(DBWrapper.class);	

	public UserAccessor(EntityStore store)
	{
		userByName = store.getPrimaryIndex(String.class, UserEntity.class);
	}

	private UserEntity getEntityByFBId(String fbid) 
	{
		return null;
	}

	public void add(String name, String password)
	{
		long time = (new Date()).getTime();
		UserEntity user = new UserEntity(name, password, time);
		putEntity(user);
	}

	public boolean hasUser(String name)
	{
		return userByName.contains(name);
	}

	public boolean del(String name)
	{
		return userByName.delete(name);
	}

	public PrimaryIndex<String, UserEntity> getPrimaryIndex() 
	{
		return userByName;
	}

	public UserEntity getEntity(String name)
	{
		return userByName.get(name);
	}

	public void putEntity(UserEntity user)
	{
		userByName.put(user);
	}

	public boolean checkPassword(String name, String password) 
	{
		UserEntity ue = getEntity(name);
		if(ue == null)
		{
			//logger.info("checkPassword: user: " + name + " is not exists");
			return false;
		}
		return ue.getPassword().equals(password);
	}

	public boolean Login(String name) 
	{
		UserEntity user = getEntity(name);
		if(user == null)
		{
			//logger.error("Login cannot find user: " + name);
			return false;
		}
		user.Login();
		putEntity(user);
		return true;
	}

	public void joinGroup(String username, Long id) 
	{
		UserEntity user = getEntity(username);
		if(user != null)
		{
			user.joinGroup(id);
			putEntity(user);
		}	
	}

	public void leaveGroup(String username, Long id) 
	{
		UserEntity user = getEntity(username);
		if(user != null)
		{
			user.leaveGroup(id);
			putEntity(user);
		}	
	}

	public boolean hasFBUser(String fbid) 
	{
		UserEntity user = getEntityByFBId(fbid);
		return user != null;
	}

	public String getUserFBId(String name) 
	{
		return null;
	}

}

