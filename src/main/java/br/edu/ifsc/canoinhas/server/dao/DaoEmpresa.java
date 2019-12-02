package br.edu.ifsc.canoinhas.server.dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.logging.Message;

import br.edu.ifsc.canoinhas.server.entities.Empresa;
import br.edu.ifsc.canoinhas.server.entities.Endereco;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoEmpresa {

	public void getAllEmpresaSubmitClient(ObjectOutputStream out, Socket client) throws IOException {

		String mensagem = "";

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		List<Empresa> listEmpresa = getAllEmpresa();

		System.out.println(listEmpresa);

		if (listEmpresa == null) {
			out.writeUTF("404");
		} else {
			for (Empresa empresa : listEmpresa) {
				mensagem = mensagem.concat("-" + empresa.getId() + ";" + empresa.getNome() + ";" + empresa.getCnpj()
						+ ";" + empresa.getEndereco().getId() + ";" + empresa.getEndereco().getRua() + ";"
						+ empresa.getEndereco().getBairro() + ";" + empresa.getEndereco().getNumero() + ";"
						+ empresa.getEndereco().getEstado() + ";" + empresa.getEndereco().getCep() + ";"
						+ empresa.getEndereco().getCidade() + ";" + empresa.getEndereco().getTelefone()

				);
			}
			out.writeUTF(mensagem);
		}

		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de empresas para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void addEmpresa(String nome, String cnpj, String rua, String bairro, String numero, String telefone,
			String estado, String cep, String cidade, ObjectOutputStream out, Socket client) throws IOException {

		EntityManager em = Conn.getEntityManager();

		try {

			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");
			System.out.println("Inserindo empresa no banco de dados");
			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");

			Endereco endereco = new Endereco(rua, bairro, numero, estado, cep, cidade, telefone, "");

			Empresa empresa = new Empresa(nome, cnpj, endereco);

			em.getTransaction().begin();
			em.persist(empresa);
			em.getTransaction().commit();

			out.writeUTF(StringUtility.ok);

		} catch (IllegalArgumentException e) {
			out.writeUTF(e.getMessage());

		} finally {
			em.close();
		}

		System.out.println("Enviando pacote de dados de empresa para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		out.writeUTF("Ok");
		System.out.println("Enviado resposta para cliente, que a empresa foi criada com sucesso !");
	}

	public List<Empresa> getAllEmpresa() {
		EntityManager em = Conn.getEntityManager();
		List<Empresa> listEmpresa = em.createQuery("FROM Empresa", Empresa.class).getResultList();
		em.close();
		return listEmpresa;
	}

	public void removeEmpresa(String idEmpresa, ObjectOutputStream out, Socket client) throws IOException {
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Removendo empresa do Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Empresa empresaSearch = em.find(Empresa.class, Integer.parseInt(idEmpresa));
		em.remove(empresaSearch);
		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (empresaSearch != null) {
			mensagem = "Ok";

		}
		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de confirmação para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");

		System.out.println("Pacote de dados enviado para cliente: " + mensagem);
		out.writeUTF(mensagem);
	}

	public void updateEmpresa(String id, String nome, String cnpj, String rua, String bairro, String numero,
			String telefone, String estado, String cep, String cidade, ObjectOutputStream out, Socket client)
			throws IOException {

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Atualizando dados da empresa no Banco de Dados");
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Empresa empresaSearch = em.find(Empresa.class, Integer.parseInt(id));

		empresaSearch.setCnpj(cnpj);
		empresaSearch.setNome(nome);

		empresaSearch.getEndereco().setBairro(bairro);
		empresaSearch.getEndereco().setCep(cep);
		empresaSearch.getEndereco().setCidade(cidade);
		empresaSearch.getEndereco().setEstado(estado);
		empresaSearch.getEndereco().setNumero(numero);
		empresaSearch.getEndereco().setRua(rua);
		empresaSearch.getEndereco().setTelefone(telefone);

		System.out.println("Empresa BD:" + empresaSearch);
		em.getTransaction().commit();
		em.close();

		String mensagem = "404";

		if (empresaSearch != null) {
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