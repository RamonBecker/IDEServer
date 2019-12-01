package br.edu.ifsc.canoinhas.server.dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.persistence.EntityManager;

import br.edu.ifsc.canoinhas.server.entities.Empresa;
import br.edu.ifsc.canoinhas.server.entities.Endereco;
import br.edu.ifsc.canoinhas.server.entities.Projeto;
import br.edu.ifsc.canoinhas.server.entities.Usuario;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoEmpresa {
	private List<Empresa> listEmpresa;

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
						+ empresa.getEndereco().getId() + ";" + empresa.getEndereco().getBairro() + ";"
						+ empresa.getEndereco().getCep() + ";" + empresa.getEndereco().getCidade() + ";"
						+ empresa.getEndereco().getComplemento() + ";" + empresa.getEndereco().getEstado() + ";"
						+ empresa.getEndereco().getNumero() + ";" + empresa.getEndereco().getRua() + ";"
						+ empresa.getEndereco().getTelefone()

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

	public void addEmpresa(String nome, String cnpj, String rua, 
			String bairro, String numero, String telefone,
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

	public void addEmpresa(Empresa empresa) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();
		em.persist(empresa);
		em.getTransaction().commit();
		em.close();
	}

	private void loadDBListEmpresa() {
		EntityManager em = Conn.getEntityManager();
		listEmpresa = em.createQuery("SELECT e FROM Empresa AS e", Empresa.class).getResultList();
		em.close();
	}

	public void deleteEmpresa(Empresa empresa) {
		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();
		Empresa empresaSearch = em.find(Empresa.class, empresa.getId());
		em.remove(empresaSearch);
		em.getTransaction().commit();
		em.close();
	}

	public void updateEmpresa(Empresa empresa) {

		EntityManager em = Conn.getEntityManager();

		em.getTransaction().begin();

		Empresa empresaSearch = em.find(Empresa.class, empresa.getId());

		empresaSearch.setCnpj(empresa.getCnpj());
		empresaSearch.setNome(empresa.getNome());

		empresaSearch.getEndereco().setBairro(empresa.getEndereco().getBairro());
		empresaSearch.getEndereco().setCep(empresa.getEndereco().getCep());
		empresaSearch.getEndereco().setCidade(empresa.getEndereco().getCidade());
		empresaSearch.getEndereco().setEstado(empresa.getEndereco().getEstado());
		empresaSearch.getEndereco().setNumero(empresa.getEndereco().getNumero());
		empresaSearch.getEndereco().setRua(empresa.getEndereco().getRua());
		empresaSearch.getEndereco().setTelefone(empresa.getEndereco().getTelefone());

		System.out.println("Empresa BD:" + empresaSearch);
		em.getTransaction().commit();
		em.close();
	}

	public List<Empresa> getListEmpresa() {
		loadDBListEmpresa();
		return listEmpresa;
	}

}