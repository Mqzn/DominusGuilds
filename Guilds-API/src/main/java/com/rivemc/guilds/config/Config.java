package com.rivemc.guilds.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface Config<CS> {
	
	String getName();
	
	String getString(String path);
	
	int getInt(String path);
	
	boolean getBoolean(String path);
	
	double getDouble(String path);
	
	long getLong(String path);
	
	List<String> getStringList(String path);
	
	List<Integer> getIntegerList(String path);
	
	List<Boolean> getBooleanList(String path);
	
	List<Double> getDoubleList(String path);
	
	List<Long> getLongList(String path);
	
	<T> T get(String path, Class<T> clazz);
	
	void set(String path, @Nullable Object value);
	
	void save();
	
	void reload();
	
	boolean isEmpty();
	
	Set<String> keys(boolean deep);
	
	Set<String> keys(String path, boolean deep);

	@Nullable CS getSection(String path);

}
