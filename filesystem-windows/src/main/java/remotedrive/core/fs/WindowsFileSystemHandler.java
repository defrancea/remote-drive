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

package remotedrive.core.fs;

import net.decasdev.dokan.ByHandleFileInformation;
import net.decasdev.dokan.Dokan;
import net.decasdev.dokan.DokanDiskFreeSpace;
import net.decasdev.dokan.DokanFileInfo;
import net.decasdev.dokan.DokanOperationException;
import net.decasdev.dokan.DokanOperations;
import net.decasdev.dokan.DokanOptions;
import net.decasdev.dokan.DokanVolumeInformation;
import net.decasdev.dokan.FileAttribute;
import net.decasdev.dokan.Win32FindData;
import remotedrive.core.Configuration;
import remotedrive.core.Drive;
import remotedrive.core.File;
import remotedrive.core.spi.Client;
import remotedrive.core.spi.FileSystemHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines Windows file system handler.
 */
public class WindowsFileSystemHandler implements FileSystemHandler
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getAvailableMountingPoints()
    {
        // Browse from A to Z
        List<String> availableLetters = new ArrayList<String>();
        for(char c = 'A'; c <= 'Z'; ++c)
        {
            // If the file exist, skip it
            String currentRoot = String.valueOf(c);
            if(!Paths.get(currentRoot + ":\\").toFile().exists())
            {
                availableLetters.add(String.valueOf(c));
            }
        }

        // Return as an array
        return availableLetters.toArray(new String[availableLetters.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount(final Client client, final Configuration configuration)
    {
        System.out.println("mounting " + configuration.readString("name"));
        /*client.authenticate("***", null);

        DokanOptions options = new DokanOptions(configuration.readString("mounting-point"), 10, DokanOptions.DOKAN_OPTION_REMOVABLE);
        Dokan.mount(options, new DokanOperations() {
            @Override
            public long onCreateFile(String fileName, int desiredAccess, int shareMode, int creationDisposition, int flagsAndAttributes, DokanFileInfo fileInfo) throws DokanOperationException {
                return 0;
            }

            @Override
            public long onOpenDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {
                return 0;
            }

            @Override
            public void onCreateDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onCleanup(String fileName, DokanFileInfo fileInfo) {
            }

            @Override
            public void onCloseFile(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public int onReadFile(String fileName, ByteBuffer buffer, long offset, DokanFileInfo fileInfo)
            {
                fileName = fileName.replace("\\", "/");
                if (fileName.startsWith("/"))
                {
                    fileName = fileName.substring(1);
                }
                byte[] content = client.retrieveDriveFileContent(fileName);
                if (null == content)
                {
                    return 0;
                }

                int copySize = Math.min(content.length, buffer.capacity());

                byte[] buffer2 = Arrays.copyOfRange(content, (int) offset, (int) offset + copySize);

                buffer.put(buffer2, 0, copySize);
                return copySize;
            }

            @Override
            public int onWriteFile(String fileName, ByteBuffer buffer, long offset, DokanFileInfo fileInfo) throws DokanOperationException {
                return 0;
            }

            @Override
            public void onFlushFileBuffers(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public ByHandleFileInformation onGetFileInformation(String fileName, DokanFileInfo fileInfo) throws DokanOperationException, IOException {
                fileName = fileName.replace("\\", "/");
                if (fileName.startsWith("/"))
                {
                    fileName = fileName.substring(1);
                }

                File diskFile = client.retrieveDriveFile(fileName);
                if (null == diskFile)
                {
                    return null;
                }

                ByHandleFileInformation fileInformation = new ByHandleFileInformation(
                        diskFile.isDirectory() ? FileAttribute.FILE_ATTRIBUTE_DIRECTORY : FileAttribute.FILE_ATTRIBUTE_NORMAL,
                        diskFile.getCreationTime(),
                        diskFile.getCreationTime(),
                        diskFile.getCreationTime(),
                        0,
                        diskFile.getSize(),
                        0,
                        0
                );

                return fileInformation;
            }

            @Override
            public Win32FindData[] onFindFiles(String pathName, DokanFileInfo fileInfo) throws DokanOperationException, IOException {
                pathName = pathName.replace("\\", "/");
                if (pathName.startsWith("/"))
                {
                    pathName = pathName.substring(1);
                }

                File[] diskFiles = client.retrieveChildren(pathName);
                if (null == diskFiles)
                {
                    return null;
                }

                Win32FindData[] win32FindData = new Win32FindData[diskFiles.length];
                for(int i = 0; i < win32FindData.length; ++i)
                {
                    Win32FindData newData = new Win32FindData();
                    File matchingFile = diskFiles[i];
                    newData.creationTime = matchingFile.getCreationTime();
                    newData.fileName = matchingFile.getTitle();
                    newData.fileSize = matchingFile.getSize();
                    newData.fileAttributes = matchingFile.isDirectory() ? FileAttribute.FILE_ATTRIBUTE_DIRECTORY : FileAttribute.FILE_ATTRIBUTE_NORMAL;
                    win32FindData[i] = newData;
                }
                return win32FindData;
            }

            @Override
            public Win32FindData[] onFindFilesWithPattern(String pathName, String searchPattern, DokanFileInfo fileInfo) throws DokanOperationException {
                return new Win32FindData[0];
            }

            @Override
            public void onSetFileAttributes(String fileName, int fileAttributes, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onSetFileTime(String fileName, long creationTime, long lastAccessTime, long lastWriteTime, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onDeleteFile(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onDeleteDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onMoveFile(String existingFileName, String newFileName, boolean replaceExisiting, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onSetEndOfFile(String fileName, long length, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onLockFile(String fileName, long byteOffset, long length, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public void onUnlockFile(String fileName, long byteOffset, long length, DokanFileInfo fileInfo) throws DokanOperationException {

            }

            @Override
            public DokanDiskFreeSpace onGetDiskFreeSpace(DokanFileInfo fileInfo) throws DokanOperationException {
                DokanDiskFreeSpace diskFreeSpace = new DokanDiskFreeSpace();
                Drive diskInformation = client.retrieveDiskInformation();
                diskFreeSpace.freeBytesAvailable = diskInformation.getBytesTotal() - diskInformation.getBytesUsed();
                diskFreeSpace.totalNumberOfBytes = diskInformation.getBytesTotal();
                diskFreeSpace.totalNumberOfFreeBytes = diskInformation.getBytesTotal() - diskInformation.getBytesUsed();
                return diskFreeSpace;
            }

            @Override
            public DokanVolumeInformation onGetVolumeInformation(String volumeName, DokanFileInfo fileInfo) throws DokanOperationException {
                DokanVolumeInformation volumeInformation = new DokanVolumeInformation();
                volumeInformation.fileSystemName = configuration.readString("name");
                volumeInformation.volumeName = configuration.readString("name");
                return volumeInformation;
            }

            @Override
            public void onUnmount(DokanFileInfo fileInfo) throws DokanOperationException {
                Dokan.removeMountPoint("Z");
            }
        });*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void umount()
    {
    }
}
