/*
 * Copyright (c) 2014, Alain Defrance. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package remotedrive.core;

import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a drive configuration.
 * It extends HashMap in order to base the persisted representation on a key/value collection.
 * It's technically possible to use HashMap members but we encourage using the Configuration member in order to respect the scoping.
 */
public class Configuration extends HashMap<String, String>
{
    /**
     * The default scope.
     */
    private static final String DEFAULT_SCOPE = "global";

    /**
     * Reads a string configuration value in the default scope.
     * @param key The configuration key.
     * @return The value as string.
     */
    public String readString(String key)
    {
        return readString(DEFAULT_SCOPE, key);
    }

    /**
     * Reads a string configuration value in the given scope.
     * @param scope The configuration scope.
     * @param key The configuration key.
     * @return The value as string.
     */
    public String readString(String scope, String key)
    {
        return getOrDefault(buildKey(scope, key), null);
    }

    /**
     * Reads a boolean configuration value in the default scope.
     * @param key The configuration key.
     * @return The value as boolean.
     */
    public boolean readBoolean(String key)
    {
        return readBoolean(DEFAULT_SCOPE, key);
    }

    /**
     * Reads a boolean configuration value in the given scope.
     * @param scope The configuration scope.
     * @param key The configuration key.
     * @return The value as boolean.
     */
    public boolean readBoolean(String scope, String key)
    {
        String value = readString(scope, key);
        if (null == value)
        {
            return false;
        }

        return Boolean.parseBoolean(value);
    }

    /**
     * Writes a string configuration value in the default scope.
     * @param key The configuration key.
     * @param value The configuration string value.
     */
    public void writeString(String key, String value)
    {
        writeString(DEFAULT_SCOPE, key, value);
    }

    /**
     * Writes a string configuration value in the given scope.
     * @param scope The configuration scope.
     * @param key The configuration key.
     * @param value The configuration string value.
     */
    public void writeString(String scope, String key, String value)
    {
        put(buildKey(scope, key), value);
    }

    /**
     * Writes a boolean configuration value in the default scope.
     * @param key The configuration key.
     * @param value The configuration boolean value.
     */
    public void writeBoolean(String key, boolean value)
    {
        writeBoolean(DEFAULT_SCOPE, key, value);
    }

    /**
     * Writes a string configuration value in the given scope.
     * @param scope The configuration scope.
     * @param key The configuration key.
     * @param value The configuration boolean value.
     */
    public void writeBoolean(String scope, String key, boolean value)
    {
        writeString(scope, key, Boolean.toString(value));
    }

    /**
     * Builds a configuration key with it's scope.
     * @param scope The configuration scope.
     * @param key The configuration key.
     * @return The configuration key.
     */
    protected String buildKey(String scope, String key) {
        return String.format(Locale.US, "[%s]%s", scope, key);
    }
}
