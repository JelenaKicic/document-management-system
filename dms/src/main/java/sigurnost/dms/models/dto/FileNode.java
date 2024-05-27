package sigurnost.dms.models.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FileNode {
    String path;
    String name;
    boolean isDirectory;
    List<FileNode> children;

    public FileNode(String path, String name, boolean isDirectory, List<FileNode> children) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = children;
    }
}