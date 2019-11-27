package br.edu.ifsc.canoinhas.server.modelDao.projeto;

import javax.persistence.EntityManager;

import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.modelDao.Conn;

public class DaoPacote {

	
	
	public void addPackage(String idProjeto, String name) {

		Pacote pacote = new Pacote(name);
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Projeto searchProjeto = em.find(Projeto.class, Integer.parseInt(idProjeto));
		searchProjeto.getListPacote().add(pacote);
		em.getTransaction().commit();
		em.close();

	}
}
