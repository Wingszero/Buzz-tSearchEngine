package com.myapp.storage.entity;

import java.util.ArrayList;
import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;


@Entity
public class UserEntity
{
	@PrimaryKey
	private String name;

	private String password;
	//jpeg
	private String head_url;

	private long login_time; 

	//news' id
	private ArrayList<Long>news= new ArrayList<Long>(); 

	//like movie' id
	private ArrayList<String>likeMovies = new ArrayList<String>(); 

	//friends username 
	private ArrayList<String>friends = new ArrayList<String>(); 

	//comment's id
	private ArrayList<Long>comments = new ArrayList<Long>(); 

	//join group 's id
	private ArrayList<Long>joinGroups = new ArrayList<Long>(); 

	//create group 's id
	private ArrayList<Long>createGroups = new ArrayList<Long>(); 
	
	public UserEntity()
	{
		
	}

	public UserEntity(String n, String p, long time) 
	{
		name = n;
		password = p;
		login_time = time;
	}

	/*get funcs*/
	public String getName()
	{
		return name;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getHeadUrl() 
	{
		return head_url;
	}

	public long getLogin() 
	{
		return login_time;
	}

	public ArrayList<Long> getJoinGroups() 
	{
		return joinGroups;
	}
	
	public ArrayList<String> getFriends() 
	{
		return friends;
	}
	
	public ArrayList<Long> getComments() 
	{
		return comments;
	}

	public ArrayList<Long> getNews() 
	{
		return news;
	}	
	
	
	/*set funcs*/
	public void setName(String data)
	{
		name = data;
	}

	public void setPassword(String data)
	{
		password = data;
	}
	
	public void setLogin(long time)
	{
		login_time = time;
	}

	public void Login()
	{
		long time = (new Date()).getTime();
		login_time = time;
	}	
	
	public void addNews(Long id)
	{
		news.add(id);
	}

	public void addFriend(String friendname) 
	{
		if(friends.contains(friendname))
		{
			return;
		}
		friends.add(friendname);
	}

	public void joinGroup(Long id) 
	{
		if(!joinGroups.contains(id))
		{
			joinGroups.add(id);
		}
	}

	public void leaveGroup(Long id) 
	{
		joinGroups.remove(id);
	}

	public boolean isMyFriend(String targetName) 
	{
		return friends.contains(targetName);
	}

	public boolean isLikeMovie(String movie_id) 
	{
		return likeMovies.contains(movie_id); 
	}

	public boolean canCreateGroup() 
	{
		return createGroups.size() < 3;
	}
}
