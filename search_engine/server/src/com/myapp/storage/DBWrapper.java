package com.myapp.storage;

import java.io.File;
import java.io.IOException;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.persist.StoreConfig;
import com.myapp.storage.accessor.*;
import com.myapp.storage.entity.UserEntity;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityStore;
import com.myapp.utils.Const;
import java.util.ArrayList;
import java.util.List;



/**
 * NoSQL part using Berkeley DB
 * store data of User, Group
 * @author Haoyun Qiu 
 *
 */
public class DBWrapper 
{
	private Environment env;
	private File env_home;
	private EntityStore store;

	/*
	 * Accessors to db
	 */
	private UserAccessor userEA;
	private IdGeneratorAccessor idEA;

	private boolean is_close;


	public DBWrapper() throws IOException
	{
		setup();
	}

	public void setup()
	{
		is_close = false;
		/*env config*/
		EnvironmentConfig ec = new EnvironmentConfig();
		ec.setAllowCreate(true);
		ec.setReadOnly(false);
		ec.setTransactional(true);

		env_home = createDir(Const.ROOT);
		try
		{
			env = new Environment(env_home, ec);
		}
		catch(EnvironmentFailureException e)
		{
			//logger.error(e.getCause());
			System.exit(-1);
		}

		/*store config*/
		StoreConfig sc = new StoreConfig();
		sc.setAllowCreate(true);
		sc.setReadOnly(false);
		sc.setTransactional(true);
		store = new EntityStore(env, Const.DB_STORE_NAME, sc);
		userEA = new UserAccessor(store);
		idEA = new IdGeneratorAccessor(store);

		//logger.info("db setup!!");
	}


	/*
	public EntityStore getStore(String name) throws DatabaseException
	{
		return store_table.get(name);
	}
	 */

	// Return a handle to the environment
	public Environment getEnv() 
	{
		return env;
	}

	public void sync()
	{
		store.sync();
	}

	// Close the store and environment.
	public void close() 
	{		
		is_close = true;
		if (store != null) 
		{
			try 
			{
				store.close();
				//print(name + " store close");
			} 
			catch(DatabaseException dbe) 
			{
				System.err.println("Error closing store: " + dbe.toString());
				System.exit(-1);
			}
		}

		if (env != null) 
		{
			try 
			{
				env.close();
				//print("env close");
			} 
			catch(DatabaseException dbe) 
			{
				System.err.println("Error closing MyDbEnv: " + dbe.toString());
				System.exit(-1);
			}
		}
	}	

	private static File createDir(String dir_name)
	{
		File dir = new File(dir_name);
		if (!dir.exists()) 
		{
			//logger.info("creating directory: " + dir_name);
			boolean res = false;
			try
			{
				dir.mkdir();
				res = true;
			} 
			catch(SecurityException se)
			{
				//logger.error("Create dir " + dir_name + " failed");
			}        
			if(res) 
			{    
				//logger.info("Create dir " + dir_name + " successed");
			}
		}
		return dir;
	}

	/**
	 ******************* Accessor funcs****************************
	 */
	public UserAccessor getUserAccessor() 
	{
		return userEA;
	}

	public void createUser(String name, String password) 
	{
		UserAccessor ua = getUserAccessor();
		ua.add(name, password);
	}

	public UserEntity getUserEntity(String name) 
	{
		UserAccessor ua = getUserAccessor();
		return ua.getEntity(name); 
	}

	public boolean hasUser(String name) 
	{
		UserAccessor ua;
		ua = getUserAccessor();
		return ua.hasUser(name);
	}

	public boolean checkLoginPassword(String name, String password) 
	{
		UserAccessor ua;
		ua = getUserAccessor();
		return ua.checkPassword(name, password);

	}

	public boolean userLogin(String name) throws IOException 
	{
		UserAccessor ua = getUserAccessor();
		ua.Login(name);
		return true;
	}

	public boolean isClose()
	{
		return is_close;
	}

	public static void print(String s)
	{
		System.out.println(s);
	}
	
	public boolean hasFBUser(String fbid) 
	{
		return userEA.hasFBUser(fbid);
	}

	public String getUserFBId(String name) 
	{
		return userEA.getUserFBId(name);
	}
}
