package org.jcpdev;

import ItemEntity.ItemsTable;

import javax.persistence.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class DatabaseActions {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private static final EntityManager entityManager=entityManagerFactory.createEntityManager();
    private static final EntityTransaction transaction = entityManager.getTransaction();

    static Logger logger = Logger.getLogger("Database Actions");
    FileHandler fileHandler = new FileHandler("DBLog.log");


    public DatabaseActions() throws IOException {
        logger.addHandler(fileHandler);
    }

    void soldItem(Long item_id) {
        try {
            transaction.begin();
            TypedQuery<ItemsTable> searchByName = entityManager.createNamedQuery("searchById", ItemsTable.class);
            searchByName.setParameter(1, item_id);
            for (ItemsTable item : searchByName.getResultList()) {
                item.setItemQuanity(item.getItemQuanity() - 1);
                logger.info(item.getItemName()+" sold.");
            }
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
        }
    }

    String getItemNameById(Long id){
        String itemName = "";
        try {
            transaction.begin();
            TypedQuery<ItemsTable> searchById = entityManager.createNamedQuery("searchById", ItemsTable.class);
            searchById.setParameter(1, id);
            for (ItemsTable item : searchById.getResultList()) {
                itemName=item.getItemName();
            }
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return itemName;
    }

    LinkedList<ItemsTable> getAllItemNames(){
        LinkedList<ItemsTable> itemList = new LinkedList<>();
        try {
            transaction.begin();
            TypedQuery<ItemsTable> searchByName = entityManager.createNamedQuery("allItemsName", ItemsTable.class);
            itemList.addAll(searchByName.getResultList());
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return itemList;
    }
}



