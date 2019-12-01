package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Classe;
import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoDBProjeto {

	public DaoDBProjeto() {
		 addFirstProjeto();
	}

	public void addProjectBD(String nome, String location, ObjectOutputStream out, Socket client) throws IOException {

		EntityManager em = Conn.getEntityManager();

		try {

			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");
			System.out.println("Inserindo projeto no banco de dados");
			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");

			Projeto projeto = new Projeto(nome, location);

			em.getTransaction().begin();
			em.persist(projeto);
			em.getTransaction().commit();

			out.writeUTF(StringUtility.ok);

		} catch (IllegalArgumentException e) {
			out.writeUTF(e.getMessage());

		} finally {
			em.close();
		}

		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		out.writeUTF("Ok");
		System.out.println("Enviado resposta para cliente, que o projeto foi criado com sucesso !");
	}

	public List<Projeto> getAllProjeto() {

		List<Projeto> listProjeto = new ArrayList<Projeto>();

		EntityManager em = Conn.getEntityManager();

		listProjeto = em.createQuery("FROM Projeto", Projeto.class).getResultList();

		em.close();

		return listProjeto;
	}

	public void getAllProjetoSubmitClient(ObjectOutputStream out, Socket client) throws IOException {

		String mensagem = "";

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		List<Projeto> listProjeto = getAllProjeto();

		System.out.println(listProjeto);

		if (listProjeto == null) {
			out.writeUTF(StringUtility.erro);
		} else {
			for (Projeto projeto : listProjeto) {
				mensagem = mensagem
						.concat("-" + projeto.getId() + ";" + projeto.getNome() + ";" + projeto.getLocation() + ";");

				for (Pacote pacote : projeto.getListPacote()) {
					if (pacote != null) {
						mensagem = mensagem.concat(pacote.getId() + "," + pacote.getNome() + ",");

						for (Classe classe : pacote.getListClasse()) {
							mensagem = mensagem
									.concat(classe.getId() + "/" + classe.getNome() + ",");
						}
					}
				}

			}
		}

		if (mensagem.isEmpty()) {
			out.writeUTF("404");
		} else {
			out.writeUTF(mensagem);
		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void addFirstProjeto() {

		Projeto projeto = new Projeto("Meu projeto de teste", "loca/projeto");

		Pacote pacote = new Pacote("Meu pacote de teste");

		Classe classeCadastroEmpresa = new Classe("CadastroEmpresa");
		Classe classeLogin = new Classe("Login");
		Classe classeBuscarEmpresa = new Classe("Buscarempresa");
		Classe classePrincipal = new Classe("Principal");
		Classe classeRedefinirSenhaUsuario = new Classe("RedefinirSenhaUsuario");
		Classe classeAtenderOcorrencia = new Classe("Atender Ocorrencia");
		Classe classeMain = new Classe("Main");

//		classeCadastroEmpresa.setCodigo(StringUtility.cadastrarEmpresa);
//		classeLogin.setCodigo(StringUtility.login);
//		classeBuscarEmpresa.setCodigo(StringUtility.buscarEmpresa);
//		classePrincipal.setCodigo(StringUtility.telaPrincipal);
//		classeRedefinirSenhaUsuario.setCodigo(StringUtility.redefinirSenhaUsuario);
//		classeAtenderOcorrencia.setCodigo(StringUtility.classeAtenderOcorrencia);
//		classeMain.setCodigo(StringUtility.main);

		pacote.setListClasse(new ArrayList<Classe>());

		pacote.addClass(classeCadastroEmpresa);
		pacote.addClass(classeLogin);
		pacote.addClass(classeBuscarEmpresa);
		pacote.addClass(classePrincipal);
		pacote.addClass(classeRedefinirSenhaUsuario);
		pacote.addClass(classeAtenderOcorrencia);
		pacote.addClass(classeMain);

		projeto.setListPacote(new ArrayList<Pacote>());

		projeto.addPackage(pacote);

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();
		em.persist(projeto);
		em.getTransaction().commit();

	}

	public void editProject(String idProject, String newName, ObjectOutputStream out, Socket client)
			throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Projeto projetoSearch = em.find(Projeto.class, Integer.parseInt(idProject));
		projetoSearch.setNome(newName);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (projetoSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void removeProject(String idProject, ObjectOutputStream out, Socket client) throws IOException {

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

		Projeto projetoSearch = em.find(Projeto.class, Integer.parseInt(idProject));

		em.remove(projetoSearch);

		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (projetoSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);
		out.writeUTF(mensagem);
	}
}
