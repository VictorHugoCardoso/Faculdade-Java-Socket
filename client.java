import java.io.*;
import java.net.*;

public class client {
	
	private Socket cliente;
	ServerSocket receiver;
	
	public static void main(String[] args) throws Exception{
		client c = new client();
		
		c.abreServidor();
		c.executaServidor();	
	}
	
	public void connect(String server, int porta) throws UnknownHostException, IOException{
		cliente = new Socket(server,porta);
	}

	public void disconnect() throws UnknownHostException, IOException{
		cliente.close();
		cliente = null;
	}

	public void abreServidor() throws Exception{
		receiver = new ServerSocket(8080);
	}
	
	public void executaServidor() throws Exception{
		client c = new client();

		Thread r = new Thread () {
			public void run () {
			while(true){
				try{
				Socket cliente = receiver.accept();
				if(cliente.isBound()){
					receber(cliente);
				}
				}catch(IOException e){
				System.out.println("IOException: " + e.getMessage());
				}
			}
			}
		};

		r.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String opcao;
		String ip;
		int port=8889;
		
		System.out.println("Entre com o IP do servidor: ");
		ip = reader.readLine().trim();

		if(ip.equals("11")){
			ip = "179.106.204.197";
		}
			
		while(true){
			System.out.println("         *---------------------------*");
			System.out.println("         |        MENU DE REDES      |");
			System.out.println("         *---------------------------*");
			System.out.println("*--------------------------------------------*");
			System.out.println("| |01| - Upload de Arquivos                  |");
			System.out.println("| |02| - Download de Arquivos                |");
			System.out.println("*--------------------------------------------*");
			System.out.println("| |00| - Sair                                |");
			System.out.println("*--------------------------------------------*");
			System.out.println("ESCOLHA: \n");
			
			opcao = reader.readLine().trim();

			switch(opcao){
				case "1":

					c.connect(ip,port);
					System.out.println("Entre com o caminho do arquivo: ");
					c.enviar(reader.readLine().trim());
					c.disconnect();

					break;

				case "2":

					c.connect(ip,port);
					System.out.println("Entre com o nome do arquivo: ");
					c.get(reader.readLine().trim());
					c.disconnect();



					break;

				case "0":

					System.exit(0);

				default:
					System.out.println("Invalido\n");
			}
		}
		
	}
	
	public void enviar(String arq) throws IOException{
		File myFile = new File(arq); //Arquivo a ser enviado 
		byte[] mybytearray = new byte[(int) myFile.length()]; //Cria um vetor de bytes do tamanho do arquivo	
			 
		FileInputStream fis = new FileInputStream(myFile);	
		BufferedInputStream bis = new BufferedInputStream(fis);	 
			 
		DataInputStream dis = new DataInputStream(bis);	 
		dis.readFully(mybytearray, 0, mybytearray.length);	
			 
		OutputStream os = cliente.getOutputStream();	
		
		DataOutputStream dos = new DataOutputStream(os);	 
		dos.writeUTF(myFile.getName()); //Enviando o nome do arquivo
		dos.writeLong(mybytearray.length); //Enviando o tamanho do arquivo	 
		dos.write(mybytearray, 0, mybytearray.length); //Enviando o arquivo
		dos.flush(); //Forca o envio
			 
		System.out.println("Arquivo " + myFile.getName() + " enviado!");	
			 
		os.close();
		dos.close();
	}
	
	public void receber(Socket socket){
	try{
		int bytesRead;

		int lidos = 0;
		long total = 0;
		long inicio = System.currentTimeMillis();

		InputStream in = socket.getInputStream();
		 
		DataInputStream socketData = new DataInputStream(in);	 
		 
		String fileName = socketData.readUTF();
		OutputStream output = new FileOutputStream(System.getProperty("user.dir")+"\\"+fileName);	 
		long size = socketData.readLong();
		byte[] buffer = new byte[1024];	 
		while (size > 0 && (bytesRead = socketData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){	 
			output.write(buffer, 0, bytesRead);	 
			size -= bytesRead;
			lidos = bytesRead;
			total += lidos;
		}	
		long fim = System.currentTimeMillis();
		
		long totalKb = total / 1024;
		long duracao = (fim - inicio) / 1000;
		long taxa = totalKb / duracao;

		System.out.println(taxa+"kb/s");	 
		System.out.println("Arquivo " + fileName + " recebido!");	 

		in.close();
		socketData.close();
		output.close();
	}catch(IOException e){
		System.out.println("IOException: " + e.getMessage());
	}
	}

	public void get(String arq) throws IOException{	
	OutputStream os = cliente.getOutputStream();	
	
	DataOutputStream dos = new DataOutputStream(os);	 
	dos.writeUTF(arq);
	dos.writeLong(0L);	
	dos.flush(); 
				 
	os.close();
	dos.close();
	}
}
