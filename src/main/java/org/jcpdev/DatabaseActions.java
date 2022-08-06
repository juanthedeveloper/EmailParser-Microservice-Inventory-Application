package org.jcpdev;

import ItemEntity.ItemsTable;

import javax.persistence.*;

public class DatabaseActions {
    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private static EntityManager entityManager=entityManagerFactory.createEntityManager();
    private static EntityTransaction transaction = entityManager.getTransaction();


    public static void soldItem(String itemName) {

        try {
            transaction.begin();
            TypedQuery<ItemsTable> searchByName = entityManager.createNamedQuery("searchByName", ItemsTable.class);
            searchByName.setParameter(1, itemName);
            for (ItemsTable item : searchByName.getResultList()) {
                item.setItemQuanity(item.getItemQuanity() - 1);
                System.out.println(itemName + " sold 1");
            }

            transaction.commit();


        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}



