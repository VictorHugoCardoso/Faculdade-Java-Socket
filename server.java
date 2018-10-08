import java.io.*;
import java.util.*;
import java.net.*;

public class server{
  
  ServerSocket servidor;
  String folder = "C:\\Users\\victo\\Documents\\faculdade\\SERVER\\";
  
  public static void main(String[] args) throws Exception{
    server s = new server();
    
    s.inicializaServidor();
    s.executaServidor();
  }
  
  public void inicializaServidor() throws Exception{
    servidor = new ServerSocket(8889);
    InetAddress ip = InetAddress.getLocalHost();
    
    System.out.println("\n[SERVIDOR INICIALIZADO]\n");
    System.out.println("------------------------------------------------------------");
    System.out.println("- IP do servidor: ["+ip.getHostAddress()+"] ouvindo na porta "+servidor.getLocalPort());
    System.out.println("------------------------------------------------------------");
  }
  
  public void executaServidor() throws IOException{
    
    Thread accept = new Thread () {
      public void run () {
        while(true){
          try{
            Socket cliente = servidor.accept();
            if(cliente.isBound()){
              System.out.println("\n- Cliente Conectado: " + cliente.getInetAddress().getHostAddress());
              receber(cliente);
            }
          }catch(IOException e){
            System.out.println("IOException: " + e.getMessage());
          }
        }
      }
    };
    accept.start();
  }
  
  public void receber(Socket socket){
    try{
      int bytesRead;  
      int current = 0;  
           
      InputStream in = socket.getInputStream();
         
      DataInputStream socketData = new DataInputStream(in);   
         
      String fileName = socketData.readUTF();          
      long size = socketData.readLong();
      
      if(size!=0L){
        OutputStream output = new FileOutputStream(folder+fileName);
        byte[] buffer = new byte[1024];     
        while (size > 0 && (bytesRead = socketData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
        {     
          output.write(buffer, 0, bytesRead);     
          size -= bytesRead;     
        }  
           
        System.out.println("Arquivo " + fileName + " recebido!");  
        output.close();
      }else{ 
        enviar(fileName,socket.getInetAddress().getHostAddress());
      }

      in.close();
      socketData.close();
      
    }catch(IOException e){
      System.out.println("IOException: " + e.getMessage());
    }
  }
  
  public void enviar(String fileName,String ip){
    try{
          
      File myFile = new File(folder+fileName);
      byte[] mybytearray = new byte[(int) myFile.length()];  
      
      FileInputStream fis = new FileInputStream(myFile);  
      BufferedInputStream bis = new BufferedInputStream(fis);   
      
      DataInputStream dis = new DataInputStream(bis);     
      dis.readFully(mybytearray, 0, mybytearray.length);  
      
      
      Socket sender = new Socket(ip,8080);

      OutputStream os = sender.getOutputStream();  

      DataOutputStream dos = new DataOutputStream(os);     
      dos.writeUTF(myFile.getName());
      dos.writeLong(mybytearray.length);    
      dos.write(mybytearray, 0, mybytearray.length); 
      dos.flush();
         
      os.close();
      dos.close();
      sender.close();
      sender = null;
    }catch(IOException e){
      System.out.println("IOException: " + e.getMessage());
    }
  }
  
}
