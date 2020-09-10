import java.io.*;
import java.net.*;

public class Client {
    public Socket socket = null;


    public Client(String adress, int port){
        try{
            socket = new Socket(adress,port);
            System.out.println("Client connected!");
        } catch (IOException ieo){
            System.out.println(ieo);
        }
    }

    public Socket getSocket(){
        return socket;
    }

    // ------------- MAIN --------------------
    public static void main(String[]args){
        Client client_thread = new Client("127.0.0.1",2000);
        Socket socket = client_thread.getSocket();
        new Output(socket);
        new Input(socket);
    }
}


class Output extends Thread{
    private BufferedReader in;
    private boolean alive = true;
    private Socket socket;
    public Output(Socket socket){
        this.socket = socket;
        start();
    }


    public void run() {
        while (alive) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(in.readLine() != null) {
                    System.out.println(in.readLine());
                }
                kill();
            } catch (IOException ioe){
                System.out.println(ioe);
            }
        }
        try {
            socket.close();
            in.close();
            System.out.println("Socket error: closing down..");
        } catch (IOException ioe){
            System.out.println(ioe);
        }


    }

    public void kill(){
        alive = false;
    }
}

class Input extends Thread{

    private Socket socket;
    private PrintWriter out;
    private boolean alive = true;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public Input(Socket socket){
        this.socket = socket;
        start();
    }

    public void run(){
        while(alive) {
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);

                String msg;
                while ((msg = stdIn.readLine()) != null) {
                    out.println(msg);
                }
                stdIn.close();
                socket.close();
                out.close();
                kill();

            } catch (UnknownHostException uknw){
                System.out.println(uknw);
            } catch (IOException ioe){
                System.out.println(ioe);
            }
        }

    }

    public void kill(){
        alive = false;
    }
}
