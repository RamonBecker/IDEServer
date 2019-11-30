package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.jboss.logging.Message;

import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;
import br.edu.ifsc.canoinhas.utility.MessageAlert;

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
	
	public void submitIdProjectServer(String idProject, String newName, String operation)
			throws UnknownHostException, IOException {

		Socket server = new Socket(ipServer, portServer);

		ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
		out.writeUTF("projeto;" + operation + ";" + idProject + ";" + newName);
		out.flush();

		ObjectInputStream in = new ObjectInputStream(server.getInputStream());
		String msg = in.readUTF();
		 
		if(msg.contains("Ok")) {
			MessageAlert.mensagemRealizadoSucesso(StringUtility.completeOperation);
		}else {
			MessageAlert.mensagemErro(StringUtility.erro);
		}
		 
		in.close();
		out.close();
		server.close();
	}
	

}
