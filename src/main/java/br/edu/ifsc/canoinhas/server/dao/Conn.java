package br.edu.ifsc.canoinhas.server.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Conn {

	private static EntityManagerFactory entityManagerFactory;
	private static List<EntityManager> entityManagers = new ArrayList<>();

	public static EntityManager getEntityManager() {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		entityManagers.add(em);
		return em;
	}

	// se ainda não abri, abro uma "conexão" com o banco
	private static EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null)
			entityManagerFactory = Persistence.createEntityManagerFactory("br.edu.ifsc.canoinhas");
		return entityManagerFactory;
	}

	// fecho todos os entity managers que eu tlvz tenha esquecido de fechar, e fecho a "conexão"
	public static void closeConn() {
		if (entityManagerFactory != null)
			entityManagerFactory.close();
		for (EntityManager em : entityManagers)
			if (em.isOpen())
				em.close();
	}
}
