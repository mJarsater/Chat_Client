import java.io.*;
import java.net.*;

public class Client {
    public Socket socket = null;

    // Konstruktor för klassen Client
    public Client(String adress, int port){
        try{
            /* Skapar en ny socket med den
            IPadress och port som har skickts
            med som parameter. */
            socket = new Socket(adress,port);
            System.out.println("Client connected");
            System.out.println("Connected to IP: "+adress+ " on Port: "+port);
        } catch (IOException ieo){
            System.out.println(ieo);
        }
    }

    // Metod som returnerar klientens socket.
    public Socket getSocket(){
        return socket;
    }

    // ------------- MAIN --------------------
    public static void main(String[]args){
        switch (args.length) {
            case 1: {
                int port = Integer.parseInt(args[0]);
                Client client = new Client("127.0.0.1", port);
                Socket socket = client.getSocket();
                new Output(socket);
                new Input(socket);
                break;
            }
            case 2: {
                String host = args[0];
                int port = Integer.parseInt(args[1]);
                Client client = new Client(host, port);
                Socket socket = client.getSocket();
                new Output(socket);
                new Input(socket);
                break;
            }
            default:
                Client client = new Client("127.0.0.1", 2000);
                Socket socket = client.getSocket();
                new Output(socket);
                new Input(socket);
                break;
        }



    }
    // ------------- MAIN END ----------------
}


class Output extends Thread{
    private BufferedReader in;
    private Socket socket;
    private boolean alive = true;


    // Konstruktor för klassen Output
    public Output(Socket socket){
        //Sätter socket till den som skickats med som parameter
        this.socket = socket;
        //Start-metod för klassen (run)
        start();
    }


    // Start metoden som körs sålänga alive är sant
    public void run() {
        while (alive) {
            try {
                // Bufferedreader som letar efter output från socketen.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /* Skriver ut outputen från bufferedreadern
                sålänge det inte är null
                */
                while(in.readLine() != null) {
                    System.out.println(in.readLine());
                }


                kill();
            } catch (IOException ioe){
                System.out.println("Server disconnected.");
                kill();

            }
        }
        try {
            //Stänger socketen.
            socket.close();
            //Stänger bufferedreadern.
            in.close();
        } catch (IOException ioe){
            System.out.println("Error: Could not close socket/bufferedReader");
        }


    }

    // Metod som "dödar" tråden och stänger programmet.
    public void kill(){
        alive = false;
        System.exit(1);
    }
}

class Input extends Thread{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader stdIn;
    private boolean alive = true;

    // Konstruktor för klassen Input
    public Input(Socket socket){
        //Sätter socket till den som skickats med som parameter
        this.socket = socket;
        /* Skapar en Bufferedreader från inputstreamreader med
        parametern System.in. Vilket "standard" inputstream,
        dvs oftast skrivbordet.
        */
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        //Start-metod för klassen (run)
        start();
    }


    // Start metoden som körs sålänga alive är sant
    public void run(){
        while(alive) {
            try {
                /* Skapar en printwriter som kan skriva till
                socketens outputstream.
                */
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
                String msg;

                //Skriver till socketen, sålänge msg != null
                while ((msg = stdIn.readLine()) != null) {
                    out.println(msg);
                }

                // Stänger bufferedreadern
                stdIn.close();
                // Stänger socketen
                socket.close();
                //Stänger printwritern
                out.close();
                kill();

            } catch (UnknownHostException uknw){
                System.out.println(uknw);
                kill();
            } catch (IOException ioe){
                System.out.println(ioe);
                kill();
            }
        }

    }

    // Metod som "dödar" tråden och stänger programmet.
    public void kill(){
        alive = false;
        System.out.println("Closing down...");
        System.exit(1);
    }
}
