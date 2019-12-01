package br.edu.ifsc.canoinhas.server.dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.persistence.EntityManager;

import br.edu.ifsc.canoinhas.server.entities.Classe;
import br.edu.ifsc.canoinhas.server.entities.Pacote;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.entities.Usuario;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoUsuario {

	private static DaoUsuario controllerDBUsuario;
	private List<Usuario> listUsuario;

	public DaoUsuario() {
	}

	public static DaoUsuario getInstance() {
		if (controllerDBUsuario == null) {
			controllerDBUsuario = new DaoUsuario();
		}
		return controllerDBUsuario;
	}

	public List<Usuario> getAllUser() {
		EntityManager em = Conn.getEntityManager();
		List<Usuario> listUsuario = em.createQuery("FROM Usuario", Usuario.class).getResultList();
		em.close();

		return listUsuario;
	}

	public void addUsuarioBD(String nome, String senha, ObjectOutputStream out, Socket client) throws IOException {

		EntityManager em = Conn.getEntityManager();

		try {
			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");
			System.out.println("Inserindo usuario no banco de dados");
			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");

			Usuario usuario = new Usuario(nome, senha);

			em.getTransaction().begin();
			em.persist(usuario);
			em.getTransaction().commit();

			out.writeUTF(StringUtility.ok);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();

		} finally {
			em.close();
		}

		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		out.writeUTF("Ok");
		System.out.println("Enviado resposta para cliente, que o usuario foi criado com sucesso !");
	}

	public void getAllUsuarioSubmitClient(ObjectOutputStream out, Socket client) throws IOException {

		String mensagem = "";

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		List<Usuario> listUsuario = getAllUser();

		System.out.println(listUsuario);

		if (listUsuario == null) {
			out.writeUTF("404");
		} else {
			for (Usuario usuario : listUsuario) {
				mensagem = mensagem
						.concat(usuario.getId() + ";" + usuario.getName() + ";" + usuario.getPassword() + ";");
			}
			out.writeUTF(mensagem);
		}

		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void addUsuario(Usuario usuario) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();
		em.persist(usuario);
		em.getTransaction().commit();
		em.close();
		// MessageAlert.mensagemRealizadoSucesso(StringUtility.createUser);
	}

	public void updateUsuario(Usuario usuario) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();

		Usuario searchUser = em.find(Usuario.class, usuario.getId());

		searchUser.setName(usuario.getName());
		searchUser.setPassword(usuario.getPassword());
		em.getTransaction().commit();
		em.close();
	}

	public void alterNameUsuario(String newName, String oldName, String password) {
		// loadUserBD();

		Usuario user = null;

		for (Usuario usuario : listUsuario) {
			if (usuario.getName().equals(oldName) && usuario.getPassword().equals(password)) {
				user = usuario;
				break;
			}
		}

		if (user != null) {
			user.setName(newName);
			updateUsuario(user);
			// MessageAlert.mensagemRealizadoSucesso(StringUtility.alterUser);
		} else {
			// MessageAlert.mensagemErro(StringUtility.loginIncorret);
		}
	}

	public void alterPassword(String nameUser, String oldPassword, String newPassword) {
		// loadUserBD();
		Usuario user = null;

		for (Usuario usuario : listUsuario) {
			if (usuario.getName().equals(nameUser) && usuario.getPassword().equals(oldPassword)) {
				user = usuario;
				break;
			}
		}

		if (user != null) {
			user.setPassword(newPassword);
			updateUsuario(user);
			// MessageAlert.mensagemRealizadoSucesso(StringUtility.alterPass);
		} else {
			// MessageAlert.mensagemErro(StringUtility.loginIncorret);
		}
	}

	public boolean login(String nameUser, String passwordUser) {

		// loadUserBD();

		for (Usuario usuario : listUsuario) {
			if (usuario.getName().equals(nameUser) && usuario.getPassword().equals(passwordUser)) {
				return true;
			}
		}
		return false;
	}

	public void editNameUsuario(String idUsuario, String newName, String senha, ObjectOutputStream out, Socket client)
			throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Alterando nome do usuário  no Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Usuario usuarioSearch = em.find(Usuario.class, Integer.parseInt(idUsuario));
		usuarioSearch.setName(newName);
		usuarioSearch.setPassword(senha);
		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (usuarioSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void removeUsuario(String idUsuario, ObjectOutputStream out, Socket client) throws IOException {
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Removendo usuário do Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Usuario usuarioSearch = em.find(Usuario.class, Integer.parseInt(idUsuario));
		em.remove(usuarioSearch);
		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (usuarioSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);
	}

	public void editPasswordUsuario(String idUsuario, String nome, String newSenha, ObjectOutputStream out,
			Socket client) throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Alterando senha do usuário  no Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Usuario usuarioSearch = em.find(Usuario.class, Integer.parseInt(idUsuario));
		usuarioSearch.setName(nome);
		usuarioSearch.setPassword(newSenha);
		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (usuarioSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		out.writeUTF(mensagem);
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public List<Usuario> getListUsuario() {
		return listUsuario;
	}

}
