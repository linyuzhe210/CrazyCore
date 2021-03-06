package de.st_ddt.crazyutil.conditions.appender;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyutil.conditions.Condition;
import de.st_ddt.crazyutil.conditions.SimpleParameterExtendingCondition;

public class ServerDateAppender extends SimpleParameterExtendingCondition
{

	public ServerDateAppender(final String parameterName, final int index)
	{
		super(parameterName, index, Date.class);
	}

	public ServerDateAppender(final Condition condition, final String parameterName, final int index)
	{
		super(condition, parameterName, index, Date.class);
	}

	public ServerDateAppender(final ConfigurationSection config, final Map<String, Integer> parameterIndexes) throws Exception
	{
		super(config, parameterIndexes, Date.class);
	}

	@Override
	protected Date getValue(final Map<Integer, ? extends Object> parameters)
	{
		return new Date();
	}

	@Override
	public Condition secure(final Map<Integer, ? extends Collection<Class<?>>> classes)
	{
		return new ServerDateAppender(condition.secure(getParameterClasses(classes)), targetName, targetIndex);
	}
}
