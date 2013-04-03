#!/usr/bin/python

import time
import subprocess
import curses
import sys

def timecat(stdscr,fd):
	colormap = dict()
	curses.init_pair(1, curses.COLOR_RED, curses.COLOR_BLACK)
	curses.init_pair(3, curses.COLOR_YELLOW, curses.COLOR_BLACK)
	curses.init_pair(7, curses.COLOR_BLUE, curses.COLOR_BLACK)
	colormap['V'] = curses.color_pair(7) | curses.A_BOLD
	colormap['D'] = curses.color_pair(0) | curses.A_BOLD
	colormap['W'] = curses.color_pair(3) | curses.A_BOLD
	colormap['E'] = curses.color_pair(1) | curses.A_BOLD

	stdscr.scrollok(True)
	try:
		for line in fd:
			if "de.szalkowski.adamsbatterysaver" in line:
				line = line.strip()
				stdscr.addstr("[%s] "%(time.asctime(),))
				stdscr.addstr(line,colormap[line[0]])
				stdscr.addstr("\n")
				stdscr.refresh()
	except KeyboardInterrupt:
		pass
	except:
		raise

if __name__ == "__main__":
	adb = subprocess.Popen(["adb","logcat"],stdout=subprocess.PIPE)
	curses.wrapper(timecat,adb.stdout)

