import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Math.log;
import static java.lang.Math.pow;


public class Client implements Runnable
{
    private static  boolean addition_statement = true;
    private BufferedReader buffered_reader;
    private PrintWriter print_writer;
    static public boolean statement = true;
    static private int operation_id = 1;
    static private int session_id;
    static boolean loop_exit_statement = true;




    private Client(String ip, int port) //Client class constructor
    {
        try
        {
            Socket client_socket = new Socket(ip, port);



            buffered_reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            print_writer = new PrintWriter(client_socket.getOutputStream(), true);

        }


        catch(Exception exception) {}
    }




    @Override
    public void run() //function which reads packets as long as they come
    {
        try
        {
            while(statement) //reading condition packets
            {
                readPacket();

            }



        }
        catch(Exception exception) {}
    }


    private void decodePacket(String packet) //decode method
    {
        double number,number1;
        long time_stamp;
        int temporaryID;

        String[] message_table = packet.split("[$]"); //whole packet is splitted to parts by $ symbol

        Hashtable<String, String> command_split = new Hashtable<>();//Stworzenie tablicy z dwoma Stringami, która przechowywać będzie rodzaj pola oraz jego komunikat np.OP AD // initalizing array with 2 string in one box which will contain type of field and message

        ArrayList<String> command_list = new ArrayList<>(); //Stworzenie tablicy przechowującej przeysłane pola, dzięki której możliwa jest odpowiednia reakcja na przesyłany komunikat// array with command list, which will allow to proper reaction

        for(String element : message_table)
        {
            String[] temporary = element.split("[=]");//Rozdzielenie przesyłanego pakietu // splitting sended packet

            if(temporary.length == 2)
            {
                command_split.put(temporary[0], temporary[1]);

                command_list.add(temporary[0]);
                command_list.add(temporary[1]);
            }
        }

        number = Double.parseDouble(command_split.get("N1"));//przypisanie odpowiednim zmiennym odebranych danych // recieved data is assigned to variables
        number1 = Double.parseDouble(command_split.get("N2"));
        session_id = Integer.parseInt(command_split.get("ID"));
        time_stamp= Long.parseLong(command_split.get("TS"));
        temporaryID = Integer.parseInt(command_split.get("OI"));




        if(command_list.get(1).equals("IN"))//pakiet inicjalizacyjny wysyłany przez serwer // initialization packet sent by server
        {
            System.out.println("Command list:");
            System.out.println("addition");
            System.out.println("power");
            System.out.println("subtract");
            System.out.println("logarithm");
            System.out.println("!exit");
            System.out.println("!disconnect");
            System.out.println("!history");

        }

        if(command_list.get(3).equals("DC")) //odpowiedz serwera na brak klienta o wysłanych ID sesji // server respond to lack of a clint with such session id
        {
            System.out.println("There is no such session id");
        }

        if(command_list.get(3).equals("DT"))
        {
            System.out.println("There is no such operation id"); //odpowiedz serwera na brak operacji o podanym ID // server respond to lack of operation with such operationID
        }

        if((command_list.get(1).equals("AD") || command_list.get(1).equals("LG") || command_list.get(1).equals("PW") || command_list.get(1).equals("SB")) && command_list.get(3).equals("RS"))
        {
            System.out.println("Result of the operation equals: " + number); //odebranie wyniku działania od serwera
        }
        else if((command_list.get(1).equals("HS")||command_list.get(1).equals("HO")) && command_list.get(3).equals("AD")) //wyświetlenie historii operacji dodawnia // displaying addition history
        {
            System.out.println("Operation: "+command_list.get(3));
            System.out.println("Number1: "+number);
            System.out.println("Number2: "+number1);
            double result=number+number1;
            System.out.println("Result: "+result);
            System.out.println("Operation_ID: "+temporaryID);
            System.out.println("Session_ID: "+session_id);
            System.out.println("TimeStamp: "+time_stamp);
        }
        else if((command_list.get(1).equals("HS")||command_list.get(1).equals("HO")) && command_list.get(3).equals("SB"))//wyświetlenie historii operacji odejmowania //displaying subtract history
        {
            System.out.println("Operation: "+command_list.get(3));
            System.out.println("Number1: "+number);
            System.out.println("Number2: "+number1);
            double result=number-number1;
            System.out.println("Result: "+result);
            System.out.println("Operation_ID: "+temporaryID);
            System.out.println("Session_ID: "+session_id);
            System.out.println("TimeStamp: "+time_stamp);
        }
        else if((command_list.get(1).equals("HS")||command_list.get(1).equals("HO")) && command_list.get(3).equals("PW")) //displaying power history
        {
            System.out.println("Operation: "+command_list.get(3));
            System.out.println("Number1: "+number);
            System.out.println("Number2: "+number1);
            double result=pow(number,number1);
            System.out.println("Result: "+result);
            System.out.println("Operation_ID: "+temporaryID);
            System.out.println("Session_ID: "+session_id);
            System.out.println("TimeStamp: "+time_stamp);
        }
        else if((command_list.get(1).equals("HS")||command_list.get(1).equals("HO")) && command_list.get(3).equals("LG")) //displaying logarithm history
        {
            System.out.println("Operation: "+command_list.get(3));
            System.out.println("Number1: "+number);
            System.out.println("Number2: "+number1);
            double result = log(number1)/log(number);
            System.out.println("Result: "+result);
            System.out.println("Operation_ID: "+temporaryID);
            System.out.println("Session_ID: "+session_id);
            System.out.println("TimeStamp: "+time_stamp);
        }
    }

    private String generatePacket(String operation, String status, double number, double number1, int operation_id, int session_id) //funkcja generująca i kodująca pakiet do zadanej postaci // function which generates and codes packets to required form
    {
        String packet = "";

        packet += "OP=" + operation + "$";
        packet += "ST=" + status + "$";
        packet += "N1=" + number + "$";
        packet += "N2=" + number1 + "$";
        packet += "OI=" + operation_id + "$";
        packet += "ID=" + session_id + "$";
        packet += "TS=" + (System.currentTimeMillis()/1000) + "$";

        return packet;
    }

    private void readPacket() throws Exception //metoda odczytująca pakiet // function which reads packets
    {
        char[] packet =new char[1024];

        int length= buffered_reader.read(packet);
        String packet1=new String(packet);

        String vpacket=packet1.substring(0,length);



        decodePacket(vpacket);
    }

    private void writePacket(String operation, String status, double number, double number1, int operation_id, int session_id)//metoda wysyłająca pakiet // send packets
    {
        print_writer.println(generatePacket(operation, status, number, number1, operation_id, session_id));
    }


    public static void main(String args[]) throws Exception
    {

        System.setProperty("line.separator", "");

        Client client = new Client("127.0.0.1",1234); //tworzenie obiektu klienta // creating client object

        Thread thread =new Thread(client); //tworzenie wątku // ilitalizing thread

        thread.start(); //wystartowanie wątku

        while(loop_exit_statement) //pętla umożliwiająca odczytywanie komendy wpisywanej przez użytkowanika // loop which allows to read the command entered by user
        {
            String message;

            Scanner scanner = new Scanner(System.in);

            message = scanner.nextLine();

            switch (message)
            {
                case "addition"://reakcja na wpisanie przez uzytkownika komendy służącej dodaniu dwóch liczb //addition
                {
                    if(addition_statement)
                    {


                        double number;
                        double number1;

                        try
                        {
                            System.out.print("Type first number: ");
                            number = scanner.nextDouble();

                            System.out.print("Type second number: ");
                            number1 = scanner.nextDouble();

                            client.writePacket("AD", "WR", number, number1, operation_id, session_id);


                            operation_id++;
                        }   catch(InputMismatchException e) {System.out.println("Wrong data"); }


                    }

                    break;
                }
                case "!exit"://reakcja na wpisanie przez uzytkownika komendu służącej wyłączeniu programu //exit the programm
                {
                    client.writePacket("EX", "NO", -1, -1, -1, session_id);

                    loop_exit_statement = false;
                    System.exit(0);

                    break;
                }
                case "!disconnect": //reakcja na wpisanie przez uzytkownika komendy służacej rozłączenia klienta od serwera //disconnecting
                {
                    client.writePacket("DC", "NO", -1, -1, -1, session_id);


                    loop_exit_statement = false;
                    System.exit(0);





                    break;
                }
                case "!history":////reakcja na wpisanie przez uzytkownika komendy służącej wyświetleniu historii/showing history
                {
                    try
                    {
                        int choice;
                        int number;


                        System.out.println("Choose a way of searching: \n1.SessionID\n2.OperationID");

                        choice=scanner.nextInt();

                        if(choice==1)
                        {
                            System.out.println("Type a sessionID");
                            System.out.println("Your session ID = "+session_id);
                            number=scanner.nextInt();
                            client.writePacket("HS", "WH", number, -1, -1, session_id);
                        }
                        else
                        {
                            System.out.println("Type a operationID");
                            number= scanner.nextInt();
                            client.writePacket("HO", "WH", number, -1, -1, session_id);
                        }

                    }   catch(InputMismatchException e) {System.out.println("Wrong data");}

                    break;
                }
                case "logarithm"://reakcja na wpisanie przez uzytkownika komendy służącej logarytmowaniu //logarithm
                {
                    try
                    {
                        double number;
                        double number1;

                        System.out.print("Type base of the logarithm: ");
                        number = scanner.nextDouble();

                        if(number < 0 || number == 1)
                        {
                            System.out.println("Base of the logarithm is incorrect");

                            break;
                        }


                        System.out.print("Type number of the logarithm: ");
                        number1 = scanner.nextDouble();

                        if(number1 < 0)
                        {
                            System.out.println("number of the logarithm is incorrect");

                            break;
                        }

                        client.writePacket("LG", "WR", number, number1, operation_id, session_id);

                        operation_id++;
                    }   catch (InputMismatchException e) {System.out.println("Wrong data");}


                    break;
                }
                case "power"://reakcja na wpisanie przez uzytkownika komendy służącej potęgowaniu //power
                {
                    try
                    {
                        double number;
                        double number1;

                        System.out.print("Type base of the power: ");
                        number = scanner.nextDouble();

                        System.out.print("Type radix of the power: ");
                        number1 = scanner.nextDouble();

                        client.writePacket("PW", "WR", number, number1, operation_id, session_id);

                        operation_id++;
                    }   catch(InputMismatchException e) {System.out.println("Wrong data");}


                    break;
                }
                case "subtract"://reakcja na wpisanie przez uzytkownika komendy służącej odejmowaniu//subtraction
                {
                    try
                    {
                        double number;
                        double number1;

                        System.out.print("Type first number: ");
                        number = scanner.nextDouble();

                        System.out.print("Type second number: ");
                        number1 = scanner.nextDouble();

                        client.writePacket("SB", "WR", number, number1, operation_id, session_id);

                        operation_id++;
                    }   catch (InputMismatchException e ) {System.out.println("Wrong data");}


                    break;
                }

                default://reakcja na wpisanie przez uzytkownika nieznanej komendy
                {
                    System.out.println("You have typed a wrong command. Try again.");

                    break;
                }
            }
        }

        thread.join();

    }
}