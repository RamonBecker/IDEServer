package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.persistence.EntityManager;
import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;

public class DaoDBPacote {

	public void addPackage(String idProjeto, String name, ObjectOutputStream out, Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Inserindo pacote no banco de dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		Pacote pacote = new Pacote(name);
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Projeto searchProjeto = em.find(Projeto.class, Integer.parseInt(idProjeto));
		searchProjeto.getListPacote().add(pacote);
		em.getTransaction().commit();
		em.close();

		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		out.writeUTF("Ok");
		System.out.println("Enviado resposta para cliente, que o pacote foi criado com sucesso !");
	}
	
	
	public void editProject(String idPacote, String newName, ObjectOutputStream out, Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Pacote pacoteSearch = em.find(Pacote.class, Integer.parseInt(idPacote));
		pacoteSearch.setNome(newName);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";
		
		if (pacoteSearch != null) {
			mensagem = "Ok";

		} 
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}
}
