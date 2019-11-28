package br.edu.ifsc.canoinhas.server.modelDao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.jboss.logging.Message;

import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.modelDao.Conn;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoPacote {

	public void addPackage(String idProjeto, String name) throws IOException {

		Pacote pacote = new Pacote(name);
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Projeto searchProjeto = em.find(Projeto.class, Integer.parseInt(idProjeto));
		searchProjeto.getListPacote().add(pacote);
		em.getTransaction().commit();
		em.close();
	}

	public void submitGetAllPackageClient(ObjectOutputStream out) throws IOException {
		String mensagemProjeto = "";
		String mensagemPacote = "";

		List<Projeto> listProjeto = new DaoProjeto().getAllProjeto();

		if (listProjeto == null) {
			out.writeUTF("404");

		} else {
			for (Projeto projeto : listProjeto) {
				mensagemProjeto = mensagemProjeto.concat(projeto.getId() + ";" + projeto.getNome() + ";");

				for (Pacote pacote : projeto.getListPacote()) {
					mensagemProjeto = mensagemProjeto.concat("|" + pacote.getId() + ";" + pacote.getNome() + "|");
				}
			}

			out.writeUTF(mensagemProjeto);
		}
	}
}
