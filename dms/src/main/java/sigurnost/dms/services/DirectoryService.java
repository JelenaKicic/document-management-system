package sigurnost.dms.services;

import org.springframework.stereotype.Service;
import sigurnost.dms.models.dto.FileNode;

import java.io.File;
import java.util.ArrayList;

@Service
public class DirectoryService {
    public FileNode getFileSystemStructure(String rootPath, boolean showJustDirectories) {
        File root = new File(rootPath);
        FileNode fileNode = new FileNode(root.getPath(), root.getName(), root.isDirectory(), new ArrayList<>());
        makeTree(fileNode, showJustDirectories);

        return fileNode;
    }

    private static void makeTree(FileNode node, boolean showJustDirectories) {
        File file = new File(node.getPath());

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileNode newNode = new FileNode(files[i].getPath(), files[i].getName(), files[i].isDirectory(), new ArrayList<>());
                if(newNode.isDirectory()) {
                    node.getChildren().add(newNode);
                    makeTree(newNode, showJustDirectories);
                } else {
                    if(!showJustDirectories) {
                        node.getChildren().add(newNode);
                    }
                }
            }
        }
    }
}
