package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.persistence.EntityManager;
import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Classe;
import br.edu.ifsc.canoinhas.server.entities.Pacote;

public class DaoDBClasse {

	public void addClass(String idPacote, String nameClass, String typeClass, String main, ObjectOutputStream out,
			Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Inserindo classe no banco de dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		Classe classe = new Classe(nameClass);
		classe.main(main);
		classe.setTypeClasse(typeClass);
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

	public void editClass(String idClass, String newName, ObjectOutputStream out, Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Classe classSearch = em.find(Classe.class, Integer.parseInt(idClass));
		classSearch.setNome(newName);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (classSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void removerClasse(String idClasse, ObjectOutputStream out, Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Removendo classe no Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Classe classeSearch = em.find(Classe.class, Integer.parseInt(idClasse));

		em.remove(classeSearch);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (classeSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());

		System.out.println("------------------------------------------------------");

		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void editCodigoClass(String idClass, String codigo, ObjectOutputStream out, Socket client)
			throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Classe classSearch = em.find(Classe.class, Integer.parseInt(idClass));
		classSearch.setCodigo(codigo);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (classSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		// out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);
	}
}
