package br.edu.ifsc.canoinhas.server.dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.persistence.EntityManager;

import br.edu.ifsc.canoinhas.server.entities.Empresa;
import br.edu.ifsc.canoinhas.server.entities.Endereco;
import br.edu.ifsc.canoinhas.server.entities.Ocorrencia;
import br.edu.ifsc.canoinhas.server.utility.StringUtility;

public class DaoOcorrencia {

	public void addOcorrencia(String nomeVitima, String gravidade, String rua, String bairro, String numero,
			String telefone, String complemento, String cep, String cidade, ObjectOutputStream out, Socket client)
			throws IOException {

		EntityManager em = Conn.getEntityManager();

		try {

			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");
			System.out.println("Inserindo ocorrencia no banco de dados");
			System.out.println("------------------------------------------------------");
			System.out.println("------------------------------------------------------");

			Endereco endereco = new Endereco(rua, bairro, numero, telefone, complemento, cep, cidade);

			Ocorrencia ocorrencia = new Ocorrencia(nomeVitima, gravidade, endereco);

			em.getTransaction().begin();
			em.persist(ocorrencia);
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
		System.out.println("Enviado resposta para cliente, que a ocorrencia foi criada com sucesso !");
	}

	public List<Ocorrencia> getAllOcorrencia() {
		EntityManager em = Conn.getEntityManager();
		List<Ocorrencia> listOcorrencia = em.createQuery("FROM Ocorrencia", Ocorrencia.class).getResultList();
		em.close();

		return listOcorrencia;
	}

	public void getAllOcorrenciaSubmitClient(ObjectOutputStream out, Socket client) throws IOException {

		String mensagem = "";

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("Realizando consulta no Banco de Dados");

		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		List<Ocorrencia> listOcorrencia = getAllOcorrencia();

		System.out.println(listOcorrencia);

		if (listOcorrencia == null) {
			out.writeUTF("404");
		} else {
			for (Ocorrencia ocorrencia : listOcorrencia) {

				mensagem = mensagem.concat("-" + ocorrencia.getId() + ";" + ocorrencia.getNomeVitima() + ";"
						+ ocorrencia.getGravidade() + ";" + ocorrencia.getEndereco().getId() + ";"
						+ ocorrencia.getEndereco().getRua() + ";" + ocorrencia.getEndereco().getBairro() + ";"
						+ ocorrencia.getEndereco().getNumero() + ";" + ocorrencia.getEndereco().getTelefone() + ";"
						+ ocorrencia.getEndereco().getComplemento() + ";" + ocorrencia.getEndereco().getCep() + ";"
						+ ocorrencia.getEndereco().getCidade()

				);
			}

			out.writeUTF(mensagem);
		}

		System.out.println("------------------------------------------------------");
		System.out.println("Enviando pacote de dados de projetos para cliente: "
				+ client.getInetAddress().getHostAddress() + "  Host Name: " + client.getInetAddress().getHostName());
		System.out.println("------------------------------------------------------");
		System.out.println("Pacote de dados enviado para cliente: " + mensagem);

	}

	public void editOcorrencia(Ocorrencia ocorrencia) {

		EntityManager em = Conn.getEntityManager();
		em.getTransaction().begin();
		Ocorrencia ocorrenciaSearch = em.find(Ocorrencia.class, ocorrencia.getId());
		ocorrenciaSearch.setStatus(ocorrencia.getStatus());
		em.getTransaction().commit();
		em.close();

	}

}
