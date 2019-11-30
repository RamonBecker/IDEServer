package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.persistence.EntityManager;

import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Classe;
import br.edu.ifsc.canoinhas.server.entities.Pacote;

public class DaoDBClasse {
	
	
	public void addClass(String idPacote, String nameClass, String typeClass, String main, ObjectOutputStream out ,Socket client) throws IOException {
		
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Inserindo classe no banco de dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		Classe classe = new Classe(nameClass);
		
		classe.setCodigoClasse(typeClass, main);
		
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Pacote searchPacote = em.find(Pacote.class, Integer.parseInt(idPacote));
		
		searchPacote.getListClasse().add(classe);
		
		em.getTransaction().commit();
		em.close();

		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		out.writeUTF("Ok");
		System.out.println("Enviado resposta para cliente, que a classe foi criada com sucesso !");
	}
}
