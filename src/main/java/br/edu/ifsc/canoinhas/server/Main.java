package br.edu.ifsc.canoinhas.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import br.edu.ifsc.canoinhas.server.dao.Conn;
import br.edu.ifsc.canoinhas.server.dao.DaoEmpresa;
import br.edu.ifsc.canoinhas.server.dao.DaoOcorrencia;
import br.edu.ifsc.canoinhas.server.dao.DaoUsuario;
import br.edu.ifsc.canoinhas.server.dao.projeto.DaoDBClasse;
import br.edu.ifsc.canoinhas.server.dao.projeto.DaoDBPacote;
import br.edu.ifsc.canoinhas.server.dao.projeto.DaoDBProjeto;
import br.edu.ifsc.canoinhas.server.exceptions.CommException;
import br.edu.ifsc.canoinhas.server.exceptions.NetDeviceException;
import br.edu.ifsc.canoinhas.server.exceptions.PortException;

public class Main {

	public static void main(String[] args) {

		ServerSocket server = null;
		try {
			Conn.getEntityManager().close();
			printServerInfo();
			server = openSocket();
			System.out.println("O servidor está aberto na porta " + server.getLocalPort());
			while (true) {
				listen(server);
			}
		} catch (PortException ex) {
			System.err.println("Nenhuma porta disponível no servidor.");
		} catch (NetDeviceException ex) {
			System.err.println("A placa de rede está com algum problema.");
		} catch (CommException ex) {
			System.err.println("Ocorreu algum problema em uma comunicação com um cliente.");
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
				}
			}
			Conn.closeConn();
		}
	}

	private static void listen(ServerSocket server) throws CommException {
		try {
			// método para falar que o servidor deve aceitar conexões
			Socket client = server.accept();
			process(client);
			client.close();
		} catch (IOException e) {
			throw new CommException();
		}
	}

	private static void process(Socket client) throws IOException {

		System.out.println("------------------------------------------------------");

		System.out.println("Cliente conectado: " + client.getInetAddress().getHostAddress());

		ObjectInputStream in = new ObjectInputStream(client.getInputStream());

		String msg = in.readUTF();
		System.out.println("Cliente enviou: " + msg);

		if (msg.contains("classe")) {
			System.out.println("Entra aqui");
		}

		ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

		String recebido[] = null;
		if (msg.contains("-")) {
			System.out.println("Entrou aqui deu certo o bagulho");
			recebido = msg.split("-");
		} else {

			recebido = msg.split(";");

		}

		for (String string : recebido) {
			System.out.println(string);
		}

		if (recebido[0].contentEquals("projeto")) {
			DaoDBProjeto daoProjeto = new DaoDBProjeto();

			if (recebido[1].contentEquals("add")) {

				daoProjeto.addProjectBD(recebido[2], recebido[3], out, client);

			}

			if (recebido[1].contentEquals("getAll")) {
				daoProjeto.getAllProjetoSubmitClient(out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoProjeto.editProject(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("remove")) {
				daoProjeto.removeProject(recebido[2], out, client);
			}
		}

		if (recebido[0].contentEquals("pacote")) {

			DaoDBPacote daoPacote = new DaoDBPacote();

			if (recebido[1].contentEquals("add")) {
				daoPacote.addPackage(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoPacote.editProject(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("remove")) {
				daoPacote.removerPacote(recebido[2], out, client);
			}
		}

		if (recebido[0].contentEquals("classe")) {
			DaoDBClasse daoDbClasse = new DaoDBClasse();

			if (recebido[1].contentEquals("add")) {
				daoDbClasse.addClass(recebido[2], recebido[3], recebido[4], recebido[5], out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoDbClasse.editClass(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("remove")) {
				daoDbClasse.removerClasse(recebido[3], out, client);
			}

			if (recebido[1].contentEquals("editCodigo")) {
				daoDbClasse.editCodigoClass(recebido[2], recebido[3], out, client);
			}
		}

		if (recebido[0].contentEquals("usuario")) {

			DaoUsuario daoUsuario = new DaoUsuario();
			if (recebido[1].contentEquals("getAll")) {
				daoUsuario.getAllUsuarioSubmitClient(out, client);
			}
			if (recebido[1].contentEquals("add")) {
				daoUsuario.addUsuarioBD(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("editName")) {
				daoUsuario.editNameUsuario(recebido[2], recebido[3], recebido[4], out, client);
			}

			if (recebido[1].contentEquals("editSenha")) {
				daoUsuario.editPasswordUsuario(recebido[2], recebido[3], recebido[4], out, client);
			}

			if (recebido[1].contentEquals("remove")) {
				daoUsuario.removeUsuario(recebido[2], out, client);
			}

		}

		if (recebido[0].contentEquals("empresa")) {
			DaoEmpresa daoEmpresa = new DaoEmpresa();

			if (recebido[1].contentEquals("getAll")) {
				daoEmpresa.getAllEmpresaSubmitClient(out, client);
			}

			if (recebido[1].contentEquals("add")) {
				daoEmpresa.addEmpresa(recebido[2], recebido[3], recebido[4], recebido[5], recebido[6], recebido[7],
						recebido[8], recebido[9], recebido[10], out, client);
			}

			if (recebido[1].contentEquals("remove")) {
				daoEmpresa.removeEmpresa(recebido[2], out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoEmpresa.updateEmpresa(recebido[2], recebido[3], recebido[4], recebido[5], recebido[6], recebido[7],
						recebido[8], recebido[9], recebido[10], recebido[11], out, client);
			}
		}

		if (recebido[0].contentEquals("ocorrencia")) {
			DaoOcorrencia daoOcorrencia = new DaoOcorrencia();
			if (recebido[1].contentEquals("add")) {
				daoOcorrencia.addOcorrencia(recebido[2], recebido[3], recebido[4], recebido[5], recebido[6],
						recebido[7], recebido[8], recebido[9], recebido[10], out, client);
			}

			if (recebido[1].contentEquals("getAll")) {
				daoOcorrencia.getAllOcorrenciaSubmitClient(out, client);
			}
			if (recebido[1].contentEquals("edit")) {
				daoOcorrencia.editStatusOcorrencia(recebido[2], recebido[3], out, client);
			}
		}

		out.flush();
		in.close();
		out.close();

	}

	private static ServerSocket openSocket() throws PortException {

		int port = 1024;
		while (port <= 65535) {
			try {
				return new ServerSocket(port);
			} catch (IOException ex) {
				port++;
			}
		}
		throw new PortException();
	}

	private static void printServerInfo() throws NetDeviceException {
		try {
			System.out.println("-----------------------------------");
			System.out.println("Informações do servidor:");
			String hostname = InetAddress.getLocalHost().getHostName();
			System.out.println("Hostname: " + hostname);
			Enumeration<NetworkInterface> myNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (myNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = myNetInterfaces.nextElement();
				System.out.println("Interface: " + netInterface.getName());
				Enumeration<InetAddress> interfaceAddrs = netInterface.getInetAddresses();
				while (interfaceAddrs.hasMoreElements()) {
					InetAddress addr = interfaceAddrs.nextElement();
					System.out.println("  " + addr.getHostAddress());
				}
			}
			System.out.println("-----------------------------------");
		} catch (SocketException e1) {
			throw new NetDeviceException();
		} catch (UnknownHostException e) {
			throw new NetDeviceException();
		}
	}
}
