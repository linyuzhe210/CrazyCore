package de.st_ddt.crazyplugin.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.st_ddt.crazyplugin.exceptions.CrazyCommandErrorException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandPermissionException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHeaderProvider;

public abstract class CrazyCommandExecutor<S extends ChatHeaderProvider> implements CrazyCommandExecutorInterface
{

	protected final S owner;

	public CrazyCommandExecutor(final S owner)
	{
		super();
		this.owner = owner;
	}

	public final S getOwner()
	{
		return owner;
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
	{
		try
		{
			if (!hasAccessPermission(sender))
				throw new CrazyCommandPermissionException();
			command(sender, args);
		}
		catch (final CrazyCommandException e)
		{
			e.addCommandPrefix(commandLabel);
			e.setCommand(commandLabel, args);
			e.print(sender, owner.getChatHeader());
		}
		catch (final CrazyException e)
		{
			e.print(sender, owner.getChatHeader());
		}
		catch (final Exception e)
		{
			new CrazyCommandErrorException(e).print(sender, owner.getChatHeader());
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public abstract void command(final CommandSender sender, final String[] args) throws CrazyException;

	@Override
	public final List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
	{
		if (isAccessible(sender))
		{
			final List<String> list = tab(sender, args);
			if (list == null)
				return null;
			else
				return list.subList(0, Math.min(30, list.size()));
		}
		else
			return null;
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		return null;
	}

	@Override
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return true;
	}

	@Override
	public boolean isAccessible(final CommandSender sender)
	{
		return hasAccessPermission(sender);
	}
}
