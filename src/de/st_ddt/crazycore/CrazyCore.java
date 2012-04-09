package de.st_ddt.crazycore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import de.st_ddt.crazyplugin.CrazyPlugin;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandParameterException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandPermissionException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandUsageException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.locales.CrazyLocale;

public class CrazyCore extends CrazyPlugin
{

	protected static CrazyCore plugin;
	protected static final ArrayList<String> defaultLanguages = new ArrayList<String>();
	protected static String defaultLanguage;
	public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	public static CrazyCore getPlugin()
	{
		return plugin;
	}

	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new ScheduledPermissionAllTask(), 20);
		super.onEnable();
	}

	@Override
	public boolean command(final CommandSender sender, final String commandLabel, final String[] args) throws CrazyException
	{
		if (commandLabel.equalsIgnoreCase("crazylist"))
		{
			commandList(sender, args);
			return true;
		}
		if (commandLabel.equalsIgnoreCase("crazylanguage") || commandLabel.equalsIgnoreCase("language"))
		{
			commandLanguage(sender, args);
			return true;
		}
		return false;
	}

	private void commandList(final CommandSender sender, final String[] args) throws CrazyCommandException
	{
		if (!sender.hasPermission("crazycore.list"))
			throw new CrazyCommandPermissionException();
		int page;
		switch (args.length)
		{
			case 0:
				page = 1;
				break;
			case 1:
				try
				{
					page = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e)
				{
					throw new CrazyCommandParameterException(1, "Integer");
				}
				break;
			default:
				throw new CrazyCommandUsageException("/crazylist [Page]");
		}
		ArrayList<CrazyPlugin> list = getCrazyPlugins();
		int lastIndex = list.size();
		if (lastIndex + 9 < page * 10)
		{
			sendLocaleMessage("COMMAND.PLUGINLIST.EMPTYPAGE", sender, String.valueOf(page));
			return;
		}
		lastIndex = Math.min(lastIndex, page * 10);
		sendLocaleMessage("COMMAND.PLUGINLIST.HEADER", sender, String.valueOf(page));
		for (int i = page * 10 - 10; i < lastIndex; i++)
			sendLocaleMessage("COMMAND.PLUGINLIST.ENTRY", sender, String.valueOf(i + 1), list.get(i).getDescription().getName(), list.get(i).getDescription().getVersion());
	}

	private void commandLanguage(final CommandSender sender, final String[] args) throws CrazyCommandException
	{
		switch (args.length)
		{
			case 0:
				String languages = ChatHelper.listToString(CrazyLocale.getLoadedLanguages());
				sendLocaleMessage("COMMAND.LANGUAGE.CURRENT", sender, CrazyLocale.getUserLanguage(sender));
				sendLocaleMessage("COMMAND.LANGUAGE.LIST.DEFAULT", sender);
				sendLocaleMessage("COMMAND.LANGUAGE.LIST.HEADER", sender);
				sendLocaleMessage("COMMAND.LANGUAGE.LIST.ENTRY", sender, languages);
				return;
			case 1:
				CrazyLocale.setUserLanguage(sender, args[0]);
				save();
				sendLocaleMessage("COMMAND.LANGUAGE.CHANGED", sender, args[0]);
				return;
			case 2:
				if (!sender.hasPermission("crazycore.language.advanced"))
					throw new CrazyCommandPermissionException();
				if (args[0].equalsIgnoreCase("print"))
				{
					if (args[1].equalsIgnoreCase("*"))
					{
						CrazyLocale.printAll(sender);
						return;
					}
					CrazyLocale.getLocaleHead().getLanguageEntry(args[1]).print(sender);
					return;
				}
				else if (args[0].equalsIgnoreCase("setdefault"))
				{
					defaultLanguage = args[1];
					CrazyLocale.loadLanguage(defaultLanguage);
					sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.SET", sender, defaultLanguage);
					return;
				}
				else if (args[0].equalsIgnoreCase("adddefault"))
				{
					defaultLanguages.add(args[1]);
					CrazyLocale.loadLanguage(args[1]);
					sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.ADDED", sender, args[1]);
					return;
				}
				else if (args[0].equalsIgnoreCase("removedefault"))
				{
					defaultLanguages.remove(args[1]);
					sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.REMOVED", sender, args[1]);
					return;
				}
				else if (args[0].equalsIgnoreCase("download"))
				{
					String download = args[1];
					if (download.equalsIgnoreCase("*"))
					{
						for (String language : CrazyLocale.getLoadedLanguages())
						{
							for (CrazyPlugin plugin : getCrazyPlugins())
							{
								plugin.downloadLanguage(language);
								plugin.loadLanguage(language, sender);
								plugin.checkLocale();
							}
							sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.RELOADED", sender, language);
						}
						return;
					}
					CrazyPlugin plugin = CrazyPlugin.getPlugin(download);
					if (plugin != null)
					{
						for (String language : CrazyLocale.getLoadedLanguages())
						{
							plugin.downloadLanguage(language);
							plugin.loadLanguage(language, sender);
							plugin.checkLocale();
							sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.RELOADED.PLUGIN", sender, language, plugin.getName());
						}
						return;
					}
					for (CrazyPlugin plugin2 : getCrazyPlugins())
					{
						plugin2.downloadLanguage(download);
						plugin2.loadLanguage(download, sender);
						plugin2.checkLocale();
					}
					sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.DOWNLOADED", sender, download);
					return;
				}
				else if (args[0].equalsIgnoreCase("reload"))
				{
					String reload = args[1];
					if (reload.equalsIgnoreCase("*"))
					{
						for (String language : CrazyLocale.getLoadedLanguages())
						{
							for (CrazyPlugin plugin : getCrazyPlugins())
							{
								plugin.loadLanguage(language, sender);
								plugin.checkLocale();
							}
							sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.RELOADED", sender, language);
						}
						return;
					}
					CrazyPlugin plugin = getPlugin(reload);
					if (plugin != null)
					{
						for (String language : CrazyLocale.getLoadedLanguages())
						{
							plugin.loadLanguage(language, sender);
							plugin.checkLocale();
							sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.RELOADED.PLUGIN", sender, language, plugin.getName());
						}
						return;
					}
					for (CrazyPlugin plugin2 : getCrazyPlugins())
					{
						plugin2.loadLanguage(reload, sender);
						plugin2.checkLocale();
					}
					sendLocaleMessage("COMMAND.LANGUAGE.DEFAULT.RELOADED", sender, reload);
					return;
				}
				else
					throw new CrazyCommandUsageException("/crazylanguage setdefault <Language>", "/crazylanguage adddefault <Language>", "/crazylanguage removedefault <Language>", "/crazylanguage download <Landuage>", "/crazylanguage reload <Landuage>");
			default:
				throw new CrazyCommandUsageException("/crazylanguage [Language]");
		}
	}

	@Override
	public void load()
	{
		super.load();
		FileConfiguration config = getConfig();
		for (String language : config.getStringList("defaultLanguages"))
		{
			defaultLanguages.add(language);
			CrazyLocale.loadLanguage(language);
		}
		defaultLanguage = config.getString("defaultLanguage", "en_en");
		CrazyLocale.loadLanguage(defaultLanguage);
		CrazyLocale.load(config.getConfigurationSection("players"));
	}

	@Override
	public void save()
	{
		FileConfiguration config = getConfig();
		config.set("defaultLanguage", defaultLanguage);
		config.set("defaultLanguages", defaultLanguages);
		CrazyLocale.save(config, "players.");
		super.save();
	}

	public static String getDefaultLanguage()
	{
		return defaultLanguage;
	}

	public static ArrayList<String> getDefaultlanguages()
	{
		return defaultLanguages;
	}
}
