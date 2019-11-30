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

		ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

		String recebido[] = msg.split(";");

		for (String string : recebido) {
			System.out.println(string);
		}

		if (recebido[0].contentEquals("projeto")) {
			DaoDBProjeto daoProjeto = new DaoDBProjeto();

			if (recebido[1].contentEquals("add")) {

				daoProjeto.addProjectBD(recebido[2], recebido[3], out);

			}

			if (recebido[1].contentEquals("getAll")) {
				daoProjeto.getAllProjetoSubmitClient(out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoProjeto.editProject(recebido[2], recebido[3], out, client);
			}

			if (recebido[1].contentEquals("remove")) {

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
		}

		if (recebido[0].contentEquals("classe")) {
			DaoDBClasse daoDbClasse = new DaoDBClasse();

			if (recebido[1].contentEquals("add")) {
				daoDbClasse.addClass(recebido[2], recebido[3], recebido[4], recebido[5], out, client);
			}

			if (recebido[1].contentEquals("edit")) {
				daoDbClasse.editClass(recebido[2], recebido[3], out, client);
			}

		}

//		if (recebido[0].contentEquals("user")) {
//			if (recebido[1].contentEquals("get"))
//				getUser(out, recebido);
//			else if (recebido[1].contentEquals("getAll"))
//				getAllUser(out);
//			else if (recebido[1].contentEquals("add"))
//				addUser(out, recebido);
//			else if (recebido[1].contentEquals("delete"))
//				deleteUser(out, recebido);
//			else if (recebido[1].contentEquals("update"))
//				updateUser(out, recebido);
//		}

		out.flush();
		in.close();
		out.close();

	}

	private static void updateUser(ObjectOutputStream out, String[] recebido) throws IOException {
//		User user = new User(recebido[2], Integer.valueOf(recebido[3]));
//		new UserDAO().update(user);

	}

	private static void deleteUser(ObjectOutputStream out, String[] recebido) throws IOException {
//		User user = new User(recebido[2], Integer.valueOf(recebido[3]));
//		new UserDAO().delete(user);
	}

	private static void addUser(ObjectOutputStream out, String[] recebido) throws IOException {
//		User user = new User(recebido[2], Integer.valueOf(recebido[3]));
//		new UserDAO().add(user);
	}

	private static void getAllUser(ObjectOutputStream out) throws IOException {
//		String msg = "";
//		List<User> users = new UserDAO().getAll();
//		if (users == null)
//			out.writeUTF("404");
//		else
//			for (User user : users)
//				msg = msg.concat(user.getName() + ";" + user.getAge() + ";" + user.getRegisterDate() + ";");
//		out.writeUTF(msg);
	}

	private static void getUser(ObjectOutputStream out, String[] recebido) throws IOException {
//		User user = new UserDAO().get(recebido[2]);
//		if (user == null) {
//			out.writeUTF("404");
//		} else {
//			out.writeUTF(user.getName() + ";" + user.getAge() + ";" + user.getRegisterDate());
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
