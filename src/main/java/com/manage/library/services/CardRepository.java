package com.manage.library.services;

import com.manage.library.model.Resource;
import com.manage.library.view.Card;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRepository {

    private static CardRepository instance;
    private Map<Integer, Card> cardCache;
    private List<Resource> listFiles;

    private CardRepository() {
        cardCache = new HashMap<>();
        listFiles = new ArrayList<>();
    }

    public static CardRepository getInstance() {
        if (instance == null) {
            instance = new CardRepository();
        }
        return instance;
    }

    public List<Card> renderCards(List<Resource> listFiles) throws Exception {
        this.listFiles = listFiles;
        List<Card> listPanel = new ArrayList<>();
        for (Resource file : listFiles) {
            Card card = cardCache.get(file.getId());
            if (card == null) {
                card = createCard(file);
                cardCache.put(file.getId(), card);
            }
            listPanel.add(card);
        }
        return listPanel;
    }

    private Card createCard(Resource file) throws NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        Card card = new Card();
        card.setFile(file);
        card.fillData();
        //card.addDeletionListener((CardListener) this);
        return card;
    }

    public void updateFileName(int id, String newName, String url) throws Exception {
        for (Resource file : listFiles) {
            if (file.getId() == id) {
                file.setName(newName);
                file.setUrl(url);
                break;
            }
        }
        renderCards(listFiles);
    }

    public void updateFiles(List<Resource> listFiles) {
        this.listFiles = listFiles;
        cardCache.clear();
    }
}
