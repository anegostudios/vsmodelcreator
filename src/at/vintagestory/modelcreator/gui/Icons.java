package at.vintagestory.modelcreator.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons
{
	public static Icon add;
	public static Icon add_rollover;
	public static Icon bin;
	public static Icon bin_open;
	public static Icon remove_rollover;
	public static Icon new_;
	public static Icon import_;
	public static Icon export;
	public static Icon texture;
	public static Icon clear;
	public static Icon copy;
	public static Icon clipboard;
	public static Icon transparent;
	public static Icon coin;
	public static Icon load;
	public static Icon disk;
	public static Icon disk_multiple;
	public static Icon exit;
	public static Icon cube;
	public static Icon inout;
	public static Icon rainbow;
	public static Icon smallcube;
	public static Icon smallcubegray;
	public static Icon point;
	public static Icon light_on;
	public static Icon light_off;
	public static Icon arrow_up;
	public static Icon arrow_down;
	public static Icon arrow_join;
	public static Icon arrow_rotate_clockwise;
	public static Icon arrow_rotate_anticlockwise;
	public static Icon reload;
	public static Icon keyboard;
	public static Icon drink;
	public static Icon wind;

	
	public static Icon arrow_up_x;
	public static Icon arrow_down_x;
	public static Icon arrow_up_y;
	public static Icon arrow_down_y;
	public static Icon arrow_up_z;
	public static Icon arrow_down_z;
	
	public static Icon facebook;
	public static Icon twitter;
	public static Icon reddit;
	public static Icon imgur;
	public static Icon planet_minecraft;
	public static Icon minecraft_forum;
	public static Icon github;
	public static Icon model_cauldron;
	public static Icon model_chair;
	
		
	public static Icon play;
	public static Icon pause;
	public static Icon previous;
	public static Icon next;
	public static Icon addremove;
	public static Icon left;
	public static Icon right;
	
	public static Icon weather_snow;

	
	public static void init(Class<?> clazz)
	{	
		ClassLoader loader = clazz.getClassLoader();
		
		wind = new ImageIcon(loader.getResource("icons/wind.png"));
		cube = new ImageIcon(loader.getResource("icons/cube.png"));
		inout = new ImageIcon(loader.getResource("icons/arrow_inout.png"));
		rainbow = new ImageIcon(loader.getResource("icons/rainbow.png"));
		smallcube = new ImageIcon(loader.getResource("icons/smallcube.png"));
		smallcubegray = new ImageIcon(loader.getResource("icons/smallcube-gray.png"));
		point = new ImageIcon(loader.getResource("icons/point.png"));
		bin = new ImageIcon(loader.getResource("icons/bin.png"));
		bin_open = new ImageIcon(loader.getResource("icons/bin_open.png"));
		add = new ImageIcon(loader.getResource("icons/add.png"));
		add_rollover = new ImageIcon(loader.getResource("icons/add_rollover.png"));
		new_ = new ImageIcon(loader.getResource("icons/new.png"));
		import_ = new ImageIcon(loader.getResource("icons/import.png"));
		export = new ImageIcon(loader.getResource("icons/export.png"));
		texture = new ImageIcon(loader.getResource("icons/texture.png"));
		clear = new ImageIcon(loader.getResource("icons/clear.png"));
		copy = new ImageIcon(loader.getResource("icons/copy.png"));
		clipboard = new ImageIcon(loader.getResource("icons/clipboard.png"));
		transparent = new ImageIcon(loader.getResource("icons/transparent.png"));
		coin = new ImageIcon(loader.getResource("icons/coin.png"));
		load = new ImageIcon(loader.getResource("icons/load.png"));
		disk = new ImageIcon(loader.getResource("icons/disk.png"));
		disk_multiple = new ImageIcon(loader.getResource("icons/disk_multiple.png"));
		exit = new ImageIcon(loader.getResource("icons/exit.png"));
		reload = new ImageIcon(loader.getResource("icons/reload.png"));
		keyboard = new ImageIcon(loader.getResource("icons/keyboard.png"));
		drink = new ImageIcon(loader.getResource("icons/drink.png"));
		
		light_on = new ImageIcon(loader.getResource("icons/box_off.png"));
		light_off = new ImageIcon(loader.getResource("icons/box_on.png"));
		
		arrow_up = new ImageIcon(loader.getResource("icons/arrow_up.png"));
		arrow_down = new ImageIcon(loader.getResource("icons/arrow_down.png"));
		arrow_join = new ImageIcon(loader.getResource("icons/arrow_join.png"));

		arrow_rotate_clockwise = new ImageIcon(loader.getResource("icons/arrow_rotate_clockwise.png"));
		arrow_rotate_anticlockwise = new ImageIcon(loader.getResource("icons/arrow_rotate_anticlockwise.png"));
		
		arrow_up_x = new ImageIcon(loader.getResource("icons/arrow_up_x.png"));
		arrow_down_x = new ImageIcon(loader.getResource("icons/arrow_down_x.png"));
		
		arrow_up_y = new ImageIcon(loader.getResource("icons/arrow_up_y.png"));
		arrow_down_y = new ImageIcon(loader.getResource("icons/arrow_down_y.png"));
		
		arrow_up_z = new ImageIcon(loader.getResource("icons/arrow_up_z.png"));
		arrow_down_z = new ImageIcon(loader.getResource("icons/arrow_down_z.png"));
		
		play = new ImageIcon(loader.getResource("icons/play.png"));
		pause = new ImageIcon(loader.getResource("icons/pause.png"));
		previous = new ImageIcon(loader.getResource("icons/prev.png"));
		next = new ImageIcon(loader.getResource("icons/next.png"));
		
		left = new ImageIcon(loader.getResource("icons/move_left.png"));
		right = new ImageIcon(loader.getResource("icons/move_right.png"));
		
		addremove = new ImageIcon(loader.getResource("icons/addremove.png"));
		
		facebook = new ImageIcon(loader.getResource("icons/facebook.png"));
		twitter = new ImageIcon(loader.getResource("icons/twitter.png"));
		reddit = new ImageIcon(loader.getResource("icons/reddit.png"));
		imgur = new ImageIcon(loader.getResource("icons/imgur.png"));
		planet_minecraft = new ImageIcon(loader.getResource("icons/planet_minecraft.png"));
		minecraft_forum = new ImageIcon(loader.getResource("icons/minecraft_forum.png"));
		github = new ImageIcon(loader.getResource("icons/github.png"));
		
		model_cauldron = new ImageIcon(loader.getResource("icons/model_cauldron.png"));
		model_chair = new ImageIcon(loader.getResource("icons/model_chair.png"));
		
		weather_snow = new ImageIcon(loader.getResource("icons/weather_snow.png"));
	}
}
