package com.manage.library.utils;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author PC
 */
public final class TreeUtils {

    public static void addNode(DefaultMutableTreeNode parentNode, String nodeName, JTree tree) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
        parentNode.add(newNode);
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
    }

    public static void deleteNode(DefaultMutableTreeNode nodeToDelete, JTree tree) {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) nodeToDelete.getParent();
        if (parentNode != null) {
            parentNode.remove(nodeToDelete);
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
        }
    }

    
    public static DefaultMutableTreeNode findNode(DefaultMutableTreeNode parentNode, String key) {
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            if (childNode.toString().equals(key)) {
                return childNode;
            }
        }
        return null; 
    }
}
