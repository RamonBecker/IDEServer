package br.edu.ifsc.canoinhas.server.dao.projeto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.entities.Classe;
import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoDBProjeto {

	public DaoDBProjeto() {
	}

//	public void addProjeto(String nameProject, String localProject) {
//
//		EntityManager em = Conn.getEntityManager();
//
//		try {
//
//			Projeto projetoNew = new Projeto(nameProject, localProject);
//
//			if (checkProjeto(nameProject.trim(), localProject.trim())) {
//				// MessageAlert.mensagemErro(StringUtility.projetoIguais);
//				return;
//			}
//
//			addProjectBD(projetoNew);
//			// MessageAlert.mensagemRealizadoSucesso(StringUtility.projectCreate);
//
//		} catch (IllegalArgumentException e) {
//			// MessageAlert.mensagemErro(e.getMessage());
//		} finally {
//			em.close();
//		}
//	}

	public void addProjectBD(String nome, String location, ObjectOutputStream out) throws IOException {

		EntityManager em = Conn.getEntityManager();

		try {

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

		if (listProjeto == null) {
			out.writeUTF(StringUtility.erro);
		} else {
			for (Projeto projeto : listProjeto) {
				mensagem = mensagem
						.concat("-" + projeto.getId() + ";" + projeto.getNome() + ";" + projeto.getLocation() + ";");

				for (Pacote pacote : projeto.getListPacote()) {
					if (pacote != null) {
						mensagem = mensagem.concat(pacote.getId() + "," + pacote.getNome()+",");

						for (Classe classe : pacote.getListClasse()) {
							mensagem = mensagem.concat(classe.getId() + "/" + classe.getNome() + ",");
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

	public void updateClasse(Projeto projeto, Pacote pacote, String nameClass, Boolean main, String typeClasse) {
		if (projeto != null && pacote != null) {

			EntityManager em = Conn.getEntityManager();
			em.getTransaction().begin();

			Projeto searchProjeto = em.find(Projeto.class, projeto.getId());
			// if (update.equals("Pacote")) {
			Pacote searchPacote = null;

			for (Pacote listPacote : searchProjeto.getListPacote()) {
				if (listPacote.getNome().equals(pacote.getNome())) {
					searchPacote = listPacote;
					break;
				}
			}

			searchPacote.addClass(nameClass, main, typeClasse);

			em.getTransaction().commit();
			em.close();

		} else {
			// MessageAlert.mensagemErro(StringUtility.projectNull);
		}
	}

	private void addProjeto() {

//		Projeto projeto = new Projeto("Meu projeto de teste", "loca/projeto");
//
//		Pacote pacote = new Pacote("Meu pacote de teste");
//
//		Classe classeCadastroEmpresa = new Classe("CadastroEmpresa");
//		Classe classeLogin = new Classe("Login");
//		Classe classeBuscarEmpresa = new Classe("Buscarempresa");
//		Classe classePrincipal = new Classe("Principal");
//		Classe classeRedefinirSenhaUsuario = new Classe("RedefinirSenhaUsuario");
//		Classe classeAtenderOcorrencia = new Classe("Atender Ocorrencia");
//		Classe classeMain = new Classe("Main");
//
//		classeCadastroEmpresa.setCodigo(StringUtility.cadastrarEmpresa);
//		classeLogin.setCodigo(StringUtility.login);
//		classeBuscarEmpresa.setCodigo(StringUtility.buscarEmpresa);
//		classePrincipal.setCodigo(StringUtility.telaPrincipal);
//		classeRedefinirSenhaUsuario.setCodigo(StringUtility.redefinirSenhaUsuario);
//		classeAtenderOcorrencia.setCodigo(StringUtility.classeAtenderOcorrencia);
//		classeMain.setCodigo(StringUtility.main);
//
//		pacote.setListClasse(new ArrayList<Classe>());
//
//		pacote.addClass(classeCadastroEmpresa);
//		pacote.addClass(classeLogin);
//		pacote.addClass(classeBuscarEmpresa);
//		pacote.addClass(classePrincipal);
//		pacote.addClass(classeRedefinirSenhaUsuario);
//		pacote.addClass(classeAtenderOcorrencia);
//		pacote.addClass(classeMain);
//
//		projeto.setListPacote(new ArrayList<Pacote>());
//
//		projeto.addPackage(pacote);
//
//		addProjectBD(projeto);

	}

	public void editProject(Projeto projeto) {
		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Projeto projetoSearch = em.find(Projeto.class, projeto.getId());
		projetoSearch.setNome(projeto.getNome());

		em.getTransaction().commit();
		em.close();
	}

	public void editPacote(Pacote pacote) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Pacote pacoteSeach = em.find(Pacote.class, pacote.getId());

		pacoteSeach.setNome(pacote.getNome());

		em.getTransaction().commit();
		em.close();
	}

	public void editClass(Classe classe) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Classe classeSearch = em.find(Classe.class, classe.getId());

		classeSearch.setNome(classe.getNome());
		classeSearch.setCodigo(classe.getCodigo());

		em.getTransaction().commit();
		em.close();
	}

	public void removeProject(Projeto projeto) {
		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Projeto projetoSearch = em.find(Projeto.class, projeto.getId());

		em.remove(projetoSearch);

		em.getTransaction().commit();
		em.close();
	}

	public void removePacote(Pacote pacote) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Pacote pacoteSearch = em.find(Pacote.class, pacote.getId());

		em.remove(pacoteSearch);

		em.getTransaction().commit();
		em.close();
	}

	public void removeClass(Classe classe) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Classe classeSearch = em.find(Classe.class, classe.getId());

		em.remove(classeSearch);

		em.getTransaction().commit();
		em.close();
	}

}
