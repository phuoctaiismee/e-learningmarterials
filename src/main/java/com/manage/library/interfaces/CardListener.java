package com.manage.library.interfaces;

public interface CardListener {
    void onDeleteCard(Integer id);
    void onUpdateCard(Integer id, String newName);
}
