package org.clarksnut.models;

public interface FileProvider {

    FileModel addFile(byte[] file, String filename, MessageModel message);

    FileModel getFile(String id);
}
