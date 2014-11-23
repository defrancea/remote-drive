/*
  JDokan : Java library for Dokan

  Copyright (C) 2008 Yu Kobayashi http://yukoba.accelart.jp/

  http://decas-dev.net/en

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along
with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package net.decasdev.dokan;

public class DokanOptions {
    public static int DOKAN_OPTION_DEBUG = 1;
    public static int DOKAN_OPTION_STDERR = 2;
    public static int DOKAN_OPTION_ALT_STREAM = 4;
    public static int DOKAN_OPTION_KEEP_ALIVE = 8;
    public static int DOKAN_OPTION_NETWORK = 16;
    public static int DOKAN_OPTION_REMOVABLE = 32;

	//public char driveLetter = 'S';
	public String mountPoint = "S:\\";
	public int threadCount = 1;
	public long optionsMode = 0;

	public DokanOptions() {
	}

	public DokanOptions(String mountPoint, int threadCount, long optionsMode) {
		//this.driveLetter = driveLetter;
		this.mountPoint = mountPoint;
		this.threadCount = threadCount;
		this.optionsMode = optionsMode;
	}

	@Override public String toString() {
		return "DokanOptions(" + "mountPoint=" + mountPoint + "," + "threadCount=" + threadCount + ","
				+ "optionsMode=" + optionsMode + ")";
	}
}