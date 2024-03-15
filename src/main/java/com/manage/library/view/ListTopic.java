package com.manage.library.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author PC
 * @param <E>
 */
public class ListTopic<E extends Object> extends JList<E> {

    public final DefaultListModel model;
    private Color selectedColor;
    private int hoverIndex = -1;

    public ListTopic() {
        setCursor(new Cursor(Cursor.HAND_CURSOR) {
        });
        model = new DefaultListModel();
        selectedColor = new Color(255, 255, 255);
        setModel(model);
//        addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                checkMouse(e);
//            }
//            
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                checkMouse(e);
//            }
//        });

    }

    private void checkMouse(MouseEvent e) {
        Point p = e.getPoint();
        int index = locationToIndex(p);
        if (index != hoverIndex) {
            hoverIndex = index;
            repaint();
        }
    }

    @Override
    public ListCellRenderer getCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                ListItem items = new ListItem();
                if (value instanceof Item item) {
                    items.setToolTipText(item.getText());
                } 
                items.setCursor(new Cursor(Cursor.HAND_CURSOR));
                items.setItem(value);
                items.setBackground(ListTopic.this.getBackground());
                items.setForeground(ListTopic.this.getForeground());
                items.setSelected(isSelected);
                if (isSelected || hoverIndex == index) {
                    items.setBackground(selectedColor);
                }
                return items;
            }

        };
    }

    public void addItem(Item item) {
//        model.removeAllElements();
        model.addElement(item);
    }

    public Item getItem(int index) {
        return (Item) model.get(index);
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

}
